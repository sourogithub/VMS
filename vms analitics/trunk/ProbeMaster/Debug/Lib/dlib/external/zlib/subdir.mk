################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../Lib/dlib/external/zlib/adler32.c \
../Lib/dlib/external/zlib/compress.c \
../Lib/dlib/external/zlib/crc32.c \
../Lib/dlib/external/zlib/deflate.c \
../Lib/dlib/external/zlib/gzclose.c \
../Lib/dlib/external/zlib/gzlib.c \
../Lib/dlib/external/zlib/gzread.c \
../Lib/dlib/external/zlib/gzwrite.c \
../Lib/dlib/external/zlib/infback.c \
../Lib/dlib/external/zlib/inffast.c \
../Lib/dlib/external/zlib/inflate.c \
../Lib/dlib/external/zlib/inftrees.c \
../Lib/dlib/external/zlib/trees.c \
../Lib/dlib/external/zlib/uncompr.c \
../Lib/dlib/external/zlib/zutil.c 

OBJS += \
./Lib/dlib/external/zlib/adler32.o \
./Lib/dlib/external/zlib/compress.o \
./Lib/dlib/external/zlib/crc32.o \
./Lib/dlib/external/zlib/deflate.o \
./Lib/dlib/external/zlib/gzclose.o \
./Lib/dlib/external/zlib/gzlib.o \
./Lib/dlib/external/zlib/gzread.o \
./Lib/dlib/external/zlib/gzwrite.o \
./Lib/dlib/external/zlib/infback.o \
./Lib/dlib/external/zlib/inffast.o \
./Lib/dlib/external/zlib/inflate.o \
./Lib/dlib/external/zlib/inftrees.o \
./Lib/dlib/external/zlib/trees.o \
./Lib/dlib/external/zlib/uncompr.o \
./Lib/dlib/external/zlib/zutil.o 

C_DEPS += \
./Lib/dlib/external/zlib/adler32.d \
./Lib/dlib/external/zlib/compress.d \
./Lib/dlib/external/zlib/crc32.d \
./Lib/dlib/external/zlib/deflate.d \
./Lib/dlib/external/zlib/gzclose.d \
./Lib/dlib/external/zlib/gzlib.d \
./Lib/dlib/external/zlib/gzread.d \
./Lib/dlib/external/zlib/gzwrite.d \
./Lib/dlib/external/zlib/infback.d \
./Lib/dlib/external/zlib/inffast.d \
./Lib/dlib/external/zlib/inflate.d \
./Lib/dlib/external/zlib/inftrees.d \
./Lib/dlib/external/zlib/trees.d \
./Lib/dlib/external/zlib/uncompr.d \
./Lib/dlib/external/zlib/zutil.d 


# Each subdirectory must supply rules for building sources it contributes
Lib/dlib/external/zlib/%.o: ../Lib/dlib/external/zlib/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


