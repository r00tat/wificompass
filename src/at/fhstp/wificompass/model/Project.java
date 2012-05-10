/*
 * Created on Dec 8, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model;

import java.io.IOException;
import java.sql.SQLException;

import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import at.fhstp.wificompass.interfaces.XMLSerializable;
import at.fhstp.wificompass.model.xml.XMLSettings;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
@DatabaseTable(tableName = Project.TABLE_NAME)
public class Project extends BaseDaoEnabled<Project, Integer>implements XMLSerializable {
	public static final String TABLE_NAME="projects";

	/**
	 * @uml.property  name="id"
	 */
	@DatabaseField(generatedId = true)
	protected int id;

	/**
	 * @uml.property  name="name"
	 */
	@DatabaseField
	protected String name;

	/**
	 * @uml.property  name="description"
	 */
	@DatabaseField
	protected String description;

//	@DatabaseField
//	protected String path;

	/**
	 * @uml.property  name="sites"
	 */
	@ForeignCollectionField
	protected ForeignCollection<ProjectSite> sites;

	protected static final String XMLTAG = "project";

	protected static final String XMLNAME = "name";

	protected static final String XMLPATH = "path";

	protected static final String XMLSITES = "sites";

	protected static final String XMLDESCRIPTION = "description";
	
	/**
	 * @uml.property  name="fIELD_ID"
	 */
	public static final String FIELD_ID="id";
	
	public static final String untitled="untitled";

	public Project() {
		this(untitled, "");
	}

	public Project(String name) {
		this(name, "");
	}

	public Project(String name, String description) {
		this.name = name;
		this.description = description;
//		this.path = path;
//		this.locations = new ForeignCollection<ProjectLocation>();
	}
	
	/**
	 * copy constructor
	 * @param copy
	 */
	public Project(Project copy){
		name=copy.name;
		description=copy.description;
		
	}

	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 * @uml.property  name="description"
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 * @uml.property  name="description"
	 */
	public void setDescription(String description) {
		this.description = description;
	}

//	public String getPath() {
//		return path;
//	}
//
//	public void setPath(String path) {
//		this.path = path;
//	}

	@Override
	public void serialize(XmlSerializer serializer) throws RuntimeException, IOException {
		serializer.setPrefix(XMLSettings.XMLSHORTNS, XMLSettings.XMLNS);
		serializer.startTag(XMLSettings.XMLNS, XMLTAG);

		serializer.startTag(XMLSettings.XMLNS, XMLNAME).text(name).endTag(XMLSettings.XMLNS, XMLNAME);
		serializer.startTag(XMLSettings.XMLNS, XMLDESCRIPTION).text(description).endTag(XMLSettings.XMLNS, XMLDESCRIPTION);
//		serializer.startTag(XMLSettings.XMLNS, XMLPATH).text(path).endTag(XMLSettings.XMLNS, XMLPATH);

		serializer.startTag(XMLSettings.XMLNS, XMLSITES);

		serializer.endTag(XMLSettings.XMLNS, XMLSITES);

		serializer.endTag(XMLSettings.XMLNS, XMLTAG);
	}

	@Override
	public void deserialize(Element e) {

	}

	/**
	 * @return
	 * @uml.property  name="id"
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return  the sites
	 * @uml.property  name="sites"
	 */
	public ForeignCollection<ProjectSite> getSites() {
		return sites;
	}

	/**
	 * @return  the fieldId
	 * @uml.property  name="fIELD_ID"
	 */
	public static String getFieldId() {
		return FIELD_ID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Project("+id+"): "+name;
	}

	/* (non-Javadoc)
	 * @see com.j256.ormlite.misc.BaseDaoEnabled#delete()
	 */
	@Override
	public int delete() throws SQLException {
		for(ProjectSite site: sites){
			site.delete();
		}
		return super.delete();
	}

}
