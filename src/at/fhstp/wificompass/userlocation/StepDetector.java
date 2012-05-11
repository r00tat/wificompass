/*
 * Created on May 11, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.userlocation;

import at.fhstp.wificompass.Logger;

/**
 * @author Paul Woelfel (paul@woelfel.at)
 */
public class StepDetector {
	protected static final int vhSize = 6;

	protected double[] values_history = new double[vhSize];

	protected int vhPointer = 0;

	public static final int WINDOW = 5;

	
	protected double a;

	protected double peak;

	protected int stepTimeoutMS;

	protected long lastStepTs = 0;

	// last acc is low pass filtered
	protected double[] lastAcc = new double[] {0.0, 0.0, 0.0};


	protected int round = 0;
	
	
	protected boolean logSteps=true;

	
	protected long lastUpdateTimestamp=0;

	public StepDetector( double a, double peak, int step_timeout_ms) {
		this.a = a;
		this.peak = peak;
		this.stepTimeoutMS = step_timeout_ms;
	}

	public synchronized void addSensorValues(long timestamp,float values[]) {
		// simple lowpass filter
		lastAcc[0]+=a*(values[0]-lastAcc[0]);
		lastAcc[1]+=a*(values[1]-lastAcc[1]);
		lastAcc[2]+=a*(values[2]-lastAcc[2]);
		lastUpdateTimestamp=timestamp;
	}

	protected double lowpassFilter(double oldValue, double newValue) {
		return oldValue + a * (newValue - oldValue);
	}

	/**
	 * This is called every INTERVAL_MS ms from the TimerTask.
	 */
	public synchronized boolean checkForStep() {
		boolean ret = false;

		// Get current time for time stamps
		

		addData(lastAcc[2]);

		// Check if a step is detected upon data
		if ((lastUpdateTimestamp - lastStepTs) > stepTimeoutMS) {

			for (int t = 1; t <= WINDOW; t++) {
				if ((values_history[(vhPointer - 1 - t + vhSize + vhSize) % vhSize] - values_history[(vhPointer - 1 + vhSize) % vhSize] > peak)) {

					if(logSteps)
						Logger.i("Detected step with t = " + t + ", diff = " + peak + " < "
							+ (values_history[(vhPointer - 1 - t + vhSize + vhSize) % vhSize] - values_history[(vhPointer - 1 + vhSize) % vhSize]));
					// Set latest detected step to "now"
					lastStepTs = lastUpdateTimestamp;
					// Call algorithm for navigation/updating position
					// st.trigger(now_ms, lCompass);
//					Logger.i( "Detected step  in  round = " + round + " @ " + now_ms);
					ret = true;
					break;
				}
			}

		}
		round++;
		return ret;
	}

	protected void addData(double value) {
		values_history[vhPointer % vhSize] = value;
		vhPointer++;
		vhPointer = vhPointer % vhSize;
	}

	/**
	 * @return the a
	 */
	public double getA() {
		return a;
	}

	/**
	 * @param a the a to set
	 */
	public void setA(double a) {
		this.a = a;
	}

	/**
	 * @return the peak
	 */
	public double getPeak() {
		return peak;
	}

	/**
	 * @param peak the peak to set
	 */
	public void setPeak(double peak) {
		this.peak = peak;
	}

	/**
	 * @return the stepTimeoutMS
	 */
	public int getStepTimeoutMS() {
		return stepTimeoutMS;
	}

	/**
	 * @param stepTimeoutMS the stepTimeoutMS to set
	 */
	public void setStepTimeoutMS(int stepTimeoutMS) {
		this.stepTimeoutMS = stepTimeoutMS;
	}

	/**
	 * @return the lastStepTs
	 */
	public long getLastStepTs() {
		return lastStepTs;
	}

	/**
	 * @return the lastAcc
	 */
	public double[] getLastAcc() {
		return lastAcc;
	}

	/**
	 * @return the round
	 */
	public int getRound() {
		return round;
	}

	/**
	 * @return the logSteps
	 */
	public boolean isLogSteps() {
		return logSteps;
	}

	/**
	 * @param logSteps the logSteps to set
	 */
	public void setLogSteps(boolean logSteps) {
		this.logSteps = logSteps;
	}

}
