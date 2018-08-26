CC       := gcc
CFLAGS := -Wall -fPIC

JAVA_HOME ?= /usr/lib/jvm/java-1.8.0-openjdk-amd64
JAVAH := $(JAVA_HOME)/bin/javah
inc := $(JAVA_HOME)/include $(JAVA_HOME)/include/linux target
inc_param := $(foreach d, $(inc), -I$d)
class_path := target/classes
native_src := src/main/native
lib_name := libtuntap.so
native_class := marnikitta.janet.tuntap.TunTap
native_class_file := $(subst .,/,$(native_class)).class
native_impl := $(subst .,_,$(native_class)).c
native_header := $(subst .,_,$(native_class)).h

all: target/$(lib_name)

target/$(lib_name): $(native_src)/$(native_impl) target/$(native_header) target
	$(CC) $(CFLAGS) $(inc_param) -Wl,-soname,tuntap.s -o $@ -shared $<

target/$(native_header): $(class_path)/$(native_class_file)
	$(JAVAH) -d target -cp $(class_path) $(native_class)

target:
	mkdir -p $@

.PHONY: clean mrproper

clean:
	rm -rf *.o

mrproper: clean
	rm -rf target
