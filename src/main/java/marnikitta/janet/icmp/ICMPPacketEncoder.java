package marnikitta.janet.icmp;

import java.nio.ByteBuffer;

public class ICMPPacketEncoder extends ICMPPacketDecoder {

  @Override
  public ICMPPacketEncoder wrap(ByteBuffer buffer, int offset, int length) {
    this.buffer = buffer;
    this.offset = offset;
    this.length = length;
    return this;
  }

  public ICMPPacketEncoder withType(ICMPType type) {
    buffer.put(offset + TYPE_OFFSET, type.value());
    return this;
  }

  public ICMPPacketEncoder withCode(byte code) {
    buffer.put(offset + CODE_OFFSET, code);
    return this;
  }

  public void withChecksum() {
    buffer.putShort(offset + CHECKSUM_OFFSET, (short) 0);
    buffer.putShort(offset + CHECKSUM_OFFSET, evalChecksum());
  }
}
