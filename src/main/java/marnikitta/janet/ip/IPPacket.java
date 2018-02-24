package marnikitta.janet.ip;

import java.nio.ByteBuffer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IPPacket {
  public static final int VERSION_OFFSET = 0;
  public static final int TOTAL_LENGTH_OFFSET = 2;
  public static final int IDENTIFICATION_OFFSET = 4;
  private static final int TTL_OFFSET = 8;
  private static final int PROTOCOL_OFFSET = 9;
  private static final int HEADER_CHECKSUM_OFFSET = 10;
  private static final int SOURCE_ADDRESS_OFFSET = 12;
  private static final int DESTINATION_ADDRESS_OFFSET = 16;
  private final ByteBuffer buffer;

  public IPPacket(ByteBuffer buffer) {
    this.buffer = buffer;
  }

  public byte version() {
    return (byte) (0xF0 & buffer.get(VERSION_OFFSET));
  }

  public byte headerLength() {
    return (byte) (0x0F & buffer.get(VERSION_OFFSET));
  }

  IPPacket withHeaderLength() {
    buffer.put(VERSION_OFFSET, (byte) 0x45);
    return this;
  }

  public int totalLength() {
    return Short.toUnsignedInt(buffer.getShort(TOTAL_LENGTH_OFFSET));
  }

  IPPacket withTotalLength() {
    final int size = buffer.remaining();
    if (0 <= size && size <= 0xFFFF) {
      buffer.putShort(TOTAL_LENGTH_OFFSET, (short) size);
      return this;
    } else {
      throw new IllegalArgumentException("IP overflow");
    }
  }

  public short identification() {
    return buffer.getShort(IDENTIFICATION_OFFSET);
  }

  IPPacket withIdentification(short identification) {
    buffer.putShort(IDENTIFICATION_OFFSET, identification);
    return this;
  }

  public byte ttl() {
    return buffer.get(TTL_OFFSET);
  }

  IPPacket withTTL(byte ttl) {
    buffer.put(TTL_OFFSET, ttl);
    return this;
  }

  public Protocol protocol() {
    return Protocol.fromByte(buffer.get(PROTOCOL_OFFSET));
  }

  IPPacket withProtocol(Protocol protocol) {
    buffer.put(PROTOCOL_OFFSET, protocol.value());
    return this;
  }

  public short headerChecksum() {
    return buffer.getShort(HEADER_CHECKSUM_OFFSET);
  }

  IPPacket withHeaderChecksum() {
    // TODO: 2/24/18
    return this;
  }

  public int sourceAdderess() {
    return buffer.getInt(SOURCE_ADDRESS_OFFSET);
  }

  IPPacket withSourceAddress(int address) {
    buffer.putInt(SOURCE_ADDRESS_OFFSET, address);
    return this;
  }

  public int destinationAddress() {
    return buffer.getInt(DESTINATION_ADDRESS_OFFSET);
  }

  IPPacket withDestinationAddress(int address) {
    buffer.putInt(DESTINATION_ADDRESS_OFFSET, address);
    return this;
  }

  @Override
  public String toString() {
    return String.format(
      "IP(ver: %x, hdr: %x, tot: %d, id: %x, ttl: %d, prot: %s, cs: %x, src: %s, dst: %s)",
      version(),
      headerLength(),
      totalLength(),
      identification(),
      ttl(),
      protocol(),
      headerChecksum(),
      niceIP(sourceAdderess()),
      niceIP(destinationAddress())
    );
  }

  public static int parseIP(String address) {
    final String[] split = address.split("\\.");
    int result = 0;
    for (String octet : split) {
      result <<= 8;
      result += Integer.parseInt(octet);
    }
    return result;
  }

  public static String niceIP(int address) {
    int rest = address;
    final String[] octets = new String[4];
    for (int i = 3; i >= 0; --i) {
      octets[i] = String.valueOf(rest & 0xFF);
      rest >>>= 8;
    }
    return Stream.of(octets).collect(Collectors.joining("."));
  }

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
}
