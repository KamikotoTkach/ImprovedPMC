package ru.cwcode.tkach.ipmc;

import lombok.Getter;
import ru.cwcode.tkach.ipmc.responsable.CallListener;
import ru.cwcode.tkach.ipmc.responsable.CallPacketWrapper;
import ru.cwcode.tkach.ipmc.responsable.ResponseListener;
import ru.cwcode.tkach.ipmc.responsable.ResponsePacketWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@Getter
public class PacketManager<C, P extends Packet, IS, OS, I extends IncomingPacketHandler<C, P, IS>, O extends OutgoingPacketHandler<C, P, OS>> {
  protected I incoming;
  protected O outgoing;
  protected ResponseListener<C, P, IS, OS, I, O> responseListener = new ResponseListener<>(this);
  protected CallListener<C, P, IS, OS, I, O> callListener = new CallListener<>(this);
  
  public PacketManager(I incoming, O outgoing) {
    this.incoming = incoming;
    this.outgoing = outgoing;
    
    registerIncomingPacket(CallPacketWrapper.class, callListener::process);
    registerIncomingPacket(ResponsePacketWrapper.class, responseListener::process);
  }
  
  public <X extends Packet> void registerIncomingPacket(Class<X> packetClass, BiConsumer<C, X> onReceive) {
    incoming.register(PacketUtils.extractChannel(packetClass), (Class<P>) packetClass, (BiConsumer<C, P>) onReceive);
  }
  
  public void registerOutgoingPacket(Class<? extends Packet> packetClass) {
    outgoing.register(PacketUtils.extractChannel(packetClass), packetClass);
  }
  
  public void onShutdown() {
    incoming.onShutdown();
    outgoing.onShutdown();
  }
  
  public void send(P packet, C connection) {
    outgoing.register(packet.channel(), packet.getClass());
    outgoing.send(packet, connection);
  }
  
  public <R extends P> CompletableFuture<R> call(P packet, Class<R> responsePacket, C connection) {
    incoming.registerWrapper(PacketUtils.extractChannel(responsePacket), (Class<P>) responsePacket);
    
    CompletableFuture<R> completable = new CompletableFuture<>();
    
    CallPacketWrapper callPacketWrapper = new CallPacketWrapper(packet);
    responseListener.waitForResponse(callPacketWrapper.getUid(), (CompletableFuture<P>) completable);
    
    send((P) callPacketWrapper, connection);
    
    return completable.orTimeout(35, TimeUnit.SECONDS)
      .exceptionally(throwable -> {
        throwable.printStackTrace();
        return null;
      });
  }
  
  public void sendResponse(P callPacket, P responsePacket) {
    callListener.onResponse(callPacket, responsePacket);
  }
}
