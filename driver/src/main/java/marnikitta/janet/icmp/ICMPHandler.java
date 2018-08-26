package marnikitta.janet.icmp;

import marnikitta.janet.PDUHandler;
import marnikitta.janet.icmp.errors.DestinationUnreachableDecoder;
import marnikitta.janet.icmp.errors.DestinationUnreachableEncoder;
import marnikitta.janet.icmp.errors.DestinationUnreachableType;
import marnikitta.janet.ip.IPHandler;
import marnikitta.janet.ip.IPPacketDecoder;
import marnikitta.janet.ip.Protocol;
import marnikitta.janet.util.BufferClaim;

import java.nio.ByteBuffer;

import static marnikitta.janet.icmp.ICMPPacketDecoder.HEADER_LENGTH;

public class ICMPHandler implements PDUHandler {
  private final IPHandler ipHandler;

  private final ICMPPacketEncoder encoder = new ICMPPacketEncoder();
  private final ICMPPacketDecoder decoder = new ICMPPacketDecoder();
  private final DestinationUnreachableEncoder destinationUnreachableEncoder = new DestinationUnreachableEncoder();
  private final IPPacketDecoder ipDecoder = new IPPacketDecoder();

  private final BufferClaim claim = new BufferClaim();

  public ICMPHandler(IPHandler ipHandler) {
    this.ipHandler = ipHandler;
  }

  @Override
  public void onPDU(ByteBuffer buffer, int offset, int length) {
    decoder.wrap(buffer, offset, length);
    if (!decoder.hasValidChecksum()) {
      throw new RuntimeException("Header has an invalid checksum");
    }

    switch (decoder.type()) {
      case ECHO:
        echoResponse(decoder);
    }
  }

  public void destinationUnreachable(DestinationUnreachableType type, ByteBuffer reason, int offset, int length) {
    ipDecoder.wrap(reason, offset, length);
    ipHandler.claim(
      ipDecoder.sourceAdderess(),
      Protocol.ICMP,
      HEADER_LENGTH + DestinationUnreachableDecoder.HEADER_SIZE + DestinationUnreachableEncoder.dataLength(length),
      claim
    );
    destinationUnreachableEncoder.wrap(
      claim.buffer(),
      claim.offset() + HEADER_LENGTH,
      claim.length() - HEADER_LENGTH
    )
      .withData(reason, offset, length)
      .withLength();
    encoder.wrap(claim.buffer(), claim.offset(), claim.length())
      .withType(ICMPType.DESTINATION_UNREACHABLE)
      .withCode(type.code())
      .withChecksum();
    ipHandler.commit(claim);
  }

  private void echoResponse(ICMPPacketDecoder decoder) {
    final int sourceAddress;
    final int echoLength = decoder.length;
    final int echoOffset = decoder.offset;
    final ByteBuffer echoBuffer = decoder.buffer;
    {
      ipDecoder.wrap(
        echoBuffer,
        echoOffset - IPPacketDecoder.HEADER_SIZE,
        echoLength + IPPacketDecoder.HEADER_SIZE
      );
      sourceAddress = ipDecoder.sourceAdderess();
    }

    ipHandler.claim(sourceAddress, Protocol.ICMP, echoLength, claim);
    final ByteBuffer claimBuffer = claim.buffer();
    final int claimOffset = claim.offset();

    for (int i = 0; i < echoLength; ++i) {
      // TODO: 3/3/18 memcpy
      final byte b = echoBuffer.get(echoOffset + i);
      claimBuffer.put(claimOffset + i, b);
    }

    encoder.wrap(claimBuffer, claimOffset, claim.length())
      .withType(ICMPType.ECHO_REPLY)
      .withCode((byte) 0)
      .withChecksum();
    if (!encoder.hasValidChecksum()) {
      System.out.println();
      encoder.buffer.putShort(encoder.offset + encoder.CHECKSUM_OFFSET, (short) 0);
      encoder.hasValidChecksum();
    }

    ipHandler.commit(claim);
  }
}
