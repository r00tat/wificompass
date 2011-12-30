/*
 * Created on Dec 8, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.fhstp.wificompass.ApplicationContext;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.interfaces.XMLSerializable;
import at.fhstp.wificompass.model.xml.XMLSettings;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "locations")
public class ProjectLocation implements XMLSerializable {

	@DatabaseField(generatedId = true)
	protected int id;

	@DatabaseField
	protected String title;
	
	@DatabaseField
	protected String description;
	
	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	protected byte[] background;
	
	protected Bitmap backgroundBitmap;
	
	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	protected byte[] image;
	
	protected Bitmap imageBitmap;
	
	protected static final int quality=100;
	
	@DatabaseField(foreign = true,foreignAutoRefresh = true)
	protected Project project;

	protected static final String XMLTAG = "location", XMLTITLE = "title";

	public ProjectLocation() {
		this(ApplicationContext.getContext().getString(R.string.untitled));
	}

	public ProjectLocation(String title) {
		this.title = title;
	}
	
	public ProjectLocation(String title,Project project){
		this.title= title;
		this.project=project;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Bitmap getBackgroundBitmap() {
		if(backgroundBitmap==null&&background!=null){
			backgroundBitmap=BitmapFactory.decodeByteArray(background, 0, background.length);
		}
		return backgroundBitmap;
	}

	public boolean setBackgroundBitmap(Bitmap backgroundBitmap) {
		
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		if(backgroundBitmap.compress(Bitmap.CompressFormat.PNG, quality, baos)){
			background=baos.toByteArray();
			this.backgroundBitmap = backgroundBitmap;
			return true;
		}
		return false;
	}

	public Bitmap getImageBitmap() {
		if(imageBitmap==null&&image!=null){
			imageBitmap=BitmapFactory.decodeByteArray(image, 0, image.length);
		}
		return imageBitmap;
	}

	public boolean setImageBitmap(Bitmap imageBitmap) {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		if(imageBitmap.compress(Bitmap.CompressFormat.PNG, quality, baos)){
			image=baos.toByteArray();
			this.imageBitmap = imageBitmap;
			return true;
		}
		return false;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public int getId() {
		return id;
	}

}
