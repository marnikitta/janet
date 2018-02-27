package marnikitta.janet.arp;

import marnikitta.janet.link.EthernetFrameEncoder;

import java.nio.ByteBuffer;

public class ARPPacketEncoder extends ARPPacketDecoder {
  @Override
  public ARPPacketEncoder wrap(ByteBuffer buffer, int offset) {
    this.buffer = buffer;
    this.offset = offset;
    buffer.putShort(offset + HARDWARE_TYPE_OFFSET, (short) 1);
    buffer.putShort(offset + PROTOCOL_TYPE_OFFSET, (short) 0x0800);
    buffer.put(offset + HARDWARE_ADDRESS_LENGTH_OFFSET, (byte) 6);
    buffer.put(offset + PROTOCOL_ADDRESS_LENGTH_OFFSET, (byte) 4);
    return this;
  }

  public ARPPacketEncoder withOperation(ARPOperation operation) {
    buffer.putShort(offset + OPERATION_OFFSET, operation.value());
    return this;
  }

  public ARPPacketEncoder withSenderHardwareAddress(long address) {
    EthernetFrameEncoder.setLinkAddress(buffer, address, offset + SENDER_HARDWARE_ADDRESS_OFFSET);
    return this;
  }

  public ARPPacketEncoder withTargetHardwareAddress(long address) {
    EthernetFrameEncoder.setLinkAddress(buffer, address, offset + TARGET_HARDWARE_ADDRESS_OFFSET);
    return this;
  }

  public ARPPacketEncoder withSenderProtocolAddress(int address) {
    buffer.putInt(offset + SENDER_PROTOCOL_ADDRESS_OFFSET, address);
    return this;
  }

  public ARPPacketEncoder withTargetProtocolAddress(int address) {
    buffer.putInt(offset + TARGET_PROTOCOL_ADDRESS_OFFSET, address);
    return this;
  }
}
