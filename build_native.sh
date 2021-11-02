#!/bin/sh

aarch64-linux-gnu-gcc \
  -shared \
  -O3 \
  -I/usr/include \
  -I/usr/lib/jvm/java-14-openjdk-amd64/include \
  -I/usr/lib/jvm/java-14-openjdk-amd64/include/linux \
  src/native/chimebox_physical_SystemManagementBus.cpp \
  -o bin/libchimebox.so