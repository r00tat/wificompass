package at.fhstp.wificompass.userlocation;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import at.fhstp.wificompass.CompassListener;
import at.fhstp.wificompass.CompassMonitor;

/**
 * This class is fed with data from the Accelerometer and Compass sensors. If a step is detected on the acc data it calls the trigger function on its interface StepTrigger, with the given direction.
 * Usage: Create an object: stepDetection = new StepDetection(this, this, a, peak, step_timeout_ms);
 * 
 * @author Paul Smith
 */
public class StepDetection implements CompassListener {
	public static final long INTERVAL_MS = 1000 / 30;

	// Hold an interface to notify the outside world of detected steps
	/**
	 * @uml.property name="st"
	 * @uml.associationEnd
	 */
	protected StepTrigger st;

	// Context needed to get access to sensor service
	protected Context context;

	protected static SensorManager sm; // Holds references to the SensorManager

	// List<Sensor> lSensor; // List of all sensors

	protected float lastComp;

	protected Timer timer;

	protected StepDetector detector;

	protected Sensor accelerometer;

	/**
	 * Handles sensor events. Updates the sensor
	 */
	public SensorEventListener mySensorEventListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// Auto-generated method stub
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				st.onAccelerometerDataReceived(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]);
				// just update the oldest z value
				detector.addSensorValues(System.currentTimeMillis(), event.values);
				break;

			default:
			}// switch (event.sensor.getType())
		}
	};

	public StepDetection(Context context, StepTrigger st, double a, double peak, int step_timeout_ms) {
		this.context = context;
		this.st = st;

		this.detector = new StepDetector(a, peak, step_timeout_ms);
	}

	public void load() {
		load(SensorManager.SENSOR_DELAY_FASTEST);
		CompassMonitor.registerListener(context, this);
	}

	/**
	 * Enable step detection
	 */
	public void load(int sensorDelay) {

		if (timer == null) {
			// Sensors
			sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

			accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

			sm.registerListener(mySensorEventListener, accelerometer, sensorDelay);

			// Register timer
			timer = new Timer("UpdateData", false);
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					updateData();
				}
			};
			timer.schedule(task, 0, INTERVAL_MS);
		}
	}

	/**
	 * Disable step detection
	 */
	public void unload() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
			sm.unregisterListener(mySensorEventListener);
		}
	}

	/**
	 * This is called every INTERVAL_MS ms from the TimerTask.
	 */
	protected synchronized void updateData() {
		// Get current time for time stamps
		long now_ms = System.currentTimeMillis();

		st.onTimerElapsed(now_ms, detector.getLastAcc(), new double[] {lastComp});

		// Check if a step is detected upon data
		if (detector.checkForStep()) {
			// Call algorithm for navigation/updating position
			st.onStepDetected(now_ms, lastComp);

		}
	}

	/**
	 * @return
	 * @uml.property name="a"
	 */
	public double getA() {
		return detector.getA();
	}

	/**
	 * @return
	 * @uml.property name="peak"
	 */
	public double getPeak() {
		return detector.getPeak();
	}

	/**
	 * @return
	 * @uml.property name="step_timeout_ms"
	 */
	public int getStep_timeout_ms() {
		return detector.getStepTimeoutMS();
	}

	/**
	 * @param a
	 * @uml.property name="a"
	 */
	public void setA(double a) {
		detector.setA(a);
	}

	/**
	 * @param peak
	 * @uml.property name="peak"
	 */
	public void setPeak(double peak) {
		detector.setPeak(peak);
	}

	/**
	 * @param stepTimeoutMs
	 * @uml.property name="step_timeout_ms"
	 */
	public void setStep_timeout_ms(int stepTimeoutMs) {
		detector.setStepTimeoutMS(stepTimeoutMs);
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.CompassListener#onCompassChanged(float, java.lang.String)
	 */
	@Override
	public void onCompassChanged(float azimuth, float angle, String direction) {
		st.onCompassDataReceived(System.currentTimeMillis(), azimuth, 0, 0);
		this.lastComp=azimuth;
	}
}
