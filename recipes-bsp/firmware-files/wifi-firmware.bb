DESCRIPTION = "Firmware files for CYW43455 WiFi chipset"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI += "file://lib/firmware/brcm/brcmfmac43455-sdio.bin"
SRC_URI += "file://lib/firmware/brcm/brcmfmac43455-sdio.clm_blob"
SRC_URI += "file://lib/firmware/brcm/brcmfmac43455-sdio.txt"
SRC_URI += "file://lib/firmware/brcm/BCM4345C0.hcd"

do_install() {
    install -d ${D}${base_libdir}/firmware/brcm
    install -m 755 ${WORKDIR}/lib/firmware/brcm/* ${D}${base_libdir}/firmware/brcm/
}

FILES_${PN} += "${base_libdir}/firmware/brcm"

