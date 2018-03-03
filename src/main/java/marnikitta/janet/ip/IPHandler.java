package marnikitta.janet.ip;

import marnikitta.janet.PDUHandler;
import marnikitta.janet.arp.ARPHandler;
import marnikitta.janet.icmp.ICMPHandler;
import marnikitta.janet.icmp.errors.DestinationUnreachableType;
import marnikitta.janet.link.EtherType;
import marnikitta.janet.link.EthernetFrameDecoder;
import marnikitta.janet.link.EthernetHandler;
import marnikitta.janet.util.BufferClaim;

import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;

import static marnikitta.janet.ip.IPPacketDecoder.HEADER_SIZE;
import static marnikitta.janet.ip.IPPacketDecoder.niceIP;

public class IPHandler implements PDUHandler {
  private final EthernetHandler ethernetHandler;
  private final ARPHandler arpHandler;
  private final Map<Protocol, PDUHandler> protocols = new EnumMap<>(Protocol.class);
  private final int localNetworkAddress;

  private final IPPacketDecoder decoder = new IPPacketDecoder();
  private final IPPacketEncoder encoder = new IPPacketEncoder();
  private final EthernetFrameDecoder etherDecoder = new EthernetFrameDecoder();

  private short id = 0;

  public IPHandler(int localNetworkAddress, EthernetHandler ethernetHandler, ARPHandler arpHandler) {
    this.localNetworkAddress = localNetworkAddress;
    this.ethernetHandler = ethernetHandler;
    this.arpHandler = arpHandler;
  }

  public void claim(int destinationAddress, Protocol protocol, int length, BufferClaim claim) {
    final long linkAddress = arpHandler.linkAddressFor(destinationAddress);
    if (linkAddress == -1) {
      throw new IllegalStateException("There is no link address for " + niceIP(destinationAddress));
    }

    ethernetHandler.claim(linkAddress, EtherType.IP_V4, length + HEADER_SIZE, claim);
    encoder.wrap(claim.buffer(), claim.offset(), claim.length())
      .withDestinationAddress(destinationAddress)
      .withIdentification(id++)
      .withProtocol(protocol)
      .withTTL((byte) 64)
      .withSourceAddress(localNetworkAddress);
    claim.reserveForHeader(HEADER_SIZE);
  }

  public void commit(BufferClaim claim) {
    claim.free(HEADER_SIZE);
    encoder.withChecksum();
    assert encoder.hasValidChecksum();
    ethernetHandler.commit(claim);
  }

  @Override
  public void onPDU(ByteBuffer buffer, int offset, int length) {
    decoder.wrap(buffer, offset, length);
    if (isValidPacket(decoder)) {
      addMapping(decoder);

      final Protocol protocol = decoder.protocol();
      if (protocols.containsKey(protocol)) {
        protocols.get(protocol).onPDU(
          buffer,
          offset + decoder.headerLength() * Integer.BYTES,
          length - decoder.headerLength() * Integer.BYTES
        );
      }
    }
  }

  public void register(Protocol protocol, PDUHandler handler) {
    protocols.put(protocol, handler);
  }

  private void addMapping(IPPacketDecoder decoder) {
    if (arpHandler.linkAddressFor(decoder.sourceAdderess()) == -1) {
      final long sourceLinkAddress;
      {
        etherDecoder.wrap(
          decoder.buffer,
          decoder.offset - EthernetFrameDecoder.HEADER_SIZE,
          decoder.length + EthernetFrameDecoder.HEADER_SIZE
        );
        sourceLinkAddress = etherDecoder.sourceLinkAddress();
        arpHandler.addMapping(decoder.sourceAdderess(), sourceLinkAddress);
      }
    }
  }

  private boolean isValidPacket(IPPacketDecoder ipDecoder) {
    if (ipDecoder.totalLength() != ipDecoder.length) {
      return false;
    }
    if (!ipDecoder.hasValidChecksum()) {
      return false;
    }
    if (ipDecoder.destinationAddress() != localNetworkAddress) {
      ((ICMPHandler) protocols.get(Protocol.ICMP)).destinationUnreachable(
        DestinationUnreachableType.HOST_UNREACHABLE,
        ipDecoder.buffer,
        ipDecoder.offset,
        ipDecoder.length
      );
      return false;
    }
    if (ipDecoder.ttl() == 0) {
      // TODO: 3/3/18 Time exceeded ICMP message
      return false;
    }

    return true;
  }
}
