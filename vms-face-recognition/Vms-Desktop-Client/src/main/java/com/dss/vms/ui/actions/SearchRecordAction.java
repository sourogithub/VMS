package com.dss.vms.ui.actions;

import java.util.List;

public interface SearchRecordAction<T> {
	
	/**
	 * records found after search
	 * @param records
	 */
	public void recordsFound(List<T> records);
}
