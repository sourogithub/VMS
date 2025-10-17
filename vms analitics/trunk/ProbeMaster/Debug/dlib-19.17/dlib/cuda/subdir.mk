################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../dlib-19.17/dlib/cuda/cpu_dlib.cpp \
../dlib-19.17/dlib/cuda/cublas_dlibapi.cpp \
../dlib-19.17/dlib/cuda/cuda_data_ptr.cpp \
../dlib-19.17/dlib/cuda/cudnn_dlibapi.cpp \
../dlib-19.17/dlib/cuda/curand_dlibapi.cpp \
../dlib-19.17/dlib/cuda/gpu_data.cpp \
../dlib-19.17/dlib/cuda/tensor_tools.cpp 

OBJS += \
./dlib-19.17/dlib/cuda/cpu_dlib.o \
./dlib-19.17/dlib/cuda/cublas_dlibapi.o \
./dlib-19.17/dlib/cuda/cuda_data_ptr.o \
./dlib-19.17/dlib/cuda/cudnn_dlibapi.o \
./dlib-19.17/dlib/cuda/curand_dlibapi.o \
./dlib-19.17/dlib/cuda/gpu_data.o \
./dlib-19.17/dlib/cuda/tensor_tools.o 

CPP_DEPS += \
./dlib-19.17/dlib/cuda/cpu_dlib.d \
./dlib-19.17/dlib/cuda/cublas_dlibapi.d \
./dlib-19.17/dlib/cuda/cuda_data_ptr.d \
./dlib-19.17/dlib/cuda/cudnn_dlibapi.d \
./dlib-19.17/dlib/cuda/curand_dlibapi.d \
./dlib-19.17/dlib/cuda/gpu_data.d \
./dlib-19.17/dlib/cuda/tensor_tools.d 


# Each subdirectory must supply rules for building sources it contributes
dlib-19.17/dlib/cuda/%.o: ../dlib-19.17/dlib/cuda/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -std=c++0x -DDLIB_JPEG_SUPPORT -DDLIB_PNG_SUPPORT -I"/home/abc/eclipse-workspace/ProbeMaster/include" -I"/home/abc/eclipse-workspace/ProbeMaster/dlib-19.17/dlib/all" -Iusr/local/include/opencv2/core/ -I/usr/lib/jvm/java-8-oracle/include/ -I/usr/local/lib -I/usr/local/include -I/usr/local/include/opencv -I/usr/lib/jvm/java-8-oracle/include/linux/ -I/usr/lib/jvm/java-11-openjdk-amd64/include -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


