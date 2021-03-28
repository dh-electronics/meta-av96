FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
  
SRC_URI += "file://etc/ppp/powerkey_ec25.sh"
SRC_URI += "file://etc/ppp/peers/quectel"
SRC_URI += "file://etc/ppp/peers/quectel-chat-connect"
SRC_URI += "file://etc/ppp/peers/quectel-chat-disconnect"

do_configure_append() {
    sed -i '/ExecStart=.*/a ExecStop=/etc/ppp/powerkey_ec25.sh stop' ${WORKDIR}/ppp@.service
    sed -i '/\[Service\]/a ExecStartPre=/etc/ppp/powerkey_ec25.sh start' ${WORKDIR}/ppp@.service
}

do_install_append() {
    install -m 755 ${WORKDIR}/etc/ppp/peers/* ${D}${sysconfdir}/ppp/peers/
    install -m 755 ${WORKDIR}/etc/ppp/powerkey_ec25.sh ${D}${sysconfdir}/ppp/
}

FILES_${PN} += "${sysconfdir}/ppp/peers"
