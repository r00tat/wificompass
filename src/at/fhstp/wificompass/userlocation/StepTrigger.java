package at.fhstp.wificompass.userlocation;

/**
 * An interface to be notified abuot detected steps and their directions. Also
 * there are hooks to to obtain values from sensors.
 * 
 * @author Paul Smith, Paul Woelfel
 *
 */
public interface StepTrigger {
	
	/**
	 * Called each time a step is triggered.
	 * 
	 * @param now_ms the time stamp of the detected step
	 * @param compDir the compass bearing
	 */
	public void onStepDetected(long now_ms, double compDir);
	
	/**
	 * Called each time the accelerometer sensor values change
	 * 
	 * @param now_ms the time stamp of the changed values
	 * @param x x-axis
	 * @param y y-axis
	 * @param z z-axis
	 */
	public void onAccelerometerDataReceived(long now_ms, double x, double y, double z);
	
	/**
	 * Called each time the compass sensor values change
	 * 
	 * @param now_ms the time stamp of the changed values
	 * @param x x-axis
	 * @param y y-axis
	 * @param z z-axis
	 */
	public void onCompassDataReceived(long now_ms, double x, double y, double z);
	
	/**
	 * Called each time a sample is used to detect steps
	 * 
	 * @param now_ms the time stamp of the sample
	 * @param acc the accelerometer value (z-axis)
	 * @param comp the compass bearing
	 */
	public void onTimerElapsed(long now_ms, double[] acc, double[] comp);
}
