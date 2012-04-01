/*
 * Created on Dec 8, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.userlocation;

import at.fhstp.wificompass.model.Location;


/**
 * @author Paul Woelfel (paul@woelfel.at)
 */
public interface LocationProvider {
	
	/**
	 * set the LocationService, which should be updated on location changes
	 * this is a callback by LocationService
	 * @param service update this service
	 */
	public void setLocationService(LocationService service);
	
	/**
	 * remove the LocationService, which should be updated on location changes
	 * this is a callback by LocationService
	 * @param service service to update
	 */
	public void unsetLocationService(LocationService service);
	

	/**
	 * return the Name of the Location Provider
	 */
	 public java.lang.String getProviderName();
	 
	 
	 /**
	  * get X coordinate
	 * @return x coordinate
	 */
	 public float getLocationX();
	 
	 /**
	  * get Y coordinate
	 * @return y coordinate
	 */
	 public float getLocationY();

	/**
	 * get Z coordinate if applicable, otherwise zero
	 * @return z coordinate
	 */
	 public float getLocationZ();
	
	/**
	 * get current Location of the user
	 * @return current location
	 */
	 public Location getLocation();
	
	 
	/**
	 * starts the location provider
	 */
	public void start();
	
	
	/**
	 * stops the location provider
	 */
	public void stop();
	

	/**
	 * set a listener, which will be informed, if the location has changed
	 * @param listener
	 */
	public void setLocationChangeListener(LocationChangeListener listener);
	
	
}
