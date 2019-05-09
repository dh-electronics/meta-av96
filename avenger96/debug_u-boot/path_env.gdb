####################################################################
# Start of path customization
# fill following lines replacing <...> with the proper absolute path
#

####################### Source path ################################
# TF-A source path
# directory <Your_absolute_path_to_TF-A_source>/

# U-Boot source path
# set substitute-path <Absolute_source_path_from_u-boot_elf_file>/  <Your_absolute_path_to_U-Boot_source>/
# Source path can be retrieve from u-boot elf with the following readelf command: readelf --string-dump=.debug_str <u-boot_elf_file> | sed -n '/\/\|\.c/{s/.*\] //p}'

# Linux kernel source path
# set substitute-path <Absolute_source_path_from_vmlinux>/ <Your_absolute_path_to_Linux_kernel_source>/
# Source path can be retrieve from vmlinux with the following readelf command: readelf --string-dump=.debug_str vmlinux | sed -n '/\/\|\.c/{s/.*\] //p}'
####################################################################

####################### Symbol path ################################
define symload_bl32
	symbol-file /mnt/work/OpenSTLinux/Distribution-kit/build-openstlinuxweston-stm32mp1/tmp-glibc/work/stm32mp1-openstlinux_weston-linux-gnueabi/tf-a-stm32mp/1.5-release.AUTOINC+ed8112606c/deploy-tf-a-stm32mp/tf-a-bl32-trusted.elf
end
document symload_bl32
	format: symbol-file <bl32_elf_full_path>
end

define symadd_bl32
	add-symbol-file /mnt/work/OpenSTLinux/Distribution-kit/build-openstlinuxweston-stm32mp1/tmp-glibc/work/stm32mp1-openstlinux_weston-linux-gnueabi/tf-a-stm32mp/1.5-release.AUTOINC+ed8112606c/deploy-tf-a-stm32mp/tf-a-bl32-trusted.elf $arg0
end
document symadd_bl32
	format: add_symbol-file <bl32_full_path> $arg0
end

define symload_bl2
	symbol-file /mnt/work/OpenSTLinux/Distribution-kit/build-openstlinuxweston-stm32mp1/tmp-glibc/work/stm32mp1-openstlinux_weston-linux-gnueabi/tf-a-stm32mp/1.5-release.AUTOINC+ed8112606c/deploy-tf-a-stm32mp/tf-a-bl2-trusted.elf
end
document symload_bl2
	format: symbol-file <bl2_full_path>
end

define symload_uboot
	symbol-file /mnt/work/OpenSTLinux/Distribution-kit/build-openstlinuxweston-stm32mp1/tmp-glibc/work/stm32mp1-openstlinux_weston-linux-gnueabi/u-boot-trusted-stm32mp/2018.03-release.AUTOINC+f95ab1fb6e/deploy-u-boot-trusted-stm32mp/u-boot-stm32mp157c-ev1-trusted.elf
end
document symload_uboot
	format: symbol-file <uboot_elf_full_path>
end

define symadd_uboot
	set $offset = ((gd_t *)$r9)->relocaddr
	symbol-file
	add-symbol-file /mnt/work/OpenSTLinux/Distribution-kit/build-openstlinuxweston-stm32mp1/tmp-glibc/work/stm32mp1-openstlinux_weston-linux-gnueabi/u-boot-trusted-stm32mp/2018.03-release.AUTOINC+f95ab1fb6e/deploy-u-boot-trusted-stm32mp/u-boot-stm32mp157c-ev1-trusted.elf $offset
end
document symadd_uboot
	format: symbol-file <uboot_full_path> $offset
	offset is given by command: set $offset = ((gd_t *)$r9)->relocaddr
end

define symload_vmlinux
	symbol-file <Your_absolute_path_to>/vmlinux
end
document symload_vmlinux
	format: symbol-file <vmlinux_full_path>
end
####################################################################

#
# End of path customization
####################################################################
