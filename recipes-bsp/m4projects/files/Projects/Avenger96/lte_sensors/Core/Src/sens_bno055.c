#include "main.h"
#include "sens_bno055.h"

#define BNO055_I2C_ADDRESS   0x28

#define BNO055_REG_OPR_MODE  0x3d
#define BNO055_OPR_MODE_NDOF 0x0c


static uint8_t init_array[] = { BNO055_REG_OPR_MODE, BNO055_OPR_MODE_NDOF };
static int initted = 0;


static int bno055_init( void )
{
	int ret;

	ret = i2c_write( BNO055_I2C_ADDRESS, init_array, sizeof( init_array ) );
	if ( !ret ) {
		HAL_Delay( 20 );
		ret = i2c_write( BNO055_I2C_ADDRESS, init_array, sizeof( init_array ) );
	}

	if ( !ret )
		HAL_Delay( 20 );

	return ret;
}

static int read_buf( uint8_t addr, uint8_t *p_buf, int len )
{
	int ret;

	ret = i2c_write( BNO055_I2C_ADDRESS, &addr, 1 );
	if ( !ret )
		ret = i2c_read( BNO055_I2C_ADDRESS, p_buf, len );

	return ret;
}

int bno055_read( bno055_data *p_data ) {
	int ret = 0;

	if ( !initted ) {
		ret = bno055_init( );
		if (!ret)
			initted = 1;
	}

	if ( p_data == NULL )
		ret = 1;

	if ( !ret )
		ret = read_buf( 0x08, (uint8_t *)p_data, sizeof( bno055_data ) );

	return ret;
}
