/*
 * Created on Jan 20, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
@DatabaseTable(tableName=WifiScanResult.TABLE_NAME)
public class WifiScanResult extends BaseDaoEnabled<WifiScanResult, Integer>{
	public static final String TABLE_NAME="wifiscanresult";
	
	/**
	 * @uml.property  name="id"
	 */
	@DatabaseField(generatedId=true)
	protected int id;
	
	/**
	 * @uml.property  name="timestamp"
	 */
	@DatabaseField
	protected long timestamp;
	
	/**
	 * @uml.property  name="bssids"
	 */
	@ForeignCollectionField
	protected ForeignCollection<BssidResult> bssids;
	
	/**
	 * @uml.property  name="location"
	 * @uml.associationEnd  
	 */
	@DatabaseField(foreign = true,foreignAutoRefresh = true, foreignAutoCreate = true)
	protected Location location;
	
	/**
	 * @uml.property  name="projectLocation"
	 * @uml.associationEnd  
	 */
	@DatabaseField(foreign=true,foreignAutoRefresh=true)
	protected ProjectSite projectLocation;
	
	
	protected ArrayList<BssidResult> tempBssids;
	
	public WifiScanResult(){
		
	}
	
	public WifiScanResult(long timestamp,Location location,ProjectSite projectLocation){
		this.timestamp=timestamp;
		this.location=location;
		this.projectLocation=projectLocation;
	}
	
	/**
	 * copy constructor
	 * @param copy
	 */
	public WifiScanResult(WifiScanResult copy){
		timestamp=copy.timestamp;
		if(copy.location!=null)
			location=new Location(copy.location);
		else
			location=null;
		projectLocation=copy.projectLocation;
	}


	/**
	 * @return  the timestamp
	 * @uml.property  name="timestamp"
	 */
	public long getTimestamp() {
		return timestamp;
	}


	/**
	 * @param timestamp  the timestamp to set
	 * @uml.property  name="timestamp"
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
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
	 * @return  the projectLocation
	 * @uml.property  name="projectLocation"
	 */
	public ProjectSite getProjectLocation() {
		return projectLocation;
	}


	/**
	 * @param projectLocation  the projectLocation to set
	 * @uml.property  name="projectLocation"
	 */
	public void setProjectLocation(ProjectSite projectLocation) {
		this.projectLocation = projectLocation;
	}


	/**
	 * @return  the id
	 * @uml.property  name="id"
	 */
	public int getId() {
		return id;
	}


	/**
	 * @return  the bssids
	 * @uml.property  name="bssids"
	 */
	public Collection<BssidResult> getBssids() {
		if(tempBssids!=null&&tempBssids.size()>0){
			return tempBssids;
		}else {
			return bssids;
		}
	}
	
	/**
	 * 
	 * add a BssidResult to the temporary Collection
	 * @param bssidResult
	 */
	public void addTempBssid(BssidResult bssidResult){
		if(tempBssids==null){
			tempBssids=new ArrayList<BssidResult>();
		}
		tempBssids.add(bssidResult);
	}
	
	/**
	 * get the temporary Bssid Results, which are not persisted.
	 * @return tempBssids
	 */
	public ArrayList<BssidResult> getTempBssids(){
		return tempBssids;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName()+"("+id+") "+timestamp+(location!=null?" "+location.toString():"");
	}

	/* (non-Javadoc)
	 * @see com.j256.ormlite.misc.BaseDaoEnabled#delete()
	 */
	@Override
	public int delete() throws SQLException {
		for(BssidResult br: bssids){
			br.delete();
		}
		return super.delete();
	}
	
	

}
