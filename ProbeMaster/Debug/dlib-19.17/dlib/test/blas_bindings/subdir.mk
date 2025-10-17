################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../dlib-19.17/dlib/test/blas_bindings/blas_bindings_dot.cpp \
../dlib-19.17/dlib/test/blas_bindings/blas_bindings_gemm.cpp \
../dlib-19.17/dlib/test/blas_bindings/blas_bindings_gemv.cpp \
../dlib-19.17/dlib/test/blas_bindings/blas_bindings_ger.cpp \
../dlib-19.17/dlib/test/blas_bindings/blas_bindings_scal_axpy.cpp \
../dlib-19.17/dlib/test/blas_bindings/vector.cpp 

OBJS += \
./dlib-19.17/dlib/test/blas_bindings/blas_bindings_dot.o \
./dlib-19.17/dlib/test/blas_bindings/blas_bindings_gemm.o \
./dlib-19.17/dlib/test/blas_bindings/blas_bindings_gemv.o \
./dlib-19.17/dlib/test/blas_bindings/blas_bindings_ger.o \
./dlib-19.17/dlib/test/blas_bindings/blas_bindings_scal_axpy.o \
./dlib-19.17/dlib/test/blas_bindings/vector.o 

CPP_DEPS += \
./dlib-19.17/dlib/test/blas_bindings/blas_bindings_dot.d \
./dlib-19.17/dlib/test/blas_bindings/blas_bindings_gemm.d \
./dlib-19.17/dlib/test/blas_bindings/blas_bindings_gemv.d \
./dlib-19.17/dlib/test/blas_bindings/blas_bindings_ger.d \
./dlib-19.17/dlib/test/blas_bindings/blas_bindings_scal_axpy.d \
./dlib-19.17/dlib/test/blas_bindings/vector.d 


# Each subdirectory must supply rules for building sources it contributes
dlib-19.17/dlib/test/blas_bindings/%.o: ../dlib-19.17/dlib/test/blas_bindings/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -std=c++0x -DDLIB_JPEG_SUPPORT -DDLIB_PNG_SUPPORT -I"/home/abc/eclipse-workspace/ProbeMaster/include" -I"/home/abc/eclipse-workspace/ProbeMaster/dlib-19.17/dlib/all" -Iusr/local/include/opencv2/core/ -I/usr/lib/jvm/java-8-oracle/include/ -I/usr/local/lib -I/usr/local/include -I/usr/local/include/opencv -I/usr/lib/jvm/java-8-oracle/include/linux/ -I/usr/lib/jvm/java-11-openjdk-amd64/include -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


