FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://0001-linux-av96-devicetree.patch \
        file://0002-dts-pinctrl-enable-pull-up-on-dcmi-pins.patch \
        file://0003-dts-rcc-mco1-clock-with-slew-rate-to-1.patch \
        file://0004-dt-bindings-Document-MIPID02-bindings.patch \
        file://0005-media-stm32-dcmi-fix-error-messages.patch \
        file://0006-media-ov5640-reduce-mipi-rate-according-to-maximum-p.patch \
        file://0007-media-v4l2-add-support-of-pclk_max_frequency-on-CSI-.patch \
        file://0008-ARM-dts-stm32-fix-the-reset-gpio-information-of-the-.patch \
        file://0009-media-ov5640-fix-overflow-while-calculating-the-mipi.patch \
        file://0010-media-ov5640-add-the-support-of-the-auto-focus.patch \
        file://0011-dts-av96-merge-stm32mp157a-av96.dts-into-stm32mp157a.patch \
        file://0012-dts-av96-add-the-stm32mp157a-av96-ov5640.dts-file.patch \
        "

KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-06-brcmfmac.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-07-hdmi.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-08-bt_sco.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-09-tpm.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-10-usb_ftdi.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-11-st_mipid02.config"

SRC_URI += "file://fragment-06-brcmfmac.config;subdir=fragments/4.19"
SRC_URI += "file://fragment-07-hdmi.config;subdir=fragments/4.19"
SRC_URI += "file://fragment-08-bt_sco.config;subdir=fragments/4.19"
SRC_URI += "file://fragment-09-tpm.config;subdir=fragments/4.19"
SRC_URI += "file://fragment-10-usb_ftdi.config;subdir=fragments/4.19"
SRC_URI += "file://fragment-11-st_mipid02.config;subdir=fragments/4.19"
