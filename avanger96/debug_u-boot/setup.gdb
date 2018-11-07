####################################################################
######################## debug config. #############################
####################################################################
# Set debug phase:
#	1: Attach at TF-A
#	2: Attach at U-Boot
#	3: Attach at Linux kernel
set $debug_phase = 2

#	0: Attach at boot
#	1: Attach running target
set $debug_mode = 0
####################################################################

# Set environment configuration
source path_env.gdb

####################################################################
########################## functions ###############################
####################################################################
define break_bl2initram
	thbreak bl2_el3_plat_prepare_exit
	c
end

define break_boot_tf-a
	thbreak bl2_entrypoint
	c
end

define break_boot_uboot
	symadd_bl32 $bl32_load_addr
	thbreak _start
	c
end

define break_boot_linuxkernel
	symadd_bl32 $bl32_load_addr
	thbreak stext
	c
end
####################################################################


######################## common config. ############################
# Disables confirmation requests
set confirm off

# Connection to the host gdbserver port for Cortex-A7 SMP
target remote localhost:3334

# Configure GDB for OpenOCD
set remote hardware-breakpoint-limit 6
set remote hardware-watchpoint-limit 4
monitor gdb_breakpoint_override hard

# Switch to Core0
monitor targets stm32mp15x.cpu0
monitor stm32mp15x.dap apsel 0
monitor stm32mp15x.dap apcsw 0x10006000
monitor cortex_a smp_gdb 0
stepi
monitor cortex_a smp_gdb -1

# No SMP, only core 0 for the moment. We'll re-enable it in kernel
monitor cortex_a smp_off

# Reset the system and halt in bootrom in case of attach at boot
if $debug_mode == 0
	monitor reset
	monitor sleep 2000
	monitor reset halt
	monitor gdb_sync
	stepi

	# get load-address of BL32
	symload_bl32
	set $bl32_load_addr = sp_min_entrypoint

	symload_bl2
	symadd_bl32 $bl32_load_addr
end
####################################################################

#####################################################
# to stop at beginning of TF-A
if $debug_phase == 1
	break_boot_tf-a
else

	#####################################################
	# To stop at beginning of U-Boot
	if $debug_phase == 2
		if $debug_mode == 0
			break_bl2initram
		end

		symload_uboot

		if $debug_mode == 1
			symadd_uboot
		end

		if $debug_mode == 0
			break_boot_uboot
			thbreak relocate_code
			c
			symadd_uboot
			thbreak initcall_run_list
			thbreak board_init
			thbreak run_main_loop
		end

	else

		#####################################################
		# To stop at beginning of Linux kernel
		if $debug_phase == 3
			if $debug_mode == 0
				break_bl2initram
			end
			
			symload_vmlinux
			
			if $debug_mode == 0
				break_boot_linuxkernel
			end

			# if you are going to halt in kernel, re-enable SMP
			monitor cortex_a smp_on
			
		end
	end
end
