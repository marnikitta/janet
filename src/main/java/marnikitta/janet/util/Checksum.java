package marnikitta.janet.util;

import java.nio.ByteBuffer;

public class Checksum {
  private Checksum() {}

  public static short internetChecksum(ByteBuffer buffer, int offset, int length) {
    int partialSum = 0;
    for (int i = 0; i < length / Short.BYTES; ++i) {
      final short word = buffer.getShort(offset + i * Short.BYTES);
      partialSum += Short.toUnsignedInt(word);
    }
    partialSum = (partialSum & 0xFFFF) + (partialSum >>> Short.SIZE & 0xFFFF);
    partialSum = (partialSum & 0xFFFF) + (partialSum >>> Short.SIZE & 0xFFFF);
    return (short) ~partialSum;
  }
}
