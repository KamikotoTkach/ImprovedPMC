package ru.cwcode.tkach.ipmc;

import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;

public class PacketUtils {
  public static String extractChannel(Class<? extends Packet> packetClass) {
    try {
      return packetClass.getConstructor().newInstance().channel();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  @SneakyThrows
  public static <T> T create(Class<T> type) {
    return type.getConstructor().newInstance();
  }
}
