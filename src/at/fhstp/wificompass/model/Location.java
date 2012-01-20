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
	protected double x;
	
	@DatabaseField
	protected double y;
	
//	protected double z;
	
	@DatabaseField
	protected double accurancy;
	
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
	public Location(double x,double y) {
		this(null,x,y,-1,null);
	}
	
	
	public Location(double x,double y,double accurancy) {
		this(null,x,y,-1,null);
	}
	
	
	public Location(String provider,double x,double y, double accurancy,Date timestamp){
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

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}


	public double getAccurancy() {
		return accurancy;
	}

	public void setAccurancy(double accurancy) {
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
	
	
	
	

}
