package ru.cwcode.tkach.ipmc;

public class PacketOptions {
  public static final int UNLIMITED_BYTES = -1;

  private final int maxBytes;

  public PacketOptions(int maxBytes) {
    this.maxBytes = maxBytes;
  }

  public int maxBytes() {
    return maxBytes;
  }

  public static PacketOptions unlimited() {
    return new PacketOptions(UNLIMITED_BYTES);
  }

  public static PacketOptions maxBytes(int maxBytes) {
    return new PacketOptions(maxBytes);
  }
}
