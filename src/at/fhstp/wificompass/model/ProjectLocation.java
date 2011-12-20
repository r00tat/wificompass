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

public class ProjectLocation implements XMLSerializable {
	
	protected String title;
	
	protected static final String XMLTAG="location",XMLTITLE="title";
	
	public ProjectLocation(){
		this(ApplicationContext.getContext().getString(R.string.untitled));
	}
	
	public ProjectLocation(String title){
		this.title=title;
	}

	@Override
	public void serialize(XmlSerializer serializer) throws RuntimeException, IOException {
		serializer.startTag(XMLSettings.XMLNS, XMLTAG);
		
		serializer.startTag(XMLSettings.XMLNS, XMLTITLE).text(title).endTag(XMLSettings.XMLNS, XMLTITLE);
		
		serializer.endTag(XMLSettings.XMLNS, XMLTITLE);
	}

	@Override
	public void deserialize(Element e) {
	}

}
