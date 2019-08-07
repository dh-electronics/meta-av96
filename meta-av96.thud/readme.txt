    Creating a uSD card image for the Avenger96 board

    Required steps for building a BSP for ST's development boards can be found here:
      https://wiki.st.com/stm32mpu/index.php/STM32MP1_Distribution_Package
    Follow the steps on this page up to and including "repo sync". When "repo sync" finishes it downloaded all the required Yocto layers for building a standard BSP. Now we need to add meta-av96 layer.
    In the same folder execute the following commands:
        $ cp .../meta-av96.thud/meta-av96 layers/ -R
        $ mv layers/meta-av96/conf/machine/stm32mp1-av96.conf layers/meta-st/meta-st-stm32mp/conf/machine/
        $ pushd .
        $ cd layers
        $ git clone git://git.yoctoproject.org/meta-security
        $ cd meta-security/
        $ git checkout 51a4c6b5179d087f647cf0c458edb8ab107826ef
        $ cd ../meta-st/meta-st-stm32mp/conf/eula/
        $ ln -s ST_EULA_SLA stm32mp1-av96
        $ popd
        $ DISTRO=openstlinux-weston MACHINE=stm32mp1-av96 source layers/meta-st/scripts/envsetup.sh
        $ bitbake-layers add-layer ../layers/meta-av96/

    Building a Weston image:
        $ bitbake av96-weston

    When the build completed it has put all the required files into the 'deploy' folder. Execute the following commands:
        $ cd tmp-glibc/deploy/images/stm32mp1-av96/
        $ ./scripts/create_sdcard_from_flashlayout.sh flashlayout_av96-weston/FlashLayout_sdcard_stm32mp157a-av96-trusted.tsv

    The last command creates flashlayout_av96-weston_FlashLayout_sdcard_stm32mp157a-av96-trusted.raw . We can write it to a uSD card with:
        $ sudo dd if=flashlayout_av96-weston_FlashLayout_sdcard_stm32mp157a-av96-trusted.raw bs=1M of=/dev/sdX && sync
    where /dev/sdX is the block device of the inserted uSD card.


