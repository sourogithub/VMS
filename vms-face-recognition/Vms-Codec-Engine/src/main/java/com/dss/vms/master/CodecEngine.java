package com.dss.vms.master;

import java.util.concurrent.ConcurrentHashMap;

import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.jni.interfaces.CodecMaster;
import com.dss.vms.jni.interfaces.common.NativeRetun;
import com.dss.vms.master.decoder.AbstractDecoder;
import com.dss.vms.master.decoder.RGBDecorder;
import com.dss.vms.video.data.MediaFrame;

/**
 * @author sibendu
 */
public class CodecEngine implements NativeRetun {
	private static CodecEngine INSTANCE = null;
	private ConcurrentHashMap<Integer, AbstractDecoder> decoderMap = new ConcurrentHashMap<Integer, AbstractDecoder>();

	/**
	 * get instance
	 * 
	 * @return
	 */
	public static synchronized CodecEngine getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CodecEngine();
		}
		return INSTANCE;
	}

	/**
	 * @param frame
	 * @return decoded frame
	 */
	public boolean decodeFrame(MediaFrame frame) {
		if (decoderMap.containsKey(frame.getChannelID())) {
			return decoderMap.get(frame.getChannelID()).decode(frame);
		}

		RGBDecorder decoder = new RGBDecorder(frame.getChannelID());
		decoderMap.put(frame.getChannelID(), decoder);
		return decoder.decode(frame);
	}

	/**
	 * shutdown decode engine
	 * 
	 * @return
	 */
	public VmsResponse shutdown() {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);

		// removing all the decoders
		for (AbstractDecoder decoder : decoderMap.values()) {
			decoder.removeDecoder();
		}

		int returnCode = CodecMaster.getInstance().tearDown();
		if (returnCode != SUCCESS) {
			response.setResponseCode(CommonResponseCode.ERROR);
			return response;
		} 
		
		return response;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.shutdown();
	}

}
