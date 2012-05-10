/*
 * Created on Mar 19, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.userlocation;

import android.content.Context;
import at.fhstp.wificompass.model.Location;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public abstract class LocationProviderImpl implements LocationProvider {

	/**
	 * @uml.property  name="loc"
	 * @uml.associationEnd  
	 */
	protected Location loc;
	
	/**
	 * @uml.property  name="locationService"
	 * @uml.associationEnd  
	 */
	protected LocationService locationService;
	
	protected Context ctx;
	
	/**
	 * @uml.property  name="running"
	 */
	protected boolean running=false;
	
	/**
	 * @uml.property  name="listener"
	 * @uml.associationEnd  
	 */
	protected LocationChangeListener listener=null;
	
	public LocationProviderImpl(Context ctx){
		this(ctx,LocationServiceFactory.getLocationService());
	}
	
	
	public LocationProviderImpl(Context ctx,LocationService locationService){
		loc=new Location();
		loc.setProvider(getProviderName());
		loc.setAccurancy(-1);
		this.ctx=ctx;
		locationService.registerProvider(this);
	}
	

	/**
	 * @param service
	 * @uml.property  name="locationService"
	 */
	@Override
	public void setLocationService(LocationService service) {
		locationService=service;
	}

	@Override
	public void unsetLocationService(LocationService service) {
		locationService=null;
	}

	@Override
	public String getProviderName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public float getLocationX() {
		return loc.getX();
	}

	@Override
	public float getLocationY() {
		return loc.getY();
	}

	@Override
	public float getLocationZ() {
		return 0;
	}

	@Override
	public Location getLocation() {
		return loc;
	}
	

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.userlocation.LocationProvider#start()
	 */
	@Override
	public void start() {
		running=true;
	}


	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.userlocation.LocationProvider#stop()
	 */
	@Override
	public void stop() {
		running=false;
	}


	/**
	 * @return  the running
	 * @uml.property  name="running"
	 */
	public boolean isRunning() {
		return running;
	}


	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.userlocation.LocationProvider#setLocationChangeListener(at.fhstp.wificompass.userlocation.LocationChangeListener)
	 */
	@Override
	public void setLocationChangeListener(LocationChangeListener listener) {
		this.listener=listener;
	}

}
