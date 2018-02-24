package marnikitta.janet.link;

import marnikitta.janet.arp.ARPPacket;
import marnikitta.janet.ip.IPPacket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EthernetFrame {
  private static final int DESTINATION_OFFSET = 0;
  private static final int SOURCE_OFFSET = 6;
  private static final int ETHER_TYPE_OFFSET = 12;
  public static final int HEADER_SIZE = 14;

  private final ByteBuffer buffer;

  private final ARPPacket arpPacket;
  private final IPPacket ipPacket;

  public EthernetFrame(ByteBuffer buffer) {
    this.buffer = buffer;
    this.buffer.order(ByteOrder.BIG_ENDIAN);
    final ByteBuffer view = buffer.duplicate();
    view.position(buffer.position() + HEADER_SIZE);
    this.arpPacket = new ARPPacket(view.slice());
    this.ipPacket = new IPPacket(view.slice());
  }

  public long sourceLinkAddress() {
    return EthernetFrame.getLinkAddress(buffer, SOURCE_OFFSET);
  }

  EthernetFrame withSourceLinkAddress(long linkAddress) {
    EthernetFrame.setLinkAddress(buffer, linkAddress, SOURCE_OFFSET);
    return this;
  }

  public long destinationLinkAddress() {
    return EthernetFrame.getLinkAddress(buffer, DESTINATION_OFFSET);
  }

  EthernetFrame withDestinationLinkAddress(long linkAddress) {
    EthernetFrame.setLinkAddress(buffer, linkAddress, DESTINATION_OFFSET);
    return this;
  }

  public EtherType etherType() {
    return EtherType.fromShort(buffer.getShort(ETHER_TYPE_OFFSET));
  }

  EthernetFrame withEtherType(EtherType type) {
    buffer.putShort(ETHER_TYPE_OFFSET, type.value());
    return this;
  }

  void complete() {
    final int size;
    if (etherType() == EtherType.ARP) {
      size = HEADER_SIZE + ARPPacket.PACKET_SIZE;
    } else {
      size = HEADER_SIZE;
    }
    buffer.limit(size);
  }

  public ARPPacket arpPacket() {
    return arpPacket;
  }

  public IPPacket ipPacket() {
    return ipPacket;
  }

  String dump() {
    final StringBuilder result = new StringBuilder();
    for (int i = 0; i < buffer.limit(); ++i) {
      result.append(String.format("%02x ", buffer.get(i)));
    }
    return result.toString();
  }

  @Override
  public String toString() {
    return String.format(
      "LINK(src: %012x, dst: %012x, type: %s)",
      sourceLinkAddress(),
      destinationLinkAddress(),
      etherType()
    );
  }

  public static long getLinkAddress(ByteBuffer buffer, int offset) {
    long result = 0;
    for (int i = offset; i < offset + 6; ++i) {
      result = (result << 8) + Byte.toUnsignedLong(buffer.get(i));
    }
    return result;
  }

  public static void setLinkAddress(ByteBuffer buffer, long linkAddress, int offset) {
    long rest = linkAddress;
    for (int i = offset + 5; i >= offset; --i) {
      buffer.put(i, (byte) rest);
      rest >>= 8;
    }
  }

  public enum EtherType {
    IP_V4((short) 0x0800),
    ARP((short) 0x0806),
    UNSUPPORTED((short) 0xFFFF);

    private final short value;

    EtherType(short value) {
      this.value = value;
    }

    public short value() {
      return value;
    }

    public static EtherType fromShort(short type) {
      switch (Short.toUnsignedInt(type)) {
        case 0x0806:
          return ARP;
        case 0x0800:
          return IP_V4;
        default:
          return UNSUPPORTED;
      }
    }
  }
}
