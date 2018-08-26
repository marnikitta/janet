package marnikitta.janet;

import java.nio.ByteBuffer;

public interface PDUHandler {
  void onPDU(ByteBuffer buffer, int offset, int length);
}
