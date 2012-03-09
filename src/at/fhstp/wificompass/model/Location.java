/*
 * Created on Dec 8, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Paul Woelfel
 */
@DatabaseTable(tableName = Location.TABLE_NAME)
public class Location {
	public static final String TABLE_NAME="locations";
	
	@DatabaseField(generatedId=true)
	protected int id;
	
	@DatabaseField
	protected float x;
	
	@DatabaseField
	protected float y;
	
//	protected float z;
	
	@DatabaseField
	protected float accurancy;
	
	@DatabaseField
	protected String provider;
	
	
	protected Date timestamp;
	
	@DatabaseField
	protected long timestampmilis;
	
	public Location(){
		this(null,0,0,-1,null);
	}

	/**
	 * 
	 */
	public Location(float x,float y) {
		this(null,x,y,-1,null);
	}
	
	
	public Location(float x,float y,float accurancy) {
		this(null,x,y,-1,null);
	}
	
	
	public Location(String provider,float x,float y, float accurancy,Date timestamp){
		this.provider=provider;
		this.x=x;
		this.y=y;
		this.accurancy=accurancy;
		if(timestamp==null){
			this.timestamp=new Date();
		}else {
			this.timestamp=timestamp;
		}
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


	public float getAccurancy() {
		return accurancy;
	}

	public void setAccurancy(float accurancy) {
		this.accurancy = accurancy;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public Date getTimestamp() {
		if(timestamp==null){
			timestamp=new Date(timestampmilis);
		}
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
		timestampmilis=timestamp.getTime();
	}

	/**
	 * @return the timestampmilis
	 */
	public long getTimestampmilis() {
		return timestampmilis;
	}
	
	
	
	

}
