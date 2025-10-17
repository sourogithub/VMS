package com.dss.vms.common.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jdeveloper
 */
public class AnalyticSession implements Serializable {

	private boolean enable = false;
	private List<Region> regions = new ArrayList<Region>();

	/**
	 * @return the enable
	 */
	public boolean isEnable() {
		return enable;
	}

	/**
	 * @param enable the enable to set
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/**
	 * @return the regions
	 */
	public List<Region> getRegions() {
		return regions;
	}

	/**
	 * @param regions the regions to set
	 */
	public void setRegions(List<Region> regions) {
		this.regions = regions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\nAnalyticSession [enable=");
		builder.append(enable);
		builder.append(", regions=");
		builder.append(regions);
		builder.append("]");
		return builder.toString();
	}

}
