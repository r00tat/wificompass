/*
 * Created on Feb 22, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.userlocation;

public class LocationServiceFactory {
	protected static LocationService ls=null;
	static void setLocationService(LocationService ls){
		LocationServiceFactory.ls=ls;
	}
	
	public static LocationService getLocationService() throws LocationServiceException{
		if(ls==null){
			throw new LocationServiceException("no location service defined!");
		}
		return ls;
	}
}
