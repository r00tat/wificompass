/*
 * Created on Dec 8, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.userlocation;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.util.Log;
import at.fhstp.wificompass.model.Location;

/**
 * @author Paul Woelfel
 */
public class LocationServiceImpl implements LocationService {

	protected Location pos;

	protected Vector<LocationProvider> providers;
	
	protected static final String TAG="LocationService";

	/**
	 * 
	 */
	public LocationServiceImpl() {
		providers = new Vector<LocationProvider>();
		pos = new Location();
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
				location.getTimestamp().after(pos.getTimestamp())||
				(
					pos.getAccurancy()==-1||
					location.getAccurancy()>=0&&(location.getAccurancy()<pos.getAccurancy())
				)
			){
			pos=location;
		}else {
			Log.d(TAG, "Location not updated");
		}
		return pos;
	}

	@Override
	public void updateLocation(Location pos) {
		this.updateLocation(pos, false);
	}

	@Override
	public void registerProvider(LocationProvider provider) {
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

		}
	}

	protected void finalize() {
		for (Iterator<LocationProvider> it = providers.iterator(); it.hasNext();) {
			LocationProvider p = it.next();
			p.removeLocationService(this);
		}
	}

	@Override
	public List<LocationProvider> getLocationProviders() {
		return providers;
	}

}
