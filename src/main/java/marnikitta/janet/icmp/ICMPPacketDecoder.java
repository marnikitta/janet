package marnikitta.janet.icmp;

import marnikitta.janet.util.Checksum;
import marnikitta.janet.util.Flyweight;

import java.nio.ByteBuffer;

public class ICMPPacketDecoder implements Flyweight {
  protected static final int TYPE_OFFSET = 0;
  protected static final int CODE_OFFSET = 1;
  protected static final int CHECKSUM_OFFSET = 2;
  protected static final int CONTENT_OFFSET = 4;
  public static final int HEADER_LENGTH = 8;

  protected ByteBuffer buffer;
  protected int offset;
  protected int length;

  @Override
  public ICMPPacketDecoder wrap(ByteBuffer buffer, int offset, int length) {
    this.buffer = buffer;
    this.offset = offset;
    this.length = length;
    return this;
  }

  public ICMPType type() {
    return ICMPType.fromByte(buffer.get(offset + TYPE_OFFSET));
  }

  public byte code() {
    return buffer.get(offset + CODE_OFFSET);
  }

  public short checksum() {
    return buffer.getShort(offset + CHECKSUM_OFFSET);
  }

  public boolean hasValidChecksum() {
    return evalChecksum() == 0;
  }

  protected short evalChecksum() {
    return Checksum.internetChecksum(buffer, offset, length);
  }

  @Override
  public String toString() {
    return String.format("ICMP(type: %s, code: %d, checksum: %x)", type(), Short.toUnsignedInt(code()), checksum());
  }
}
