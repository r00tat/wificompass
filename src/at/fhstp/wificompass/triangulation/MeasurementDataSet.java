package at.fhstp.wificompass.triangulation;

public class MeasurementDataSet {
	protected float x;
	protected float y;
	protected float rssi;
	
	public MeasurementDataSet() {}

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
}
