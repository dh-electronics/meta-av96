SUMMARY = "OpenSTLinux weston image with basic Wayland support (if enable in distro)."
LICENSE = "Proprietary"

include recipes-st/images/st-image-weston.bb

IMAGE_INSTALL += "openssl \
                  openssl-bin \
                  stress-ng \
                  av96-root-files \
                  ap1302-firmware \
                  wifi-firmware \
                  python3 \
                  python3-pip \
                  python3-pyserial \
                  python3-flask-socketio \
                  python3-werkzeug-tests \
                  lte-azure-demo \
                  lte-sensors-dashboard \
                  glibc-utils \
                  localedef \
                  ppp \
                  minicom \
                  python3-gpiod \
                  python3-azure-iot-device \
                  python3-paho-mqtt \
                  python3-packaging \
                  python3-requests-unixsocket \
                  python3-deprecation \
                  python3-janus \
"

IMAGE_LINGUAS += "${@bb.utils.contains('GLIBC_GENERATE_LOCALES','fr_FR.UTF-8','fr-fr','',d)}"

