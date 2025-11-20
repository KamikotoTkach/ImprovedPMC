package ru.cwcode.tkach.ipmc.responsable;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;
import ru.cwcode.tkach.ipmc.Packet;

@NoArgsConstructor
public class ResponsePacketWrapper implements Packet {
  long uid;
  String targetChannel;
  byte[] responseBytes;
  
  public ResponsePacketWrapper(Packet response, long uid) {
    this.responseBytes = response.asByteArray();
    this.targetChannel = response.channel();
    this.uid = uid;
  }
  
  @Override
  public void read(ByteArrayDataInput inputStream) {
    uid = inputStream.readLong();
    targetChannel = inputStream.readUTF();
    
    int size = inputStream.readInt();
    if (size < 0) return;
    
    responseBytes = new byte[size];
    inputStream.readFully(responseBytes);
  }
  
  @Override
  public void write(ByteArrayDataOutput outputStream) {
    outputStream.writeLong(uid);
    outputStream.writeUTF(targetChannel);
    if (responseBytes == null) {
      outputStream.writeInt(-1);
      return;
    }
    
    outputStream.writeInt(responseBytes.length);
    outputStream.write(responseBytes);
  }
  
  @Override
  public String channel() {
    return "ipmc:response";
  }
}
