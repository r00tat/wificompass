/*
 * Created on May 16, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass;

/**
 * @author Paul Woelfel (paul@woelfel.at)
 */
public interface CompassListener {
	public void onCompassChanged(float azimuth,String direction);
}
