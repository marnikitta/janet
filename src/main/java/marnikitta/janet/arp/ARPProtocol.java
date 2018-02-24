package marnikitta.janet.arp;

import java.util.function.Consumer;

public interface ARPProtocol extends Consumer<ARPPacket> {
  void request(int networkAddress);

  long linkAddressFor(int networkAddress);
}
