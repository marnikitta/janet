package marnikitta.janet.ip;

import java.nio.ByteBuffer;

public class IPPacketEncoder extends IPPacketDecoder {
  @Override
  public IPPacketEncoder wrap(ByteBuffer buffer, int offset) {
    this.buffer = buffer;
    this.offset = offset;
    return this;
  }

  public IPPacketEncoder withIdentification(short identification) {
    buffer.putShort(offset + IDENTIFICATION_OFFSET, identification);
    return this;
  }

  public IPPacketEncoder withTTL(byte ttl) {
    buffer.put(offset + TTL_OFFSET, ttl);
    return this;
  }

  public IPPacketEncoder withProtocol(Protocol protocol) {
    buffer.put(offset + PROTOCOL_OFFSET, protocol.value());
    return this;
  }

  public IPPacketEncoder withSourceAddress(int address) {
    buffer.putInt(offset + SOURCE_ADDRESS_OFFSET, address);
    return this;
  }

  public IPPacketEncoder withDestinationAddress(int address) {
    buffer.putInt(offset + DESTINATION_ADDRESS_OFFSET, address);
    return this;
  }

  public void build() {
    buffer.put(VERSION_OFFSET, (byte) 0x45);

    final int size = buffer.remaining();
    if (0 <= size && size <= 0xFFFF) {
      buffer.putShort(TOTAL_LENGTH_OFFSET, (short) size);
    } else {
      throw new IllegalArgumentException("IP overflow");
    }
    buffer.putShort(offset + HEADER_CHECKSUM_OFFSET, evalChecksum());
  }
}
