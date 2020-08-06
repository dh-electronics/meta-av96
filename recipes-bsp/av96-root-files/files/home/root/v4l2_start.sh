#!/bin/sh

v4l2-ctl -L
# v4l2-ctl --set-ctrl auto_exposure=1
# v4l2-ctl --set-ctrl exposure=2000
gst-launch-1.0 v4l2src ! "video/x-raw, width=1024" ! queue ! autovideosink -e
