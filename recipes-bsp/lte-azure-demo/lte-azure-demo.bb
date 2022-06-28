DESCRIPTION = "lte-sensors-azure-demo"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://README-lte-azure-demo.txt"
SRC_URI += "file://LTE_sensors_azure_demo/send_sensors.py"
SRC_URI += "file://LTE_sensors_azure_demo/set_env.sh"

do_install() {
    install -d ${D}/home/root/LTE_sensors_azure_demo

    install -m 755 ${WORKDIR}/README-lte-azure-demo.txt ${D}/home/root/
    install -m 755 ${WORKDIR}/LTE_sensors_azure_demo/* ${D}/home/root/LTE_sensors_azure_demo
}

FILES:${PN} += "/home/root"
RDEPENDS:${PN} = "python3-core bash"
