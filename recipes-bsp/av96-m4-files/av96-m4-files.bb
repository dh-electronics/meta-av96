DESCRIPTION = "Root filesystem customization"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://usr/local/projects/tof_i2c_CM4/README"
SRC_URI += "file://usr/local/projects/tof_i2c_CM4/fw_cortex_m4.sh"
SRC_URI += "file://usr/local/projects/tof_i2c_CM4/lib/firmware/tof_i2c_CM4.elf"

do_install() {
    install -d ${D}${prefix}/local/projects/tof_i2c_CM4/lib/firmware
    install -d ${D}${base_prefix}/home/root

    install -m 755 ${WORKDIR}/usr/local/projects/tof_i2c_CM4/README ${D}${prefix}/local/projects/tof_i2c_CM4/
    install -m 755 ${WORKDIR}/usr/local/projects/tof_i2c_CM4/fw_cortex_m4.sh ${D}${prefix}/local/projects/tof_i2c_CM4/
    install -m 755 ${WORKDIR}/usr/local/projects/tof_i2c_CM4/lib/firmware/tof_i2c_CM4.elf ${D}${prefix}/local/projects/tof_i2c_CM4/lib/firmware/
    lnr ${D}${prefix}/local/projects/tof_i2c_CM4/fw_cortex_m4.sh ${D}${base_prefix}/home/root/fw_cortex_m4.sh
}

FILES_${PN} += "${prefix}/local/projects/tof_i2c_CM4/README"
FILES_${PN} += "${prefix}/local/projects/tof_i2c_CM4/fw_cortex_m4.sh"
FILES_${PN} += "${prefix}/local/projects/tof_i2c_CM4/lib/firmware/tof_i2c_CM4.elf"
FILES_${PN} += "${base_prefix}/home/root/fw_cortex_m4.sh"

