/*
 * Created on Dec 29, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import at.fhstp.wificompass.ApplicationContext;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;

public class SensorsActivity extends Activity implements SensorEventListener {

	protected static final Logger log = new Logger(SensorsActivity.class);

	protected SensorManager sensorManager;

	protected Sensor accelerometer, gyroscope;

	protected float[] gravity;

	protected float[] linear_acceleration;

	protected float[] rotation;
	
	protected final float alpha = 0.8f;

	public SensorsActivity() {
		gravity = new float[3];
		linear_acceleration = new float[3];
		rotation=new float[3];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log.debug("created sensors activity");
		setContentView(R.layout.sensors);

		// log.debug("registering sensor listener");
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		// sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		log.debug("unregistering sensor listener");
		sensorManager.unregisterListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		log.debug("setting context");
		ApplicationContext.setContext(this);
		log.debug("registering sensor listener");
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			log.debug("accelerometer sensor changed!");
//			gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
//			gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
//			gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
//
//			linear_acceleration[0] = event.values[0] - gravity[0];
//			linear_acceleration[1] = event.values[1] - gravity[1];
//			linear_acceleration[2] = event.values[2] - gravity[2];

			linear_acceleration[0] = event.values[0];
			linear_acceleration[1] = event.values[1];
			linear_acceleration[2] = event.values[2];

			
			((TextView) findViewById(R.id.sensors_accelerometer_text)).setText(getString(R.string.sensors_accelerometer_format,
					linear_acceleration[0], linear_acceleration[1], linear_acceleration[2]));
			
		} else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			log.debug("gyroscope sensor changed");
			rotation[0]= event.values[0];
			rotation[1]= event.values[1];
			rotation[2]= event.values[2];
			
			
			((TextView) findViewById(R.id.sensors_gyroscope_text)).setText(getString(R.string.sensors_gyroscope_format,
					rotation[0],rotation[1],rotation[2]));
			
			
		} else {
			log.debug("sensor unkown");
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
