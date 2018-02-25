package marnikitta.janet.ip;

import marnikitta.janet.arp.ARPProtocol;
import marnikitta.janet.link.EthernetProtocol;

public class IPProtocolImpl implements IPProtocol {
  private final EthernetProtocol ethernetProtocol;
  private final ARPProtocol arpProtocol;
  private final int localNetworkAddress;

  private short id = 0;

  public IPProtocolImpl(EthernetProtocol ethernetProtocol,
                        ARPProtocol arpProtocol,
                        int localNetworkAddress) {
    this.ethernetProtocol = ethernetProtocol;
    this.arpProtocol = arpProtocol;
    this.localNetworkAddress = localNetworkAddress;
  }

  @Override
  public void accept(IPPacket ipPacket) {
    if (ipPacket.hasValidChecksum()) {
      System.out.println("WIN");
    } else {
      System.out.println("OOPS");
    }
    System.out.println(ipPacket);
  }

  @Override
  public IPPacket next() {
    return ethernetProtocol.next().ipPacket();
  }

  @Override
  public void commit(int dest, IPPacket.Protocol protocol, IPPacket packet) {
    packet
      .withProtocol(protocol)
      .withDestinationAddress(dest)
      .withSourceAddress(localNetworkAddress)
      .withIdentification(id++)
      .withTTL((byte) 64)
      .complete();
  }
}
