################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/rtsp/groupsock/GroupEId.cpp \
../src/rtsp/groupsock/Groupsock.cpp \
../src/rtsp/groupsock/GroupsockHelper.cpp \
../src/rtsp/groupsock/IOHandlers.cpp \
../src/rtsp/groupsock/NetAddress.cpp \
../src/rtsp/groupsock/NetInterface.cpp \
../src/rtsp/groupsock/inet.cpp 

OBJS += \
./src/rtsp/groupsock/GroupEId.o \
./src/rtsp/groupsock/Groupsock.o \
./src/rtsp/groupsock/GroupsockHelper.o \
./src/rtsp/groupsock/IOHandlers.o \
./src/rtsp/groupsock/NetAddress.o \
./src/rtsp/groupsock/NetInterface.o \
./src/rtsp/groupsock/inet.o 

CPP_DEPS += \
./src/rtsp/groupsock/GroupEId.d \
./src/rtsp/groupsock/Groupsock.d \
./src/rtsp/groupsock/GroupsockHelper.d \
./src/rtsp/groupsock/IOHandlers.d \
./src/rtsp/groupsock/NetAddress.d \
./src/rtsp/groupsock/NetInterface.d \
./src/rtsp/groupsock/inet.d 


# Each subdirectory must supply rules for building sources it contributes
src/rtsp/groupsock/%.o: ../src/rtsp/groupsock/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -I"/home/lycan/eclipse-workspace/StreamMaster_v1/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/BasicUsageEnvironment/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/UsageEnvironment/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/rtspClient/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/groupsock/include" -I"/home/lycan/eclipse-workspace/StreamMaster_v1/src/rtsp/liveMedia/include" -I/usr/lib/jvm/java-11-openjdk-amd64/include/linux/ -I/usr/lib/jvm/java-11-openjdk-amd64/include/ -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -pthread -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


