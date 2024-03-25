package ru.cwcode.tkach.ipmc;

import com.google.common.io.ByteStreams;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class IncomingPacketHandler<P, T extends Packet, S> {
  protected S plugin;
  HashMap<String, IncomingPacketWrapper<P, T>> registeredIncomingPackets = new HashMap<>();
  
  public IncomingPacketHandler(S plugin) {
    this.plugin = plugin;
  }
  
  public  void register(String channel, Class<T> packetClass, BiConsumer<P, T> onReceive) {
    registeredIncomingPackets.put(channel, new IncomingPacketWrapper<>(packetClass, onReceive));
  }
  public void unregister(String channel) {
    registeredIncomingPackets.remove(channel);
  }
  
  public void receive(P player, String channel, byte[] packet) {
    IncomingPacketWrapper<P, T> wrapper = registeredIncomingPackets.get(channel);
    if (wrapper == null) return;
    
    Class<? extends Packet> packetClass = wrapper.packetClass();
    if (packetClass == null) return;
    
    Packet packetInstance;
    try {
      packetInstance = packetClass.getConstructor().newInstance();
      packetInstance.read(ByteStreams.newDataInput(packet));
      wrapper.onReceive(player, (T) packetInstance);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
  }
  
  public void onShutdown() {
    for (String channel : registeredIncomingPackets.keySet()) {
      unregister(channel);
    }
  }
}
