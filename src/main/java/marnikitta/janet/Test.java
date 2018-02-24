package marnikitta.janet;

import marnikitta.janet.arp.ARPProtocol;
import marnikitta.janet.arp.ARPProtocolImpl;
import marnikitta.janet.ip.IPPacket;
import marnikitta.janet.ip.IPProtocol;
import marnikitta.janet.ip.IPProtocolImpl;
import marnikitta.janet.link.EthernetFrame;
import marnikitta.janet.link.EthernetProtocol;
import marnikitta.janet.link.EthernetProtocolImpl;
import marnikitta.janet.tuntap.TunTap;

import java.io.IOException;
import java.nio.channels.ByteChannel;

public class Test {
  public static void main(String... args) throws IOException {
    final long localLinkAddress = 0x32080803208080L;
    final int localNetworkAddress = IPPacket.parseIP("10.0.0.1");
    final ByteChannel tunTap = TunTap.tun();

    final EthernetProtocol ethernetProtocol = new EthernetProtocolImpl(localLinkAddress, tunTap);
    final ARPProtocol arpProtocol = new ARPProtocolImpl(localLinkAddress, localNetworkAddress, ethernetProtocol);
    final IPProtocol ipProtocol = new IPProtocolImpl(ethernetProtocol, arpProtocol, localNetworkAddress);

    ethernetProtocol.registerProtocol(EthernetFrame.EtherType.ARP, arpProtocol);
    ethernetProtocol.registerProtocol(EthernetFrame.EtherType.IP_V4, ipProtocol);
    ethernetProtocol.run();
  }
}
