SUMMARY = "Avenger96 firmware examples for CM4"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM  = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

PROJECTS_LIST = 'Avenger96/lte_sensors'
M4_BOARDS = 'Avenger96'
M4_BOARDS_SERVICE = "0"

require recipes-extended/m4projects/m4projects.inc

PV = "1.0.0"

# Get STM32CubeMP1 source code
SRC_URI += "git://github.com/STMicroelectronics/STM32CubeMP1.git;protocol=https;branch=master"
SRCREV  = "39fe4ecb2871a844720f6e039f7fa91be9294bdf"

SRC_URI += "git://github.com/mjbogusz/vl53l0x-linux;protocol=https;name=vl53l0x;destsuffix=git/vl53l0x"
SRCREV_vl53l0x="61f0e9adb25f7bbbf09127c08497c6b019ba07a9"

SRC_URI += "file://0001-m4-build.patch;patchdir=${WORKDIR}"
SRC_URI += "file://0002-vl53l0x.patch;patchdir=${S}/vl53l0x"

SRC_URI += "file://Projects/Avenger96/lte_sensors/Remoteproc/README;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Remoteproc/fw_cortex_m4.sh;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Src/lock_resource.c;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Src/syscalls.c;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Src/sysmem.c;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Src/stm32mp1xx_it.c;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Src/system_stm32mp1xx.c;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Src/rsc_table.c;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Src/mbox_ipcc.c;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Src/openamp.c;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Src/main.c;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Src/sens_adux1020.c;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Src/sens_bno055.c;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Src/stm32mp1xx_hal_msp.c;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Src/openamp_log.c;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Startup/startup_stm32mp157aacx.s;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Inc/mbox_ipcc.h;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Inc/stm32mp1xx_it.h;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Inc/stm32mp1xx_hal_conf.h;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Inc/openamp.h;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Inc/main.h;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Inc/rsc_table.h;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Inc/sens_adux1020.h;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Inc/sens_bno055.h;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Inc/lock_resource.h;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Inc/openamp_log.h;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/Inc/openamp_conf.h;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/VL53L0X/single_shot.cpp;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/Core/VL53L0X/I2Cdev.cpp;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/STM32CubeIDE/lte_sensors/lte_sensors_Debug.launch;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/STM32CubeIDE/lte_sensors/STM32MP157AACX_RAM.ld;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/STM32CubeIDE/lte_sensors/.project;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/STM32CubeIDE/lte_sensors/.cproject;subdir=git"
SRC_URI += "file://Projects/Avenger96/lte_sensors/STM32CubeIDE/lte_sensors/lte_sensors_Debug.cfg;subdir=git"

S = "${WORKDIR}/git"

do_compile_prepend() {
    if [ ! -x "${S}/Projects/Avenger96/lte_sensors/SW4STM32" ]; then
        lnr ${S}/Projects/Avenger96/lte_sensors/STM32CubeIDE ${S}/Projects/Avenger96/lte_sensors/SW4STM32
    fi
}

do_install_append() {
    install -d ${D}/home/root
    lnr ${D}/usr/local/Cube-M4-examples/Avenger96/lte_sensors/fw_cortex_m4.sh ${D}/home/root/fw_cortex_m4.sh
}

FILES_${PN} += "/home/root"


SYSTEMD_PACKAGES_remove = " m4projects-stm32mp1 "
SYSTEMD_SERVICE_${PN} = ""
