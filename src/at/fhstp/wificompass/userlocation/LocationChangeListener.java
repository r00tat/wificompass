/*
 * Created on Mar 31, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.userlocation;

import at.fhstp.wificompass.model.Location;

public interface LocationChangeListener {
	public void locationChanged(Location loc);
}
