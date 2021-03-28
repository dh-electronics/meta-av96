DESCRIPTION = "Root filesystem customization"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://etc/issue"
SRC_URI += "file://etc/xdg/weston/weston.ini"
SRC_URI += "file://etc/systemd/network/90-dhcp.network"
SRC_URI += "file://etc/wpa_supplicant/wpa_supplicant-wlan0.conf"
SRC_URI += "file://sbin/stm32_usbotg_eth_config.sh"
SRC_URI += "file://usr/bin/gldemo_start_stop.sh"
SRC_URI += "file://usr/share/weston/icon/meld.png"
SRC_URI += "file://home/root/v4l2_start.sh"
SRC_URI += "file://home/root/README-ap1302.txt"
SRC_URI += "file://home/root/README-wlan.txt"
SRC_URI += "file://lib/systemd/network/53-usb-otg.network"

do_install() {
    install -d ${D}${sysconfdir}
    install -d ${D}${sysconfdir}/xdg/weston
    install -d ${D}${sysconfdir}/systemd/network
    install -d ${D}${sysconfdir}/wpa_supplicant
    install -d ${D}${base_sbindir}
    install -d ${D}${bindir}
    install -d ${D}${datadir}/weston/icon
    install -d ${D}/home/root
    install -d ${D}${systemd_unitdir}/network

    install -m 755 ${WORKDIR}/etc/issue ${D}${sysconfdir}

    if ${@bb.utils.contains('GLIBC_GENERATE_LOCALES','fr_FR.UTF-8','true','false',d)}; then
        sed 's/keymap_layout=us/keymap_layout=fr/g' -i ${WORKDIR}/etc/xdg/weston/weston.ini
    fi
    install -m 755 ${WORKDIR}/etc/xdg/weston/* ${D}${sysconfdir}/xdg/weston/
    install -m 755 ${WORKDIR}/etc/systemd/network/* ${D}${sysconfdir}/systemd/network/
    install -m 755 ${WORKDIR}/etc/wpa_supplicant/* ${D}${sysconfdir}/wpa_supplicant/
    install -m 755 ${WORKDIR}/sbin/* ${D}${base_sbindir}
    install -m 755 ${WORKDIR}/usr/bin/* ${D}${bindir}
    install -m 755 ${WORKDIR}/usr/share/weston/icon/* ${D}${datadir}/weston/icon/
    install -m 755 ${WORKDIR}/home/root/v4l2_start.sh ${D}/home/root/
    install -m 755 ${WORKDIR}/home/root/README-ap1302.txt ${D}/home/root/
    install -m 755 ${WORKDIR}/home/root/README-wlan.txt ${D}/home/root/
    install -m 0644 ${WORKDIR}/lib/systemd/network/53-usb-otg.network ${D}${systemd_unitdir}/network/
}

FILES_${PN} += "${sysconfdir}"
FILES_${PN} += "${sysconfdir}/xdg/weston"
FILES_${PN} += "${sysconfdir}/systemd/network"
FILES_${PN} += "${sysconfdir}/wpa_supplicant"
FILES_${PN} += "${base_sbindir}"
FILES_${PN} += "${bindir}"
FILES_${PN} += "${datadir}/weston/icon"
FILES_${PN} += "/home/root"
FILES_${PN} += "${systemd_unitdir}/network"

RDEPENDS_${PN} += "bash"
