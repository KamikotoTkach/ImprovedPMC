package ru.cwcode.ipmcprobe;

import ru.cwcode.tkach.ipmc.SerializablePacket;

import java.util.ArrayList;

public class ProbeObject extends SerializablePacket<ArrayList<String>> {
  public ProbeObject() {
  }

  public ProbeObject(ArrayList<String> list) {
    super(list);
  }
}
