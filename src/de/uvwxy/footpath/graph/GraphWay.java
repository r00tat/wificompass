package de.uvwxy.footpath.graph;

import java.util.LinkedList;

/**
 * A class to maintain a way with given parameters found in OSM/XML data.
 * @author  Paul Smith
 */
public class GraphWay {
	// all nodes on this path ( ref0 -> ref1 -> ref2  -> ...)
	/**
	 * @uml.property  name="refs"
	 */
	private LinkedList<Integer> refs;
	/**
	 * @uml.property  name="id"
	 */
	private int id;
	/**
	 * @uml.property  name="wheelchair"
	 */
	private short wheelchair;

	// >0 := number correct steps given
	//  0 := no steps
	// -1 := undefined number of steps
	// -2 := elevator
	private int numSteps = 0;	
	
	// Float.MAX_VALUE == undefined!
	/**
	 * @uml.property  name="level"
	 */
	private float level;
	/**
	 * @uml.property  name="isIndoor"
	 */
	private boolean isIndoor;
	
	/**
	 * Constructor to create an empty way.
	 */
	public GraphWay() {
		this.refs = new LinkedList<Integer>();
		this.id = 0;
		this.wheelchair = 1;
		this.level = Float.MAX_VALUE;
	}
	
	/**
	 * Constructor to create a coordinate with given parameters.
	 * 
	 * @param refs  a LinkedList of Integers, references to GraphNodes
	 * @param id the id of this way
	 * @param wheelchair the value concerning the wheelchair attribute
	 * @param level the level of this way
	 */
	public GraphWay(LinkedList<Integer> refs, int id, short wheelchair, float level) {
		this.refs = refs;
		this.id = id;
		this.wheelchair = wheelchair;
		this.level = level;
	}
	
	/**
	 * @return
	 * @uml.property  name="refs"
	 */
	public LinkedList<Integer> getRefs() {
		return refs;
	}
	
	/**
	 * @return
	 * @uml.property  name="id"
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return
	 * @uml.property  name="wheelchair"
	 */
	public short getWheelchair() {
		return wheelchair;
	}
	
	public int getSteps(){
		return numSteps;
	}
	
	/**
	 * @return
	 * @uml.property  name="level"
	 */
	public float getLevel(){
		return level;
	}
	
	/**
	 * @return
	 * @uml.property  name="isIndoor"
	 */
	public boolean isIndoor(){
		return isIndoor;
	}
	
	/**
	 * @param refs
	 * @uml.property  name="refs"
	 */
	public void setRefs(LinkedList<Integer> refs) {
		this.refs = refs;
	}
	
	/**
	 * @param id
	 * @uml.property  name="id"
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @param wheelchair
	 * @uml.property  name="wheelchair"
	 */
	public void setWheelchair(short wheelchair) {
		this.wheelchair = wheelchair;
	}
	
	public void setSteps(int numSteps){
		this.numSteps = numSteps;
	}
	
	/**
	 * @param level
	 * @uml.property  name="level"
	 */
	public void setLevel(float level){
		this.level = level;
	}
	
	/**
	 * @param isIndoor
	 * @uml.property  name="isIndoor"
	 */
	public void setIndoor(boolean isIndoor){
		this.isIndoor = isIndoor;
	}
	
	public void addRef(int ref){
		this.refs.add(new Integer(ref));
	}
	
	public String toString(){
		String ret = "\nWay(" + this.id +"): ";
		ret += this.wheelchair >= 0 ? "(wheelchair)" : "(non-wheelchair)";
		ret += "\nRefs:";
		for(Integer ref: refs){
			ret += "\n    " + ref.intValue();
		}
		return ret;
	}
}
