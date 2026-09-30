package ru.cwcode.ipmcprobe;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import ru.cwcode.tkach.ipmc.Packet;

public class ProbePing implements Packet {
  public String text;
  public int number;
  public byte[] payload = new byte[0];

  public ProbePing() {
  }

  public ProbePing(String text, int number, int payloadSize) {
    this.text = text;
    this.number = number;
    this.payload = new byte[payloadSize];
  }

  @Override
  public void read(ByteArrayDataInput in) {
    text = in.readUTF();
    number = in.readInt();
    payload = new byte[in.readInt()];
    in.readFully(payload);
  }

  @Override
  public void write(ByteArrayDataOutput out) {
    out.writeUTF(text);
    out.writeInt(number);
    out.writeInt(payload.length);
    out.write(payload);
  }
}
