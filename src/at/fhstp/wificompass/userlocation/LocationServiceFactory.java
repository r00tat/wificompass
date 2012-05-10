/*
 * Created on Feb 22, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.userlocation;



/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public class LocationServiceFactory {
	/**
	 * @uml.property  name="ls"
	 * @uml.associationEnd  
	 */
	protected static LocationService ls=null;
	static void setLocationService(LocationService ls){
		LocationServiceFactory.ls=ls;
	}
	
	public static LocationService getLocationService() {
		if(ls==null){
//			throw new LocationServiceException("no location service defined!");
			ls=new LocationServiceImpl();
		}
		return ls;
	}
}
