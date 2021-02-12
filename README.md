### Creating a uSD card image for the Avenger96 board
  
Required steps for building a BSP for ST's development boards can be found here:
https://wiki.st.com/stm32mpu/index.php/STM32MP1_Distribution_Package

Building the BSP for the Avenger96 follows the scheme of the above build process but starts with a different 'repo' manifest file:
- create the build directory following ST's recommendations at
        https://wiki.st.com/stm32mpu/wiki/Example_of_directory_structure_for_Packages
- enter the working directory and execute the following commands
```
$ repo init -u https://github.com/dh-electronics/manifest-av96 -b dunfell
$ repo sync
$ source layers/meta-arrow/scripts/init-build-env.sh
$ bitbake av96-weston`
```

The last command will build the BSP. The full build will take about 1-2 hours dpending on machine and internet speed and will require around 50GB disk space. When the build completes it puts all the required files into the 'deploy' folder. Execute the following commands:
```
$ cd tmp-glibc/deploy/images/stm32mp1-av96/
$ ./scripts/create_sdcard_from_flashlayout.sh flashlayout_av96-weston/trusted/FlashLayout_sdcard_stm32mp157a-av96-trusted.tsv
```

The last command creates FlashLayout_sdcard_stm32mp157a-av96-trusted.raw .
We can write it to a uSD card with:
```
$ sudo dd if=FlashLayout_sdcard_stm32mp157a-av96-trusted.raw bs=1M of=/dev/sdX && sync
```
where /dev/sdX is the block device of the inserted uSD card.

<br></br>
#### Qt5 support
The following commands will create a uSD card image with Qt5 libraries and demos along with the normal Weston GUI:
```
$ bitbake av96-qtwayland
$ cd tmp-glibc/deploy/images/stm32mp1-av96/
$ ./scripts/create_sdcard_from_flashlayout.sh flashlayout_av96-qtwayland/trusted/FlashLayout_sdcard_stm32mp157a-av96-trusted.tsv
$ sudo dd if=FlashLayout_sdcard_stm32mp157a-av96-trusted.raw bs=1M of=/dev/sdX && sync
```

<br></br>
#### French locale and keyboard support in Weston
`conf/machine/stm32mp1-av96.conf` must be modified to enable French locale. Uncomment the line containing GLIBC_GENERATE_LOCALES and start a clean build for adding French locale.


