package ru.cwcode.tkach.ipmc.responsable;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketUtils;

import java.util.concurrent.atomic.AtomicLong;

@NoArgsConstructor
public class CallPacketWrapper implements Packet {
  private static final AtomicLong counter = new AtomicLong(0);
  
  @Getter
  long uid;
  String targetChannel;
  public byte[] callPacketBytes;
  
  public CallPacketWrapper(Packet origin) {
    this.targetChannel = origin.channel();
    this.callPacketBytes = origin.write();
    this.uid = counter.getAndIncrement();
  }
  
  
  @Override
  public void read(ByteArrayDataInput inputStream) {
    uid = inputStream.readLong();
    targetChannel = inputStream.readUTF();
    
    int size = inputStream.readInt();
    if (size < 0) return;
    if (size > PacketUtils.DEFAULT_MAX_NESTED_PACKET_BYTES) {
      throw new IllegalArgumentException("Nested call packet is too large: " + size + " bytes");
    }
    
    callPacketBytes = new byte[size];
    inputStream.readFully(callPacketBytes);
  }
  
  @Override
  public void write(ByteArrayDataOutput outputStream) {
    outputStream.writeLong(uid);
    outputStream.writeUTF(targetChannel);
    if (callPacketBytes == null) {
      outputStream.writeInt(-1);
      return;
    }
    
    outputStream.writeInt(callPacketBytes.length);
    outputStream.write(callPacketBytes);
  }
  
  @Override
  public String channel() {
    return "ipmc:call";
  }
}
