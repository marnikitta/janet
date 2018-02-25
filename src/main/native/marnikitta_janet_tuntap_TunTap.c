#include <jni.h>
#include <sys/socket.h>
#include <linux/if.h>
#include <linux/if_tun.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include "marnikitta_janet_tuntap_TunTap.h"

int tun_alloc(char *dev, int flags) {

  struct ifreq ifr;
  int fd, err;
  char *clonedev = "/dev/net/tun";

  /* Arguments taken by the function:
   *
   * char *dev: the name of an interface (or '\0'). MUST have enough
   *   space to hold the interface name if '\0' is passed
   * int flags: interface flags (eg, IFF_TUN etc.)
   */

   /* open the clone device */
   if((fd = open(clonedev, O_RDWR)) < 0) {
     return fd;
   }

   /* preparation of the struct ifr, of type "struct ifreq" */
   memset(&ifr, 0, sizeof(ifr));

   ifr.ifr_flags = flags;   /* IFF_TUN or IFF_TAP, plus maybe IFF_NO_PI */

   if (*dev) {
     /* if a device name was specified, put it in the structure; otherwise,
      * the kernel will try to allocate the "next" device of the
      * specified type */
     strncpy(ifr.ifr_name, dev, IFNAMSIZ);
   }

   /* try to create the device */
   if((err = ioctl(fd, TUNSETIFF, (void *) &ifr)) < 0) {
     close(fd);
     return err;
   }

  /* if the operation was successful, write back the name of the
   * interface to the variable "dev", so the caller can know
   * it. Note that the caller MUST reserve space in *dev (see calling
   * code below) */
  strcpy(dev, ifr.ifr_name);

  if (fcntl(fd, F_SETFL, O_NONBLOCK) < 0) {
    return -1;
  }

  /* this is the special file descriptor that the caller will use to talk
   * with the virtual interface */
  return fd;
}

/*
 * Class:     marnikitta_janet_tuntap_TunTap
 * Method:    initTap
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_marnikitta_janet_tuntap_TunTap_initTap (JNIEnv * env, jclass clazz, jstring tapName) {
  char tap_name[IFNAMSIZ];
  const char *nativeString = (*env)->GetStringUTFChars(env, tapName, 0);
  strcpy(tap_name, nativeString);
  (*env)->ReleaseStringUTFChars(env, tapName, nativeString);
  return tun_alloc(tap_name, IFF_TAP | IFF_NO_PI);
}

/*
 * Class:     marnikitta_janet_tuntap_TunTap
 * Method:    initTun
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_marnikitta_janet_tuntap_TunTap_initTun (JNIEnv * env, jclass clazz, jstring tunName) {
  char tun_name[IFNAMSIZ];
  const char *nativeString = (*env)->GetStringUTFChars(env, tunName, 0);
  strcpy(tun_name, nativeString);
  (*env)->ReleaseStringUTFChars(env, tunName, nativeString);
  return tun_alloc(tun_name, IFF_TUN | IFF_NO_PI);
}