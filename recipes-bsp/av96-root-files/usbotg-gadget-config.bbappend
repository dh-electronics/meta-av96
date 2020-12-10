
do_install_append () {
    rm -f ${D}${base_sbindir}/stm32_usbotg_eth_config.sh
    rm -f ${D}${systemd_unitdir}/network/53-usb-otg.network
}
