package marnikitta.janet.ip;

import java.nio.ByteBuffer;

public class IPPacketEncoder extends IPPacketDecoder {
  @Override
  public IPPacketEncoder wrap(ByteBuffer buffer, int offset, int length) {
    this.buffer = buffer;
    this.offset = offset;
    this.length = length;

    buffer.put(offset + VERSION_OFFSET, (byte) 0x45);
    buffer.putShort(offset + OFFSET_OFFSET, (short) 0);
    buffer.put(offset + DS_OFFSET, (byte) 0);
    buffer.putShort(offset + HEADER_CHECKSUM_OFFSET, (short) 0);
    buffer.putShort(offset + TOTAL_LENGTH_OFFSET, (short) length);

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

  public void withChecksum() {
    buffer.putShort(offset + HEADER_CHECKSUM_OFFSET, evalChecksum());
  }
}
