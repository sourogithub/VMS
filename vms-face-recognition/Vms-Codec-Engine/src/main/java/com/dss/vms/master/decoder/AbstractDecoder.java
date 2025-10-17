package com.dss.vms.master.decoder;

import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.video.data.MediaFrame;

public interface AbstractDecoder {
	public VmsResponse addDecoder();

	public VmsResponse removeDecoder();

	public boolean decode(MediaFrame frame);
}
