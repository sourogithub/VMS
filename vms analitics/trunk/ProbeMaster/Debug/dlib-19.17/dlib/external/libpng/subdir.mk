################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../dlib-19.17/dlib/external/libpng/png.c \
../dlib-19.17/dlib/external/libpng/pngerror.c \
../dlib-19.17/dlib/external/libpng/pngget.c \
../dlib-19.17/dlib/external/libpng/pngmem.c \
../dlib-19.17/dlib/external/libpng/pngpread.c \
../dlib-19.17/dlib/external/libpng/pngread.c \
../dlib-19.17/dlib/external/libpng/pngrio.c \
../dlib-19.17/dlib/external/libpng/pngrtran.c \
../dlib-19.17/dlib/external/libpng/pngrutil.c \
../dlib-19.17/dlib/external/libpng/pngset.c \
../dlib-19.17/dlib/external/libpng/pngtrans.c \
../dlib-19.17/dlib/external/libpng/pngwio.c \
../dlib-19.17/dlib/external/libpng/pngwrite.c \
../dlib-19.17/dlib/external/libpng/pngwtran.c \
../dlib-19.17/dlib/external/libpng/pngwutil.c 

OBJS += \
./dlib-19.17/dlib/external/libpng/png.o \
./dlib-19.17/dlib/external/libpng/pngerror.o \
./dlib-19.17/dlib/external/libpng/pngget.o \
./dlib-19.17/dlib/external/libpng/pngmem.o \
./dlib-19.17/dlib/external/libpng/pngpread.o \
./dlib-19.17/dlib/external/libpng/pngread.o \
./dlib-19.17/dlib/external/libpng/pngrio.o \
./dlib-19.17/dlib/external/libpng/pngrtran.o \
./dlib-19.17/dlib/external/libpng/pngrutil.o \
./dlib-19.17/dlib/external/libpng/pngset.o \
./dlib-19.17/dlib/external/libpng/pngtrans.o \
./dlib-19.17/dlib/external/libpng/pngwio.o \
./dlib-19.17/dlib/external/libpng/pngwrite.o \
./dlib-19.17/dlib/external/libpng/pngwtran.o \
./dlib-19.17/dlib/external/libpng/pngwutil.o 

C_DEPS += \
./dlib-19.17/dlib/external/libpng/png.d \
./dlib-19.17/dlib/external/libpng/pngerror.d \
./dlib-19.17/dlib/external/libpng/pngget.d \
./dlib-19.17/dlib/external/libpng/pngmem.d \
./dlib-19.17/dlib/external/libpng/pngpread.d \
./dlib-19.17/dlib/external/libpng/pngread.d \
./dlib-19.17/dlib/external/libpng/pngrio.d \
./dlib-19.17/dlib/external/libpng/pngrtran.d \
./dlib-19.17/dlib/external/libpng/pngrutil.d \
./dlib-19.17/dlib/external/libpng/pngset.d \
./dlib-19.17/dlib/external/libpng/pngtrans.d \
./dlib-19.17/dlib/external/libpng/pngwio.d \
./dlib-19.17/dlib/external/libpng/pngwrite.d \
./dlib-19.17/dlib/external/libpng/pngwtran.d \
./dlib-19.17/dlib/external/libpng/pngwutil.d 


# Each subdirectory must supply rules for building sources it contributes
dlib-19.17/dlib/external/libpng/%.o: ../dlib-19.17/dlib/external/libpng/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


