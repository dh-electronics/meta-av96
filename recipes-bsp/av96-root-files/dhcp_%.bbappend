SYSTEMD_AUTO_ENABLE_${PN}-server = "enable"

do_install_append () {
    rm -f ${D}${sysconfdir}/dhcp/dhcpd.conf
    rm -f ${D}${sysconfdir}/default/dhcp-server
}
