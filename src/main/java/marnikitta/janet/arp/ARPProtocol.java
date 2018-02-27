package marnikitta.janet.arp;

import marnikitta.janet.PDUHandler;
import marnikitta.janet.link.EtherType;
import marnikitta.janet.link.EthernetProtocol;
import marnikitta.janet.util.BufferClaim;

import java.nio.ByteBuffer;

import static marnikitta.janet.arp.ARPOperation.REQUEST;
import static marnikitta.janet.arp.ARPOperation.RESPONSE;

public class ARPProtocol implements PDUHandler {
  private final EthernetProtocol ethernetProtocol;

  private final ARPPacketEncoder arpEncoder = new ARPPacketEncoder();
  private final ARPPacketDecoder arpDecoder = new ARPPacketDecoder();
  private final BufferClaim arpClaim = new BufferClaim();

  private final long localLinkAddress;
  private final int localNetworkAddress;

  public ARPProtocol(long localLinkAddress,
                     int localNetworkAddress,
                     EthernetProtocol ethernetProtocol) {
    this.localLinkAddress = localLinkAddress;
    this.localNetworkAddress = localNetworkAddress;
    this.ethernetProtocol = ethernetProtocol;
  }

  @Override
  public void onPDU(ByteBuffer buffer, int offset, int length) {
    arpDecoder.wrap(buffer, offset);
    if (arpDecoder.operation() == REQUEST && arpDecoder.targetProtocolAddress() == localNetworkAddress) {
      ethernetProtocol.claim(
        arpDecoder.senderHardwareAddress(),
        EtherType.ARP,
        ARPPacketDecoder.PACKET_SIZE,
        arpClaim
      );
      arpEncoder.wrap(arpClaim.buffer(), arpClaim.offset())
        .withOperation(RESPONSE)
        .withSenderHardwareAddress(localLinkAddress)
        .withSenderProtocolAddress(localNetworkAddress)
        .withTargetHardwareAddress(arpDecoder.senderHardwareAddress())
        .withTargetProtocolAddress(arpDecoder.senderProtocolAddress());
      ethernetProtocol.commit(arpClaim);
    }
  }
}
