package at.fhstp.wificompass.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = AccessPoint.TABLE_NAME)
public class AccessPoint {
	protected static final String TABLE_NAME="accesspoints";
	
	@DatabaseField(generatedId=true)
	protected int id;
	
	@DatabaseField
	protected String bssid;
	
	@DatabaseField
	protected String ssid;
	
	@DatabaseField
	protected String capabilities;
	
	@DatabaseField
	protected int frequency;
	
	@DatabaseField(foreign = true,foreignAutoRefresh = true)
	protected ProjectSite projectSite;
	
	public AccessPoint() {
		
	}
	
	public AccessPoint(BssidResult bssidResult) {
		this.bssid = bssidResult.getBssid();
		this.ssid = bssidResult.getSsid();
		this.capabilities = bssidResult.getCapabilities();
		this.frequency = bssidResult.getFrequency();
	}
	
	public String getSsid() {
		return this.ssid;
	}
	
	public String getBssid() {
		return this.bssid;
	}
}
