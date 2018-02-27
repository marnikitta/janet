package marnikitta.janet.link;

import marnikitta.janet.util.Flyweight;

import java.nio.ByteBuffer;

public class EthernetFrameDecoder implements Flyweight {
  protected static final int DESTINATION_OFFSET = 0;
  protected static final int SOURCE_OFFSET = 6;
  protected static final int ETHER_TYPE_OFFSET = 12;
  public static final int HEADER_SIZE = 14;

  protected ByteBuffer buffer;
  protected int offset;

  @Override
  public EthernetFrameDecoder wrap(ByteBuffer buffer, int offset) {
    this.buffer = buffer;
    this.offset = offset;
    return this;
  }

  public long sourceLinkAddress() {
    return EthernetFrameDecoder.getLinkAddress(buffer, offset + SOURCE_OFFSET);
  }

  public long destinationLinkAddress() {
    return EthernetFrameEncoder.getLinkAddress(buffer, offset + DESTINATION_OFFSET);
  }

  public EtherType etherType() {
    return EtherType.fromShort(buffer.getShort(offset + ETHER_TYPE_OFFSET));
  }

  @Override
  public String toString() {
    return String.format(
      "LINK(src: %012x, dst: %012x, type: %s)",
      sourceLinkAddress(),
      destinationLinkAddress(),
      etherType()
    );
  }

  public static long getLinkAddress(ByteBuffer buffer, int offset) {
    long result = 0;
    for (int i = offset; i < offset + 6; ++i) {
      result = (result << 8) + Byte.toUnsignedLong(buffer.get(i));
    }
    return result;
  }
}
