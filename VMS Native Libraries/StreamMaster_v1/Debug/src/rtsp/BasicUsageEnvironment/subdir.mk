################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/rtsp/BasicUsageEnvironment/BasicHashTable.cpp \
../src/rtsp/BasicUsageEnvironment/BasicTaskScheduler.cpp \
../src/rtsp/BasicUsageEnvironment/BasicTaskScheduler0.cpp \
../src/rtsp/BasicUsageEnvironment/BasicUsageEnvironment.cpp \
../src/rtsp/BasicUsageEnvironment/BasicUsageEnvironment0.cpp \
../src/rtsp/BasicUsageEnvironment/DelayQueue.cpp 

OBJS += \
./src/rtsp/BasicUsageEnvironment/BasicHashTable.o \
./src/rtsp/BasicUsageEnvironment/BasicTaskScheduler.o \
./src/rtsp/BasicUsageEnvironment/BasicTaskScheduler0.o \
./src/rtsp/BasicUsageEnvironment/BasicUsageEnvironment.o \
./src/rtsp/BasicUsageEnvironment/BasicUsageEnvironment0.o \
./src/rtsp/BasicUsageEnvironment/DelayQueue.o 

CPP_DEPS += \
./src/rtsp/BasicUsageEnvironment/BasicHashTable.d \
./src/rtsp/BasicUsageEnvironment/BasicTaskScheduler.d \
./src/rtsp/BasicUsageEnvironment/BasicTaskScheduler0.d \
./src/rtsp/BasicUsageEnvironment/BasicUsageEnvironment.d \
./src/rtsp/BasicUsageEnvironment/BasicUsageEnvironment0.d \
./src/rtsp/BasicUsageEnvironment/DelayQueue.d 


# Each subdirectory must supply rules for building sources it contributes
src/rtsp/BasicUsageEnvironment/%.o: ../src/rtsp/BasicUsageEnvironment/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -I"/home/lycan/eclipse-workspace/StreamMaster_v1/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/BasicUsageEnvironment/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/UsageEnvironment/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/rtspClient/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/groupsock/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/liveMedia/include" -I/usr/lib/jvm/java-11-openjdk-amd64/include/linux/ -I/usr/lib/jvm/java-11-openjdk-amd64/include/ -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -pthread -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


