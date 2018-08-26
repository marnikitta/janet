package marnikitta.janet;

import marnikitta.janet.arp.ARPHandler;
import marnikitta.janet.channel.JanetChannel;
import marnikitta.janet.icmp.ICMPHandler;
import marnikitta.janet.ip.IPHandler;
import marnikitta.janet.ip.IPPacketDecoder;
import marnikitta.janet.ip.Protocol;
import marnikitta.janet.link.EtherType;
import marnikitta.janet.link.EthernetHandler;
import marnikitta.janet.tuntap.TunTap;

import java.io.IOException;
import java.nio.channels.ByteChannel;

public class Driver {
  private final int localNetworkAddress;
  private final String tuntapName;
  private final long localLinkAddress = 0x121212121212L;

  public Driver(String tuntapName, String localNetworkAddress) {
    this.localNetworkAddress = IPPacketDecoder.parseIP(localNetworkAddress);
    this.tuntapName = tuntapName;
  }

  public void start() throws IOException {
    final ByteChannel tunTap = TunTap.tap(tuntapName);
    final JanetChannel channel = new JanetChannel(tunTap);

    final EthernetHandler ethernetHandler = new EthernetHandler(localLinkAddress, channel);
    final ARPHandler arpHandler = new ARPHandler(localLinkAddress, localNetworkAddress, ethernetHandler);
    final IPHandler ipHandler = new IPHandler(localNetworkAddress, ethernetHandler, arpHandler);
    final ICMPHandler icmpHandler = new ICMPHandler(ipHandler);
    ipHandler.register(Protocol.ICMP, icmpHandler);

    ethernetHandler.register(EtherType.ARP, arpHandler);
    ethernetHandler.register(EtherType.IP_V4, ipHandler);

    new Thread(ethernetHandler).start();
  }

  public static void main(String... args) throws IOException {
    final Driver driver;
    if (args.length == 2) {
      driver = new Driver(args[0], args[1]);
    } else {
      driver = new Driver("tun2", "10.0.0.2");
    }
    driver.start();
  }
}
