/*
 * Created on Apr 3, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.ToolBox;

/**
 * @author Paul Woelfel (paul@woelfel.at)
 */
public class UserCompassDrawable extends MultiTouchDrawable implements SensorEventListener {

	protected static BitmapDrawable icon;

	protected SensorManager sensorManager;

	protected Sensor compass, accelerometer;

	float[] inR = new float[16];

	float[] I = new float[16];

	float[] gravity = new float[3];

	float[] geomag = new float[3];

	float[] orientVals = new float[3];

	protected TextPopupDrawable popup;
	
	int popupAngle=0;

	 double azimuth = 0;
	
	// double pitch = 0;
	//
	// double roll = 0;

	/**
	 * <p>
	 * Only change the angle, if the compass changes more than minAngleChange degrees. Per default 3°.
	 * </p>
	 */
	protected static final float minAngleChange = (float) Math.toRadians(3d), minAngleChangeForPopup = (float) Math.toRadians(1);

	/**
	 * @param context
	 * @param superDrawable
	 */
	public UserCompassDrawable(Context context, MultiTouchDrawable superDrawable) {
		super(context, superDrawable);
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.north_small);
		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();

		sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
		compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		popup = new TextPopupDrawable(ctx, this.superDrawable);
		popup.setText("0°");
		popup.setActive(true);
		popup.setPersistent(true);
		popup.setWidth(40);
		popup.setRelativePosition(60,0);

	}

	public void start() {
		try {
			sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_UI);
			sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		} catch (Exception e) {
			Logger.w("could not register listener", e);
		}
	}

	public void stop() {
		try {
			sensorManager.unregisterListener(this);
		} catch (Exception e) {
			Logger.w("could not unregister listener", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#getDrawable()
	 */
	@Override
	public Drawable getDrawable() {
		return icon;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#isScalable()
	 */
	@Override
	public boolean isScalable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#isRotateable()
	 */
	@Override
	public boolean isRotateable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#isDragable()
	 */
	@Override
	public boolean isDragable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#isOnlyInSuper()
	 */
	@Override
	public boolean isOnlyInSuper() {
		return false;
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
				 azimuth =Math.toDegrees(ToolBox.normalizeAngle(orientVals[0]));
				// pitch = Math.toDegrees(orientVals[1]);
				// roll = Math.toDegrees(orientVals[2]);
				// Logger.d("azimuth: "+azimuth+" pitch: "+pitch+" roll: "+roll);
				float newAngle = (float) (Math.PI * 2 - ToolBox.normalizeAngle(orientVals[0])) ;
//				if ((int)azimuth!=popupAngle) {
//					popupAngle=(int)azimuth;
//					popup.setText(ctx.getString(R.string.user_compass_degrees, popupAngle));
//					refresher.invalidate();
//				}
				
				if (Math.abs(angle - newAngle) > minAngleChange) {
					this.setAngle(newAngle);
					// we do not have to set the angle, the angle of the popup is always 0.
//					popup.setAngle(-newAngle);
					popupAngle=(int)azimuth;
					popup.setText(ctx.getString(R.string.user_compass_degrees, popupAngle));
					refresher.invalidate();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		stop();
		super.finalize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#load()
	 */
	@Override
	public void load() {
		super.load();
		start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#unload()
	 */
	@Override
	public void unload() {
		stop();
		super.unload();
	}

}
