# Copyright (C) 2019, STMicroelectronics - All Rights Reserved
SUMMARY = "Create package containing MobileNetV1 models for EdgeTPU "
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI  = " https://tfhub.dev/tensorflow/lite-model/deeplabv3/1/metadata/2?lite-format=tflite;subdir=${BPN}-${PV}/tflite-model-deeplabv3;name=model_deeplabv3;downloadfilename=lite-model_deeplabv3_1_metadata_2.tflite"
SRC_URI[model_deeplabv3.sha256sum] = "9711334db2b01d5894feb8ed0f5cb3e97d125b8d229f8d8692f625801818f5ef"

S = "${WORKDIR}/${BPN}-${PV}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${prefix}/local/demo-ai/image-segmentation

    # install mobilenet models
    install -m 0644 ${S}/tflite-model-deeplabv3/*.tflite ${D}${prefix}/local/demo-ai/image-segmentation/
}

PACKAGES_remove = "${PN}-dbg"
PACKAGES_remove = "${PN}-dev"
PACKAGES_remove = "${PN}-staticdev"

FILES_${PN} += "${prefix}/local/"

