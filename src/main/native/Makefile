CC       = g++
JAVA_INCLUDE = /usr/lib/jvm/java-1.8.0-openjdk-amd64/include/

all: libtuntap.so

libtuntap.so: marnikitta_janet_tuntap_TunTap.c
	$(CC) $(CFLAGS)  -fPIC -I"$(JAVA_INCLUDE)" -I"$(JAVA_INCLUDE)/linux" -o $@ -shared -Wl,-soname,tuntap.so $?

.PHONY: clean

clean:
	rm -rf *.o
			
