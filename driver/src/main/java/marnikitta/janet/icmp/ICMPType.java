package marnikitta.janet.icmp;

public enum ICMPType {
  ECHO_REPLY((byte) 0),
  DESTINATION_UNREACHABLE((byte) 3),
  ECHO((byte) 8),
  TIME_EXCEEDED((byte) 11),
  PARAMETER_PROBLEM((byte) 12),
  UNSUPPORTED((byte) 0xff);

  private final byte type;

  ICMPType(byte type) {
    this.type = type;
  }

  public byte value() {
    return type;
  }

  public static ICMPType fromByte(byte b) {
    switch (b) {
      case 0:
        return ECHO_REPLY;
      case 3:
        return DESTINATION_UNREACHABLE;
      case 8:
        return ECHO;
      case 11:
        return TIME_EXCEEDED;
      case 12:
        return PARAMETER_PROBLEM;
      default:
        return UNSUPPORTED;
    }
  }
}
