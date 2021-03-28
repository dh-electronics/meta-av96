SUMMARY = "GPIOD"
HOMEPAGE = "https://github.com/hhk7734/python3-gpiod"
SECTION = "devel/python"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM = "file://README.md;md5=08a71b79145a59b91d530f609d00f866"

SRC_URI[sha256sum] = "8605e3eadc42f628fd00b2343506dc2ebf4b66904121030acd23b1abd94c967a"

inherit pypi setuptools3

PYPI_PACKAGE = "azure-iot-device"

BBCLASSEXTEND = "native"
