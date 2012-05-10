/*
 * Created on Dec 31, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model;

import android.hardware.SensorEvent;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
@DatabaseTable(tableName = "sensordata")
public class SensorData extends BaseDaoEnabled<SensorData,Integer>{
	
	/**
	 * @uml.property  name="id"
	 */
	@DatabaseField(generatedId = true)
	protected int id;
	
	/**
	 * @uml.property  name="sensorName"
	 */
	@DatabaseField
	protected String sensorName;
	
	/**
	 * @uml.property  name="sensorType"
	 */
	@DatabaseField
	protected int sensorType;
	
	/**
	 * @uml.property  name="accuracy"
	 */
	@DatabaseField
	protected int accuracy;
	
	/**
	 * @uml.property  name="timestamp"
	 */
	@DatabaseField
	protected long timestamp;
	
	/**
	 * @uml.property  name="length"
	 */
	@DatabaseField
	protected int length;
	
	// not more than 4 values are expected
	/**
	 * @uml.property  name="value0"
	 */
	@DatabaseField
	protected float value0;

	/**
	 * @uml.property  name="value1"
	 */
	@DatabaseField
	protected float value1;

	/**
	 * @uml.property  name="value2"
	 */
	@DatabaseField
	protected float value2;

	/**
	 * @uml.property  name="value3"
	 */
	@DatabaseField
	protected float value3;
	
	/**
	 * @uml.property  name="normalizedValue"
	 */
	@DatabaseField
	protected float normalizedValue;
	
	public static final String FIELD_TYPE = "sensorType";

	public static final String FIELD_NAME = "sensorName";

	public static final String FIELD_TIMESTAMP = "timestamp";

	
	public SensorData(){
		
	}

	public SensorData(SensorEvent event){
		sensorName=event.sensor.getName();
		sensorType=(event.sensor.getType());
		timestamp=(event.timestamp);
		accuracy=(event.accuracy);

		if (event.values.length >= 1)
			value0=(event.values[0]);
		if (event.values.length >= 2)
			value1=(event.values[1]);
		if (event.values.length >= 3)
			value2=(event.values[2]);
		if (event.values.length >= 4)
			value3=(event.values[3]);
		
		normalizedValue=(value0<0?value0*-1:value0)+(value1<0?value1*-1:value1)+(value2<0?value2*-1:value2)+(value3<0?value3*-1:value3);
		length=event.values.length;
		
		
	}

	/**
	 * @return  the sensorName
	 * @uml.property  name="sensorName"
	 */
	public String getSensorName() {
		return sensorName;
	}



	/**
	 * @param sensorName  the sensorName to set
	 * @uml.property  name="sensorName"
	 */
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}



	/**
	 * @return  the sensorType
	 * @uml.property  name="sensorType"
	 */
	public int getSensorType() {
		return sensorType;
	}



	/**
	 * @param sensorType  the sensorType to set
	 * @uml.property  name="sensorType"
	 */
	public void setSensorType(int sensorType) {
		this.sensorType = sensorType;
	}



	/**
	 * @return  the accuracy
	 * @uml.property  name="accuracy"
	 */
	public int getAccuracy() {
		return accuracy;
	}



	/**
	 * @param accuracy  the accuracy to set
	 * @uml.property  name="accuracy"
	 */
	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
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
	 * @return  the value0
	 * @uml.property  name="value0"
	 */
	public float getValue0() {
		return value0;
	}



	/**
	 * @param value0  the value0 to set
	 * @uml.property  name="value0"
	 */
	public void setValue0(float value0) {
		this.value0 = value0;
	}



	/**
	 * @return  the value1
	 * @uml.property  name="value1"
	 */
	public float getValue1() {
		return value1;
	}



	/**
	 * @param value1  the value1 to set
	 * @uml.property  name="value1"
	 */
	public void setValue1(float value1) {
		this.value1 = value1;
	}



	/**
	 * @return  the value2
	 * @uml.property  name="value2"
	 */
	public float getValue2() {
		return value2;
	}



	/**
	 * @param value2  the value2 to set
	 * @uml.property  name="value2"
	 */
	public void setValue2(float value2) {
		this.value2 = value2;
	}



	/**
	 * @return  the value3
	 * @uml.property  name="value3"
	 */
	public float getValue3() {
		return value3;
	}



	/**
	 * @param value3  the value3 to set
	 * @uml.property  name="value3"
	 */
	public void setValue3(float value3) {
		this.value3 = value3;
	}



	/**
	 * @return  the normalizedValue
	 * @uml.property  name="normalizedValue"
	 */
	public float getNormalizedValue() {
		return normalizedValue;
	}



	/**
	 * @param normalizedValue  the normalizedValue to set
	 * @uml.property  name="normalizedValue"
	 */
	public void setNormalizedValue(float normalizedValue) {
		this.normalizedValue = normalizedValue;
	}



	/**
	 * @return  the id
	 * @uml.property  name="id"
	 */
	public int getId() {
		return id;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return sensorName+" ("+sensorType+") "+timestamp+": "+value0+" "+value1+" "+value2+" "+value3+" "+accuracy+" "+normalizedValue;
	}



	/**
	 * @return  the length
	 * @uml.property  name="length"
	 */
	public int getLength() {
		return length;
	}



	/**
	 * @param length  the length to set
	 * @uml.property  name="length"
	 */
	public void setLength(int length) {
		this.length = length;
	}
	
	

}
