/*
 * Created on May 8, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Paul Woelfel (paul@woelfel.at)
 */
@DatabaseTable(tableName = BssidSelection.TABLE_NAME)
public class BssidSelection extends BaseDaoEnabled<BssidSelection, Integer>{
	
	public static final String TABLE_NAME="bssidselections";
	
	@DatabaseField(generatedId = true)
	protected int id;

	@DatabaseField
	protected String bssid;
	
	@DatabaseField 
	protected boolean active=false;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	protected ProjectSite projectSite;
	
	public BssidSelection(){
		this(null,null,true);
	}
	
	public BssidSelection(ProjectSite site,String bssid, boolean active){
		this.projectSite=site;
		this.bssid=bssid;
		this.active=active;
		
	}
	
	public BssidSelection(BssidSelection copy){
		this.active=copy.active;
		this.bssid=copy.bssid;
		this.projectSite=copy.projectSite;
	}

	/**
	 * @return the bssid
	 */
	public String getBssid() {
		return bssid;
	}

	/**
	 * @param bssid the bssid to set
	 */
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
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

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	

}
