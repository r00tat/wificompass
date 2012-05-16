/*
 * Created on May 16, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * @author Paul Woelfel (paul@woelfel.at)
 */
public class CompassSensorWatcher implements SensorEventListener {

	protected SensorManager sensorManager;

	protected Sensor compass;

	protected Sensor accelerometer;

	protected Context context;

	float[] inR = new float[16];

	float[] I = new float[16];

	float[] gravity = new float[3];

	float[] geomag = new float[3];

	float[] orientVals = new float[3];

	float angle = 0;

	protected float minimumAngleChange = (float) Math.toRadians(2.0f);
	protected float smoothFactor = 0.4f;
	
	protected CompassListener listener;

	protected float lastAngle = 0f;

	public CompassSensorWatcher(Context context,CompassListener cl,float smoothFactor) {
		this.context = context;
		this.listener=cl;
		this.smoothFactor = smoothFactor;
		
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		try {
			sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_UI);
			sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		} catch (Exception e) {
			Logger.e("could not register listener", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {

		// Logger.d("sensor changed "+event);
		// we use TYPE_MAGNETIC_FIELD to get changes in the direction, but use SensorManager to get directions
		if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
			return;

		// Gets the value of the sensor that has been changed
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			gravity = event.values.clone();
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			geomag = event.values.clone();

			break;
		}

		// If gravity and geomag have values then find rotation matrix
		if (gravity != null && geomag != null) {

			// checks that the rotation matrix is found
			boolean success = SensorManager.getRotationMatrix(inR, I, gravity, geomag);
			if (success) {
				SensorManager.getOrientation(inR, orientVals);

				angle = (float) ToolBox.normalizeAngle(orientVals[0]);

				lowPassFilter();
				
				lastAngle = angle;

//				azimuthText = getAzimuthLetter(azimuth) + " " + Integer.toString((int) azimuth) + "°";

				if(listener!=null){
					listener.onCompassChanged(angle,getAzimuthLetter(angle));
				}
			}
		}
	}
	
	public void stop(){
		try {
			sensorManager.unregisterListener(this);
		} catch (Exception e) {
			Logger.w("could not unregister listener", e);
		}
	}

	public String getAzimuthLetter(float angle) {
		String letter = "";
		int a = (int) Math.toDegrees(angle);

		if (a < 23 || a >= 315) {
			letter = "N";
		} else if (a < 45 + 23) {
			letter = "NO";
		} else if (a < 90 + 23) {
			letter = "O";
		} else if (a < 135 + 23) {
			letter = "SO";
		} else if (a < (180 + 23)) {
			letter = "S";
		} else if (a < (225 + 23)) {
			letter = "SW";
		} else if (a < (270 + 23)) {
			letter = "W";
		} else {
			letter = "NW";
		}

		return letter;
	}

	protected void lowPassFilter() {
		angle = ToolBox.normalizeAngle(angle);
		
		float difference = Math.abs(angle - lastAngle);
		
		
		float halfCirle = (float) Math.PI;
		float wholeCircle = (float) (2 * Math.PI);
						
		if (difference < minimumAngleChange) {
			angle = lastAngle;
		} else  if (difference < halfCirle) {
			angle = lastAngle + smoothFactor * (angle - lastAngle);
		}
		else {
	        if (lastAngle > angle) {
	        	angle = (lastAngle + smoothFactor * ((wholeCircle + angle - lastAngle) % wholeCircle) + wholeCircle) % wholeCircle;
	        } 
	        else {
	        	angle = (lastAngle - smoothFactor * ((wholeCircle - angle + lastAngle) % wholeCircle) + wholeCircle) % wholeCircle;
	        }
		}
	}

}
