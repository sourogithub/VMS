package com.dss.vms.master.decoder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.constants.ColorSpace;
import com.dss.vms.common.constants.MediaType;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.jni.interfaces.CodecMaster;
import com.dss.vms.jni.interfaces.common.NativeConstants;
import com.dss.vms.jni.interfaces.common.NativeRetun;
import com.dss.vms.video.data.MediaFrame;

public class RGBDecorder implements NativeConstants, NativeRetun, AbstractDecoder {
	private static final Logger LOGGER = LoggerFactory.getLogger(RGBDecorder.class); 
	private static CodecMaster codecMaster = CodecMaster.getInstance();
	private static final int INVALID_BUFFER_SIZE = -1;

	private ByteBuffer mappingBuffer;
	private ByteBuffer encodedBuffer;
	private IntBuffer decodedBuffer;

	private int allocatedEncodedBufferSize = INVALID_BUFFER_SIZE;
	private int allocatedDecodedBufferSize = INVALID_BUFFER_SIZE;

	public RGBDecorder(int channelID) {

		// mapping buffer allocation
		mappingBuffer = ByteBuffer.allocateDirect(MAPPING_BUFFER_SIZE).order(ByteOrder.nativeOrder());

		// encoded buffer allocation
		encodedBuffer = ByteBuffer.allocateDirect(INITIAL_ENCODER_BUFFER_SIZE + SIZE_OF_BUFFER_SIZE)
				.order(ByteOrder.nativeOrder());
		allocatedEncodedBufferSize = INITIAL_ENCODER_BUFFER_SIZE + SIZE_OF_BUFFER_SIZE;

		// decodedBuffer allocation
		decodedBuffer = ByteBuffer.allocateDirect(INITIAL_DECODER_BUFFER_SIZE * Integer.SIZE + SIZE_OF_BUFFER_SIZE) 
				.order(ByteOrder.nativeOrder()).asIntBuffer();

		allocatedDecodedBufferSize = INITIAL_DECODER_BUFFER_SIZE + SIZE_OF_BUFFER_SIZE;

		this.addDecoder();
	}

	/**
	 * addDecoder
	 * 
	 * @return
	 */
	@Override
	public VmsResponse addDecoder() {
		VmsResponse response = new VmsResponse();
		mappingBuffer.putInt(MAPPING_BUFFER_SIZE);
		mappingBuffer.rewind();
		int res = codecMaster.addDecorder(mappingBuffer, MediaType.H264.value(), ColorSpace.ARGB.value());

		if (res == SUCCESS) { /* success */
			response.setResponseCode(CommonResponseCode.SUCCESS);
			LOGGER.info("H264StreamDecoder - SUCCESSFULY ADDED DECODER TO CODEC-MASTER");
		} else {
			response.setResponseCode(CommonResponseCode.ERROR);
		}

		return response;
	}

	/**
	 * remove decoder
	 * 
	 * @return
	 */
	@Override
	public VmsResponse removeDecoder() {
		VmsResponse response = new VmsResponse();
		int retVal = codecMaster.removeDecorder(mappingBuffer);

		if (retVal == SUCCESS) {
			response.setResponseCode(CommonResponseCode.SUCCESS);
		} else {
			response.setResponseCode(CommonResponseCode.ERROR);
		}
		return response;
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		this.removeDecoder();
	}

	/**
	 * 
	 * @param frame
	 * @return
	 */
	@Override
	public boolean decode(MediaFrame frame) { /* create buffered image for this */
		
		if (frame.isDecoded()) return true;

		int encodedFrameSize = frame.getRawFrame().length;

		if ((allocatedEncodedBufferSize - SIZE_OF_BUFFER_SIZE) < encodedFrameSize) {

			allocatedEncodedBufferSize = encodedFrameSize + SIZE_OF_BUFFER_SIZE;
			// allocating encodedbuffer
			encodedBuffer = ByteBuffer.allocateDirect(allocatedEncodedBufferSize).order(ByteOrder.nativeOrder());
		}

		// now putting values to encodedbuffer
		encodedBuffer.rewind();
		encodedBuffer.putInt(frame.getRawFrame().length);
		encodedBuffer.put(frame.getRawFrame());
		encodedBuffer.rewind();

		decodedBuffer.rewind();

		int decodeResponse = codecMaster.decode(mappingBuffer, encodedBuffer, decodedBuffer);
		if (decodeResponse == SUCCESS) {
			decodedBuffer.rewind();
			int decodeBufferSize = decodedBuffer.get(); /* skipping sizeofbuffer */
//			LOGGER.debug("got size = " + decodeBufferSize);

			int width = decodedBuffer.get();
//			LOGGER.info("Decoded WIDTH -> " + width);
			int height = decodedBuffer.get(); 
//			LOGGER.info("HEIGHT -> " + height);
			if (decodeBufferSize > 0 && width > 0 && height > 0) {
				int[] imageFrame = new int[width * height];
				decodedBuffer.get(imageFrame);
				frame.setRgbFrame(imageFrame);
				frame.setRgbHeight(height);
				frame.setRgbWidth(width);
				frame.setDecoded(true);
				return true;
			}
		} else if (decodeResponse == INSUFICIENT_MEMORY) {
			decodedBuffer.rewind();
			
			allocatedDecodedBufferSize = decodedBuffer.get() * Integer.SIZE + SIZE_OF_BUFFER_SIZE * 3;
			
			LOGGER.error("CODEC BUFFER INSUFFICIENT MEMORY [allocation required - " + allocatedDecodedBufferSize + " ]");
			decodedBuffer = ByteBuffer.allocateDirect(allocatedDecodedBufferSize)
					.order(ByteOrder.nativeOrder()).asIntBuffer();
			
			decodedBuffer.put(allocatedDecodedBufferSize - SIZE_OF_BUFFER_SIZE);
			decodedBuffer.rewind();
			LOGGER.debug("Allocated decode buffer capacity - " + decodedBuffer.capacity());

		} else if (decodeResponse == ERROR) {
			LOGGER.error("ERROR IN DECODE RESPONSE - NativeReturn " + decodeResponse);
		}

		return false;
	}

//	static int testFileCounter = 0;
	
	/**
	 * For testing
	 * 
	 * @param height
	 * @param width
	 * @param imageframe
	 */
//	private void dumpPPM(int height, int width, int[] imageFrame) {
//		RandomAccessFile raf = null;
//		try {
//			raf = new RandomAccessFile(new File("SampleFrame" + (testFileCounter++) + ".ppm"), "rw");
//			raf.writeBytes("P6 ");
//			raf.writeBytes(Integer.toString(width));
//			raf.writeBytes(" ");
//			raf.writeBytes(Integer.toString(height));
//			raf.writeBytes(" ");
//			raf.writeBytes(Integer.toString(255));
//			raf.writeBytes(" ");
//
//			for (int value : imageFrame) {
//				//byte alpha = (value >> 24) & 0xFF;
//				byte red = (byte) ((value >> 16) & 0xFF);
//				byte green = (byte) ((value >> 8) & 0xFF);
//				byte blue = (byte) ((value) & 0xFF);
//				/** get R G B values and skip the Alpha part and then write **/
//				raf.writeByte(red);
//				// raf.writeBytes(" ");
//				raf.writeByte(green);
//				// raf.writeBytes(" ");
//				raf.writeByte(blue);
//				// raf.writeBytes(" ");
//				// raf.writeBytes(Integer.toString(value));
//			}
////			LOGGER.info(" frame successfully dumped ! ");
//		} catch (Exception e) {}
//		  finally {
//			try { raf.close();} catch (IOException e) {}
//		}
//	}

}