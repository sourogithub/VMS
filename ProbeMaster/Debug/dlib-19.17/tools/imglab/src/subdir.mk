################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../dlib-19.17/tools/imglab/src/cluster.cpp \
../dlib-19.17/tools/imglab/src/common.cpp \
../dlib-19.17/tools/imglab/src/convert_idl.cpp \
../dlib-19.17/tools/imglab/src/convert_pascal_v1.cpp \
../dlib-19.17/tools/imglab/src/convert_pascal_xml.cpp \
../dlib-19.17/tools/imglab/src/flip_dataset.cpp \
../dlib-19.17/tools/imglab/src/main.cpp \
../dlib-19.17/tools/imglab/src/metadata_editor.cpp 

OBJS += \
./dlib-19.17/tools/imglab/src/cluster.o \
./dlib-19.17/tools/imglab/src/common.o \
./dlib-19.17/tools/imglab/src/convert_idl.o \
./dlib-19.17/tools/imglab/src/convert_pascal_v1.o \
./dlib-19.17/tools/imglab/src/convert_pascal_xml.o \
./dlib-19.17/tools/imglab/src/flip_dataset.o \
./dlib-19.17/tools/imglab/src/main.o \
./dlib-19.17/tools/imglab/src/metadata_editor.o 

CPP_DEPS += \
./dlib-19.17/tools/imglab/src/cluster.d \
./dlib-19.17/tools/imglab/src/common.d \
./dlib-19.17/tools/imglab/src/convert_idl.d \
./dlib-19.17/tools/imglab/src/convert_pascal_v1.d \
./dlib-19.17/tools/imglab/src/convert_pascal_xml.d \
./dlib-19.17/tools/imglab/src/flip_dataset.d \
./dlib-19.17/tools/imglab/src/main.d \
./dlib-19.17/tools/imglab/src/metadata_editor.d 


# Each subdirectory must supply rules for building sources it contributes
dlib-19.17/tools/imglab/src/%.o: ../dlib-19.17/tools/imglab/src/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -std=c++0x -DDLIB_JPEG_SUPPORT -DDLIB_PNG_SUPPORT -I"/home/abc/eclipse-workspace/ProbeMaster/include" -I"/home/abc/eclipse-workspace/ProbeMaster/dlib-19.17/dlib/all" -Iusr/local/include/opencv2/core/ -I/usr/lib/jvm/java-8-oracle/include/ -I/usr/local/lib -I/usr/local/include -I/usr/local/include/opencv -I/usr/lib/jvm/java-8-oracle/include/linux/ -I/usr/lib/jvm/java-11-openjdk-amd64/include -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


