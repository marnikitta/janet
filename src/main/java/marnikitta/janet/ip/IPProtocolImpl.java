package marnikitta.janet.ip;

public class IPProtocolImpl implements IPProtocol {
  @Override
  public void accept(IPPacket ipPacket) {
    System.out.println(ipPacket);
  }
}
