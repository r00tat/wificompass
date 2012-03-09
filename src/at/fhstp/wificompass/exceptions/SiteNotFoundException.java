/*
 * Created on Mar 9, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.exceptions;

public class SiteNotFoundException extends WifiCompassException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2140169366213317953L;

	public SiteNotFoundException() {
	}

	public SiteNotFoundException(String detailMessage) {
		super(detailMessage);

	}

	public SiteNotFoundException(Throwable throwable) {
		super(throwable);

	}

	public SiteNotFoundException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);

	}

}
