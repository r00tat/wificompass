/*
 * Created on Mar 9, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.userlocation;

import java.util.Date;

import android.content.Context;
import at.fhstp.wificompass.model.Location;

public class ManualLocationProvider implements LocationProvider {
	
	protected Location loc;
	
	protected LocationService locationService;
	
	protected Context ctx;
	
	public ManualLocationProvider(Context ctx){
		this(ctx,LocationServiceFactory.getLocationService());
	}
	
	
	public ManualLocationProvider(Context ctx,LocationService locationService){
		loc=new Location();
		loc.setProvider(getProviderName());
		loc.setAccurancy(0);
		this.ctx=ctx;
		locationService.registerProvider(this);
	}
	

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
		return this.getClass().getName();
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
	
	public void updateCurrentPosition(float x,float y){
		loc.setX(x);
		loc.setY(y);
		loc.setTimestamp(new Date());
		locationService.updateLocation(loc);
	}

}
