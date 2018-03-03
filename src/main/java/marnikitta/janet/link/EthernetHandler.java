package marnikitta.janet.link;

import marnikitta.janet.PDUHandler;
import marnikitta.janet.channel.JanetChannel;
import marnikitta.janet.util.BufferClaim;

import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

import static marnikitta.janet.link.EthernetFrameDecoder.HEADER_SIZE;

public class EthernetHandler implements Runnable, PDUHandler {
  private final Map<EtherType, PDUHandler> protocols = new EnumMap<>(EtherType.class);
  private final JanetChannel channel;
  private final long localLinkAddress;

  private final EthernetFrameEncoder frameEncoder = new EthernetFrameEncoder();
  private final EthernetFrameDecoder frameDecoder = new EthernetFrameDecoder();

  public EthernetHandler(long localLinkAddress, JanetChannel channel) {
    this.localLinkAddress = localLinkAddress;
    this.channel = channel;
  }

  public void claim(long dest, EtherType type, int length, BufferClaim claim) {
    channel.claim(claim, length + HEADER_SIZE);
    assert length + HEADER_SIZE == claim.length();

    frameEncoder.wrap(claim.buffer(), claim.offset(), claim.length())
      .withDestinationLinkAddress(dest)
      .withEtherType(type)
      .withSourceLinkAddress(localLinkAddress);
    claim.reserveForHeader(HEADER_SIZE);
  }

  public void commit(BufferClaim claim) {
    channel.commit(claim.free(HEADER_SIZE));
  }

  public void register(EtherType type, PDUHandler handler) {
    protocols.put(type, handler);
  }

  @Override
  public void onPDU(ByteBuffer buffer, int offset, int length) {
    frameDecoder.wrap(buffer, offset, length);
    final EtherType type = frameDecoder.etherType();
    if (protocols.containsKey(type)) {
      protocols.get(type).onPDU(
        buffer,
        offset + HEADER_SIZE,
        length - HEADER_SIZE
      );
    }
  }

  @Override
  public void run() {
    while (true) {
      channel.poll(this);
    }
  }
}
