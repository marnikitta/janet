package marnikitta.janet.icmp.errors;

public enum DestinationUnreachableType {
  HOST_UNREACHABLE((byte) 1),
  PORT_UNREACHABLE((byte) 3),
  UNSUPPORTED((byte) 0xff);

  private final byte code;

  DestinationUnreachableType(byte code) {this.code = code;}

  public byte code() {
    return code;
  }

  public static DestinationUnreachableType fromByte(byte code) {
    switch (code) {
      case 1:
        return HOST_UNREACHABLE;
      case 3:
        return PORT_UNREACHABLE;
      default:
        return UNSUPPORTED;
    }
  }
}
