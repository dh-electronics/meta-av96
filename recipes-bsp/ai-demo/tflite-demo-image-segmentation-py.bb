DESCRIPTION = "Image segmentation demo"
 
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI  = "file://image_segmentation_demo.py;subdir=${BPN}-${PV}"
SRC_URI  += "file://background_1.jpg;subdir=${BPN}-${PV}"
SRC_URI  += "file://background_2.jpg;subdir=${BPN}-${PV}"
SRC_URI  += "file://background_3.jpg;subdir=${BPN}-${PV}"
SRC_URI  += "file://background_4.jpg;subdir=${BPN}-${PV}"
SRC_URI  += "file://background_5.jpg;subdir=${BPN}-${PV}"
SRC_URI  += "file://launch_image_segmentation.sh;subdir=${BPN}-${PV}"
SRC_URI  += "file://070-tflite-image-segmentation-python.yaml;subdir=${BPN}-${PV}"
SRC_URI  += "file://README.txt;subdir=${BPN}-${PV}"

S = "${WORKDIR}/${BPN}-${PV}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${prefix}/local/demo-ai/image-segmentation
    install -d ${D}${prefix}/local/demo/application

    install -m 0644 ${S}/*.jpg ${D}${prefix}/local/demo-ai/image-segmentation/
    install -m 0644 ${S}/*.txt ${D}${prefix}/local/demo-ai/image-segmentation/
    install -m 0755 ${S}/*.py ${D}${prefix}/local/demo-ai/image-segmentation/
    install -m 0755 ${S}/*.sh ${D}${prefix}/local/demo-ai/image-segmentation/
    install -m 0644 ${S}/*.yaml ${D}${prefix}/local/demo/application/
}

PACKAGES_remove = "${PN}-dbg"
PACKAGES_remove = "${PN}-dev"
PACKAGES_remove = "${PN}-staticdev"

FILES_${PN} += "${prefix}/local/"

RDEPENDS_${PN} += " \
        python3-core \
        python3-ctypes \
        python3-multiprocessing \
        python3-numpy \
        python3-opencv \
        python3-threading \
        python3-pillow \
        python3-pygobject \
        python3-tensorflow-lite \
        tflite-model-deeplabv3 \
"
