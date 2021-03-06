#ifndef HG_APPS
#define HG_APPS

#include <stdio.h>
#include "fsl_os_abstraction.h"
#include "host_mcu_interface.h"
#include "HEXIWEAR_types.h"
#include "menu_types.h"
#include "menu_driver.h"
#include "sensor_driver.h"
#include "OLED_driver.h"

#include "apps_resources.h"
#include "error.h"

#define SCREEN_BYTE_SIZE ( OLED_SCREEN_WIDTH * OLED_SCREEN_HEIGHT * OLED_BYTES_PER_PIXEL )

#include "watch.h"

typedef enum name
{
  APPS_STATUS_SUCCESS,
  APPS_STATUS_ERROR

} apps_status_t;

typedef struct
{
	oled_dynamic_area_t  dynamicArea;
	const uint8_t       *img;
} apps_image_t;

typedef struct
{
	oled_dynamic_area_t    dynamicArea;
	oled_text_properties_t textProperties;
	uint8_t               *caption;
} apps_label_t;

void apps_InitLabel(apps_label_t *label);
void apps_DrawLabel(apps_label_t *label);
void apps_InitImage(apps_image_t *image);
void apps_DrawImage(apps_image_t *image);

void apps_Register( sensor_packet_t packetToReceive );
void apps_Unregister( sensor_packet_t packetToReceive );

#endif
