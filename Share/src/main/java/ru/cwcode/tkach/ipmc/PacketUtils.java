package ru.cwcode.tkach.ipmc;

import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;

public class PacketUtils {
  public static String extractChannel(Class<? extends Packet> packetClass) {
    try {
      return packetClass.getConstructor().newInstance().getFinalChannel();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  @SneakyThrows
  public static <T> T create(Class<T> type) {
    return type.getConstructor().newInstance();
  }
  
  public static String getString(String channel) {
    if (channel.startsWith("ipmc")) return channel;
    return "ipmc:" + channel.replace(':', '_');
  }
  
  
  public static boolean isJavaSerializationPacket(ByteArrayInputStream data) {
    if (data.available() < 4) {
      return false;
    }
    
    int b1 = data.read();
    int b2 = data.read();
    int b3 = data.read();
    int b4 = data.read();
    
    return b1 == 0xAC &&
           b2 == 0xED &&
           b3 == 0x00 &&
           b4 == 0x05;
  }
  
  public static boolean isJavaSerializationPacket(byte[] data) {
    if (data == null || data.length < 4) {
      return false;
    }
    
    return data[0] == (byte) 0xAC &&
           data[1] == (byte) 0xED &&
           data[2] == (byte) 0x00 &&
           data[3] == (byte) 0x05;
  }
}
