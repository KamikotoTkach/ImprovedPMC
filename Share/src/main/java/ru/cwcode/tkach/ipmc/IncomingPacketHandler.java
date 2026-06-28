package ru.cwcode.tkach.ipmc;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

public class IncomingPacketHandler<P, T extends Packet, S> {
  protected S plugin;
  protected ConcurrentMap<String, IncomingPacketWrapper<P, T>> registeredIncomingPackets = new ConcurrentHashMap<>();
  
  public IncomingPacketHandler(S plugin) {
    this.plugin = plugin;
  }
  
  public void register(String channel, Class<T> packetClass, BiConsumer<P, T> onReceive) {
    register(channel, packetClass, PacketOptions.unlimited(), onReceive);
  }
  
  public void register(String channel, Class<T> packetClass, PacketOptions options, BiConsumer<P, T> onReceive) {
    registerWrapper(channel, packetClass, options).addConsumer(onReceive);
  }
  
  public IncomingPacketWrapper<P, T> registerWrapper(String channel, Class<T> packetClass) {
    return registerWrapper(channel, packetClass, PacketOptions.unlimited());
  }
  
  public IncomingPacketWrapper<P, T> registerWrapper(String channel, Class<T> packetClass, PacketOptions options) {
    return registeredIncomingPackets.computeIfAbsent(channel, k -> new IncomingPacketWrapper<>(packetClass, options));
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
    return parse(channel, packet, wrapper);
  }

  protected T parse(String channel, byte[] packet, IncomingPacketWrapper<P, T> wrapper) {
    if (wrapper == null) return null;
    
    Class<? extends Packet> packetClass = wrapper.packetClass();
    if (packetClass == null) return null;
    
    int maxBytes = wrapper.options().maxBytes();
    if (maxBytes > PacketOptions.UNLIMITED_BYTES && packet.length > maxBytes) {
      Logger.getLogger("IPMC").warning("Rejected oversized packet %s: %s bytes > %s bytes"
                                         .formatted(channel, packet.length, maxBytes));
      return null;
    }
    
    Packet packetInstance = null;
    try {
      packetInstance = packetClass.getConstructor().newInstance();
      packetInstance.read(packet);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      Logger.getLogger("IPMC").warning("Cannot instantiate packet %s: %s".formatted(channel, e.getMessage()));
      return null;
    } catch (Exception e) {
      Logger.getLogger("IPMC").warning("Cannot parse packet %s: %s".formatted(channel, e.getMessage()));
      return null;
    }
    
    return (T) packetInstance;
  }
  
  public void receive(P player, String channel, byte[] packet) {
    IncomingPacketWrapper<P, T> wrapper = registeredIncomingPackets.get(channel);
    T parsed = parse(channel, packet, wrapper);
    
    if (parsed != null) {
      wrapper.onReceive(player, parsed);
    }
  }
  
  public void onShutdown() {
    registeredIncomingPackets.clear();
  }
}
