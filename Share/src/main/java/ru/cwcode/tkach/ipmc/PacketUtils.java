package ru.cwcode.tkach.ipmc;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;

public class PacketUtils {
  public static final String INTERNAL_CHANNEL = "ipmc:internal";
  
  public static String extractChannel(Class<? extends Packet> packetClass) {
    try {
      return packetClass.getConstructor().newInstance().channel();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public static String extractChannel(byte[] data) {
    try {
      ByteArrayDataInput input = ByteStreams.newDataInput(data);
      return input.readUTF();
    } catch (Exception e) {
      return null;
    }
  }
  
  public static String extractChannel(ByteArrayInputStream data) {
    try {
      ByteArrayDataInput input = ByteStreams.newDataInput(data);
      return input.readUTF();
    } catch (Exception e) {
      return null;
    }
  }
}
