package marnikitta.janet.link;

import java.util.function.Consumer;

public interface EthernetProtocol extends Consumer<EthernetFrame>, Runnable {
  EthernetFrame next(int size);

  void commit(long dest, EthernetFrame.EtherType type, EthernetFrame frame);

  <PU> void registerProtocol(EthernetFrame.EtherType type, Consumer<PU> protocol);
}
