package marnikitta.janet.tuntap;

import sun.nio.ch.FileChannelImpl;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.ByteChannel;

public final class TunTap {
  private TunTap() {
  }

  static {
    System.loadLibrary("tuntap");
  }

  private static native int initTap(String tapName);

  private static native int initTun(String tunName);

  private static FileDescriptor initDescriptor(int fd) {
    try {
      final FileDescriptor d = new FileDescriptor();
      final Field fd1 = d.getClass().getDeclaredField("fd");
      fd1.setAccessible(true);
      fd1.set(d, fd);
      return d;
    } catch (NoSuchFieldException | IllegalAccessException ignored) {
      throw new RuntimeException("Ooops");
    }
  }

  public static ByteChannel tap(String tapName) throws IOException {
    final int fs = initTap(tapName);
    if (fs > 0) {
      // Reads and writes are not concurrent. There is a monitor that protects reads and writes
      return FileChannelImpl.open(initDescriptor(fs), "", true, true, null);
    } else {
      throw new IOException("Unable to create a tap, err: " + fs);
    }
  }
}
