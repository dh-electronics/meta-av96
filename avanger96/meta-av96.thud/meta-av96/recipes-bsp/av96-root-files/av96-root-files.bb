DESCRIPTION = "Root filesystem customization"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://etc/issue"
SRC_URI += "file://etc/xdg/weston/weston.ini"
SRC_URI += "file://etc/dhcp/dhcpd.conf"
SRC_URI += "file://etc/default/dhcp-server"
SRC_URI += "file://lib/firmware/brcm/brcmfmac43455-sdio.bin"
SRC_URI += "file://lib/firmware/brcm/brcmfmac43455-sdio.clm_blob"
SRC_URI += "file://lib/firmware/brcm/brcmfmac43455-sdio.txt"
SRC_URI += "file://lib/firmware/brcm/BCM4345C0.hcd"
SRC_URI += "file://sbin/stm32_usbotg_eth_config.sh"
SRC_URI += "file://usr/bin/gldemo_start_stop.sh"
SRC_URI += "file://usr/share/weston/icon/meld.png"

do_install() {
    install -d ${D}${sysconfdir}/xdg/weston
    install -d ${D}${sysconfdir}/dhcp
    install -d ${D}${sysconfdir}/default
    install -d ${D}${base_libdir}/firmware/brcm
    install -d ${D}${base_sbindir}
    install -d ${D}${bindir}
    install -d ${D}${datadir}/weston/icon

    install -m 755 ${WORKDIR}/etc/issue ${D}${sysconfdir}
    install -m 755 ${WORKDIR}/etc/xdg/weston/* ${D}${sysconfdir}/xdg/weston/
    install -m 755 ${WORKDIR}/etc/dhcp/* ${D}${sysconfdir}/dhcp/
    install -m 755 ${WORKDIR}/etc/default/* ${D}${sysconfdir}/default/
    install -m 755 ${WORKDIR}/lib/firmware/brcm/* ${D}${base_libdir}/firmware/brcm/
    install -m 755 ${WORKDIR}/sbin/* ${D}${base_sbindir}
    install -m 755 ${WORKDIR}/usr/bin/* ${D}${bindir}
    install -m 755 ${WORKDIR}/usr/share/weston/icon/* ${D}${datadir}/weston/icon/
}

FILES_${PN} += "${sysconfdir}"
FILES_${PN} += "${sysconfdir}/xdg/weston"
FILES_${PN} += "${sysconfdir}/dhcp"
FILES_${PN} += "${sysconfdir}/default"
FILES_${PN} += "${base_libdir}/firmware/brcm"
FILES_${PN} += "${base_sbindir}"
FILES_${PN} += "${bindir}"
FILES_${PN} += "${datadir}/weston/icon"

RDEPENDS_${PN} += "bash"
