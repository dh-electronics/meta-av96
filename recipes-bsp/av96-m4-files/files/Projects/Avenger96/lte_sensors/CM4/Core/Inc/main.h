#ifndef __MAIN_H
#define __MAIN_H

#include "stm32mp1xx_hal.h"
#include "lock_resource.h"

#define COUNTOF(__BUFFER__)   (sizeof(__BUFFER__) / sizeof(*(__BUFFER__)))

#define DEFAULT_IRQ_PRIO      1U
#define I2C_ADDRESS ( 0x29 << 1 )


#define TXBUFFERSIZE                      (COUNTOF(aTxBuffer) - 1)
#define RXBUFFERSIZE                      TXBUFFERSIZE


#ifdef __cplusplus
 extern "C" {
#endif
#ifdef __cplusplus
}
#endif

#endif /* __MAIN_H */

