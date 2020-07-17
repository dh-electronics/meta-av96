/*
 * single_shot.cpp
 *
 *  Created on: Jul 16, 2020
 *      Author: bo19
 */

#include "VL53L0X.hpp"
#include "stm32mp1xx_hal.h"

static VL53L0X sensor;

extern "C" int single_shot( uint16_t *p_dst ) {
	int ret = -1;
	int retry = 3;

	if ( p_dst != NULL ) {
		try {
			if ( !sensor.initialized ) {
				sensor.initialize();
				sensor.setTimeout(200);
			}

			while ( retry-- > 0 ) {
				*p_dst = sensor.readRangeSingleMillimeters();
				if ( !sensor.timeoutOccurred() ) {
					ret = 0;
					break;
				}
				HAL_Delay( 100 );
			}
		}
		catch (...)
		{
			ret = -1;
		}
	}

	return ret;
}



