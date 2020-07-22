SUMMARY = "OpenSTLinux weston image with basic Wayland support (if enable in distro)."
LICENSE = "Proprietary"

include recipes-st/images/st-image-weston.bb

IMAGE_INSTALL += "dhcp-server \
                  openssl \
                  openssl-bin \
                  tpm2-tools \
                  libtss2 \
                  trousers \
                  tpm2-tss-engine \
                  stress-ng \
                  av96-root-files \
                  av96-m4-files"

