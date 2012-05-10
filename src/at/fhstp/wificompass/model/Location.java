/*
 * Created on Dec 8, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author  Paul Woelfel
 */
@DatabaseTable(tableName = Location.TABLE_NAME)
public class Location extends BaseDaoEnabled<Location,Integer>{
	public static final String TABLE_NAME="locations";
	
	public static final String PROVIDER_NONE="defined";
	
	/**
	 * @uml.property  name="id"
	 */
	@DatabaseField(generatedId=true)
	protected int id;
	
	/**
	 * @uml.property  name="x"
	 */
	@DatabaseField
	protected float x;
	
	/**
	 * @uml.property  name="y"
	 */
	@DatabaseField
	protected float y;
	
//	protected float z;
	
	/**
	 * @uml.property  name="accurancy"
	 */
	@DatabaseField
	protected float accurancy;
	
	/**
	 * @uml.property  name="provider"
	 */
	@DatabaseField
	protected String provider;
	
	
	/**
	 * @uml.property  name="timestamp"
	 */
	protected Date timestamp;
	
	/**
	 * @uml.property  name="timestampmilis"
	 */
	@DatabaseField
	protected long timestampmilis;
	
	public Location(){
		this(PROVIDER_NONE,0,0,-1,null);
	}

	/**
	 * 
	 */
	public Location(float x,float y) {
		this(PROVIDER_NONE,x,y,-1,null);
	}
	
	
	public Location(float x,float y,float accurancy) {
		this(PROVIDER_NONE,x,y,-1,null);
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
	
	/**
	 * copy constructor
	 * @param copy
	 */
	public Location(Location copy){
		x=copy.x;
		y=copy.y;
//		z=copy.z;
		accurancy=copy.accurancy;
		provider=copy.provider;
		timestamp=copy.timestamp;
		timestampmilis=copy.timestampmilis;
	}

	/**
	 * @return
	 * @uml.property  name="x"
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x
	 * @uml.property  name="x"
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return
	 * @uml.property  name="y"
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y
	 * @uml.property  name="y"
	 */
	public void setY(float y) {
		this.y = y;
	}


	/**
	 * @return
	 * @uml.property  name="accurancy"
	 */
	public float getAccurancy() {
		return accurancy;
	}

	/**
	 * @param accurancy
	 * @uml.property  name="accurancy"
	 */
	public void setAccurancy(float accurancy) {
		this.accurancy = accurancy;
	}

	/**
	 * @return
	 * @uml.property  name="provider"
	 */
	public String getProvider() {
		return provider;
	}

	/**
	 * @param provider
	 * @uml.property  name="provider"
	 */
	public void setProvider(String provider) {
		this.provider = provider;
	}

	/**
	 * @return
	 * @uml.property  name="timestamp"
	 */
	public Date getTimestamp() {
		if(timestamp==null){
			timestamp=new Date(timestampmilis);
		}
		return timestamp;
	}

	/**
	 * @param timestamp
	 * @uml.property  name="timestamp"
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
		timestampmilis=timestamp.getTime();
	}

	/**
	 * @return  the timestampmilis
	 * @uml.property  name="timestampmilis"
	 */
	public long getTimestampmilis() {
		return timestampmilis;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Location("+id+") "+x+","+y+" accurate "+accurancy+ " "+ timestamp.toLocaleString() ;
	}

	/**
	 * @return  the id
	 * @uml.property  name="id"
	 */
	public int getId() {
		return id;
	}

}
