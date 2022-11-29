DESCRIPTION = "Firmware files for CYW43455 WiFi chipset"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI += "file://lib/firmware/ap1302/on_ar1337_cdaf_dw9718.290520_01.bin"
SRC_URI += "file://lib/firmware/ap1302/test_pattern.bin"

do_install() {
    install -d ${D}${base_libdir}/firmware/ap1302
    install -m 755 ${WORKDIR}/lib/firmware/ap1302/* ${D}${base_libdir}/firmware/ap1302/
}

FILES:${PN} += "${base_libdir}/firmware/ap1302"

