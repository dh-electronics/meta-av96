
do_configure:append() {
    if [ -e ${WORKDIR}/create_sdcard_from_flashlayout.sh ]; then
        bbnote "Update DEFAULT_ROOTFS_PARTITION_SIZE to ${ROOTFS_PARTITION_SIZE}"
        sed 's/^DEFAULT_ROOTFS_PARTITION_SIZE=.*$/DEFAULT_ROOTFS_PARTITION_SIZE='"${ROOTFS_PARTITION_SIZE}"'/' -i ${WORKDIR}/create_sdcard_from_flashlayout.sh
        if [ ${ROOTFS_PARTITION_SIZE} -gt 1572864 ]; then
            # rootfs > 1.5GB then put sdcard raw size = ROOTFS_PARTITION_SIZE + 128MB
            raw_size=$(expr ${ROOTFS_PARTITION_SIZE} / 1024 )
            raw_size=$(expr $raw_size + 320)
            sed 's/^DEFAULT_RAW_SIZE=.*$/DEFAULT_RAW_SIZE='"$raw_size"'/' -i ${WORKDIR}/create_sdcard_from_flashlayout.sh
        fi
    fi
}

