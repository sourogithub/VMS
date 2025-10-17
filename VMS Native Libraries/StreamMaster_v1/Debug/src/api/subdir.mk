################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/api/StreamMaster.cpp \
../src/api/StreamMasterJni.cpp \
../src/api/api_cpp.cpp 

OBJS += \
./src/api/StreamMaster.o \
./src/api/StreamMasterJni.o \
./src/api/api_cpp.o 

CPP_DEPS += \
./src/api/StreamMaster.d \
./src/api/StreamMasterJni.d \
./src/api/api_cpp.d 


# Each subdirectory must supply rules for building sources it contributes
src/api/%.o: ../src/api/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -I"/home/lycan/eclipse-workspace/StreamMaster_v1/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/BasicUsageEnvironment/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/UsageEnvironment/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/rtspClient/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/groupsock/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/liveMedia/include" -I/usr/lib/jvm/java-11-openjdk-amd64/include/linux/ -I/usr/lib/jvm/java-11-openjdk-amd64/include/ -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -pthread -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


