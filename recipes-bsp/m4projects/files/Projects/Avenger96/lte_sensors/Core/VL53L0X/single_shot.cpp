#include "VL53L0X.hpp"
#include "stm32mp1xx_hal.h"

static VL53L0X sensor;

/**
 * read distance value from time-of-flight sensor
 */
extern "C" int read_tof( uint16_t *p_dst ) {
	int ret = -1;
	int retry = 3;

	if ( p_dst != NULL ) {
		try {
			if ( !sensor.initialized ) {
				/**
				 * initialize with default values
				 * addr: VL53L0X_ADDRESS_DEFAULT (0x29)
				 */
				sensor.initialize();
				sensor.setTimeout(200);
			}

			/**
			 * sometimes initialization fails at 1st attempt
			 * so we do it in a retry-loop
			 */
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



