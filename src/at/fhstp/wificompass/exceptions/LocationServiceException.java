/*
 * Created on Feb 22, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.exceptions;

public class LocationServiceException extends Exception {

	/**
	 * 
	 */
	public LocationServiceException() {
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public LocationServiceException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		
	}

	/**
	 * @param detailMessage
	 */
	public LocationServiceException(String detailMessage) {
		super(detailMessage);
		
	}

	/**
	 * @param throwable
	 */
	public LocationServiceException(Throwable throwable) {
		super(throwable);
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2922198407322703974L;

}
