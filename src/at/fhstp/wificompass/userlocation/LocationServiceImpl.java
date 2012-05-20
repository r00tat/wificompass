/*
 * Created on Dec 8, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.userlocation;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.ToolBox;
import at.fhstp.wificompass.model.Location;

/**
 * @author  Paul Woelfel
 */
public class LocationServiceImpl implements LocationService {

	/**
	 * @uml.property  name="pos"
	 * @uml.associationEnd  
	 */
	protected Location pos;

	protected Vector<LocationProvider> providers;
	
	protected static final String TAG="LocationService";
	
	protected float angle=0f;
	
	/**
	 * @uml.property  name="gridSpacingX"
	 */
	protected float gridSpacingX=30f;

	/**
	 * @uml.property  name="gridSpacingY"
	 */
	protected float gridSpacingY=30f;
	
	static {
		// register as LocationService
		LocationServiceFactory.setLocationService(new LocationServiceImpl());
	}

	/**
	 * 
	 */
	public LocationServiceImpl() {
		providers = new Vector<LocationProvider>();
		pos = new Location();
		LocationServiceFactory.setLocationService(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.aploc.interfaces.LocationService#getLocation()
	 */
	@Override
	public Location getLocation() {
		return pos;
	}

	@Override
	public Location updateLocation(Location location, boolean force) {
		/*
		 * updates location if force 
		 * or location information is newer
		 * or more accurate
		 * 
		 * maybe not always the newer location should be used, maybe it should sometimes use the more accurate
		 */
		
		if(force||
				location.getTimestampmilis()>=pos.getTimestampmilis()||
				(
					pos.getAccurancy()==-1||
					location.getAccurancy()>=0&&(location.getAccurancy()<pos.getAccurancy())
				)
			){
			pos=location;
		}else {
			Logger.d( "Location not updated");
		}
		return pos;
	}

	@Override
	public void updateLocation(Location pos) {
		this.updateLocation(pos, false);
	}

	@Override
	public void registerProvider(LocationProvider provider) {
		providers.add(provider);
		provider.setLocationService(this);
	}

	@Override
	public void registerProvider(String provider) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Object o = Class.forName(provider).newInstance();
		if (!(o instanceof LocationProvider)) {
			throw new InstantiationException("Provider " + provider + " is not a Provider");
		}
		this.registerProvider((LocationProvider) o);

	}

	@Override
	public void unregisterProvider(LocationProvider provider) {
		if (providers.contains(provider)) {
			providers.remove(provider);
		}
	}

	protected void finalize() {
		for (Iterator<LocationProvider> it = providers.iterator(); it.hasNext();) {
			LocationProvider p = it.next();
			p.unsetLocationService(this);
		}
	}

	@Override
	public List<LocationProvider> getLocationProviders() {
		return providers;
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.userlocation.LocationService#setRelativeNorth(float)
	 */
	@Override
	public void setRelativeNorth(float angle) {
		this.angle =ToolBox.normalizeAngle(angle);
	}

	@Override
	public float getRelativeNorth() {
		return angle;
	}

	@Override
	public void setGridSpacing(float x, float y) {
		gridSpacingX=x;
		gridSpacingY=y;
	}

	/**
	 * @return
	 * @uml.property  name="gridSpacingX"
	 */
	@Override
	public float getGridSpacingX() {
		return gridSpacingX;
	}

	/**
	 * @return
	 * @uml.property  name="gridSpacingY"
	 */
	@Override
	public float getGridSpacingY() {
		return gridSpacingY;
	}

}
