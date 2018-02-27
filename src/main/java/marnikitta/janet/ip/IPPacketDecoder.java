package marnikitta.janet.ip;

import marnikitta.janet.util.Flyweight;

import java.nio.ByteBuffer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IPPacketDecoder implements Flyweight {
  protected static final int VERSION_OFFSET = 0;
  protected static final int TOTAL_LENGTH_OFFSET = 2;
  protected static final int IDENTIFICATION_OFFSET = 4;
  protected static final int TTL_OFFSET = 8;
  protected static final int PROTOCOL_OFFSET = 9;
  protected static final int HEADER_CHECKSUM_OFFSET = 10;
  protected static final int SOURCE_ADDRESS_OFFSET = 12;
  protected static final int DESTINATION_ADDRESS_OFFSET = 16;

  protected ByteBuffer buffer;
  protected int offset;

  @Override
  public IPPacketDecoder wrap(ByteBuffer buffer, int offset) {
    this.buffer = buffer;
    this.offset = offset;
    return this;
  }

  public byte version() {
    return (byte) ((0xF0 & buffer.get(offset + VERSION_OFFSET)) >> 4);
  }

  public byte headerLength() {
    return (byte) (0x0F & buffer.get(offset + VERSION_OFFSET));
  }

  public int totalLength() {
    return Short.toUnsignedInt(buffer.getShort(offset + TOTAL_LENGTH_OFFSET));
  }

  public short identification() {
    return buffer.getShort(offset + IDENTIFICATION_OFFSET);
  }

  public byte ttl() {
    return buffer.get(offset + TTL_OFFSET);
  }

  public Protocol protocol() {
    return Protocol.fromByte(buffer.get(offset + PROTOCOL_OFFSET));
  }

  public short headerChecksum() {
    return buffer.getShort(offset + HEADER_CHECKSUM_OFFSET);
  }

  public int sourceAdderess() {
    return buffer.getInt(offset + SOURCE_ADDRESS_OFFSET);
  }

  public int destinationAddress() {
    return buffer.getInt(offset + DESTINATION_ADDRESS_OFFSET);
  }

  public boolean hasValidChecksum() {
    return headerChecksum() == evalChecksum();
  }

  protected short evalChecksum() {
    final int length = headerLength();
    int partialSum = 0;
    for (int i = 0; i < length * 2; ++i) {
      if (i != 5) {
        final short word = buffer.getShort(offset + i * Short.BYTES);
        partialSum += Short.toUnsignedInt(word);
      }
    }
    final int result = (partialSum & 0xFFFF) + (partialSum >>> Short.SIZE & 0xFFFF);
    return (short) ~result;
  }

  @Override
  public String toString() {
    return String.format(
      "IPv4(length: %d, id: %d, ttl: %d, proto: %s, checksum: %x, src: %s, dst: %s)",
      totalLength(),
      Short.toUnsignedInt(identification()),
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
}
