package at.fhstp.wificompass.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = AccessPoint.TABLE_NAME)
public class AccessPoint extends BaseDaoEnabled<AccessPoint, Integer> {
	protected static final String TABLE_NAME="accesspoints";
	
	@DatabaseField(generatedId=true)
	protected int id=0;
	
	@DatabaseField
	protected String bssid;
	
	@DatabaseField
	protected String ssid;
	
	@DatabaseField
	protected String capabilities;
	
	@DatabaseField
	protected int frequency;
	
	@DatabaseField(foreign = true,foreignAutoRefresh = true)
	protected Location location;
	
	@DatabaseField(foreign = true,foreignAutoRefresh = true)
	protected ProjectSite projectSite;
	
	@DatabaseField
	protected boolean calculated = true;
	
	public AccessPoint() {
		
	}
	
	public AccessPoint(BssidResult bssidResult) {
		this.bssid = bssidResult.getBssid();
		this.ssid = bssidResult.getSsid();
		this.capabilities = bssidResult.getCapabilities();
		this.frequency = bssidResult.getFrequency();
	}
	
	public AccessPoint(AccessPoint copy){
		this.bssid=copy.bssid;
		ssid=copy.ssid;
		capabilities=copy.ssid;
		frequency=copy.frequency;
		calculated=copy.calculated;
		if(copy.location!=null)
			location=new Location(copy.location);
		else
			location=null;
		projectSite=copy.projectSite;
	}
	
	public String getSsid() {
		return this.ssid;
	}
	
	public String getBssid() {
		return this.bssid;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return the projectSite
	 */
	public ProjectSite getProjectSite() {
		return projectSite;
	}

	/**
	 * @param projectSite the projectSite to set
	 */
	public void setProjectSite(ProjectSite projectSite) {
		this.projectSite = projectSite;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AccessPoint("+id+") "+ssid+" "+bssid+" "+frequency+" "+capabilities+(location!=null?" "+location.toString():"");
	}

	/**
	 * @return the calculated
	 */
	public boolean isCalculated() {
		return calculated;
	}

	/**
	 * @param calculated the calculated to set
	 */
	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
	}

	/**
	 * @return the capabilities
	 */
	public String getCapabilities() {
		return capabilities;
	}

	/**
	 * @param capabilities the capabilities to set
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
	 * @param frequency the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param bssid the bssid to set
	 */
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	/**
	 * @param ssid the ssid to set
	 */
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
}
