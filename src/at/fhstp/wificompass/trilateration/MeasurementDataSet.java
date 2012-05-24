package at.fhstp.wificompass.trilateration;

import java.util.Vector;

import android.graphics.PointF;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public class MeasurementDataSet {

	public static final int VALUE_X = 0;

	public static final int VALUE_Y = 1;

	public static final int VALUE_RSSI = 3;

	/**
	 * @uml.property  name="x"
	 */
	protected float x;
	/**
	 * @uml.property  name="y"
	 */
	protected float y;
	/**
	 * @uml.property  name="rssi"
	 */
	protected float rssi;

	public MeasurementDataSet() {
	}

	public MeasurementDataSet(float x, float y, float rssi) {
		this.x = x;
		this.y = y;
		this.rssi = rssi;
	}

	/**
	 * @return
	 * @uml.property  name="x"
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x
	 * @uml.property  name="x"
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return
	 * @uml.property  name="y"
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y
	 * @uml.property  name="y"
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @return
	 * @uml.property  name="rssi"
	 */
	public float getRssi() {
		return rssi;
	}

	/**
	 * @param rssi
	 * @uml.property  name="rssi"
	 */
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
	public static float getMinimumValue(Vector<MeasurementDataSet> data,
			int value) {
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
	public static float getMaximumValue(Vector<MeasurementDataSet> data,
			int value) {
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

	/**
	 * Returns the sum of all values <b>VALUE_X</b>, <b>VALUE_Y</b> or
	 * <b>VALUE_RSSI</b> in a vector of <b>MeasurementDataSet</b> objects
	 */
	public static float getSum(Vector<MeasurementDataSet> data, int value) {
		float sum = 0.0f;
		
		for (MeasurementDataSet dataSet : data) {
			float currentValue = 0.0f;

			if (value == MeasurementDataSet.VALUE_X)
				currentValue = dataSet.getX();
			else if (value == MeasurementDataSet.VALUE_Y)
				currentValue = dataSet.getY();
			else if (value == MeasurementDataSet.VALUE_RSSI)
				currentValue = dataSet.getRssi();
			
			sum += currentValue;
		}
		
		return sum;
	}
}
