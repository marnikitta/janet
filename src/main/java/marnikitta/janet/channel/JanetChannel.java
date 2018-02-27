package marnikitta.janet.channel;

import marnikitta.janet.PDUHandler;
import marnikitta.janet.util.BufferClaim;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;

/**
 * Non-thread safe. Yet.
 * Single claim at a time. Yet.
 */
public class JanetChannel {
  private final ByteBuffer sendBuffer = ByteBuffer.allocateDirect(1500).order(ByteOrder.BIG_ENDIAN);
  private final ByteBuffer receiveBuffer = ByteBuffer.allocateDirect(1500).order(ByteOrder.BIG_ENDIAN);
  private final ByteChannel channel;

  public JanetChannel(ByteChannel channel) {
    this.channel = channel;
  }

  public void claim(BufferClaim claim, int length) {
    sendBuffer.limit(length);
    claim.wrap(sendBuffer, 0, length);
  }

  public void commit(BufferClaim claim) {
    try {
      channel.write(claim.buffer());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public int poll(PDUHandler handler) {
    try {
      final int read = channel.read(receiveBuffer);
      if (read != 0) {
        handler.onPDU(receiveBuffer, 0, read);
        receiveBuffer.clear();
      }
      return read;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
