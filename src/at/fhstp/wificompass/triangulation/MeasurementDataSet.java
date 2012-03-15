package at.fhstp.wificompass.triangulation;

import java.util.Vector;

import android.graphics.PointF;

public class MeasurementDataSet {

	public static final int VALUE_X = 0, VALUE_Y = 1, VALUE_RSSI = 3;

	protected float x;
	protected float y;
	protected float rssi;

	public MeasurementDataSet() {
	}

	public MeasurementDataSet(float x, float y, float rssi) {
		this.x = x;
		this.y = y;
		this.rssi = rssi;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getRssi() {
		return rssi;
	}

	public void setRssi(float rssi) {
		this.rssi = rssi;
	}

	public PointF getPointF() {
		return new PointF(this.x, this.y);
	}

	/**
	 * Returns the minimum value of the vector of <b>MeasurementDataSet</b>s.
	 * The <b>value</b> is one of the static constants <b>VALUE_X</b>,
	 * <b>VALUE_Y</b> or <b>VALUE_RSSI</b> of the <b>MeasurementDataSet</b>
	 * class
	 * 
	 * @param data
	 *            The vector of <b>MeasurementDataSet</b> objects
	 * @return The minimum x value of all
	 */
	public static float getMinimumValue(Vector<MeasurementDataSet> data, int value) {
		float minValue = 0.0f;

		for (MeasurementDataSet dataSet : data) {
			float currentValue = 0.0f;
			
			if (value == MeasurementDataSet.VALUE_X)
				currentValue = dataSet.getX();
			else if (value == MeasurementDataSet.VALUE_Y)
				currentValue = dataSet.getY();
			else if (value == MeasurementDataSet.VALUE_RSSI)
				currentValue = dataSet.getRssi();
		
			if ((data.indexOf(dataSet) == 0) || (currentValue < minValue)) {
				minValue = currentValue;
			}
		}

		return minValue;
	}
	
	/**
	 * Returns the minimum value of the vector of <b>MeasurementDataSet</b>s.
	 * The <b>value</b> is one of the static constants <b>VALUE_X</b>,
	 * <b>VALUE_Y</b> or <b>VALUE_RSSI</b> of the <b>MeasurementDataSet</b>
	 * class
	 * 
	 * @param data
	 *            The vector of <b>MeasurementDataSet</b> objects
	 * @return The minimum x value of all
	 */
	public static float getMaximumValue(Vector<MeasurementDataSet> data, int value) {
		float maxValue = 0.0f;

		for (MeasurementDataSet dataSet : data) {
			float currentValue = 0.0f;
			
			if (value == MeasurementDataSet.VALUE_X)
				currentValue = dataSet.getX();
			else if (value == MeasurementDataSet.VALUE_Y)
				currentValue = dataSet.getY();
			else if (value == MeasurementDataSet.VALUE_RSSI)
				currentValue = dataSet.getRssi();
		
			if ((data.indexOf(dataSet) == 0) || (currentValue > maxValue)) {
				maxValue = currentValue;
			}
		}

		return maxValue;
	}
}
