package ru.cwcode.tkach.ipmc;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.*;

import java.io.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class SerializablePacket<T extends Serializable> implements Packet {
  private T object;
  
  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public void read(ByteArrayDataInput input) {
    int size = input.readInt();
    validateSerializedSize(size);
    byte[] data = new byte[size];
    
    input.readFully(data);
    
    try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
         ObjectInputStream ois = new ObjectInputStream(bis)) {
      ois.setObjectInputFilter(objectInputFilter());
      object = (T) ois.readObject();
    }
  }
  
  @Override
  @SneakyThrows
  public void write(ByteArrayDataOutput output) {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(bos)) {
      
      oos.writeObject(object);
      
      byte[] bytes = bos.toByteArray();
      validateSerializedSize(bytes.length);
      output.writeInt(bytes.length);
      output.write(bytes);
    }
  }

  protected int maxSerializedBytes() {
    return PacketUtils.DEFAULT_MAX_SERIALIZED_PACKET_BYTES;
  }

  protected ObjectInputFilter objectInputFilter() {
    int maxBytes = maxSerializedBytes();
    String filter = maxBytes > PacketOptions.UNLIMITED_BYTES
                    ? "maxbytes=" + maxBytes + ";maxdepth=50;maxrefs=10000;*"
                    : "maxdepth=50;maxrefs=10000;*";
    return ObjectInputFilter.Config.createFilter(filter);
  }

  private void validateSerializedSize(int size) throws IOException {
    if (size < 0) {
      throw new IOException("Serialized packet size cannot be negative: " + size);
    }

    int maxBytes = maxSerializedBytes();
    if (maxBytes > PacketOptions.UNLIMITED_BYTES && size > maxBytes) {
      throw new IOException("Serialized packet is too large: " + size + " bytes");
    }
  }
}
