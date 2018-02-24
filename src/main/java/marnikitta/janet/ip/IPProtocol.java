package marnikitta.janet.ip;

import java.util.function.Consumer;

public interface IPProtocol extends Consumer<IPPacket> {
  IPPacket next();

  void commit(int dest, IPPacket.Protocol protocol, IPPacket packet);
}
