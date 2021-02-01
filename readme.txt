    Creating a uSD card image for the Avenger96 board

    Required steps for building a BSP for ST's development boards can be found here:
      https://wiki.st.com/stm32mpu/index.php/STM32MP1_Distribution_Package

    Building the BSP for the Avenger96 follows the scheme of the above build
process but starts with a different 'repo' manifest file:
    - create the build directory following ST's recommendations at
        https://wiki.st.com/stm32mpu/wiki/Example_of_directory_structure_for_Packages
    - enter the working directory and execute the following commands
      $ repo init -u https://github.com/dh-electronics/manifest-av96 -b dunfell-ai-demo
      $ repo sync
      $ source layers/meta-arrow/scripts/init-build-env.sh
      $ bitbake av96-ai-demo

    The last command will build the BSP. The full build will take about 2-3
hours dpending on machine and internet speed and will require around 50GB
disk space. When the build completes it puts all the required files into the
'deploy' folder. Execute the following commands:
      $ cd tmp-glibc/deploy/images/stm32mp1-av96/
      $ ./scripts/create_sdcard_from_flashlayout.sh flashlayout_av96-weston/trusted/FlashLayout_sdcard_stm32mp157a-av96-trusted.tsv

    The last command creates FlashLayout_sdcard_stm32mp157a-av96-trusted.raw .
We can write it to a uSD card with:
      $ sudo dd if=FlashLayout_sdcard_stm32mp157a-av96-trusted.raw bs=1M of=/dev/sdX && sync
    where /dev/sdX is the block device of the inserted uSD card.

