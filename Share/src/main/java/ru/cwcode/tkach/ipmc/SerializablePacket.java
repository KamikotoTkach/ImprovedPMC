package ru.cwcode.tkach.ipmc;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class SerializablePacket<T extends Serializable> implements Packet {
  private T object;
  
  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public void read(ByteArrayDataInput input) {
    byte[] data = new byte[input.readInt()];
    
    input.readFully(data);
    
    try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
         ObjectInputStream ois = new ObjectInputStream(bis)) {
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
      output.writeInt(bytes.length);
      output.write(bytes);
    }
  }
}