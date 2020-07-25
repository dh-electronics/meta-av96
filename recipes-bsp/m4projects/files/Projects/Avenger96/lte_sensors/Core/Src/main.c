#include "main.h"
#include "openamp.h"

#include "sens_adux1020.h"


#define MAX_BUFFER_SIZE RPMSG_BUFFER_SIZE

#define ADT7410_ADDR   0x48


IPCC_HandleTypeDef hipcc;
I2C_HandleTypeDef hi2c1;
VIRT_UART_HandleTypeDef virtUART0;

__IO FlagStatus VirtUart0RxMsg = RESET;
uint8_t VirtUart0ChannelBuffRx[MAX_BUFFER_SIZE];
uint16_t VirtUart0ChannelRxSize = 0;

char tx_buf[ 256 ];


void Error_Handler(void);
void SystemClock_Config(void);
static void MX_GPIO_Init(void);
static void MX_I2C1_Init(void);

static void MX_IPCC_Init(void);
int MX_OPENAMP_Init(int RPMsgRole, rpmsg_ns_bind_cb ns_bind_cb);
void virt_UART0_cb0(VIRT_UART_HandleTypeDef *huart);

/* external function in single_shot.cpp */
int read_tof( uint16_t *p_dst );


/**
 * polling version of i2c_write() - it waits in an endless loop for
 * the transfer to complete
 */
int i2c_write( uint16_t addr, uint8_t *p_buf, int len )
{
	int ret = 0;

	if(HAL_I2C_Master_Transmit_IT(&hi2c1, addr << 1, p_buf, len)!= HAL_OK)
	{
		Error_Handler();
	}

	while (HAL_I2C_GetState(&hi2c1) != HAL_I2C_STATE_READY)
	{
	}

	/* see if target device ACKed */
	if (HAL_I2C_GetError(&hi2c1) == HAL_I2C_ERROR_AF)
		ret = -1;

	return ret;
}

/**
 * polling version of i2c_read() - it waits in an endless loop for
 * the transfer to complete
 */
int i2c_read( uint16_t addr, uint8_t *p_buf, int len )
{
	int ret = 0;

	if(HAL_I2C_Master_Receive_IT(&hi2c1, addr << 1, p_buf, len)!= HAL_OK)
	{
		Error_Handler();
	}

	while (HAL_I2C_GetState(&hi2c1) != HAL_I2C_STATE_READY)
	{
	}

	/* see if target device ACKed */
	if (HAL_I2C_GetError(&hi2c1) == HAL_I2C_ERROR_AF)
		ret = -1;

	return ret;
}


uint8_t adt7410_init[] = { 0x03, 0x80 };

/**
 * read temperature value from ADT7410 sensor
 */
int read_temp( uint16_t *p_dst )
{
	static int temp_initialized = 0;
	int ret = 0;
	uint8_t resp[ 2 ];

	if ( p_dst == NULL )
		ret = 1;

	if ( !ret && !temp_initialized ) {
		ret = i2c_write( ADT7410_ADDR, adt7410_init, 2 );
		if ( !ret )
			temp_initialized = 1;
		HAL_Delay( 100 );
	}

	if ( !ret ) {
		resp[ 0 ] = 0;
		ret = i2c_write( ADT7410_ADDR, resp, 1 );
	}

	if ( !ret ) {
		ret = i2c_read( ADT7410_ADDR, resp, 2 );
		if ( !ret )
			*p_dst = ( (uint16_t )resp[0] << 8 ) + resp[1];
	}

	return ret;
}


enum SEND_STATE {
        IDLE = 0,
        STARTED,
        STOPPED,
        EXIT
};

/* send_state is our main state variable */
static int send_state;

/* dirty trick to emulate breakpoint in main() */
static volatile int run = 0;


