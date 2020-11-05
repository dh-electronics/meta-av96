DESCRIPTION = "lte-sensors-dashboard"
SECTION = "examples" 
LICENSE = "CLOSED" 
PR = "r0" 

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_URI = "git://github.com/bkardosa/LTE_sensors_av96_dashboard.git;protocol=https;name=gitlab"
SRCREV_gitlab = "master"

SRC_URI += "file://README-lte-sensors-demo.txt"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}/home/root/LTE_sensors_av96_dashboard

    cp -r ${S}/* ${D}/home/root/LTE_sensors_av96_dashboard
    install -m 755 ${WORKDIR}/README-lte-sensors-demo.txt ${D}/home/root/
}

FILES_${PN} += "/home/root"

