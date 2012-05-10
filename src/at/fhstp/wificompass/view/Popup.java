/*
 * Created on Mar 11, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.view;

/**
 * @author   Paul Woelfel (paul@woelfel.at)
 */
public interface Popup {
	/**
	 * @param  isPopupActive
	 * @uml.property  name="active"
	 */
	public void setActive(boolean isPopupActive);
	/**
	 * @return
	 * @uml.property  name="active"
	 */
	public boolean isActive();
}
