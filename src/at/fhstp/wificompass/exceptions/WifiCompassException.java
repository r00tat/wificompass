/*
 * Created on Mar 9, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.exceptions;

public class WifiCompassException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7781140940519857808L;

	public WifiCompassException() {
	}

	public WifiCompassException(String detailMessage) {
		super(detailMessage);

	}

	public WifiCompassException(Throwable throwable) {
		super(throwable);

	}

	public WifiCompassException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);

	}

}
