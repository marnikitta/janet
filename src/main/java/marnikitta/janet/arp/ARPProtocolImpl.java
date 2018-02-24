package marnikitta.janet.arp;

import marnikitta.janet.link.EthernetFrame;
import marnikitta.janet.link.EthernetProtocol;

import java.util.HashMap;
import java.util.Map;

public class ARPProtocolImpl implements ARPProtocol {
  private final long localLinkAddress;
  private final int localNetworkAddress;
  private final EthernetProtocol ethernetProtocol;
  // FIXME: 2/24/18 Primitive collection
  private final Map<Integer, Long> arpCache = new HashMap<>();

  public ARPProtocolImpl(long localLinkAddress,
                         int localNetworkAddress,
                         EthernetProtocol ethernetProtocol) {
    this.localLinkAddress = localLinkAddress;
    this.localNetworkAddress = localNetworkAddress;
    this.ethernetProtocol = ethernetProtocol;
  }

  @Override
  public void request(int networkAddress) {
    final EthernetFrame next = ethernetProtocol.next(1500);

    next.withEtherType(EthernetFrame.EtherType.ARP);
    next.arpPacket()
      .withProtocol()
      .withOperation(ARPPacket.Operation.RESPONSE)
      .withSenderHardwareAddress(localLinkAddress)
      .withSenderProtocolAddress(localNetworkAddress)
      .withTargetHardwareAddress(0)
      .withTargetProtocolAddress(networkAddress);
    ethernetProtocol.commit(0xffffffff, next);
  }

  @Override
  public long getMapping(int networkAddress) {
    return arpCache.getOrDefault(networkAddress, -1L);
  }

  @Override
  public void accept(ARPPacket packet) {
    if (packet.operation() == ARPPacket.Operation.RESPONSE) {
      arpCache.put(packet.senderProtocolAddress(), packet.senderHardwareAddress());
    } else if (packet.operation() == ARPPacket.Operation.REQUEST
      && packet.targetProtocolAddress() == localNetworkAddress) {
      final EthernetFrame next = ethernetProtocol.next(1500);

      next.withEtherType(EthernetFrame.EtherType.ARP);
      next.arpPacket()
        .withProtocol()
        .withOperation(ARPPacket.Operation.RESPONSE)
        .withSenderHardwareAddress(localLinkAddress)
        .withSenderProtocolAddress(localNetworkAddress)
        .withTargetHardwareAddress(packet.senderHardwareAddress())
        .withTargetProtocolAddress(packet.senderProtocolAddress());
      ethernetProtocol.commit(packet.senderHardwareAddress(), next);
    }
  }
}
