    Disclaimer
    In its current state the meta-av96 layer doesn't aim to be elegant or portable.
It's mere purpose is to be verifiable and compact. Because of this it contains a lot
of temporary hacks.
    In addition to the original targets meta-av96 creates new images for stm32mp157a-av96. Don't use stm32mp157c-ev1 on other than RevC boards.


    Building the Yocto image

    - follow the build intructions up to and including "repo sync":
    http://localhost:7272/STMicroelectronics_userguide_3.5/index.php/STM32MP1_Distribution_Package

    - change to working directory (eg. /mnt/work/OpenSTLinux/Distribution-kit) and copy necessary files:
        $ cp .../meta-av96.beta1/meta-st/* ./meta-st/ -R
        $ cd meta-st/meta-st-stm32mp/conf/eula/
        $ ln -s ST_EULA stm32mp1-av96

    - change back to the working directory, accept ST's EULA and build the demo image:
        $ cd ../../../../
        $ DISTRO=openstlinux-weston MACHINE=stm32mp1-av96 source meta-st/scripts/envsetup.sh
        $ bitbake-layers add-layer ../meta-st/meta-av96/
        $ bitbake st-image-weston


    Creating SD card image

    - still in .../tmp-glibc/deploy/images/stm32mp1/ execute:
        $ ./scripts/create_sdcard_from_flashlayout.sh flashlayout_st-image-weston/FlashLayout_sdcard_stm32mp157a-av96-trusted.tsv
        $ sudo dd if=flashlayout_st-image-weston_FlashLayout_sdcard_stm32mp157a-av96-trusted.raw bs=1M of=/dev/sdX && sync


    Finalizing card image

    Some file need to be added to the uSD image in order to make WiFi work and
adding release info.
    - insert uSD card into PC and let PC mount rootfs and bootfs partitions
    - copy necessary files from ./sdcrad/ to uSD card


