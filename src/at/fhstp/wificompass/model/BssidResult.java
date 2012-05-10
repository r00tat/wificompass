/*
 * Created on Jan 20, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model;

import java.sql.SQLException;

import android.net.wifi.ScanResult;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
@DatabaseTable(tableName = BssidResult.TABLE_NAME)
public class BssidResult extends BaseDaoEnabled<BssidResult,Integer> {
	protected static final String TABLE_NAME = "bssidresults";

	/**
	 * @uml.property  name="id"
	 */
	@DatabaseField(generatedId = true)
	protected int id;

	/**
	 * @uml.property  name="bssid"
	 */
	@DatabaseField
	protected String bssid;

	/**
	 * @uml.property  name="ssid"
	 */
	@DatabaseField
	protected String ssid;

	/**
	 * @uml.property  name="capabilities"
	 */
	@DatabaseField
	protected String capabilities;

	/**
	 * @uml.property  name="frequency"
	 */
	@DatabaseField
	protected int frequency;

	/**
	 * @uml.property  name="level"
	 */
	@DatabaseField
	protected int level;

	/**
	 * @uml.property  name="scanResult"
	 * @uml.associationEnd  
	 */
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
	 * @return  the bssid
	 * @uml.property  name="bssid"
	 */
	public String getBssid() {
		return bssid;
	}

	/**
	 * @param bssid  the bssid to set
	 * @uml.property  name="bssid"
	 */
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	/**
	 * @return  the ssid
	 * @uml.property  name="ssid"
	 */
	public String getSsid() {
		return ssid;
	}

	/**
	 * @param ssid  the ssid to set
	 * @uml.property  name="ssid"
	 */
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	/**
	 * @return  the capabilities
	 * @uml.property  name="capabilities"
	 */
	public String getCapabilities() {
		return capabilities;
	}

	/**
	 * @param capabilities  the capabilities to set
	 * @uml.property  name="capabilities"
	 */
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	/**
	 * @return  the frequency
	 * @uml.property  name="frequency"
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency  the frequency to set
	 * @uml.property  name="frequency"
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return  the level
	 * @uml.property  name="level"
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level  the level to set
	 * @uml.property  name="level"
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return  the id
	 * @uml.property  name="id"
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return  the scanResult
	 * @uml.property  name="scanResult"
	 */
	public WifiScanResult getScanResult() {
		return scanResult;
	}

	/**
	 * @param scanResult  the scanResult to set
	 * @uml.property  name="scanResult"
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

	/* (non-Javadoc)
	 * @see com.j256.ormlite.misc.BaseDaoEnabled#delete()
	 */
	@Override
	public int delete() throws SQLException {
		return super.delete();
	}

}
