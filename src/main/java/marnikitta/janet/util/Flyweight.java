package marnikitta.janet.util;

import java.nio.ByteBuffer;

public interface Flyweight {
  Flyweight wrap(ByteBuffer buffer, int offset);
}
