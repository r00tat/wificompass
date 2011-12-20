/*
 * Created on Dec 8, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.userlocation;

import java.util.Date;

/**
 * @author Paul Woelfel
 */
public class Location {
	
	protected double x;
	
	protected double y;
	
	protected double z;
	
	protected double accurancy;
	
	protected String provider;
	
	protected Date timestamp;
	
	public Location(){
		this(null,0,0,0,-1,null);
	}

	/**
	 * 
	 */
	public Location(double x,double y) {
		this(null,x,y,0,-1,null);
	}
	
	public Location(double x,double y,double z) {
		this(null,x,y,z,-1,null);
	}
	
	public Location(double x,double y,double z,double accurancy) {
		this(null,x,y,z,-1,null);
	}
	
	
	public Location(String provider,double x,double y,double z, double accurancy,Date timestamp){
		this.provider=provider;
		this.x=x;
		this.y=y;
		this.z=z;
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

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
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
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
	

}
