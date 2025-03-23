package ru.cwcode.tkach.ipmc.responsable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketManager;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class CallListener<C, P extends Packet, IS, OS, I extends IncomingPacketHandler<C, P, IS>, O extends OutgoingPacketHandler<C, P, OS>> {
  private final PacketManager<C, P, IS, OS, I, O> packetManager;
  Map<P, AwaitingResponse<C>> awaitingResponses = Collections.synchronizedMap(new WeakHashMap<>());
  
  public CallListener(PacketManager<C, P, IS, OS, I, O> packetManager) {
    this.packetManager = packetManager;
  }
  
  public void process(C connection, CallPacketWrapper callPacketWrapper) {
    if (callPacketWrapper.callPacketBytes == null) return;
    
    P parsed = packetManager.getIncoming().parse(callPacketWrapper.targetChannel, callPacketWrapper.callPacketBytes);
    if (parsed == null) return;
    
    awaitingResponses.put(parsed, new AwaitingResponse<>(callPacketWrapper.uid, connection));
    
    packetManager.getIncoming().getPacketHandlersWrapper(callPacketWrapper.targetChannel).onReceive(connection, parsed);
  }
  
  public void onResponse(P callPacket, P responsePacket) {
    AwaitingResponse<C> awaitingResponse = awaitingResponses.remove(callPacket);
    if (awaitingResponse != null) {
      packetManager.send((P) new ResponsePacketWrapper(responsePacket, awaitingResponse.callUid), awaitingResponse.connection);
    }
  }
  
  @Getter
  @AllArgsConstructor
  static class AwaitingResponse<C> {
    long callUid;
    C connection;
  }
}