int main(void)
{
  uint16_t dst_mm, temp;
  adux1020_data  proxy_data;

  /* Reset of all peripherals, Initialize the Systick. */
  HAL_Init();

  if(IS_ENGINEERING_BOOT_MODE())
  {
	/* this code requires production mode */
    Error_Handler( );
  }

  while( !run ) {
  }

  __HAL_RCC_HSEM_CLK_ENABLE();

  /* Initialize all configured peripherals */
  MX_GPIO_Init();
  MX_I2C1_Init();

  /* IPCC initialisation */
  MX_IPCC_Init();
  /* OpenAmp initialisation */
  MX_OPENAMP_Init(RPMSG_REMOTE, NULL);

  /* let's set up a single virtual UART */
  /* virtUART0 initialization and cb association*/
  if (VIRT_UART_Init(&virtUART0) != VIRT_UART_OK)
  {
    Error_Handler();
  }

  if (VIRT_UART_RegisterCallback(&virtUART0, VIRT_UART_RXCPLT_CB_ID, virt_UART0_cb0) != VIRT_UART_OK)
  {
    Error_Handler();
  }


  /**
   * now we wait for the host to send a valid command
   * the M4 side can't use the channel while the host didn't initialize it
   */
  while ( send_state == IDLE )
    OPENAMP_check_for_message();

  /**
   * main loop
   */
  while ( send_state != EXIT ) {
	  OPENAMP_check_for_message();

	  /* in "STARTED" state we send out a ToF distance value every 500ms */
	  if ( send_state == STARTED ) {
		  if( adux1020_read( &proxy_data ) )
			  break;

		  if( read_tof( &dst_mm ) )
			  break;

		  if ( read_temp( &temp ) )
			  break;

		  sprintf( tx_buf, "tof:%i temp:%.2f x1:%i y1: %i x2:%i y2:%i i:%i x:%i y:%i\n",
				  (int )dst_mm, (float )temp / 128.0,
				  proxy_data.x1, proxy_data.y1, proxy_data.x2, proxy_data.y2,
				  proxy_data.i, proxy_data.x, proxy_data.y );
		  VIRT_UART_Transmit(&virtUART0, (uint8_t *)tx_buf, strlen(tx_buf) + 1 );
		  HAL_Delay( 500 );
	  }
  }

  return 0;
}

/*Open AMP callbacks*/
/*
 * This function is called in the context of IdleTask inside the function
 * OPENAMP_check_for_message.
 * OpenAMP is not thread safe, so we can't release the semaphore here because
 * FreeRTOS is not able to manage context switching in this situation.
 */
void virt_UART0_cb0(VIRT_UART_HandleTypeDef *huart)
{
  /* let's evaluate the received string */
  if ( !strncmp( (char *)huart->pRxBuffPtr, "START\n", MAX_BUFFER_SIZE - 1 ) ||
		  !strncmp( (char *)huart->pRxBuffPtr, "START", MAX_BUFFER_SIZE - 1 ) )
	  send_state = STARTED;
  if ( !strncmp( (char *)huart->pRxBuffPtr, "STOP\n", MAX_BUFFER_SIZE - 1 ) ||
		  !strncmp( (char *)huart->pRxBuffPtr, "STOP", MAX_BUFFER_SIZE - 1 ) )
	  send_state = STOPPED;
  if ( !strncmp( (char *)huart->pRxBuffPtr, "EXIT\n", MAX_BUFFER_SIZE - 1 ) ||
		  !strncmp( (char *)huart->pRxBuffPtr, "EXIT", MAX_BUFFER_SIZE - 1 ) )
	  send_state = EXIT;
}

/**
  * @brief System Clock Configuration
  * @retval None
  */
