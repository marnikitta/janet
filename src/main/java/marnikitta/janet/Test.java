package marnikitta.janet;

import marnikitta.janet.arp.ARPHandler;
import marnikitta.janet.channel.JanetChannel;
import marnikitta.janet.icmp.ICMPHandler;
import marnikitta.janet.ip.IPPacketDecoder;
import marnikitta.janet.ip.IPHandler;
import marnikitta.janet.ip.Protocol;
import marnikitta.janet.link.EtherType;
import marnikitta.janet.link.EthernetHandler;
import marnikitta.janet.tuntap.TunTap;

import java.io.IOException;
import java.nio.channels.ByteChannel;

public class Test {
  public static void main(String... args) throws IOException {
    final long localLinkAddress = 0x121212121212L;
    final int localNetworkAddress = IPPacketDecoder.parseIP("10.0.0.1");
    final ByteChannel tunTap = TunTap.tap("tun2");
    final JanetChannel channel = new JanetChannel(tunTap);

    final EthernetHandler ethernetHandler = new EthernetHandler(localLinkAddress, channel);
    final ARPHandler arpHandler = new ARPHandler(localLinkAddress, localNetworkAddress, ethernetHandler);
    final IPHandler ipHandler = new IPHandler(localNetworkAddress, ethernetHandler, arpHandler);
    final ICMPHandler icmpHandler = new ICMPHandler(ipHandler);
    ipHandler.register(Protocol.ICMP, icmpHandler);

    ethernetHandler.register(EtherType.ARP, arpHandler);
    ethernetHandler.register(EtherType.IP_V4, ipHandler);
    ethernetHandler.run();
  }
}
