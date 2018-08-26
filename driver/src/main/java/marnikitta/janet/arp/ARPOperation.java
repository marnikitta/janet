package marnikitta.janet.arp;

public enum ARPOperation {
  REQUEST((short) 1),
  RESPONSE((short) 2),
  UNKNOWN((short) 0);

  private final short value;

  ARPOperation(short value) {this.value = value;}

  public short value() {
    return value;
  }

  public static ARPOperation fromShort(short s) {
    switch (s) {
      case 1:
        return REQUEST;
      case 2:
        return RESPONSE;
      default:
        return UNKNOWN;
    }
  }
}
