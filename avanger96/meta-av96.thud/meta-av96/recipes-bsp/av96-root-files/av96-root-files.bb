DESCRIPTION = "Root filesystem customization"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://issue"

do_install() {
    install -d ${D}${sysconfdir}

    install -m 755 ${WORKDIR}/issue ${D}${sysconfdir}
}

FILES_${PN} += "${sysconfdir}"

