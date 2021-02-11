SUMMARY = "OpenSTLinux weston image with basic Wayland support (if enable in distro)."
LICENSE = "Proprietary"

include recipes-st/images/st-image-weston.bb

IMAGE_INSTALL += "openssl \
                  openssl-bin \
                  packagegroup-security-tpm2 \
                  stress-ng \
                  av96-root-files \
                  python3 \
                  python3-pip \
                  python3-pyserial \
                  python3-flask-socketio \
                  python3-werkzeug-tests \
                  m4projects-av96 \
                  lte-sensors-dashboard \
                  docker-ce \
                  docker-ce-contrib \
                  python3-docker-compose \
                  glibc-utils \
                  localedef \
"

IMAGE_LINGUAS += "${@bb.utils.contains('GLIBC_GENERATE_LOCALES','fr_FR.UTF-8','fr-fr','',d)}"

