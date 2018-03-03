package marnikitta.janet.icmp.errors;

import marnikitta.janet.util.Flyweight;

import java.nio.ByteBuffer;

public class DestinationUnreachableDecoder implements Flyweight {
  protected static final int LENGTH_OFFSET = 1;
  protected static final int VARIOUS_OFFSET = 2;
  public static final int HEADER_SIZE = 4;
  public static final int DATA_OFFSET = 4;
  public static final int MAX_DATA_SIZE = 548;

  protected ByteBuffer buffer;
  protected int offset;
  protected int length;

  @Override
  public DestinationUnreachableDecoder wrap(ByteBuffer buffer, int offset, int length) {
    this.buffer = buffer;
    this.offset = offset;
    this.length = length;
    return this;
  }

  public byte length() {
    return buffer.get(offset + LENGTH_OFFSET);
  }

  public short various() {
    return buffer.getShort(offset + VARIOUS_OFFSET);
  }
}
