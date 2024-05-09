package ru.cwcode.tkach.ipmc;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.*;

import java.io.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class ObjectPacket<T extends Serializable> implements Packet {
  private T object;
  
  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public void read(ByteArrayDataInput input) {
    byte[] data = new byte[input.readInt()];
    input.readFully(data);
    try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
         ObjectInputStream ois = new ObjectInputStream(bis)) {
      this.object = (T) ois.readObject();
    }
  }
  
  @Override
  @SneakyThrows
  public void write(ByteArrayDataOutput output) {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(bos)) {
      oos.writeObject(this.object);
      
      byte[] bytes = bos.toByteArray();
      output.writeInt(bytes.length);
      output.write(bytes);
    }
  }
}
