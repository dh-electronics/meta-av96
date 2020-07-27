#include <stdint.h>

#ifndef __SENS_ADUX1020
#define __SENS_ADUX1020

#define ADUX1020_I2C_ADDRESS 0x64

typedef struct {
	uint16_t x1;
	uint16_t y1;
	uint16_t x2;
	uint16_t y2;
	uint16_t i;
	uint16_t x;
	uint16_t y;
	uint16_t rezerv;
} adux1020_data;

#ifdef __cplusplus
 extern "C" {
#endif

int adux1020_read( adux1020_data *p_data );

#ifdef __cplusplus
}
#endif

#endif /* __SENS_ADUX1020 */

