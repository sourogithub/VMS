################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../dlib-19.17/dlib/external/zlib/adler32.c \
../dlib-19.17/dlib/external/zlib/compress.c \
../dlib-19.17/dlib/external/zlib/crc32.c \
../dlib-19.17/dlib/external/zlib/deflate.c \
../dlib-19.17/dlib/external/zlib/gzclose.c \
../dlib-19.17/dlib/external/zlib/gzlib.c \
../dlib-19.17/dlib/external/zlib/gzread.c \
../dlib-19.17/dlib/external/zlib/gzwrite.c \
../dlib-19.17/dlib/external/zlib/infback.c \
../dlib-19.17/dlib/external/zlib/inffast.c \
../dlib-19.17/dlib/external/zlib/inflate.c \
../dlib-19.17/dlib/external/zlib/inftrees.c \
../dlib-19.17/dlib/external/zlib/trees.c \
../dlib-19.17/dlib/external/zlib/uncompr.c \
../dlib-19.17/dlib/external/zlib/zutil.c 

OBJS += \
./dlib-19.17/dlib/external/zlib/adler32.o \
./dlib-19.17/dlib/external/zlib/compress.o \
./dlib-19.17/dlib/external/zlib/crc32.o \
./dlib-19.17/dlib/external/zlib/deflate.o \
./dlib-19.17/dlib/external/zlib/gzclose.o \
./dlib-19.17/dlib/external/zlib/gzlib.o \
./dlib-19.17/dlib/external/zlib/gzread.o \
./dlib-19.17/dlib/external/zlib/gzwrite.o \
./dlib-19.17/dlib/external/zlib/infback.o \
./dlib-19.17/dlib/external/zlib/inffast.o \
./dlib-19.17/dlib/external/zlib/inflate.o \
./dlib-19.17/dlib/external/zlib/inftrees.o \
./dlib-19.17/dlib/external/zlib/trees.o \
./dlib-19.17/dlib/external/zlib/uncompr.o \
./dlib-19.17/dlib/external/zlib/zutil.o 

C_DEPS += \
./dlib-19.17/dlib/external/zlib/adler32.d \
./dlib-19.17/dlib/external/zlib/compress.d \
./dlib-19.17/dlib/external/zlib/crc32.d \
./dlib-19.17/dlib/external/zlib/deflate.d \
./dlib-19.17/dlib/external/zlib/gzclose.d \
./dlib-19.17/dlib/external/zlib/gzlib.d \
./dlib-19.17/dlib/external/zlib/gzread.d \
./dlib-19.17/dlib/external/zlib/gzwrite.d \
./dlib-19.17/dlib/external/zlib/infback.d \
./dlib-19.17/dlib/external/zlib/inffast.d \
./dlib-19.17/dlib/external/zlib/inflate.d \
./dlib-19.17/dlib/external/zlib/inftrees.d \
./dlib-19.17/dlib/external/zlib/trees.d \
./dlib-19.17/dlib/external/zlib/uncompr.d \
./dlib-19.17/dlib/external/zlib/zutil.d 


# Each subdirectory must supply rules for building sources it contributes
dlib-19.17/dlib/external/zlib/%.o: ../dlib-19.17/dlib/external/zlib/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


