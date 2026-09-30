package ru.cwcode.ipmcprobe;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import ru.cwcode.tkach.ipmc.Packet;

public class ProbeResponse implements Packet {
  public String answer;

  public ProbeResponse() {
  }

  public ProbeResponse(String answer) {
    this.answer = answer;
  }

  @Override
  public void read(ByteArrayDataInput in) {
    answer = in.readUTF();
  }

  @Override
  public void write(ByteArrayDataOutput out) {
    out.writeUTF(answer);
  }
}
