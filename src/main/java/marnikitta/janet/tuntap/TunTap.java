package marnikitta.janet.tuntap;

import sun.nio.ch.FileChannelImpl;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class TunTap {
  private TunTap() {
  }

  static {
    System.loadLibrary("tuntap");
  }

  private static native int initTap();

  private static native int initTun();

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

  public static ByteChannel tun() throws IOException {
    final int fs = initTap();
    if (fs > 0) {
      return FileChannelImpl.open(initDescriptor(fs), "", true, true, null);
    } else {
      throw new IOException("Unable to create a tun, err: " + fs);
    }
  }
}
