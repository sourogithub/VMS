################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../dlib-19.17/examples/build/CMakeFiles/3.10.2/CompilerIdC/CMakeCCompilerId.c 

OBJS += \
./dlib-19.17/examples/build/CMakeFiles/3.10.2/CompilerIdC/CMakeCCompilerId.o 

C_DEPS += \
./dlib-19.17/examples/build/CMakeFiles/3.10.2/CompilerIdC/CMakeCCompilerId.d 


# Each subdirectory must supply rules for building sources it contributes
dlib-19.17/examples/build/CMakeFiles/3.10.2/CompilerIdC/%.o: ../dlib-19.17/examples/build/CMakeFiles/3.10.2/CompilerIdC/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


