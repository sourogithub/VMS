################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../Lib/dlib/threads/async.cpp \
../Lib/dlib/threads/multithreaded_object_extension.cpp \
../Lib/dlib/threads/thread_pool_extension.cpp \
../Lib/dlib/threads/threaded_object_extension.cpp \
../Lib/dlib/threads/threads_kernel_1.cpp \
../Lib/dlib/threads/threads_kernel_2.cpp \
../Lib/dlib/threads/threads_kernel_shared.cpp 

OBJS += \
./Lib/dlib/threads/async.o \
./Lib/dlib/threads/multithreaded_object_extension.o \
./Lib/dlib/threads/thread_pool_extension.o \
./Lib/dlib/threads/threaded_object_extension.o \
./Lib/dlib/threads/threads_kernel_1.o \
./Lib/dlib/threads/threads_kernel_2.o \
./Lib/dlib/threads/threads_kernel_shared.o 

CPP_DEPS += \
./Lib/dlib/threads/async.d \
./Lib/dlib/threads/multithreaded_object_extension.d \
./Lib/dlib/threads/thread_pool_extension.d \
./Lib/dlib/threads/threaded_object_extension.d \
./Lib/dlib/threads/threads_kernel_1.d \
./Lib/dlib/threads/threads_kernel_2.d \
./Lib/dlib/threads/threads_kernel_shared.d 


# Each subdirectory must supply rules for building sources it contributes
Lib/dlib/threads/%.o: ../Lib/dlib/threads/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -std=c++0x -DDLIB_JPEG_SUPPORT -DDLIB_PNG_SUPPORT -I"/home/abc/eclipse-workspace/ProbeMaster/include" -I"/home/abc/eclipse-workspace/ProbeMaster/dlib-19.17/dlib/all" -I"/home/abc/eclipse-workspace/ProbeMaster/Lib/dlib/all" -Iusr/local/include/opencv2/core/ -I/usr/lib/jvm/java-8-oracle/include/ -I/usr/local/lib -I/usr/local/include -I/usr/local/include/opencv -I/usr/lib/jvm/java-8-oracle/include/linux/ -I/usr/lib/jvm/java-11-openjdk-amd64/include -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


