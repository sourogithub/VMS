/*
 * jpegIO.h
 * Author: 
 */

#include <stdio.h>
#include <setjmp.h>
#include "jpeglib.h"

int writeJPEG (unsigned char * image_buffer, int image_height, int image_width, char * filename, int quality);
int readJPEG(char * filename, int *height, int *width, unsigned char **outbuffer);

