/*
 * Created on Dec 8, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import at.fhstp.wificompass.ApplicationContext;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.interfaces.XMLSerializable;
import at.fhstp.wificompass.model.xml.XMLSettings;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "projects")
public class Project implements XMLSerializable {

	@DatabaseField(generatedId = true)
	protected int id;

	@DatabaseField
	protected String name;

	@DatabaseField
	protected String description;

//	@DatabaseField
//	protected String path;

	@ForeignCollectionField
	protected ForeignCollection<ProjectLocation> locations;

	protected static final String XMLTAG = "project", XMLNAME = "name", XMLPATH = "path", XMLLOCATIONS = "locations", XMLDESCRIPTION = "description";

	public Project() {
		this(ApplicationContext.getContext().getString(R.string.untitled), "");
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

		serializer.startTag(XMLSettings.XMLNS, XMLLOCATIONS);

		serializer.endTag(XMLSettings.XMLNS, XMLLOCATIONS);

		serializer.endTag(XMLSettings.XMLNS, XMLTAG);
	}

	@Override
	public void deserialize(Element e) {

	}

	public int getId() {
		return id;
	}

}
