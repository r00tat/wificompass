/*
 * Created on Jan 20, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model;

import android.net.wifi.ScanResult;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = BssidResult.TABLE_NAME)
public class BssidResult {
	protected static final String TABLE_NAME = "bssidresults";

	@DatabaseField(generatedId = true)
	protected int id;

	@DatabaseField
	protected String bssid;

	@DatabaseField
	protected String ssid;

	@DatabaseField
	protected String capabilities;

	@DatabaseField
	protected int frequency;

	@DatabaseField
	protected int level;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	protected WifiScanResult scanResult;

	public BssidResult() {

	}

	public BssidResult(ScanResult sr) {
		this(sr, null);
	}

	public BssidResult(ScanResult sr, WifiScanResult result) {
		bssid = sr.BSSID;
		ssid = sr.SSID;
		capabilities = sr.capabilities;
		frequency = sr.frequency;
		level = sr.level;
		this.scanResult = result;
	}
	
	/**
	 * copy constructor
	 * @param copy
	 */
	public BssidResult(BssidResult copy){
		bssid=copy.bssid;
		ssid=copy.ssid;
		capabilities=copy.capabilities;
		frequency=copy.frequency;
		level=copy.level;
		scanResult=copy.scanResult;
	}

	/**
	 * @return the bssid
	 */
	public String getBssid() {
		return bssid;
	}

	/**
	 * @param bssid
	 *            the bssid to set
	 */
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	/**
	 * @return the ssid
	 */
	public String getSsid() {
		return ssid;
	}

	/**
	 * @param ssid
	 *            the ssid to set
	 */
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	/**
	 * @return the capabilities
	 */
	public String getCapabilities() {
		return capabilities;
	}

	/**
	 * @param capabilities
	 *            the capabilities to set
	 */
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency
	 *            the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the scanResult
	 */
	public WifiScanResult getScanResult() {
		return scanResult;
	}

	/**
	 * @param scanResult
	 *            the scanResult to set
	 */
	public void setScanResult(WifiScanResult scanResult) {
		this.scanResult = scanResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ssid + " " + bssid + " " + level + "dBm " + frequency + "MHz " + capabilities;
	}

}
