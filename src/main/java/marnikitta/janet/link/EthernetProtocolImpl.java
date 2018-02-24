package marnikitta.janet.link;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public class EthernetProtocolImpl implements EthernetProtocol {
  private final Map<EthernetFrame.EtherType, Consumer<Object>> protocols = new EnumMap<>(EthernetFrame.EtherType.class);
  private final long localLinkAddress;
  private final ByteChannel channel;
  private final ByteBuffer sendBuffer = ByteBuffer.allocateDirect(1500).order(ByteOrder.BIG_ENDIAN);
  private EthernetFrame sendFrame = new EthernetFrame(sendBuffer);


  public EthernetProtocolImpl(long localLinkAddress, ByteChannel channel) {
    this.localLinkAddress = localLinkAddress;
    protocols.put(EthernetFrame.EtherType.UNSUPPORTED, System.out::println);
    this.channel = channel;
  }

  @Override
  public EthernetFrame next(int size) {
    final EthernetFrame frame = sendFrame;
    sendFrame = null;
    return frame;
  }

  @Override
  public void commit(long dest, EthernetFrame.EtherType etherType, EthernetFrame frame) {
    frame
      .withDestinationLinkAddress(dest)
      .withSourceLinkAddress(localLinkAddress)
      .withEtherType(etherType)
      .complete();
    try {
      System.out.println("Out: " + frame + ' ' + frame.dump());
      channel.write(sendBuffer);
      sendFrame = frame;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public <PU> void registerProtocol(EthernetFrame.EtherType type, Consumer<PU> protocol) {
    protocols.put(type, (Consumer<Object>) protocol);
  }

  @Override
  public void accept(EthernetFrame frame) {
    System.out.println("In: " + frame + ' ' + frame.dump());
    switch (frame.etherType()) {
      case ARP:
        protocols.get(EthernetFrame.EtherType.ARP).accept(frame.arpPacket());
        break;
      case IP_V4:
        protocols.get(EthernetFrame.EtherType.IP_V4).accept(frame.ipPacket());
      default:
        break;
    }
  }

  @Override
  public void run() {
    try {
      final ByteBuffer recieveBuffer = ByteBuffer.allocateDirect(1500);
      final EthernetFrame frame = new EthernetFrame(recieveBuffer);
      int read = 0;

      //noinspection NestedAssignment
      while ((read = channel.read(recieveBuffer)) >= 0) {
        if (read != 0) {
          recieveBuffer.flip();
          accept(frame);

          recieveBuffer.rewind();
        }
        Thread.sleep(1000);
      }
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
    }
  }
}
