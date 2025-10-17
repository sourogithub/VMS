package com.dss.vms.common.response;

public class AuthResponse {
	boolean sucess;
	UserType userType;
	
	/**
	 * @param sucess
	 * @param admin
	 */
	public AuthResponse(boolean sucess, UserType admin) {
		super();
		this.sucess = sucess;
		this.userType = admin;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
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
