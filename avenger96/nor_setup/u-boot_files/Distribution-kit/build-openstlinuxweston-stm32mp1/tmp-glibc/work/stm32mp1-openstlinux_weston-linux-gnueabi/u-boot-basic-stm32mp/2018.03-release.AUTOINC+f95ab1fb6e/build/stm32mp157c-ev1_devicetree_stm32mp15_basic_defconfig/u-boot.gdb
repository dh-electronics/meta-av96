target remote 127.0.0.1:3334
file u-boot
load
restore u-boot-dtb.bin binary 0xc0100000
thbreak relocate_code
c
set $offset = ((gd_t *)$r9)->relocaddr
symbol-file
add-symbol-file u-boot $offset
thbreak main.c:57
c
set *((int *)$r4)=1
c

