package marnikitta.janet.icmp.errors;

import java.nio.ByteBuffer;

public class DestinationUnreachableEncoder extends DestinationUnreachableDecoder {
  @Override
  public DestinationUnreachableEncoder wrap(ByteBuffer buffer, int offset, int length) {
    this.buffer = buffer;
    this.offset = offset;
    this.length = length;
    return withVarious((short) 0);
  }

  public DestinationUnreachableEncoder withLength() {
    assert (length - HEADER_SIZE) % 4 == 0;
    buffer.put(offset + LENGTH_OFFSET, (byte) ((length - HEADER_SIZE) / 4));
    return this;
  }

  public DestinationUnreachableEncoder withVarious(short various) {
    buffer.putShort(offset + VARIOUS_OFFSET, various);
    return this;
  }

  public DestinationUnreachableEncoder withData(ByteBuffer reason, int reasonOffset, int reasonLength) {
    try {

      for (int i = 0; i < dataLength(reasonLength); ++i) {
        buffer.put(offset + DATA_OFFSET + i, reason.get(reasonOffset + i));
      }
    } catch (IndexOutOfBoundsException e) {
      System.out.println(e);
    }
    return this;
  }

  public static int dataLength(int reasonLength) {
    final int length = Math.min(reasonLength, MAX_DATA_SIZE);
    final int octetsCount = length / Integer.BYTES;
    return octetsCount * Integer.BYTES;
  }
}
