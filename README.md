# General
This repository is a fork from the official meta-av96 repository:
<https://github.com/dh-electronics/meta-av96>

# Goal
The aim of this fork is to provide an integration of the Avenger96 board in
the OpenSTLinux build system taking into account Openembedded Yocto Thud
environement and adding the support of the ov5640 CSI camera.

The target is to merge all the patches provided by this fork into the official
meta-av96.

This repository will no more be maintained once the official meta-av96 layer
will be up to date.

# Software compatibility
Compatible with the following OpenSTLinux Distribution release (v1.1.0):
* [openstlinux-4.19-thud-mp1-19-10-09](https://github.com/STMicroelectronics/oe-manifest)

# Board compatibility
Compatible with the [Avenger96 board rev 588-200](https://www.arrow.com/en/products/avenger96/arrow-development-tools)

# Further information
This repository is used within the X-LINUX-AI-CV 1.1.0 STM32MP1 expansion
package.
To know more about how to install, how to use, ..., please read following wiki
article:
<https://wiki.st.com/stm32mpu/wiki/STM32MP1_artificial_intelligence_expansion_packages>
