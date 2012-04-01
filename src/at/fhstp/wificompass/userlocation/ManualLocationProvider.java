/*
 * Created on Mar 9, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.userlocation;

import java.util.Date;

import android.content.Context;
import at.fhstp.wificompass.model.Location;

public class ManualLocationProvider extends LocationProviderImpl {

	public ManualLocationProvider(Context ctx) {
		this(ctx, LocationServiceFactory.getLocationService());
	}

	public ManualLocationProvider(Context ctx, LocationService locationService) {
		super(ctx, locationService);

	}

	public void updateCurrentPosition(float x, float y) {
		loc = new Location(getProviderName(), x, y, 0, new Date());
		if (locationService != null)
			locationService.updateLocation(loc);

		if (listener != null) {
			listener.locationChanged(loc);
		}
	}

}
