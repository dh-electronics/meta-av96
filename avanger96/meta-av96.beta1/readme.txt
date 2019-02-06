    Disclaimer
    In its current state the meta-av96 layer doesn't aim to be elegant or portable.
It's mere purpose is to be verifiable and compact. Because of this it contains a lot
of temporary hacks.
    In addition to the original targets meta-av96 creates new images for stm32mp157a-av96. Don't use stm32mp157c-ev1 on other than RevC boards.


    Building the Yocto image

    - follow the build intructions up to and including "repo sync":
    http://localhost:7272/STMicroelectronics_userguide_3.5/index.php/STM32MP1_Distribution_Package

    - change to working directory (eg. /mnt/work/OpenSTLinux/Distribution-kit) and execute:
        $ DISTRO=openstlinux-weston MACHINE=stm32mp1-boards-revc source meta-st/scripts/envsetup.sh

    - copy meta-av96 layer into meta-st folder
        $ cp .../meta-st/meta-av96 ../meta-st/ -R

    - add meta-av96 layer and build demo image
        $ bitbake-layers add-layer ../meta-st/meta-av96/
        $ bitbake st-image-weston



