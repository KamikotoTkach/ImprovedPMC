package ru.cwcode.tkach.ipmc.responsable;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.cwcode.tkach.ipmc.Packet;

import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
public class CallPacketWrapper implements Packet {
  private static final AtomicInteger counter = new AtomicInteger(0);
  
  @Getter
  long uid;
  String targetChannel;
  public byte[] callPacketBytes;
  
  public CallPacketWrapper(Packet origin) {
    this.targetChannel = origin.channel();
    this.callPacketBytes = origin.asByteArray();
    this.uid = counter.getAndIncrement();
  }
  
  
  @Override
  public void read(ByteArrayDataInput inputStream) {
    uid = inputStream.readLong();
    targetChannel = inputStream.readUTF();
    
    int size = inputStream.readInt();
    if (size < 0) return;
    
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
