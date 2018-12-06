FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://0001-linux-av96-devicetree.patch"

KERNEL_CONFIG_FRAGMENTS += "${@bb.utils.contains('COMBINED_FEATURES', 'optee', '${WORKDIR}/fragments/4.14/fragment-05-brcmfmac.config', '', d)}"
KERNEL_CONFIG_FRAGMENTS += "${@bb.utils.contains('COMBINED_FEATURES', 'optee', '${WORKDIR}/fragments/4.14/fragment-06-hdmi.config', '', d)}"

SRC_URI += "file://fragment-05-brcmfmac.config;subdir=fragments/4.14"
SRC_URI += "file://fragment-06-hdmi.config;subdir=fragments/4.14"

