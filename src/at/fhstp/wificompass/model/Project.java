/*
 * Created on Dec 8, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model;

import java.io.IOException;
import java.util.Vector;

import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import at.fhstp.wificompass.interfaces.XMLSerializable;

public class Project implements XMLSerializable {
	protected String name;

	protected String description;
	
	protected String path;

	protected Vector<ProjectLocation> locations;
	
	protected static final String XMLTAG="project",XMLNAME="name",XMLPATH="path",XMLLOCATIONS="locations",XMLDESCRIPTION="description";

	public Project() {
		this("","",null);
	}

	public Project(String name) {
		this(name,"",null);
	}

	public Project(String name,String description,String path) {
		this.name=name;
		this.description=description;
		this.path=path;
		this.locations=new Vector<ProjectLocation>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public void serialize(XmlSerializer serializer) throws RuntimeException, IOException  {
		serializer.startTag(null, XMLTAG);
		
		serializer.startTag(null, XMLNAME).text(name).endTag(null, XMLNAME);
		serializer.startTag(null, XMLDESCRIPTION).text(description).endTag(null, XMLDESCRIPTION);
		serializer.startTag(null, XMLPATH).text(path).endTag(null, XMLPATH);
		
		serializer.startTag(null, XMLLOCATIONS);
		
		
		serializer.endTag(null, XMLLOCATIONS);
		
		serializer.endTag(null, XMLTAG);
	}

	@Override
	public void deserialize(Element e) {
		
	}

}
