################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CXX_SRCS += \
../dlib-19.17/examples/build/CMakeFiles/feature_tests.cxx 

C_SRCS += \
../dlib-19.17/examples/build/CMakeFiles/feature_tests.c 

CXX_DEPS += \
./dlib-19.17/examples/build/CMakeFiles/feature_tests.d 

OBJS += \
./dlib-19.17/examples/build/CMakeFiles/feature_tests.o 

C_DEPS += \
./dlib-19.17/examples/build/CMakeFiles/feature_tests.d 


# Each subdirectory must supply rules for building sources it contributes
dlib-19.17/examples/build/CMakeFiles/%.o: ../dlib-19.17/examples/build/CMakeFiles/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

dlib-19.17/examples/build/CMakeFiles/%.o: ../dlib-19.17/examples/build/CMakeFiles/%.cxx
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -std=c++0x -DDLIB_JPEG_SUPPORT -DDLIB_PNG_SUPPORT -I"/home/abc/eclipse-workspace/ProbeMaster/include" -I"/home/abc/eclipse-workspace/ProbeMaster/dlib-19.17/dlib/all" -Iusr/local/include/opencv2/core/ -I/usr/lib/jvm/java-8-oracle/include/ -I/usr/local/lib -I/usr/local/include -I/usr/local/include/opencv -I/usr/lib/jvm/java-8-oracle/include/linux/ -I/usr/lib/jvm/java-11-openjdk-amd64/include -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


