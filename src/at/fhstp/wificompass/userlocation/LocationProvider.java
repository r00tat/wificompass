/*
 * Created on Dec 8, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.userlocation;

import at.fhstp.wificompass.model.Location;


/**
 * @author Paul Woelfel
 */
public interface LocationProvider {
	
	/**
	 * set the LocationService, which should be updated on location changes
	 * @param service update this service
	 */
	public void setLocationService(LocationService service);
	
	/**
	 * remove the LocationService, which should be updated on location changes
	 * @param service service to update
	 */
	public void unsetLocationService(LocationService service);
	

	/**
	 * return the Name of the Location Provider
	 */
	 java.lang.String getProviderName();
	 
	 
	 /**
	  * get X coordinate
	 * @return x coordinate
	 */
	double getLocationX();
	 
	 /**
	  * get Y coordinate
	 * @return y coordinate
	 */
	double getLocationY();

	/**
	 * get Z coordinate if applicable, otherwise zero
	 * @return z coordinate
	 */
	double getLocationZ();
	
	/**
	 * get current Location of the user
	 * @return current location
	 */
	Location getLocation();
}
