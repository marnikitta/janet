package marnikitta.janet.link;

import marnikitta.janet.PDUHandler;
import marnikitta.janet.channel.JanetChannel;
import marnikitta.janet.util.BufferClaim;

import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;

public class EthernetProtocol implements Runnable, PDUHandler {
  private final Map<EtherType, PDUHandler> protocols = new EnumMap<>(EtherType.class);
  private final JanetChannel channel;
  private final long localLinkAddress;

  private final EthernetFrameEncoder frameEncoder = new EthernetFrameEncoder();
  private final EthernetFrameDecoder frameDecoder = new EthernetFrameDecoder();
  private final BufferClaim etherClaim = new BufferClaim();

  public EthernetProtocol(long localLinkAddress, JanetChannel channel) {
    this.localLinkAddress = localLinkAddress;
    this.channel = channel;
  }

  public void claim(long dest, EtherType type, int length, BufferClaim claim) {
    channel.claim(etherClaim, length + EthernetFrameDecoder.HEADER_SIZE);
    frameEncoder.wrap(etherClaim.buffer(), etherClaim.offset())
      .withDestinationLinkAddress(dest)
      .withEtherType(type)
      .withSourceLinkAddress(localLinkAddress);
    claim.wrap(etherClaim.buffer(), etherClaim.offset() + EthernetFrameDecoder.HEADER_SIZE, length);
  }

  public void commit(BufferClaim claim) {
    channel.commit(etherClaim);
  }

  public void registerProtocol(EtherType type, PDUHandler handler) {
    protocols.put(type, handler);
  }

  @Override
  public void onPDU(ByteBuffer buffer, int offset, int length) {
    frameDecoder.wrap(buffer, offset);
    final EtherType type = frameDecoder.etherType();
    if (protocols.containsKey(type)) {
      protocols.get(type).onPDU(
        buffer,
        offset + EthernetFrameDecoder.HEADER_SIZE,
        length - EthernetFrameDecoder.HEADER_SIZE
      );
    } else {
      System.out.println(frameDecoder);
    }
  }

  @Override
  public void run() {
    while (true) {
      try {
        channel.poll(this);
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
