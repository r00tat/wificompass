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
 * @author  Paul Woelfel (paul@woelfel.at)
 */
@DatabaseTable(tableName = BssidSelection.TABLE_NAME)
public class BssidSelection extends BaseDaoEnabled<BssidSelection, Integer>{
	
	public static final String TABLE_NAME="bssidselections";
	
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
	 * @uml.property  name="active"
	 */
	@DatabaseField 
	protected boolean active=false;
	
	/**
	 * @uml.property  name="projectSite"
	 * @uml.associationEnd  
	 */
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
	 * @return  the active
	 * @uml.property  name="active"
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active  the active to set
	 * @uml.property  name="active"
	 */
	public void setActive(boolean active) {
		this.active = active;
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

	/**
	 * @return  the id
	 * @uml.property  name="id"
	 */
	public int getId() {
		return id;
	}
	
	

}
