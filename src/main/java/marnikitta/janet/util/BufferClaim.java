package marnikitta.janet.util;

import java.nio.ByteBuffer;

public class BufferClaim {
  private ByteBuffer buffer = ByteBuffer.allocate(0);
  private int offset;
  private int length;

  public BufferClaim wrap(ByteBuffer buffer, int offset, int length) {
    this.buffer = buffer;
    this.offset = offset;
    this.length = length;
    return this;
  }

  public BufferClaim withOffset(int offset) {
    this.offset = offset;
    return this;
  }

  public BufferClaim withLength(int length) {
    this.length = length;
    return this;
  }

  public BufferClaim reserveForHeader(int headerLength) {
    return withLength(length - headerLength).withOffset(offset + headerLength);
  }

  public BufferClaim free(int headerLength) {
    return withLength(length + headerLength).withOffset(offset - headerLength);
  }

  public ByteBuffer buffer() {
    return buffer;
  }

  public int offset() {
    return offset;
  }

  public int length() {
    return length;
  }
}
