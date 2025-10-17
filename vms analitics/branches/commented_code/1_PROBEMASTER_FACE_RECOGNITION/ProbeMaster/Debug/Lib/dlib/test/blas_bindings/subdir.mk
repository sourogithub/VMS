################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../Lib/dlib/test/blas_bindings/blas_bindings_dot.cpp \
../Lib/dlib/test/blas_bindings/blas_bindings_gemm.cpp \
../Lib/dlib/test/blas_bindings/blas_bindings_gemv.cpp \
../Lib/dlib/test/blas_bindings/blas_bindings_ger.cpp \
../Lib/dlib/test/blas_bindings/blas_bindings_scal_axpy.cpp \
../Lib/dlib/test/blas_bindings/vector.cpp 

OBJS += \
./Lib/dlib/test/blas_bindings/blas_bindings_dot.o \
./Lib/dlib/test/blas_bindings/blas_bindings_gemm.o \
./Lib/dlib/test/blas_bindings/blas_bindings_gemv.o \
./Lib/dlib/test/blas_bindings/blas_bindings_ger.o \
./Lib/dlib/test/blas_bindings/blas_bindings_scal_axpy.o \
./Lib/dlib/test/blas_bindings/vector.o 

CPP_DEPS += \
./Lib/dlib/test/blas_bindings/blas_bindings_dot.d \
./Lib/dlib/test/blas_bindings/blas_bindings_gemm.d \
./Lib/dlib/test/blas_bindings/blas_bindings_gemv.d \
./Lib/dlib/test/blas_bindings/blas_bindings_ger.d \
./Lib/dlib/test/blas_bindings/blas_bindings_scal_axpy.d \
./Lib/dlib/test/blas_bindings/vector.d 


# Each subdirectory must supply rules for building sources it contributes
Lib/dlib/test/blas_bindings/%.o: ../Lib/dlib/test/blas_bindings/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -std=c++0x -DDLIB_JPEG_SUPPORT -DDLIB_PNG_SUPPORT -I"/home/abc/eclipse-workspace/ProbeMaster/include" -I"/home/abc/eclipse-workspace/ProbeMaster/dlib-19.17/dlib/all" -I"/home/abc/eclipse-workspace/ProbeMaster/Lib/dlib/all" -Iusr/local/include/opencv2/core/ -I/usr/lib/jvm/java-8-oracle/include/ -I/usr/local/lib -I/usr/local/include -I/usr/local/include/opencv -I/usr/lib/jvm/java-8-oracle/include/linux/ -I/usr/lib/jvm/java-11-openjdk-amd64/include -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


