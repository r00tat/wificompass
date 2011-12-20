/*
 * Created on Dec 20, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.Properties;

import android.util.Xml;

public class XmlSerializer implements org.xmlpull.v1.XmlSerializer {

	protected org.xmlpull.v1.XmlSerializer serializer;

	public XmlSerializer() {
		serializer = Xml.newSerializer();
	}

	public void addSimpleType(String namespace, String name, Properties attributes) throws IllegalArgumentException, IllegalStateException,
			IOException {
		serializer.startTag(namespace, name);
		if (attributes != null)
			for (Iterator<Object> it = attributes.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				if (key.contains(":")&&key.indexOf(":")+1<key.length())
					serializer.attribute(key.substring(0, key.indexOf(":")), key.substring(key.indexOf(":")+1), attributes.getProperty(key));
				else
					serializer.attribute(null, key, attributes.getProperty(key));
			}

		serializer.endTag(namespace, name);
	}

	@Override
	public org.xmlpull.v1.XmlSerializer attribute(String namespace, String name, String value) throws IOException, IllegalArgumentException,
			IllegalStateException {
		return serializer.attribute(namespace, name, value);
	}

	@Override
	public void cdsect(String text) throws IOException, IllegalArgumentException, IllegalStateException {
		serializer.cdsect(text);
	}

	@Override
	public void comment(String text) throws IOException, IllegalArgumentException, IllegalStateException {
		serializer.comment(text);
	}

	@Override
	public void docdecl(String text) throws IOException, IllegalArgumentException, IllegalStateException {
		serializer.docdecl(text);
	}

	@Override
	public void endDocument() throws IOException, IllegalArgumentException, IllegalStateException {
		serializer.endDocument();
	}

	@Override
	public org.xmlpull.v1.XmlSerializer endTag(String namespace, String name) throws IOException, IllegalArgumentException, IllegalStateException {
		return serializer.endTag(namespace, name);
	}

	@Override
	public void entityRef(String text) throws IOException, IllegalArgumentException, IllegalStateException {
		serializer.entityRef(text);
	}

	@Override
	public void flush() throws IOException {
		serializer.flush();
	}

	@Override
	public int getDepth() {
		return serializer.getDepth();
	}

	@Override
	public boolean getFeature(String name) {
		return serializer.getFeature(name);
	}

	@Override
	public String getName() {
		return serializer.getName();
	}

	@Override
	public String getNamespace() {
		return serializer.getNamespace();
	}

	@Override
	public String getPrefix(String namespace, boolean generatePrefix) throws IllegalArgumentException {
		return serializer.getPrefix(namespace, generatePrefix);
	}

	@Override
	public Object getProperty(String name) {
		return serializer.getProperty(name);
	}

	@Override
	public void ignorableWhitespace(String text) throws IOException, IllegalArgumentException, IllegalStateException {
		serializer.ignorableWhitespace(text);
	}

	@Override
	public void processingInstruction(String text) throws IOException, IllegalArgumentException, IllegalStateException {
		serializer.processingInstruction(text);
	}

	@Override
	public void setFeature(String name, boolean state) throws IllegalArgumentException, IllegalStateException {
		serializer.setFeature(name, state);
	}

	@Override
	public void setOutput(Writer writer) throws IOException, IllegalArgumentException, IllegalStateException {
		serializer.setOutput(writer);
	}

	@Override
	public void setOutput(OutputStream os, String encoding) throws IOException, IllegalArgumentException, IllegalStateException {
		serializer.setOutput(os, encoding);
	}

	@Override
	public void setPrefix(String prefix, String namespace) throws IOException, IllegalArgumentException, IllegalStateException {
		serializer.setPrefix(prefix, namespace);
	}

	@Override
	public void setProperty(String name, Object value) throws IllegalArgumentException, IllegalStateException {
		serializer.setProperty(name, value);
	}

	@Override
	public void startDocument(String encoding, Boolean standalone) throws IOException, IllegalArgumentException, IllegalStateException {
		serializer.startDocument(encoding, standalone);
	}

	@Override
	public org.xmlpull.v1.XmlSerializer startTag(String namespace, String name) throws IOException, IllegalArgumentException, IllegalStateException {
		return serializer.startTag(namespace, name);
	}

	@Override
	public org.xmlpull.v1.XmlSerializer text(String text) throws IOException, IllegalArgumentException, IllegalStateException {
		return serializer.text(text);
	}

	@Override
	public org.xmlpull.v1.XmlSerializer text(char[] buf, int start, int len) throws IOException, IllegalArgumentException, IllegalStateException {
		return serializer.text(buf, start, len);
	}

}