void SystemClock_Config(void)
{
  RCC_OscInitTypeDef RCC_OscInitStruct = {0};
  RCC_ClkInitTypeDef RCC_ClkInitStruct = {0};

  /**Configure LSE Drive Capability
  */
  HAL_PWR_EnableBkUpAccess();
  __HAL_RCC_LSEDRIVE_CONFIG(RCC_LSEDRIVE_MEDIUMHIGH);

  /**Initializes the CPU, AHB and APB busses clocks 
  */
  RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSI|RCC_OSCILLATORTYPE_HSE
                |RCC_OSCILLATORTYPE_LSE;
  RCC_OscInitStruct.HSEState = RCC_HSE_ON;
  RCC_OscInitStruct.LSEState = RCC_LSE_ON;
  RCC_OscInitStruct.HSIState = RCC_HSI_ON;
  RCC_OscInitStruct.HSICalibrationValue = 7;
  RCC_OscInitStruct.HSIDivValue = RCC_HSI_DIV1;

  /**PLL1 Config
  */
  RCC_OscInitStruct.PLL.PLLState = RCC_PLL_ON;
  RCC_OscInitStruct.PLL.PLLSource = RCC_PLL12SOURCE_HSE;
  RCC_OscInitStruct.PLL.PLLM = 2;
  RCC_OscInitStruct.PLL.PLLN = 80;
  RCC_OscInitStruct.PLL.PLLP = 0;
  RCC_OscInitStruct.PLL.PLLQ = 0;
  RCC_OscInitStruct.PLL.PLLR = 0;
  RCC_OscInitStruct.PLL.PLLFRACV = 0x800;
  RCC_OscInitStruct.PLL.PLLMODE = RCC_PLL_FRACTIONAL;
  RCC_OscInitStruct.PLL.RPDFN_DIS = RCC_RPDFN_DIS_DISABLED;
  RCC_OscInitStruct.PLL.TPDFN_DIS = RCC_TPDFN_DIS_DISABLED;

    /**PLL2 Config
    */
  RCC_OscInitStruct.PLL2.PLLState = RCC_PLL_ON;
  RCC_OscInitStruct.PLL2.PLLSource = RCC_PLL12SOURCE_HSE;
  RCC_OscInitStruct.PLL2.PLLM = 2;
  RCC_OscInitStruct.PLL2.PLLN = 65;
  RCC_OscInitStruct.PLL2.PLLP = 1;
  RCC_OscInitStruct.PLL2.PLLQ = 0;
  RCC_OscInitStruct.PLL2.PLLR = 0;
  RCC_OscInitStruct.PLL2.PLLFRACV = 0x1400;
  RCC_OscInitStruct.PLL2.PLLMODE = RCC_PLL_FRACTIONAL;
  RCC_OscInitStruct.PLL2.RPDFN_DIS = RCC_RPDFN_DIS_DISABLED;
  RCC_OscInitStruct.PLL2.TPDFN_DIS = RCC_TPDFN_DIS_DISABLED;

    /**PLL3 Config
    */
  RCC_OscInitStruct.PLL3.PLLState = RCC_PLL_ON;
  RCC_OscInitStruct.PLL3.PLLSource = RCC_PLL3SOURCE_HSE;
  RCC_OscInitStruct.PLL3.PLLM = 1;
  RCC_OscInitStruct.PLL3.PLLN = 33;
  RCC_OscInitStruct.PLL3.PLLP = 1;
  RCC_OscInitStruct.PLL3.PLLQ = 16;
  RCC_OscInitStruct.PLL3.PLLR = 36;
  RCC_OscInitStruct.PLL3.PLLRGE = RCC_PLL3IFRANGE_1;
  RCC_OscInitStruct.PLL3.PLLFRACV = 0x1A04;
  RCC_OscInitStruct.PLL3.PLLMODE = RCC_PLL_FRACTIONAL;
  RCC_OscInitStruct.PLL3.RPDFN_DIS = RCC_RPDFN_DIS_DISABLED;
  RCC_OscInitStruct.PLL3.TPDFN_DIS = RCC_TPDFN_DIS_DISABLED;

    /**PLL4 Config
    */
  RCC_OscInitStruct.PLL4.PLLState = RCC_PLL_ON;
  RCC_OscInitStruct.PLL4.PLLSource = RCC_PLL4SOURCE_HSE;
  RCC_OscInitStruct.PLL4.PLLM = 3;
  RCC_OscInitStruct.PLL4.PLLN = 98;
  RCC_OscInitStruct.PLL4.PLLP = 7;
  RCC_OscInitStruct.PLL4.PLLQ = 7;
  RCC_OscInitStruct.PLL4.PLLR = 7;
  RCC_OscInitStruct.PLL4.PLLRGE = RCC_PLL4IFRANGE_0;
  RCC_OscInitStruct.PLL4.PLLFRACV = 0;
  RCC_OscInitStruct.PLL4.PLLMODE = RCC_PLL_INTEGER;
  RCC_OscInitStruct.PLL4.RPDFN_DIS = RCC_RPDFN_DIS_DISABLED;
  RCC_OscInitStruct.PLL4.TPDFN_DIS = RCC_TPDFN_DIS_DISABLED;

  if (HAL_RCC_OscConfig(&RCC_OscInitStruct) != HAL_OK)
  {
  Error_Handler();
  }
  /**RCC Clock Config
  */
  /* RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_HCLK|RCC_CLOCKTYPE_ACLK
                |RCC_CLOCKTYPE_PCLK1|RCC_CLOCKTYPE_PCLK2
                |RCC_CLOCKTYPE_PCLK3|RCC_CLOCKTYPE_PCLK4
                |RCC_CLOCKTYPE_PCLK5|RCC_CLOCKTYPE_MPU; */
  RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_ACLK
                |RCC_CLOCKTYPE_PCLK1|RCC_CLOCKTYPE_PCLK3
				|RCC_CLOCKTYPE_MPU;
  RCC_ClkInitStruct.MPUInit.MPU_Clock = RCC_MPUSOURCE_PLL1;
  RCC_ClkInitStruct.MPUInit.MPU_Div = RCC_MPU_DIV2;
  RCC_ClkInitStruct.AXISSInit.AXI_Clock = RCC_AXISSOURCE_PLL2;
  RCC_ClkInitStruct.AXISSInit.AXI_Div = RCC_AXI_DIV1;
  RCC_ClkInitStruct.MCUInit.MCU_Clock = RCC_MCUSSOURCE_PLL3;
  RCC_ClkInitStruct.MCUInit.MCU_Div = RCC_MCU_DIV1;
  RCC_ClkInitStruct.APB4_Div = RCC_APB4_DIV2;
  RCC_ClkInitStruct.APB5_Div = RCC_APB5_DIV4;
  RCC_ClkInitStruct.APB1_Div = RCC_APB1_DIV2;
  RCC_ClkInitStruct.APB2_Div = RCC_APB2_DIV2;
  RCC_ClkInitStruct.APB3_Div = RCC_APB3_DIV2;

  if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct) != HAL_OK)
  {
  Error_Handler();
  }

  /**Set the HSE division factor for RTC clock
  */
  __HAL_RCC_RTC_HSEDIV(24);
}

