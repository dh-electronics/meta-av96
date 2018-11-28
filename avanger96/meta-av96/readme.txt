    Disclaimer
    In its current state the meta-av96 layer doesn't aim to be elegant or portable.
It's mare purpose is to be verifiable and compact. Because of this it contains a lot
of temporary hacks.
    Also please don't try to execute images created by meta-av96 on other HW than the
Avanger96 platform.


    Building new Yocto image

    - follow documentation and execute steps up to "Building the OpenSTLinux distribution":
      	     http://localhost:7272/STMicroelectronics_userguide_3.4/index.php/STM32MP1_Distribution_kit

    - patch bblayers.conf file with these commands:
        $ cd conf
        $ patch < .../build-openstlinuxweston-stm32mp1/conf/bblayers.conf.patch
        $ cd ..

    - copy meta-av96 folder under meta-st:
        $ cp .../meta-av96/meta-st/meta-av96/ ../meta-st/ -R

    - rebuild tf-a, u-boot-basic, u-boot-trusted
        $ bitbake tf-a-stm32mp -c clean -f
        $ bitbake u-boot-basic-stm32mp -c clean -f

    - rebuild whole image again:
        $ bitbake st-image-weston

    - the last compile process will fail with the following error message:
        ...
ERROR: st-image-weston-1.0-r0 do_image_complete: Missing tf-a-stm32mp157a-av96-trusted.stm32 binary file in deploy folder
        ...

    - let's fix that with two copy commands and finish the build:
        $ cp tmp-glibc/deploy/images/stm32mp1/tf-a-stm32mp157c-ev1-trusted.stm32 tmp-glibc/deploy/images/stm32mp1/tf-a-stm32mp157a-av96-trusted.stm32
        $ cp tmp-glibc/deploy/images/stm32mp1/u-boot-stm32mp157c-ev1-trusted.stm32 tmp-glibc/deploy/images/stm32mp1/u-boot-stm32mp157a-av96-trusted.stm32
        $ bitbake st-image-weston



    Populating uSD card after Yocto build has completed

    - insert uSD card into Av96 board
    - set boot mode to 0-0-0 on S3
    - power up board, connect USB OTG connector to PC
    - verify that PC enumerates "DFU in HS mode" device:
        $ .../stm32_programmer_package_v1.0.7p1_mpu_linux-x64/bin/STM32_Programmer_CLI -l usb

    - let ST's CubeProgrammer populate uSD card:
        $ cd tmp-glibc/deploy/images/stm32mp1/
        $ .../stm32_programmer_package_v1.0.7p1_mpu_linux-x64/bin/STM32_Programmer_CLI \
        -c port=usb1 -w flashlayout_st-image-weston/FlashLayout_sdcard_stm32mp157c-ev1-trusted.tsv -tm 13000

    - the last step will take some minutes
    - change back boot mode to 1-0-1 (uSD mode) and reboot board



    Rebuilding only u-boot-basic

    - "cd" to build directory, usually .../Distribution-kit/build-openstlinuxweston-stm32mp1
        $ bitbake u-boot-basic-stm32mp -C compile -f

    - u-boot-basic files will be generated in
      ./tmp-glibc/work/stm32mp1-openstlinux_weston-linux-gnueabi/u-boot-basic-stm32mp/2018.03-release.AUTOINC+f95ab1fb6e/deploy-u-boot-basic-stm32mp

    - writing u-boot-spl and u-boot-basic to uSD card:
        $ sudo dd if=u-boot-spl.stm32-stm32mp157c-ev1-basic bs=1M of=/dev/sdX1 && sync
        $ sudo dd if=u-boot-stm32mp157c-ev1-basic.img bs=1M of=/dev/sdX3 && sync
      where /dev/sdX is the block device for the inserted uSD card
