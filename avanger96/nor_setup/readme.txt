- set up "Distribution Kit", build st-image-weston
  see: http://localhost:7272/STMicroelectronics_userguide_3.4/index.php/STM32MP1_Distribution_kit

- rebuild U-Boot
    $ cd /mnt/work/OpenSTLinux/Distribution-kit/
    $ DISTRO=openstlinux-weston MACHINE=stm32mp1 source meta-st/scripts/envsetup.sh
    $ bitbake u-boot-basic-stm32mp -c clean -f
    $ bitbake u-boot-basic-stm32mp -c patch -f

- copy over u-boot_files (maybe "touch" them as well)

- continue rebuilding u-boot
    $ bitbake u-boot-basic-stm32mp -c compile -f
    $ bitbake u-boot-basic-stm32mp -c deploy -f
    $ bitbake st-image-weston

- set up "Developer Kit", start SDK in another terminal
  http://localhost:7272/STMicroelectronics_userguide_3.4/index.php/STM32MP1_Developer_kit

- set board to USB boot
  http://localhost:7272/STMicroelectronics_userguide_3.4/index.php/STM32MP157C-EV1_-_hardware_description

- power on board, see LD2 blinking
- connect USB OTG (CN16), see board being enumerated - blinking will stop

- connect USB-serial converter: JP4.2 -> converter RX, JP5.2 -> TX
- in another terminal start minicom and set 115200,N81

- IMPORTANT note before the next step: USB cable must be unplugged in a few seconds after the command has been issued

- in another terminal cd to .../mnt/work/OpenSTLinux/Distribution-kit/build-openstlinuxweston-stm32mp1/tmp-glibc/deploy/images/stm32mp1
  $ .../stm32_programmer_package_v1.0.7p1_mpu_linux-x64/bin/STM32_Programmer_CLI -c port=usb1 -w u-boot-spl.stm32-stm32mp157c-ev1-basic 0x01 --start 0x01

- USB cable must be unplugged es soon as "File download complete" apeears (green text)

- in minicom verify that DDR3 has been initialized

- connect J-Link and execute in the SDK terminal:
  $OECORE_NATIVE_SYSROOT/usr/bin/openocd -s $OECORE_NATIVE_SYSROOT/usr/share/openocd/scripts -f interface/jlink.cfg -f board/stm32mp15x_ev1_jlink.cfg

- in another terminal start SDK and cd to /mnt/work/OpenSTLinux/Distribution-kit/build-openstlinuxweston-stm32mp1/tmp-glibc/work/stm32mp1-openstlinux_weston-linux-gnueabi/u-boot-basic-stm32mp/2018.03-release.AUTOINC+f95ab1fb6e/build/stm32mp157c-ev1_devicetree_stm32mp15_basic_defconfig
  $ arm-openstlinux_weston-linux-gnueabi-gdb -x u-boot.gdb

- now we have a running U-Boot in minicom

- exit from GDB (Ctrl-C, Ctrl-D, Ctrl-D)
- cd to /mnt/work/OpenSTLinux/Distribution-kit/build-openstlinuxweston-stm32mp1/tmp-glibc/deploy/images/stm32mp1

- restart OpenOCD (in the previous OpenOCD terminal)

- in the GDB terminal issue:
  $ arm-openstlinux_weston-linux-gnueabi-gdb
  (gdb) target remote localhost:3334
  (gdb) restore u-boot-spl.stm32-stm32mp157c-ev1-basic binary 0xc0000000
  (gdb) restore u-boot-stm32mp157c-ev1-basic.img binary 0xc0020000
  (gdb) restore splash.bin binary 0xc00e0000
  (gdb) c

- in minicom enter
  STM32MP> sf erase 0 0x2d0000
  STM32MP> sf write 0xc0000000 0 0x1e000
  STM32MP> sf write 0xc0000000 0x40000 0x1e000
  STM32MP> sf write 0xc0020000 0x80000 0xb3000
  STM32MP> sf write 0xc00e0000 0x280000 0xa000 

- exit GDB
- execute
  $ ./scripts/create_sdcard_from_flashlayout.sh flashlayout_st-image-weston/FlashLayout_nor-sdcard_stm32mp157c-ev1-trusted.tsv
  $ sudo dd if=flashlayout_st-image-weston_FlashLayout_nor-sdcard_stm32mp157c-ev1-trusted.raw bs=1M of=/dev/sdX && sync

- insert uSD into board
- set board to NOR boot
- reboot
- in minicom stop autoboot
- in U-Boot enter:
  STM32MP> setenv boot_targets mmc0
  STM32MP> run bootcmd

- lie back

