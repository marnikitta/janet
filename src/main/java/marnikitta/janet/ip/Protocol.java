package marnikitta.janet.ip;

public enum Protocol {
  TCP((byte) 0x06),
  ICMP((byte) 0x01),
  UDP((byte) 0x11),
  UNSUPPORTED((byte) 0xFF);

  private final byte value;

  Protocol(byte value) {
    this.value = value;
  }

  public byte value() {
    return value;
  }

  public static Protocol fromByte(byte value) {
    switch (value) {
      case 0x11:
        return UDP;
      case 0x06:
        return TCP;
      case 0x01:
        return ICMP;
      default:
        return UNSUPPORTED;
    }
  }
}
