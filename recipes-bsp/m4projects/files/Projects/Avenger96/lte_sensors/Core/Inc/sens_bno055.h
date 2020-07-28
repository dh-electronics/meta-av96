#include <stdint.h>

#ifndef __SENS_BNO055
#define __SENS_BNO055

typedef struct {
	int16_t acc_x, acc_y, acc_z;
	int16_t mag_x, mag_y, mag_z;
	int16_t gyr_x, gyr_y, gyr_z;
} bno055_data;

#ifdef __cplusplus
 extern "C" {
#endif

int bno055_read( bno055_data *p_data );

#ifdef __cplusplus
}
#endif

#endif /* __SENS_BNO055 */

