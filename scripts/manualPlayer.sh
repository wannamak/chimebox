#!/bin/sh

CLASSPATH=bin
CLASSPATH=$CLASSPATH:lib/guava-21.0.jar
CLASSPATH=$CLASSPATH:lib/jSerialComm-2.7.0.jar

java \
  -Djava.util.logging.config.file=logging.properties \
  -Djava.library.path=$(pwd)/bin \
  -cp $CLASSPATH \
  chimebox.manual.ManualPlayer $@
