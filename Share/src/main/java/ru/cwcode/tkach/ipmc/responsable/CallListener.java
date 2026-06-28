package ru.cwcode.tkach.ipmc.responsable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketManager;
import ru.cwcode.tkach.ipmc.PacketUtils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CallListener<C, P extends Packet, IS, OS, I extends IncomingPacketHandler<C, P, IS>, O extends OutgoingPacketHandler<C, P, OS>> {
  private final PacketManager<C, P, IS, OS, I, O> packetManager;
  Map<IdentityKey<P>, AwaitingResponse<C>> awaitingResponses = new ConcurrentHashMap<>();
  
  public CallListener(PacketManager<C, P, IS, OS, I, O> packetManager) {
    this.packetManager = packetManager;
  }
  
  public void process(C connection, CallPacketWrapper callPacketWrapper) {
    if (callPacketWrapper.callPacketBytes == null) return;
    
    P parsed = packetManager.getIncoming().parse(callPacketWrapper.targetChannel, callPacketWrapper.callPacketBytes);
    if (parsed == null) return;
    
    IdentityKey<P> key = new IdentityKey<>(parsed);
    AwaitingResponse<C> awaitingResponse = new AwaitingResponse<>(callPacketWrapper.uid, connection);
    awaitingResponses.put(key, awaitingResponse);
    CompletableFuture.delayedExecutor(PacketUtils.DEFAULT_CALL_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                     .execute(() -> awaitingResponses.remove(key, awaitingResponse));
    
    packetManager.getIncoming().getPacketHandlersWrapper(callPacketWrapper.targetChannel).onReceive(connection, parsed);
  }
  
  public void onResponse(P callPacket, P responsePacket) {
    AwaitingResponse<C> awaitingResponse = awaitingResponses.remove(new IdentityKey<>(callPacket));
    if (awaitingResponse != null) {
      packetManager.send((P) new ResponsePacketWrapper(responsePacket, awaitingResponse.callUid), awaitingResponse.connection);
    }
  }
  
  public boolean isAwaitingResponse(P callPacket) {
    return awaitingResponses.containsKey(new IdentityKey<>(callPacket));
  }

  public void onShutdown() {
    awaitingResponses.clear();
  }
  
  @Getter
  @AllArgsConstructor
  static class AwaitingResponse<C> {
    long callUid;
    C connection;
  }

  @AllArgsConstructor
  static class IdentityKey<P> {
    P value;

    @Override
    public int hashCode() {
      return System.identityHashCode(value);
    }

    @Override
    public boolean equals(Object object) {
      if (!(object instanceof IdentityKey<?> identityKey)) return false;
      return value == identityKey.value;
    }
  }
}
