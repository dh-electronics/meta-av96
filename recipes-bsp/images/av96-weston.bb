SUMMARY = "OpenSTLinux weston image with basic Wayland support (if enable in distro)."
LICENSE = "Proprietary"

include recipes-st/images/st-image-weston.bb

IMAGE_INSTALL += "dhcp-server \
                  openssl \
                  openssl-bin \
                  stress-ng \
                  av96-root-files"

