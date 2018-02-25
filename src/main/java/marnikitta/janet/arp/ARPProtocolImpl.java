package marnikitta.janet.arp;

import gnu.trove.map.TIntLongMap;
import gnu.trove.map.hash.TIntLongHashMap;
import marnikitta.janet.link.EthernetFrame;
import marnikitta.janet.link.EthernetProtocol;

import java.util.HashMap;
import java.util.Map;

public class ARPProtocolImpl implements ARPProtocol {
  private final long localLinkAddress;
  private final int localNetworkAddress;
  private final EthernetProtocol ethernetProtocol;
  private final TIntLongMap arpCache = new TIntLongHashMap();

  public ARPProtocolImpl(long localLinkAddress,
                         int localNetworkAddress,
                         EthernetProtocol ethernetProtocol) {
    this.localLinkAddress = localLinkAddress;
    this.localNetworkAddress = localNetworkAddress;
    this.ethernetProtocol = ethernetProtocol;
  }

  @Override
  public void request(int networkAddress) {
    final EthernetFrame next = ethernetProtocol.next();

    next.arpPacket()
      .withProtocol()
      .withOperation(ARPPacket.Operation.RESPONSE)
      .withSenderHardwareAddress(localLinkAddress)
      .withSenderProtocolAddress(localNetworkAddress)
      .withTargetHardwareAddress(0)
      .withTargetProtocolAddress(networkAddress);
    ethernetProtocol.commit(0xFFFFFFFF, EthernetFrame.EtherType.ARP, next);
  }

  @Override
  public long linkAddressFor(int networkAddress) {
    if(arpCache.containsKey(networkAddress)) {
      return arpCache.get(networkAddress);
    } else {
      return -1;
    }
  }

  @Override
  public void accept(ARPPacket packet) {
    System.out.println(packet);
    if (packet.operation() == ARPPacket.Operation.RESPONSE) {
      arpCache.put(packet.senderProtocolAddress(), packet.senderHardwareAddress());
    } else if (packet.operation() == ARPPacket.Operation.REQUEST
      && packet.targetProtocolAddress() == localNetworkAddress) {
      final EthernetFrame next = ethernetProtocol.next();

      next.arpPacket()
        .withProtocol()
        .withOperation(ARPPacket.Operation.RESPONSE)
        .withSenderHardwareAddress(localLinkAddress)
        .withSenderProtocolAddress(localNetworkAddress)
        .withTargetHardwareAddress(packet.senderHardwareAddress())
        .withTargetProtocolAddress(packet.senderProtocolAddress());
      ethernetProtocol.commit(packet.senderHardwareAddress(), EthernetFrame.EtherType.ARP, next);
    }
  }
}
