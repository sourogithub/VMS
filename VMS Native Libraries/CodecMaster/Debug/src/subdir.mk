################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/CodecMaster.cpp \
../src/CodecMasterJni.cpp \
../src/api_cpp.cpp 

OBJS += \
./src/CodecMaster.o \
./src/CodecMasterJni.o \
./src/api_cpp.o 

CPP_DEPS += \
./src/CodecMaster.d \
./src/CodecMasterJni.d \
./src/api_cpp.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -I"/home/lycan/eclipse-workspace/CodecMaster/include" -I/usr/lib/jvm/java-11-openjdk-amd64/include/ -I/usr/lib/jvm/java-11-openjdk-amd64/include/linux/ -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -pthread -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


