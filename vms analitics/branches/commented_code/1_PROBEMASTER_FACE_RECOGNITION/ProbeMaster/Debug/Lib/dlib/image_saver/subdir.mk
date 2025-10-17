################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../Lib/dlib/image_saver/save_jpeg.cpp \
../Lib/dlib/image_saver/save_png.cpp 

OBJS += \
./Lib/dlib/image_saver/save_jpeg.o \
./Lib/dlib/image_saver/save_png.o 

CPP_DEPS += \
./Lib/dlib/image_saver/save_jpeg.d \
./Lib/dlib/image_saver/save_png.d 


# Each subdirectory must supply rules for building sources it contributes
Lib/dlib/image_saver/%.o: ../Lib/dlib/image_saver/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -std=c++0x -DDLIB_JPEG_SUPPORT -DDLIB_PNG_SUPPORT -I"/home/abc/eclipse-workspace/ProbeMaster/include" -I"/home/abc/eclipse-workspace/ProbeMaster/dlib-19.17/dlib/all" -I"/home/abc/eclipse-workspace/ProbeMaster/Lib/dlib/all" -Iusr/local/include/opencv2/core/ -I/usr/lib/jvm/java-8-oracle/include/ -I/usr/local/lib -I/usr/local/include -I/usr/local/include/opencv -I/usr/lib/jvm/java-8-oracle/include/linux/ -I/usr/lib/jvm/java-11-openjdk-amd64/include -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


