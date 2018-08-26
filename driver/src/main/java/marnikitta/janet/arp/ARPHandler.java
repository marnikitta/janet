package marnikitta.janet.arp;

import gnu.trove.map.TIntLongMap;
import gnu.trove.map.hash.TIntLongHashMap;
import marnikitta.janet.PDUHandler;
import marnikitta.janet.link.EtherType;
import marnikitta.janet.link.EthernetHandler;
import marnikitta.janet.util.BufferClaim;

import java.nio.ByteBuffer;

import static marnikitta.janet.arp.ARPOperation.REQUEST;
import static marnikitta.janet.arp.ARPOperation.RESPONSE;

public class ARPHandler implements PDUHandler {
  private final EthernetHandler ethernetHandler;

  private final long localLinkAddress;
  private final int localNetworkAddress;

  private final ARPPacketEncoder encoder = new ARPPacketEncoder();
  private final ARPPacketDecoder decoder = new ARPPacketDecoder();
  private final BufferClaim claim = new BufferClaim();

  private final TIntLongMap arpCache = new TIntLongHashMap();

  public ARPHandler(long localLinkAddress,
                    int localNetworkAddress,
                    EthernetHandler ethernetHandler) {
    this.localLinkAddress = localLinkAddress;
    this.localNetworkAddress = localNetworkAddress;
    this.ethernetHandler = ethernetHandler;
  }

  public void addMapping(int networkAddress, long linkAddress) {
    arpCache.put(networkAddress, linkAddress);
  }

  public long linkAddressFor(int networkAddress) {
    if (arpCache.containsKey(networkAddress)) {
      return arpCache.get(networkAddress);
    } else {
      return -1;
    }
  }

  @Override
  public void onPDU(ByteBuffer buffer, int offset, int length) {
    decoder.wrap(buffer, offset, length);
    arpCache.put(decoder.senderProtocolAddress(), decoder.senderHardwareAddress());
    if (decoder.operation() == REQUEST && decoder.targetProtocolAddress() == localNetworkAddress) {
      ethernetHandler.claim(
        decoder.senderHardwareAddress(),
        EtherType.ARP,
        ARPPacketDecoder.PACKET_SIZE,
        claim
      );
      encoder.wrap(claim.buffer(), claim.offset(), length)
        .withOperation(RESPONSE)
        .withSenderHardwareAddress(localLinkAddress)
        .withSenderProtocolAddress(localNetworkAddress)
        .withTargetHardwareAddress(decoder.senderHardwareAddress())
        .withTargetProtocolAddress(decoder.senderProtocolAddress());
      ethernetHandler.commit(claim);
    }
  }
}
