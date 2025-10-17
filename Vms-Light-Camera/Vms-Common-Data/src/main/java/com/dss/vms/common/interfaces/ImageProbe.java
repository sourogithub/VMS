package com.dss.vms.common.interfaces;

import java.util.List;

import com.dss.vms.common.constants.AnalyticType;
import com.dss.vms.common.data.Region;
import com.dss.vms.common.response.VmsResponse;

/**
 * @author jdeveloper
 */
public interface ImageProbe {

	/**
	 * @param cameraID
	 * @return
	 */
	VmsResponse addCamera(Integer cameraID);

	/**
	 * @param cameraID
	 * @return
	 */
	VmsResponse deleteCamera(Integer... cameraID);

	/**
	 * @param cameraID
	 * @param analyticType
	 * @param regions
	 * @return
	 */
	VmsResponse setRegion(Integer cameraID, AnalyticType analyticType, List<Region> regions);

	/**
	 * @param cameraID
	 * @param analyticType
	 * @return
	 */
	VmsResponse startAnaytic(Integer cameraID, AnalyticType analyticType);

	/**
	 * @param cameraID
	 * @param analyticType
	 * @return
	 */
	VmsResponse stopAnaytic(Integer cameraID, AnalyticType analyticType);
}
