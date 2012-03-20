/*
 * Created on Mar 19, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.userlocation;

import android.content.Context;
import at.fhstp.wificompass.Logger;
import de.uvwxy.footpath.core.StepDetection;
import de.uvwxy.footpath.core.StepTrigger;


public class StepDetectionProvider extends LocationProviderImpl implements StepTrigger {
	
	float step=0.0f;

	public final static String CALIB_DATA="SensorCalibration",PEAK="peak",TIMEOUT="timeout",FILTER="a",STEP="step";
	
	protected StepDetection stepDetector;
	
	public StepDetectionProvider(Context ctx){
		this(ctx,LocationServiceFactory.getLocationService());
	}
	
	
	public StepDetectionProvider(Context ctx,LocationService locationService){
		super(ctx,locationService);
		
		// this are the default values of footpath
					double a = ctx.getSharedPreferences(CALIB_DATA,0).getFloat(FILTER, 0.3f);
		double peak = ctx.getSharedPreferences(CALIB_DATA,0).getFloat(PEAK, 0.75f);
		int step_timeout_ms = ctx.getSharedPreferences(CALIB_DATA,0).getInt(TIMEOUT, 666);
		
		step = ctx.getSharedPreferences(CALIB_DATA,0).getInt(STEP, 666);
		
		stepDetector=new StepDetection(ctx, this, a,peak,step_timeout_ms);
//		stepDetector.load();
	}
	

	
	@Override
	public void trigger(long now_ms, double compDir) {
		// a step has been triggered
//		loc=new Location(getProviderName(),x,y,0,new Date());
		
		
		
		Logger.d("a step has been detected "+compDir);
	}


	@Override
	public void dataHookAcc(long now_ms, double x, double y, double z) {
	}


	@Override
	public void dataHookComp(long now_ms, double x, double y, double z) {
	}


	@Override
	public void timedDataHook(long now_ms, double[] acc, double[] comp) {
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
