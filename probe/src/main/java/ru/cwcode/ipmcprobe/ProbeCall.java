package ru.cwcode.ipmcprobe;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import ru.cwcode.tkach.ipmc.Packet;

public class ProbeCall implements Packet {
  public String question;

  public ProbeCall() {
  }

  public ProbeCall(String question) {
    this.question = question;
  }

  @Override
  public void read(ByteArrayDataInput in) {
    question = in.readUTF();
  }

  @Override
  public void write(ByteArrayDataOutput out) {
    out.writeUTF(question);
  }
}
