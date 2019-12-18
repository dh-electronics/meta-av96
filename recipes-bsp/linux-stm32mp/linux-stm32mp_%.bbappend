FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
	file://0001-linux-av96-devicetree.patch \
	file://0002-dts-pinctrl-enable-pull-up-on-dcmi-pins.patch \
	file://0003-dts-rcc-mco1-clock-with-slew-rate-to-1.patch \
	file://0004-media-uapi-Add-MEDIA_BUS_FMT_BGR888_3X8-media-bus-fo.patch \
	file://0005-dt-bindings-Document-MIPID02-bindings.patch \
	file://0006-media-st-mipid02-MIPID02-CSI-2-to-PARALLEL-bridge-dr.patch \
	file://0007-dts-av96-cleanup-of-eval-board-config-for-dcmi-ov564.patch \
	file://0008-dts-av96-add-mipid02-CSI-bridge.patch \
	file://0009-dts-av96-add-DCMI-support.patch \
	file://0010-dts-av96-add-support-of-OV5640-D3-mezzanine-board.patch \
	file://0011-dt-disable-i2c1.patch \
	file://0012-ov5640-always-on-for-debug.patch \
	file://0013-mipid02-4.19-compat.patch \
	file://0014-TMP-force-mipi-build.patch \
	file://0015-DT-HACK-fix-rcc-MCO-not-well-configured.patch \
	file://0016-media-ov5640-refresh-from-latest-media-tree.patch \
	file://0017-media-stm32-dcmi-fix-error-messages.patch \
	file://0018-media-ov5640-fix-JPEG-regression.patch \
	file://0019-media-ov5640-reduce-mipi-rate-according-to-maximum-p.patch \
	file://0020-media-stm32-dcmi-improve-sensor-subdev-naming.patch \
	file://0021-media-stm32-dcmi-add-media-controller-support.patch \
	file://0022-media-stm32-dcmi-add-support-of-several-sub-devices.patch \
	file://0023-media-st-mipid02-add-support-of-RGB565.patch \
	file://0024-media-st-mipid02-add-support-of-YUYV8-and-UYVY8.patch \
	file://0025-media-st-mipid02-add-support-of-JPEG.patch \
	file://0026-media-st-mipid02-add-support-of-V4L2_CID_LINK_FREQ.patch \
	file://0027-media-ov5640-add-support-of-V4L2_CID_LINK_FREQ.patch \
	file://0028-media-v4l2-add-support-of-pclk_max_frequency-on-CSI-.patch \
	file://0029-ARM-dts-stm32-limit-OV5640-pixel-clock-to-77MHz-on-s.patch \
	file://0030-ARM-dts-stm32-fix-the-reset-gpio-information-of-the-.patch \
	file://0031-media-ov5640-add-the-support-of-the-auto-focus.patch \
	file://0032-media-ov5640-fix-overflow-while-calculating-the-mipi.patch"

KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-06-brcmfmac.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-07-hdmi.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-08-bt_sco.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-09-tpm.config"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/fragments/4.19/fragment-10-usb_ftdi.config"

SRC_URI += "file://fragment-06-brcmfmac.config;subdir=fragments/4.19"
SRC_URI += "file://fragment-07-hdmi.config;subdir=fragments/4.19"
SRC_URI += "file://fragment-08-bt_sco.config;subdir=fragments/4.19"
SRC_URI += "file://fragment-09-tpm.config;subdir=fragments/4.19"
SRC_URI += "file://fragment-10-usb_ftdi.config;subdir=fragments/4.19"
