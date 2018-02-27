package marnikitta.janet;

import marnikitta.janet.arp.ARPProtocol;
import marnikitta.janet.channel.JanetChannel;
import marnikitta.janet.ip.IPPacketDecoder;
import marnikitta.janet.ip.IPProtocol;
import marnikitta.janet.link.EtherType;
import marnikitta.janet.link.EthernetProtocol;
import marnikitta.janet.tuntap.TunTap;

import java.io.IOException;
import java.nio.channels.ByteChannel;

public class Test {
  public static void main(String... args) throws IOException {
    final long localLinkAddress = 0x32080803208080L;
    final int localNetworkAddress = IPPacketDecoder.parseIP("10.0.0.1");
    final ByteChannel tunTap = TunTap.tap("tun2");
    final JanetChannel channel = new JanetChannel(tunTap);

    final EthernetProtocol ethernetProtocol = new EthernetProtocol(localLinkAddress, channel);
    final ARPProtocol arpProtocol = new ARPProtocol(localLinkAddress, localNetworkAddress, ethernetProtocol);
    final IPProtocol ipProtocol = new IPProtocol();

    ethernetProtocol.registerProtocol(EtherType.ARP, arpProtocol);
    ethernetProtocol.registerProtocol(EtherType.IP_V4, ipProtocol);
    ethernetProtocol.run();
  }
}
