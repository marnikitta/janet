package marnikitta.janet.link;

import marnikitta.janet.util.BufferClaim;

import java.nio.ByteBuffer;

public class EthernetFrameEncoder extends EthernetFrameDecoder {
  @Override
  public EthernetFrameEncoder wrap(ByteBuffer buffer, int offset, int length) {
    this.buffer = buffer;
    this.offset = offset;
    this.length = length;
    return this;
  }

  public EthernetFrameEncoder withSourceLinkAddress(long linkAddress) {
    setLinkAddress(buffer, linkAddress, offset + SOURCE_OFFSET);
    return this;
  }

  public EthernetFrameEncoder withDestinationLinkAddress(long linkAddress) {
    setLinkAddress(buffer, linkAddress, offset + DESTINATION_OFFSET);
    return this;
  }

  public EthernetFrameEncoder withEtherType(EtherType type) {
    buffer.putShort(offset + ETHER_TYPE_OFFSET, type.value());
    return this;
  }

  public static void setLinkAddress(ByteBuffer buffer, long linkAddress, int offset) {
    long rest = linkAddress;
    for (int i = offset + 5; i >= offset; --i) {
      buffer.put(i, (byte) rest);
      rest >>= 8;
    }
  }
}
