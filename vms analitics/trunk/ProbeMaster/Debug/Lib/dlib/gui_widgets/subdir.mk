################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../Lib/dlib/gui_widgets/base_widgets.cpp \
../Lib/dlib/gui_widgets/canvas_drawing.cpp \
../Lib/dlib/gui_widgets/drawable.cpp \
../Lib/dlib/gui_widgets/fonts.cpp \
../Lib/dlib/gui_widgets/style.cpp \
../Lib/dlib/gui_widgets/widgets.cpp 

OBJS += \
./Lib/dlib/gui_widgets/base_widgets.o \
./Lib/dlib/gui_widgets/canvas_drawing.o \
./Lib/dlib/gui_widgets/drawable.o \
./Lib/dlib/gui_widgets/fonts.o \
./Lib/dlib/gui_widgets/style.o \
./Lib/dlib/gui_widgets/widgets.o 

CPP_DEPS += \
./Lib/dlib/gui_widgets/base_widgets.d \
./Lib/dlib/gui_widgets/canvas_drawing.d \
./Lib/dlib/gui_widgets/drawable.d \
./Lib/dlib/gui_widgets/fonts.d \
./Lib/dlib/gui_widgets/style.d \
./Lib/dlib/gui_widgets/widgets.d 


# Each subdirectory must supply rules for building sources it contributes
Lib/dlib/gui_widgets/%.o: ../Lib/dlib/gui_widgets/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -std=c++0x -DDLIB_JPEG_SUPPORT -DDLIB_PNG_SUPPORT -I"/home/abc/eclipse-workspace/ProbeMaster/include" -I"/home/abc/eclipse-workspace/ProbeMaster/dlib-19.17/dlib/all" -I"/home/abc/eclipse-workspace/ProbeMaster/Lib/dlib/all" -Iusr/local/include/opencv2/core/ -I/usr/lib/jvm/java-8-oracle/include/ -I/usr/local/lib -I/usr/local/include -I/usr/local/include/opencv -I/usr/lib/jvm/java-8-oracle/include/linux/ -I/usr/lib/jvm/java-11-openjdk-amd64/include -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


