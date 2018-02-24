package marnikitta.janet.arp;

import marnikitta.janet.ip.IPPacket;
import marnikitta.janet.link.EthernetFrame;

import java.nio.ByteBuffer;

public class ARPPacket {
  private static final int HARDWARE_TYPE_OFFSET = 0;
  private static final int PROTOCOL_TYPE_OFFSET = 2;
  private static final int HARDWARE_ADDRESS_LENGTH_OFFSET = 4;
  private static final int PROTOCOL_ADDRESS_LENGTH_OFFSET = 5;
  private static final int OPERATION_OFFSET = 6;
  private static final int SENDER_HARDWARE_ADDRESS_OFFSET = 8;
  private static final int SENDER_PROTOCOL_ADDRESS_OFFSET = 14;
  private static final int TARGET_HARDWARE_ADDRESS_OFFSET = 18;
  private static final int TARGET_PROTOCOL_ADDRESS_OFFSET = 24;
  public static final int PACKET_SIZE = 28;

  private final ByteBuffer buffer;

  public ARPPacket(ByteBuffer buffer) {
    this.buffer = buffer;
  }

  public Operation operation() {
    return Operation.fromShort(buffer.getShort(OPERATION_OFFSET));
  }

  ARPPacket withOperation(Operation operation) {
    buffer.putShort(OPERATION_OFFSET, operation.value());
    return this;
  }

  public long senderHardwareAddress() {
    return EthernetFrame.getLinkAddress(buffer, SENDER_HARDWARE_ADDRESS_OFFSET);
  }

  ARPPacket withProtocol() {
    buffer.putShort(HARDWARE_TYPE_OFFSET, (short) 1);
    buffer.putShort(PROTOCOL_TYPE_OFFSET, (short) 0x0800);
    buffer.put(HARDWARE_ADDRESS_LENGTH_OFFSET, (byte) 6);
    buffer.put(PROTOCOL_ADDRESS_LENGTH_OFFSET, (byte) 4);
    return this;
  }

  ARPPacket withSenderHardwareAddress(long address) {
    EthernetFrame.setLinkAddress(buffer, address, SENDER_HARDWARE_ADDRESS_OFFSET);
    return this;
  }

  public long targetHardwareAddress() {
    return EthernetFrame.getLinkAddress(buffer, TARGET_HARDWARE_ADDRESS_OFFSET);
  }

  ARPPacket withTargetHardwareAddress(long address) {
    EthernetFrame.setLinkAddress(buffer, address, TARGET_HARDWARE_ADDRESS_OFFSET);
    return this;
  }

  public int senderProtocolAddress() {
    return buffer.getInt(SENDER_PROTOCOL_ADDRESS_OFFSET);
  }

  ARPPacket withSenderProtocolAddress(int address) {
    buffer.putInt(SENDER_PROTOCOL_ADDRESS_OFFSET, address);
    return this;
  }

  public int targetProtocolAddress() {
    return buffer.getInt(TARGET_PROTOCOL_ADDRESS_OFFSET);
  }

  ARPPacket withTargetProtocolAddress(int address) {
    buffer.putInt(TARGET_PROTOCOL_ADDRESS_OFFSET, address);
    return this;
  }

  @Override
  public String toString() {
    return String.format(
      "ARP(operation: %s, sender-hw: %012x, sender-prot: %s, target-hw: %012x, target-prot: %s)",
      operation(),
      senderHardwareAddress(),
      IPPacket.niceIP(senderProtocolAddress()),
      targetHardwareAddress(),
      IPPacket.niceIP(targetProtocolAddress())
    );
  }

  public enum Operation {
    REQUEST((short) 1),
    RESPONSE((short) 2),
    UNKNOWN((short) 0);

    private final short value;

    Operation(short value) {this.value = value;}

    public short value() {
      return value;
    }

    public static Operation fromShort(short s) {
      switch (s) {
        case 1:
          return REQUEST;
        case 2:
          return RESPONSE;
        default:
          return UNKNOWN;
      }
    }
  }
}
