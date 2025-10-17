################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../dlib-19.17/tools/python/src/basic.cpp \
../dlib-19.17/tools/python/src/cca.cpp \
../dlib-19.17/tools/python/src/cnn_face_detector.cpp \
../dlib-19.17/tools/python/src/correlation_tracker.cpp \
../dlib-19.17/tools/python/src/decision_functions.cpp \
../dlib-19.17/tools/python/src/dlib.cpp \
../dlib-19.17/tools/python/src/face_recognition.cpp \
../dlib-19.17/tools/python/src/global_optimization.cpp \
../dlib-19.17/tools/python/src/gui.cpp \
../dlib-19.17/tools/python/src/image.cpp \
../dlib-19.17/tools/python/src/image2.cpp \
../dlib-19.17/tools/python/src/image3.cpp \
../dlib-19.17/tools/python/src/image4.cpp \
../dlib-19.17/tools/python/src/image_dataset_metadata.cpp \
../dlib-19.17/tools/python/src/line.cpp \
../dlib-19.17/tools/python/src/matrix.cpp \
../dlib-19.17/tools/python/src/numpy_returns.cpp \
../dlib-19.17/tools/python/src/object_detection.cpp \
../dlib-19.17/tools/python/src/other.cpp \
../dlib-19.17/tools/python/src/rectangles.cpp \
../dlib-19.17/tools/python/src/sequence_segmenter.cpp \
../dlib-19.17/tools/python/src/shape_predictor.cpp \
../dlib-19.17/tools/python/src/svm_c_trainer.cpp \
../dlib-19.17/tools/python/src/svm_rank_trainer.cpp \
../dlib-19.17/tools/python/src/svm_struct.cpp \
../dlib-19.17/tools/python/src/vector.cpp 

OBJS += \
./dlib-19.17/tools/python/src/basic.o \
./dlib-19.17/tools/python/src/cca.o \
./dlib-19.17/tools/python/src/cnn_face_detector.o \
./dlib-19.17/tools/python/src/correlation_tracker.o \
./dlib-19.17/tools/python/src/decision_functions.o \
./dlib-19.17/tools/python/src/dlib.o \
./dlib-19.17/tools/python/src/face_recognition.o \
./dlib-19.17/tools/python/src/global_optimization.o \
./dlib-19.17/tools/python/src/gui.o \
./dlib-19.17/tools/python/src/image.o \
./dlib-19.17/tools/python/src/image2.o \
./dlib-19.17/tools/python/src/image3.o \
./dlib-19.17/tools/python/src/image4.o \
./dlib-19.17/tools/python/src/image_dataset_metadata.o \
./dlib-19.17/tools/python/src/line.o \
./dlib-19.17/tools/python/src/matrix.o \
./dlib-19.17/tools/python/src/numpy_returns.o \
./dlib-19.17/tools/python/src/object_detection.o \
./dlib-19.17/tools/python/src/other.o \
./dlib-19.17/tools/python/src/rectangles.o \
./dlib-19.17/tools/python/src/sequence_segmenter.o \
./dlib-19.17/tools/python/src/shape_predictor.o \
./dlib-19.17/tools/python/src/svm_c_trainer.o \
./dlib-19.17/tools/python/src/svm_rank_trainer.o \
./dlib-19.17/tools/python/src/svm_struct.o \
./dlib-19.17/tools/python/src/vector.o 

CPP_DEPS += \
./dlib-19.17/tools/python/src/basic.d \
./dlib-19.17/tools/python/src/cca.d \
./dlib-19.17/tools/python/src/cnn_face_detector.d \
./dlib-19.17/tools/python/src/correlation_tracker.d \
./dlib-19.17/tools/python/src/decision_functions.d \
./dlib-19.17/tools/python/src/dlib.d \
./dlib-19.17/tools/python/src/face_recognition.d \
./dlib-19.17/tools/python/src/global_optimization.d \
./dlib-19.17/tools/python/src/gui.d \
./dlib-19.17/tools/python/src/image.d \
./dlib-19.17/tools/python/src/image2.d \
./dlib-19.17/tools/python/src/image3.d \
./dlib-19.17/tools/python/src/image4.d \
./dlib-19.17/tools/python/src/image_dataset_metadata.d \
./dlib-19.17/tools/python/src/line.d \
./dlib-19.17/tools/python/src/matrix.d \
./dlib-19.17/tools/python/src/numpy_returns.d \
./dlib-19.17/tools/python/src/object_detection.d \
./dlib-19.17/tools/python/src/other.d \
./dlib-19.17/tools/python/src/rectangles.d \
./dlib-19.17/tools/python/src/sequence_segmenter.d \
./dlib-19.17/tools/python/src/shape_predictor.d \
./dlib-19.17/tools/python/src/svm_c_trainer.d \
./dlib-19.17/tools/python/src/svm_rank_trainer.d \
./dlib-19.17/tools/python/src/svm_struct.d \
./dlib-19.17/tools/python/src/vector.d 


# Each subdirectory must supply rules for building sources it contributes
dlib-19.17/tools/python/src/%.o: ../dlib-19.17/tools/python/src/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -std=c++0x -DDLIB_JPEG_SUPPORT -DDLIB_PNG_SUPPORT -I"/home/abc/eclipse-workspace/ProbeMaster/include" -I"/home/abc/eclipse-workspace/ProbeMaster/dlib-19.17/dlib/all" -Iusr/local/include/opencv2/core/ -I/usr/lib/jvm/java-8-oracle/include/ -I/usr/local/lib -I/usr/local/include -I/usr/local/include/opencv -I/usr/lib/jvm/java-8-oracle/include/linux/ -I/usr/lib/jvm/java-11-openjdk-amd64/include -O0 -g3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


