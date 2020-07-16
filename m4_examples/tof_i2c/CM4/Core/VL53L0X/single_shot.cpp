/*
 * single_shot.cpp
 *
 *  Created on: Jul 16, 2020
 *      Author: bo19
 */

#include "VL53L0X.hpp"

extern "C" int single_shot( uint16_t *p_dst ) {
	int ret = -1;
	VL53L0X sensor;

	if ( p_dst != NULL ) {
		try {
			sensor.initialize();
			sensor.setTimeout(200);
			*p_dst = sensor.readRangeSingleMillimeters();
			if ( !sensor.timeoutOccurred() )
				ret = 0;
		}
		catch (...)
		{
			ret = -1;
		}
	}

	return ret;
}



