FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://0001-linux-av96-devicetree.patch"

KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.14/fragment-06-brcmfmac.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.14/fragment-07-hdmi.config"

SRC_URI += "file://fragment-06-brcmfmac.config;subdir=fragments/4.14"
SRC_URI += "file://fragment-07-hdmi.config;subdir=fragments/4.14"

