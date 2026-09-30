package ru.cwcode.ipmcprobe;

import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class ProbeCore {
  private ProbeCore() {
  }

  public interface Check {
    boolean run() throws Exception;
  }

  public static void check(Consumer<String> out, int[] score, String name, Check check) {
    score[1]++;
    try {
      if (check.run()) {
        score[0]++;
        out.accept("PROBE ok   " + name);
      } else {
        out.accept("PROBE FAIL " + name);
      }
    } catch (Throwable t) {
      out.accept("PROBE FAIL " + name + ": " + t);
      t.printStackTrace();
    }
  }

  /**
   * The connection must be a loopback: what the outgoing handler sends has to come back through the platform's
   * own receive path. Wire format and handler are the library's; only the transport is replaced.
   */
  public static <C, IS, OS, I extends IncomingPacketHandler<C, Packet, IS>, O extends OutgoingPacketHandler<C, Packet, OS>>
  int[] run(PacketManager<C, Packet, IS, OS, I, O> pm, C loopback, Consumer<String> out) {
    int[] score = new int[2];

    check(out, score, "packet write/read round trip", () -> {
      ProbePing sent = new ProbePing("привет", 7, 3);
      ProbePing read = new ProbePing();
      read.read(sent.write());
      return "привет".equals(read.text) && read.number == 7 && read.payload.length == 3;
    });

    check(out, score, "SerializablePacket round trip", () -> {
      ArrayList<String> list = new ArrayList<>(List.of("a", "b", "в"));
      ProbeObject read = new ProbeObject();
      read.read(new ProbeObject(list).write());
      return list.equals(read.getObject());
    });

    List<ProbePing> pings = new CopyOnWriteArrayList<>();
    pm.registerIncomingPacket(ProbePing.class, (connection, packet) -> pings.add(packet));

    check(out, score, "packet through handlers", () -> {
      pm.send(new ProbePing("loop", 42, 10), loopback);
      return pings.size() == 1 && "loop".equals(pings.get(0).text) && pings.get(0).number == 42;
    });

    List<ProbeObject> objects = new CopyOnWriteArrayList<>();
    pm.registerIncomingPacket(ProbeObject.class, (connection, packet) -> objects.add(packet));

    check(out, score, "SerializablePacket through handlers", () -> {
      pm.send(new ProbeObject(new ArrayList<>(List.of("x", "y"))), loopback);
      return objects.size() == 1 && objects.get(0).getObject().equals(List.of("x", "y"));
    });

    pm.registerIncomingPacket(ProbeCall.class, (connection, call) -> pm.sendResponse(call, new ProbeResponse("re:" + call.question)));

    check(out, score, "call / response", () -> {
      ProbeResponse response = pm.call(new ProbeCall("q"), ProbeResponse.class, loopback).get(3, TimeUnit.SECONDS);
      return "re:q".equals(response.answer);
    });

    return score;
  }
}
