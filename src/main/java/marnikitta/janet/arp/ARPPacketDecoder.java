package marnikitta.janet.arp;

import marnikitta.janet.ip.IPPacketDecoder;
import marnikitta.janet.link.EthernetFrameDecoder;
import marnikitta.janet.util.Flyweight;

import java.nio.ByteBuffer;

public class ARPPacketDecoder implements Flyweight {
  protected static final int HARDWARE_TYPE_OFFSET = 0;
  protected static final int PROTOCOL_TYPE_OFFSET = 2;
  protected static final int HARDWARE_ADDRESS_LENGTH_OFFSET = 4;
  protected static final int PROTOCOL_ADDRESS_LENGTH_OFFSET = 5;
  protected static final int OPERATION_OFFSET = 6;
  protected static final int SENDER_HARDWARE_ADDRESS_OFFSET = 8;
  protected static final int SENDER_PROTOCOL_ADDRESS_OFFSET = 14;
  protected static final int TARGET_HARDWARE_ADDRESS_OFFSET = 18;
  protected static final int TARGET_PROTOCOL_ADDRESS_OFFSET = 24;
  public static final int PACKET_SIZE = 28;

  protected ByteBuffer buffer;
  protected int offset;

  @Override
  public ARPPacketDecoder wrap(ByteBuffer buffer, int offset) {
    this.buffer = buffer;
    this.offset = offset;
    return this;
  }

  public ARPOperation operation() {
    return ARPOperation.fromShort(buffer.getShort(offset + OPERATION_OFFSET));
  }

  public long senderHardwareAddress() {
    return EthernetFrameDecoder.getLinkAddress(buffer, offset + SENDER_HARDWARE_ADDRESS_OFFSET);
  }

  public long targetHardwareAddress() {
    return EthernetFrameDecoder.getLinkAddress(buffer, offset + TARGET_HARDWARE_ADDRESS_OFFSET);
  }

  public int senderProtocolAddress() {
    return buffer.getInt(offset + SENDER_PROTOCOL_ADDRESS_OFFSET);
  }

  public int targetProtocolAddress() {
    return buffer.getInt(offset + TARGET_PROTOCOL_ADDRESS_OFFSET);
  }

  @Override
  public String toString() {
    return String.format(
      "ARP(operation: %s, sender-hw: %012x, sender-prot: %s, target-hw: %012x, target-prot: %s)",
      operation(),
      senderHardwareAddress(),
      IPPacketDecoder.niceIP(senderProtocolAddress()),
      targetHardwareAddress(),
      IPPacketDecoder.niceIP(targetProtocolAddress())
    );
  }
}
