/*
 * Created on Mar 19, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.userlocation;

import android.content.Context;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.model.Location;


/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public class StepDetectionProvider extends LocationProviderImpl implements StepTrigger {
	
	float step=0.0f;
	
	public static final float FILTER_DEFAULT=0.3f;

	public static final float PEAK_DEFAULT=0.75f;

	public static final float STEP_DEFAULT=0.50f;
	public static final int TIMEOUT_DEFAULT=400;

	public final static String CALIB_DATA="SensorCalibration";

	public final static String PEAK="peak";

	public final static String TIMEOUT="timeout";

	public final static String FILTER="a";

	public final static String STEP="step";
	
	/**
	 * @uml.property  name="stepDetector"
	 * @uml.associationEnd  
	 */
	protected StepDetection stepDetector;
	
	public StepDetectionProvider(Context ctx){
		this(ctx,LocationServiceFactory.getLocationService());
	}
	
	
	public StepDetectionProvider(Context ctx,LocationService locationService){
		super(ctx,locationService);
		
		// this are the default values of footpath
					double a = ctx.getSharedPreferences(CALIB_DATA,0).getFloat(FILTER, FILTER_DEFAULT);
		double peak = ctx.getSharedPreferences(CALIB_DATA,0).getFloat(PEAK, PEAK_DEFAULT);
		int step_timeout_ms = ctx.getSharedPreferences(CALIB_DATA,0).getInt(TIMEOUT, TIMEOUT_DEFAULT);
		
		step = ctx.getSharedPreferences(CALIB_DATA,0).getFloat(STEP, STEP_DEFAULT);
		
		stepDetector=new StepDetection(ctx, this, a,peak,step_timeout_ms);
//		stepDetector.load();
	}
	

	
	@Override
	public void onStepDetected(long now_ms, double compDir) {
		// a step has been triggered
	
		float curX=locationService.getLocation().getX(),curY=locationService.getLocation().getY();
		
		float angle=(float) (Math.toRadians(compDir)+locationService.getRelativeNorth());
		
		float dx=(float) (Math.sin(angle)*step)*locationService.getGridSpacingX();
		float dy=(float) (Math.cos(angle)*step)*locationService.getGridSpacingY();
		
				Logger.d("walked a step dx: "+dx+" dy:"+dy);
		
		loc=new Location(getProviderName(),curX+dx,curY-dy,0,null);

		if(listener!=null){
			listener.onLocationChange(loc);
		}
		
	}


	@Override
	public void onAccelerometerDataReceived(long now_ms, double x, double y, double z) {
	}


	@Override
	public void onCompassDataReceived(long now_ms, double x, double y, double z) {
	}


	@Override
	public void onTimerElapsed(long now_ms, double[] acc, double[] comp) {
	}


	@Override
	public void start() {
		super.start();
		stepDetector.load();
	}


	@Override
	public void stop() {
		super.stop();
		stepDetector.unload();
	}

}
