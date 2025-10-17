################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../Lib/dlib/external/libpng/arm/arm_init.c \
../Lib/dlib/external/libpng/arm/filter_neon_intrinsics.c 

S_UPPER_SRCS += \
../Lib/dlib/external/libpng/arm/filter_neon.S 

OBJS += \
./Lib/dlib/external/libpng/arm/arm_init.o \
./Lib/dlib/external/libpng/arm/filter_neon.o \
./Lib/dlib/external/libpng/arm/filter_neon_intrinsics.o 

C_DEPS += \
./Lib/dlib/external/libpng/arm/arm_init.d \
./Lib/dlib/external/libpng/arm/filter_neon_intrinsics.d 


# Each subdirectory must supply rules for building sources it contributes
Lib/dlib/external/libpng/arm/%.o: ../Lib/dlib/external/libpng/arm/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

Lib/dlib/external/libpng/arm/%.o: ../Lib/dlib/external/libpng/arm/%.S
	@echo 'Building file: $<'
	@echo 'Invoking: GCC Assembler'
	as  -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


