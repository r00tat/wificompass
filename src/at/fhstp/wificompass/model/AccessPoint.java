package at.fhstp.wificompass.model;

import java.sql.SQLException;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
@DatabaseTable(tableName = AccessPoint.TABLE_NAME)
public class AccessPoint extends BaseDaoEnabled<AccessPoint, Integer> {
	protected static final String TABLE_NAME="accesspoints";
	
	/**
	 * @uml.property  name="id"
	 */
	@DatabaseField(generatedId=true)
	protected int id=0;
	
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
	 * @uml.property  name="location"
	 * @uml.associationEnd  
	 */
	@DatabaseField(foreign = true,foreignAutoRefresh = true, foreignAutoCreate = true)
	protected Location location;
	
	/**
	 * @uml.property  name="projectSite"
	 * @uml.associationEnd  
	 */
	@DatabaseField(foreign = true,foreignAutoRefresh = true)
	protected ProjectSite projectSite;
	
	/**
	 * @uml.property  name="calculated"
	 */
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
	
	/**
	 * @return
	 * @uml.property  name="ssid"
	 */
	public String getSsid() {
		return this.ssid;
	}
	
	/**
	 * @return
	 * @uml.property  name="bssid"
	 */
	public String getBssid() {
		return this.bssid;
	}

	/**
	 * @return  the location
	 * @uml.property  name="location"
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location  the location to set
	 * @uml.property  name="location"
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return  the projectSite
	 * @uml.property  name="projectSite"
	 */
	public ProjectSite getProjectSite() {
		return projectSite;
	}

	/**
	 * @param projectSite  the projectSite to set
	 * @uml.property  name="projectSite"
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
	 * @return  the calculated
	 * @uml.property  name="calculated"
	 */
	public boolean isCalculated() {
		return calculated;
	}

	/**
	 * @param calculated  the calculated to set
	 * @uml.property  name="calculated"
	 */
	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
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
	 * @return  the id
	 * @uml.property  name="id"
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param bssid  the bssid to set
	 * @uml.property  name="bssid"
	 */
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	/**
	 * @param ssid  the ssid to set
	 * @uml.property  name="ssid"
	 */
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	/* (non-Javadoc)
	 * @see com.j256.ormlite.misc.BaseDaoEnabled#delete()
	 */
	@Override
	public int delete() throws SQLException {
		if(location!=null&&location.getId()!=0) location.delete();
		return super.delete();
	}
}
