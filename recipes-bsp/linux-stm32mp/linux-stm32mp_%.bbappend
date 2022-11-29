FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://0001-linux-ap1302.patch"
SRC_URI += "file://0002-media-ov5640-reduce-mipi-rate-according-to-maximum-p.patch"
SRC_URI += "file://0003-media-v4l2-add-support-of-pclk_max_frequency-on-CSI-.patch"
SRC_URI += "file://0004-media-ov5640-add-the-support-of-the-auto-focus.patch"


KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-06-brcmfmac.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-07-hdmi.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-08-bt_sco.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-09-tpm.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-10-usb_ftdi.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-11-ap1302.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-12-ppp.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/5.4/fragment-13-usb-serial.config"

SRC_URI += "file://fragment-06-brcmfmac.config;subdir=fragments/5.4"
SRC_URI += "file://fragment-07-hdmi.config;subdir=fragments/5.4"
SRC_URI += "file://fragment-08-bt_sco.config;subdir=fragments/5.4"
SRC_URI += "file://fragment-09-tpm.config;subdir=fragments/5.4"
SRC_URI += "file://fragment-10-usb_ftdi.config;subdir=fragments/5.4"
SRC_URI += "file://fragment-11-ap1302.config;subdir=fragments/5.4"
SRC_URI += "file://fragment-12-ppp.config;subdir=fragments/5.4"
SRC_URI += "file://fragment-13-usb-serial.config;subdir=fragments/5.4"

