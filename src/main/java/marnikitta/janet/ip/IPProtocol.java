package marnikitta.janet.ip;

import marnikitta.janet.PDUHandler;
import marnikitta.janet.util.BufferClaim;

import java.nio.ByteBuffer;

public class IPProtocol implements PDUHandler {
  private final IPPacketDecoder ipDecoder = new IPPacketDecoder();

  @Override
  public void onPDU(ByteBuffer buffer, int offset, int length) {
    ipDecoder.wrap(buffer, offset);
    assert ipDecoder.wrap(buffer, offset).hasValidChecksum();

    System.out.println(ipDecoder);
  }
}
