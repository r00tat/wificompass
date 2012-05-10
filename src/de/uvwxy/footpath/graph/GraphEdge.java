package de.uvwxy.footpath.graph;

/**
 * A class to maintain an edge in the graph.
 * @author  Paul Smith
 */
public class GraphEdge {
	/**
	 * @uml.property  name="node0"
	 * @uml.associationEnd  
	 */
	private GraphNode node0;
	/**
	 * @uml.property  name="node1"
	 * @uml.associationEnd  
	 */
	private GraphNode node1;
	/**
	 * @uml.property  name="len"
	 */
	private double len;
	private double bearing;
	/**
	 * @uml.property  name="wheelchair"
	 */
	private short wheelchair;
	/**
	 * @uml.property  name="isStairs"
	 */
	private boolean isStairs = false;
	/**
	 * @uml.property  name="isElevator"
	 */
	private boolean isElevator = false;
	
	// >0 := number correct steps given
	//  0 := no steps
	// -1 := undefined number of steps
	// -2 := elevator
	private int numSteps = 0;
	
	/**
	 * @uml.property  name="level"
	 */
	private float level;
	/**
	 * @uml.property  name="isIndoor"
	 */
	private boolean isIndoor;
	
	/**
	 * Constructor to create an empty edge with everything set to 0/null/false
	 */
	public GraphEdge() {
		this.node0 = null;
		this.node1 = null;
		this.len = 0.0;
		this.wheelchair = 1;
		this.level = Float.MAX_VALUE;
		this.isIndoor = false;
	}
	
	/**
	 * Constructor to create an edge with given parameters.
	 * 
	 * @param node0 the first GraphNode
	 * @param node1 the second GraphNode
	 * @param len the length of this edge
	 * @param compDir the direction of this edge (node0 -> node1)
	 * @param wheelchair the value concerning the wheelchair attribute
	 * @param level the level of this edge
	 * @param isIndoor true if is indoor
	 */
	public GraphEdge(GraphNode node0, GraphNode node1, double len, double compDir, short wheelchair, float level, boolean isIndoor) {
		this.node0 = node0;
		this.node1 = node1;
		this.len = len;
		this.bearing = compDir;
		this.wheelchair = wheelchair;
		this.level = level;
		this.isIndoor = isIndoor;
	}
	
	public double getCompDir() {
		return bearing;
	}
	
	/**
	 * @return
	 * @uml.property  name="node0"
	 */
	public GraphNode getNode0() {
		return node0;
	}
	
	/**
	 * @return
	 * @uml.property  name="node1"
	 */
	public GraphNode getNode1() {
		return node1;
	}
	
	/**
	 * @return
	 * @uml.property  name="len"
	 */
	public double getLen() {
		return len;
	}
	
	/**
	 * @return
	 * @uml.property  name="wheelchair"
	 */
	public short getWheelchair() {
		return wheelchair;
	}
	
	/**
	 * @return
	 * @uml.property  name="isStairs"
	 */
	public boolean isStairs(){
		return isStairs;
	}
	
	/**
	 * @return
	 * @uml.property  name="isElevator"
	 */
	public boolean isElevator(){
		return isElevator;
	}
	
	public int getSteps(){
		return numSteps;
	}
	
	/**
	 * @return
	 * @uml.property  name="level"
	 */
	public float getLevel() {
		return level;
	}
	
	/**
	 * @return
	 * @uml.property  name="isIndoor"
	 */
	public boolean isIndoor(){
		return isIndoor;
	}
	
	public void setCompDir(double compDir) {
		this.bearing = compDir;
	}
	
	/**
	 * @param node0
	 * @uml.property  name="node0"
	 */
	public void setNode0(GraphNode node0) {
		this.node0 = node0;
	}
	
	/**
	 * @param node1
	 * @uml.property  name="node1"
	 */
	public void setNode1(GraphNode node1) {
		this.node1 = node1;
	}
	
	/**
	 * @param len
	 * @uml.property  name="len"
	 */
	public void setLen(double len) {
		this.len = len;
	}
	
	/**
	 * @param wheelchair
	 * @uml.property  name="wheelchair"
	 */
	public void setWheelchair(short wheelchair) {
		this.wheelchair = wheelchair;
	}
	
	/**
	 * @param isStairs
	 * @uml.property  name="isStairs"
	 */
	public void setStairs(boolean isStairs) {
		this.isStairs = isStairs;
	}
	
	/**
	 * @param isElevator
	 * @uml.property  name="isElevator"
	 */
	public void setElevator(boolean isElevator) {
		this.isElevator = isElevator;
	}
	
	public void setSteps(int numSteps){
		this.numSteps = numSteps;
		if(numSteps>0)
			this.setWheelchair((short)-1);//if steps, NO wheelchair
	}
	
	/**
	 * @param level
	 * @uml.property  name="level"
	 */
	public void setLevel(float level) {
		this.level = level;
	}
	
	public void setLevel(boolean isIndoor){
		this.isIndoor = isIndoor;
	}

	public boolean equals(GraphEdge edge){
		if(edge == null)
			return false;
		return this.node0.equals(edge.getNode0()) && this.node1.equals(edge.getNode1())
				|| this.node0.equals(edge.getNode1()) && this.node1.equals(edge.getNode0());
	}
	
	public boolean contains(GraphNode node){
		return getNode0().equals(node) || getNode1().equals(node);
	}
	
	public String toString(){
		String ret = "\nEdge(" + this.node0.getId() + " to " + this.node1.getId() + "): ";
		ret += "\n    Length: " + this.len;
		ret += "\n    Bearing: " + this.bearing;
		if(isStairs()){
			ret += "\n    Staircase with: " + this.getSteps() + " steps";
		}
		if(isElevator()){
			ret += "\n    Elevator: yes";
		}
		ret+="\n    Level: " + level;
		return ret;
	}
}
