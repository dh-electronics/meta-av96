target remote 127.0.0.1:3334
symbol-file u-boot-spl
thbreak arch/arm/mach-stm32mp/spl.c:147
c
set var a=1

