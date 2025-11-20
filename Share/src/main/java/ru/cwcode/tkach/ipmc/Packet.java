package ru.cwcode.tkach.ipmc;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public interface Packet {
  void read(ByteArrayDataInput inputStream);
  void write(ByteArrayDataOutput outputStream);
  
  default String channel() {
    return getClass().getName();
  }
  
  default byte[] asByteArray() {
    ByteArrayDataOutput outputStream = ByteStreams.newDataOutput();
    outputStream.writeUTF(channel());
    write(outputStream);
    return outputStream.toByteArray();
  }
}
