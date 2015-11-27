################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../drivers/OLED/src/OLED_defs.c \
../drivers/OLED/src/OLED_driver.c 

OBJS += \
./drivers/OLED/src/OLED_defs.o \
./drivers/OLED/src/OLED_driver.o 

C_DEPS += \
./drivers/OLED/src/OLED_defs.d \
./drivers/OLED/src/OLED_driver.d 


# Each subdirectory must supply rules for building sources it contributes
drivers/OLED/src/%.o: ../drivers/OLED/src/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross ARM C Compiler'
	arm-none-eabi-gcc -mcpu=cortex-m4 -mthumb -mfloat-abi=hard -mfpu=fpv4-sp-d16 -O0 -fmessage-length=0 -fsigned-char -ffunction-sections -fdata-sections -Wall  -g3 -D"FSL_OSA_BM_TIMER_CONFIG=2" -D"CPU_MK64FN1M0VDC12" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/SDK/platform/hal/inc" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/SDK/platform/hal/src/sim/MK64F12" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/SDK/platform/system/src/clock/MK64F12" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/SDK/platform/drivers/inc" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/menu/inc" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/drivers/FLASH/inc" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/drivers/OLED/inc" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/drivers/power/inc" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/drivers/SPI/inc" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/exceptions/inc/" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/SDK/platform/system/inc" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/SDK/platform/osa/inc" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/SDK/platform/CMSIS/Include" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/SDK/platform/devices" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/SDK/platform/devices/MK64F12/include" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/SDK/platform/devices/MK64F12/startup" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/Generated_Code/SDK/platform/devices/MK64F12/startup" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/Sources" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/Generated_Code" -I"D:/Marko/tasks/freescale/projects/HEXIWEAR_OLED/SDK/platform/system/src/power" -std=c99 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


