package ru.cwcode.tkach.ipmc.responsable;

import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketManager;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseListener<C, P extends Packet, IS, OS, I extends IncomingPacketHandler<C, P, IS>, O extends OutgoingPacketHandler<C, P, OS>> {
  private final PacketManager<C, P, IS, OS, I, O> packetManager;
  Map<Long, CompletableFuture<P>> awaitingResponses = new ConcurrentHashMap<>();
  
  public ResponseListener(PacketManager<C, P, IS, OS, I, O> packetManager) {
    this.packetManager = packetManager;
  }
  
  public void process(C connection, ResponsePacketWrapper responsePacketWrapper) {
    P parsed = packetManager.getIncoming().parse(responsePacketWrapper.targetChannel, responsePacketWrapper.responseBytes);
    if (parsed == null) return;
    
    CompletableFuture<P> awaiting = awaitingResponses.remove(responsePacketWrapper.uid);
    if (awaiting != null && !awaiting.isDone()) {
      awaiting.complete(parsed);
    }
  }
  
  public void waitForResponse(long uid, CompletableFuture<P> completable) {
    awaitingResponses.put(uid, completable);
    
    completable.whenComplete((object, throwable) -> {
      awaitingResponses.remove(uid);
    });
  }
}
