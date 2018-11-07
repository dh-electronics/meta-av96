- connect ST-Link + UART, reset board, stop U-Boot in autoboot prompt

- start SDK in one terminal
- execute
  $ $OECORE_NATIVE_SYSROOT/usr/bin/openocd -s /mnt/work/OpenSTLinux/Developer-kit/SDK/sysroots/x86_64-openstlinux_weston_sdk-linux/usr/share/openocd/scripts -f board/stm32mp15x_ev1_stlink_jtag.cfg
- make sure .../target/stm32mp15x_stpmu1.cfg is the original

- start SDK in another terminal
- execute
  $ arm-openstlinux_weston-linux-gnueabi-gdb -x setup.gdb
- only every 2nd attempt will succeed

