#!/bin/sh

if [ "$1" == "start" ]; then
  if [ ! -c /dev/ttyUSB3 ]; then
    # POWERKEY cycle
    gpioset 0 14=0
    sleep 1
    gpioset 0 14=1
    sleep 0.4
    gpioset 0 14=0

    # give modem some time to be enumerated on USB
    sleep 13
  fi
fi

if [ "$1" == "stop" ]; then
  if [ -c /dev/ttyUSB3 ]; then
    gpioset 0 14=1
    sleep 1
    gpioset 0 14=0
    sleep 1
    gpioset 0 14=1
    sleep 1
    gpioset 0 14=0

    # wait for the modem to shut down
    sleep 30
  fi
fi
