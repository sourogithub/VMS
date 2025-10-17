package com.dss.vms.common.response;

import java.io.Serializable;

public class Authorization implements Serializable {
	private boolean sucess;
	private UserType userType;

	/**
	 * @param sucess
	 * @param userType
	 */
	public Authorization(boolean sucess, UserType userType) {
		this.sucess = sucess;
		this.userType = userType;
	}

	/**
	 * @return the sucess
	 */
	public boolean isSucess() {
		return sucess;
	}

	/**
	 * @param sucess the sucess to set
	 */
	public void setSucess(boolean sucess) {
		this.sucess = sucess;
	}

	/**
	 * @return the userType
	 */
	public UserType getUserType() {
		return userType;
	}

	/**
	 * @param userType the userType to set
	 */
	public void setUserType(UserType userType) {
		this.userType = userType;
	}

}
