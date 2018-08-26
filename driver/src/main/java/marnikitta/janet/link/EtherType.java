package marnikitta.janet.link;

public enum EtherType {
  IP_V4((short) 0x0800),
  ARP((short) 0x0806),
  UNSUPPORTED((short) 0xFFFF);

  private final short value;

  EtherType(short value) {
    this.value = value;
  }

  public short value() {
    return value;
  }

  public static EtherType fromShort(short type) {
    switch (Short.toUnsignedInt(type)) {
      case 0x0806:
        return ARP;
      case 0x0800:
        return IP_V4;
      default:
        return UNSUPPORTED;
    }
  }
}
