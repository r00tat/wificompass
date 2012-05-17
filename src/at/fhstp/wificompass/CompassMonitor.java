/*
 * Created on May 16, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass;

import java.util.ArrayList;

import android.content.Context;


/**
 * @author Paul Woelfel (paul@woelfel.at)
 */
public class CompassMonitor {
	
	static protected ArrayList<CompassListener> listeners=new ArrayList<CompassListener>();
	
	static protected CompassSensorWatcher monitor=null;
	
	static public synchronized void registerListener(Context context,CompassListener listener){
		
//		Logger.d("adding listener");
		if(listeners.size()==0){
			monitor=new CompassSensorWatcher(context,new CompassListener(){

				@Override
				public void onCompassChanged(float azimuth, float angle,String direction) {
					notifyListeners(azimuth,angle,direction);
				}
				
			},0.3f);
		}
		listeners.add(listener);
	}
	
	static synchronized public void unregisterListener(CompassListener listener){
		if (listeners != null && listener != null)
			listeners.remove(listener);
		
		
		if (listeners != null && listeners.size() == 0 && monitor != null) {
			try {
				monitor.stop();
			} catch (Exception e) {
				Logger.w("could not stop Compass Monitor", e);
			}
			monitor = null;
		}
	}
	
	static synchronized protected void notifyListeners(float azimuth,float angle, String direction){
//		Logger.d("notifying listeners");
		for(CompassListener l:listeners){
			try{
				l.onCompassChanged(azimuth,angle,direction);
			}catch(Exception ex){}
		}
	}

}
