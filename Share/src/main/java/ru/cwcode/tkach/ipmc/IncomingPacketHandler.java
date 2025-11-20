package ru.cwcode.tkach.ipmc;

import com.google.common.io.ByteStreams;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class IncomingPacketHandler<P, T extends Packet, S> {
  protected S plugin;
  protected HashMap<String, IncomingPacketWrapper<P, T>> registeredIncomingPackets = new HashMap<>();
  
  public IncomingPacketHandler(S plugin) {
    this.plugin = plugin;
  }
  
  public void register(String channel, Class<T> packetClass, BiConsumer<P, T> onReceive) {
    registerWrapper(channel,packetClass).addConsumer(onReceive);
  }
  
  public IncomingPacketWrapper<P, T> registerWrapper(String channel, Class<T> packetClass) {
    return registeredIncomingPackets.computeIfAbsent(channel, k -> new IncomingPacketWrapper<>(packetClass));
  }
  
  public IncomingPacketWrapper<P, T> getPacketHandlersWrapper(String channel) {
    return registeredIncomingPackets.get(channel);
  }
  
  public void unregister(String channel) {
    registeredIncomingPackets.remove(channel);
  }
  
  public boolean isRegistered(String channel) {
    return registeredIncomingPackets.containsKey(channel);
  }
  
  public T parse(String channel, byte[] packet) {
    IncomingPacketWrapper<P, T> wrapper = registeredIncomingPackets.get(channel);
    if (wrapper == null) return null;
    
    Class<? extends Packet> packetClass = wrapper.packetClass();
    if (packetClass == null) return null;
    
    Packet packetInstance = null;
    try {
      packetInstance = packetClass.getConstructor().newInstance();
      packetInstance.read(ByteStreams.newDataInput(packet));
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    
    return (T) packetInstance;
  }
  
  public void receive(P player, String channel, byte[] packet) {
    T parsed = parse(channel, packet);
    
    if (parsed != null) {
      registeredIncomingPackets.get(channel).onReceive(player, parsed);
    }
  }
  
  public void onShutdown() {
    for (String channel : registeredIncomingPackets.keySet()) {
      unregister(channel);
    }
  }
}
