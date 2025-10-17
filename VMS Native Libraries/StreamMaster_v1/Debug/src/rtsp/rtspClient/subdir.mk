################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/rtsp/rtspClient/testRTSPClient.cpp 

OBJS += \
./src/rtsp/rtspClient/testRTSPClient.o 

CPP_DEPS += \
./src/rtsp/rtspClient/testRTSPClient.d 


# Each subdirectory must supply rules for building sources it contributes
src/rtsp/rtspClient/%.o: ../src/rtsp/rtspClient/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -I"/home/lycan/eclipse-workspace/StreamMaster_v1/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/BasicUsageEnvironment/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/UsageEnvironment/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/rtspClient/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/groupsock/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/liveMedia/include" -I/usr/lib/jvm/java-11-openjdk-amd64/include/linux/ -I/usr/lib/jvm/java-11-openjdk-amd64/include/ -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -pthread -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


