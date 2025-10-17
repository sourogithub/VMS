################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../Lib/dlib/external/libpng/png.c \
../Lib/dlib/external/libpng/pngerror.c \
../Lib/dlib/external/libpng/pngget.c \
../Lib/dlib/external/libpng/pngmem.c \
../Lib/dlib/external/libpng/pngpread.c \
../Lib/dlib/external/libpng/pngread.c \
../Lib/dlib/external/libpng/pngrio.c \
../Lib/dlib/external/libpng/pngrtran.c \
../Lib/dlib/external/libpng/pngrutil.c \
../Lib/dlib/external/libpng/pngset.c \
../Lib/dlib/external/libpng/pngtrans.c \
../Lib/dlib/external/libpng/pngwio.c \
../Lib/dlib/external/libpng/pngwrite.c \
../Lib/dlib/external/libpng/pngwtran.c \
../Lib/dlib/external/libpng/pngwutil.c 

OBJS += \
./Lib/dlib/external/libpng/png.o \
./Lib/dlib/external/libpng/pngerror.o \
./Lib/dlib/external/libpng/pngget.o \
./Lib/dlib/external/libpng/pngmem.o \
./Lib/dlib/external/libpng/pngpread.o \
./Lib/dlib/external/libpng/pngread.o \
./Lib/dlib/external/libpng/pngrio.o \
./Lib/dlib/external/libpng/pngrtran.o \
./Lib/dlib/external/libpng/pngrutil.o \
./Lib/dlib/external/libpng/pngset.o \
./Lib/dlib/external/libpng/pngtrans.o \
./Lib/dlib/external/libpng/pngwio.o \
./Lib/dlib/external/libpng/pngwrite.o \
./Lib/dlib/external/libpng/pngwtran.o \
./Lib/dlib/external/libpng/pngwutil.o 

C_DEPS += \
./Lib/dlib/external/libpng/png.d \
./Lib/dlib/external/libpng/pngerror.d \
./Lib/dlib/external/libpng/pngget.d \
./Lib/dlib/external/libpng/pngmem.d \
./Lib/dlib/external/libpng/pngpread.d \
./Lib/dlib/external/libpng/pngread.d \
./Lib/dlib/external/libpng/pngrio.d \
./Lib/dlib/external/libpng/pngrtran.d \
./Lib/dlib/external/libpng/pngrutil.d \
./Lib/dlib/external/libpng/pngset.d \
./Lib/dlib/external/libpng/pngtrans.d \
./Lib/dlib/external/libpng/pngwio.d \
./Lib/dlib/external/libpng/pngwrite.d \
./Lib/dlib/external/libpng/pngwtran.d \
./Lib/dlib/external/libpng/pngwutil.d 


# Each subdirectory must supply rules for building sources it contributes
Lib/dlib/external/libpng/%.o: ../Lib/dlib/external/libpng/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


