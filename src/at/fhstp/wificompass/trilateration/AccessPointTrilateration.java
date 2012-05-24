package at.fhstp.wificompass.trilateration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PointF;
import at.fhstp.wificompass.model.AccessPoint;
import at.fhstp.wificompass.model.BssidResult;
import at.fhstp.wificompass.model.Location;
import at.fhstp.wificompass.model.ProjectSite;
import at.fhstp.wificompass.model.WifiScanResult;
import at.fhstp.wificompass.view.AccessPointDrawable;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public abstract class AccessPointTrilateration {

	/** The current context. Is required for creating AccessPointDrawables */
	protected Context context;

	/**
	 * The project site. This is where the measurement data is located
	 * @uml.property  name="projectSite"
	 * @uml.associationEnd  
	 */
	protected ProjectSite projectSite;

	/** The hash map that links access points to measurement data */
	protected HashMap<AccessPoint, Vector<MeasurementDataSet>> measurementData;

	/** The hash map that links BSSIDs to access points */
	protected HashMap<String, AccessPoint> accessPoints;
	
	protected ProgressDialog progressDialog = null;

	/**
	 * The default constructor. Requires the context and the project site as
	 * parameters.
	 * 
	 * @param context
	 * @param projectSite
	 */
	public AccessPointTrilateration(Context context, ProjectSite projectSite) {
		this.context = context;
		this.projectSite = projectSite;
		this.parseMeasurementData();
	}
	
	public AccessPointTrilateration(Context context, ProjectSite projectSite, ProgressDialog progressDialog) {
		this.context = context;
		this.projectSite = projectSite;
		this.progressDialog = progressDialog;
		this.parseMeasurementData();
	}

	/**
	 * Takes the measurement data from the project site and converts it into
	 * data that can be used by the algorithms. Requires that the projectSite is
	 * already set (which is forced by the constructor).
	 */
	protected void parseMeasurementData() {
		measurementData = new HashMap<AccessPoint, Vector<MeasurementDataSet>>();
		accessPoints = new HashMap<String, AccessPoint>();

		// Walk through all measuring points
		for (Iterator<WifiScanResult> it = projectSite.getScanResults()
				.iterator(); it.hasNext();) {
			WifiScanResult result = it.next();

			// Walk through all captured BSSIDs within the measuring point
			for (Iterator<BssidResult> itb = result.getBssids().iterator(); itb
					.hasNext();) {
				BssidResult bssidResult = itb.next();

				// Only calculate the BSSID location if the BSSID is selected
				if (projectSite.isBssidSelected(bssidResult.getBssid())) {
					Vector<MeasurementDataSet> measurements;

					if (!accessPoints.containsKey(bssidResult.getBssid()))
						accessPoints.put(bssidResult.getBssid(), new AccessPoint(
								bssidResult));

					if (!measurementData.containsKey(accessPoints.get(bssidResult
							.getBssid()))) {
						measurements = new Vector<MeasurementDataSet>();
						measurementData.put(
								accessPoints.get(bssidResult.getBssid()),
								measurements);
					} else {
						measurements = measurementData.get(accessPoints
								.get(bssidResult.getBssid()));
					}

					Location loc = result.getLocation();
					measurements.add(new MeasurementDataSet(loc.getX(), loc.getY(),
							bssidResult.getLevel()));
				}
			}
		}
	}

	/**
	 * Takes all measuring data it has, calculates the access points positions,
	 * creates <b>AccessPointDrawable</b>s and sets the relative positions of
	 * the access points. An <b>AccessPoint</b> object is handed over to the
	 * <b>AccessPointDrawable</b> so that the popup text can be set accordingly.
	 * 
	 * @return A vector of AccessPointDrawables
	 */
	public Vector<AccessPointDrawable> calculateAllAndGetDrawables() {
		Vector<AccessPointDrawable> aps = new Vector<AccessPointDrawable>();

		int count = 0;
		
		if (progressDialog != null)
			progressDialog.setMax(measurementData.entrySet().size());
			
		
		for (Iterator<Entry<AccessPoint, Vector<MeasurementDataSet>>> itm = measurementData
				.entrySet().iterator(); itm.hasNext();) {
			Entry<AccessPoint, Vector<MeasurementDataSet>> pair = (Entry<AccessPoint, Vector<MeasurementDataSet>>) itm
					.next();

			PointF position = this.calculateAccessPointPosition(pair.getKey());

			if (position != null) {
				pair.getKey().setLocation(new Location(position.x, position.y));
				AccessPointDrawable ap = new AccessPointDrawable(context,
						pair.getKey());
				ap.setRelativePosition(position);
				aps.add(ap);
			}
			
			count ++;
			
			if (progressDialog != null)
				progressDialog.setProgress(count);
				
		}
		
		return aps;
	}

	/**
	 * Here the magic happens. The access point location is calculated with the
	 * measuring data given.
	 * 
	 * @param ap
	 *            The access point to calculate the position of
	 * @return The point where the access point is believed to be located
	 */
	public abstract PointF calculateAccessPointPosition(AccessPoint ap);

	
}