/* I2C1 init function */
static void MX_I2C1_Init(void)
{

  hi2c1.Instance = I2C1;
  hi2c1.Init.Timing = 0x00701313;
  hi2c1.Init.OwnAddress1 = 0;
  hi2c1.Init.AddressingMode = I2C_ADDRESSINGMODE_7BIT;
  hi2c1.Init.DualAddressMode = I2C_DUALADDRESS_DISABLE;
  hi2c1.Init.OwnAddress2 = 0;
  hi2c1.Init.OwnAddress2Masks = I2C_OA2_NOMASK;
  hi2c1.Init.GeneralCallMode = I2C_GENERALCALL_DISABLE;
  hi2c1.Init.NoStretchMode = I2C_NOSTRETCH_DISABLE;
  if (HAL_I2C_Init(&hi2c1) != HAL_OK)
  {
    Error_Handler();
  }

    /**Configure Analogue filter 
    */
  if (HAL_I2CEx_ConfigAnalogFilter(&hi2c1, I2C_ANALOGFILTER_ENABLE) != HAL_OK)
  {
    Error_Handler();
  }

    /**Configure Digital filter 
    */
  if (HAL_I2CEx_ConfigDigitalFilter(&hi2c1, 0) != HAL_OK)
  {
    Error_Handler();
  }

  /**I2C Fast mode Plus enable
  */
  HAL_I2CEx_EnableFastModePlus(I2C_FASTMODEPLUS_I2C1);
}

static void MX_GPIO_Init(void)
{

  /* GPIO Ports Clock Enable */
  __HAL_RCC_GPIOF_CLK_ENABLE();
}

/**
  * @brief IPCC Initialization Function
  */
static void MX_IPCC_Init(void)
{
  hipcc.Instance = IPCC;
  if (HAL_IPCC_Init(&hipcc) != HAL_OK)
  {
    Error_Handler();
  }
}

/**
  * @brief  Tx Transfer completed callback.
  * @param  I2cHandle: I2C handle. 
  * @note   This example shows a simple way to report end of IT Tx transfer, and 
  *         you can add your own implementation. 
  * @retval None
  */
void HAL_I2C_MasterTxCpltCallback(I2C_HandleTypeDef *I2cHandle)
{
  /* Toggle LED7: Transfer in transmission process is correct */
  /* BSP_LED_Toggle(LED7); */
}

/**
  * @brief  Rx Transfer completed callback.
  * @param  I2cHandle: I2C handle
  * @note   This example shows a simple way to report end of IT Rx transfer, and 
  *         you can add your own implementation.
  * @retval None
  */
void HAL_I2C_MasterRxCpltCallback(I2C_HandleTypeDef *I2cHandle)
{
  /* Toggle LED7: Transfer in reception process is correct */
  /* BSP_LED_Toggle(LED7); */
}

/**
  * @brief  I2C error callbacks.
  * @param  I2cHandle: I2C handle
  * @note   This example shows a simple way to report transfer error, and you can
  *         add your own implementation.
  * @retval None
  */
void HAL_I2C_ErrorCallback(I2C_HandleTypeDef *I2cHandle)
{
  /** Error_Handler() function is called when error occurs.
    * 1- When Slave doesn't acknowledge its address, Master restarts communication.
    * 2- When Master doesn't acknowledge the last data transferred, Slave doesn't care in this example.
    */
  if (HAL_I2C_GetError(I2cHandle) != HAL_I2C_ERROR_AF)
  {
    Error_Handler();
  }
}

/**
  * @brief  This function is executed in case of error occurrence.
  * @param  file: The file name as string.
  * @param  line: The line in file as a number.
  * @retval None
  */
void Error_Handler(void)
{
  while(1)
  {    
    HAL_Delay(1000);
  } 
}

