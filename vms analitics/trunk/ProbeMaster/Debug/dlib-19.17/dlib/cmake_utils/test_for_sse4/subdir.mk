################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../dlib-19.17/dlib/cmake_utils/test_for_sse4/sse4_test.cpp \
../dlib-19.17/dlib/cmake_utils/test_for_sse4/this_file_doesnt_compile.cpp 

OBJS += \
./dlib-19.17/dlib/cmake_utils/test_for_sse4/sse4_test.o \
./dlib-19.17/dlib/cmake_utils/test_for_sse4/this_file_doesnt_compile.o 

CPP_DEPS += \
./dlib-19.17/dlib/cmake_utils/test_for_sse4/sse4_test.d \
./dlib-19.17/dlib/cmake_utils/test_for_sse4/this_file_doesnt_compile.d 


# Each subdirectory must supply rules for building sources it contributes
dlib-19.17/dlib/cmake_utils/test_for_sse4/%.o: ../dlib-19.17/dlib/cmake_utils/test_for_sse4/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -std=c++0x -DDLIB_JPEG_SUPPORT -DDLIB_PNG_SUPPORT -I"/home/abc/eclipse-workspace/ProbeMaster/include" -I"/home/abc/eclipse-workspace/ProbeMaster/dlib-19.17/dlib/all" -Iusr/local/include/opencv2/core/ -I/usr/lib/jvm/java-8-oracle/include/ -I/usr/local/lib -I/usr/local/include -I/usr/local/include/opencv -I/usr/lib/jvm/java-8-oracle/include/linux/ -I/usr/lib/jvm/java-11-openjdk-amd64/include -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


