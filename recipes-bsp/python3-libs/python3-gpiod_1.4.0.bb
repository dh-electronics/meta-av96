SUMMARY = "GPIOD"
HOMEPAGE = "https://github.com/hhk7734/python3-gpiod"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f96212d9a01c399ef62bedbbba921089"

SRC_URI[sha256sum] = "0b07cd3454c0ae42ce155320a2300a81eea09dd9d74f7b4c36e15532846d40c0"

inherit pypi setuptools3

PYPI_PACKAGE = "gpiod"

BBCLASSEXTEND = "native"
