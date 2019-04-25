#!/bin/bash

killall -9 glmark2-es2-wayland
if [ $? -ne 0 ]; then
  glmark2-es2-wayland --run-forever --annotate
fi
