/*
 * Created on Dec 8, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.fhstp.wificompass.interfaces.XMLSerializable;
import at.fhstp.wificompass.model.xml.XMLSettings;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
@DatabaseTable(tableName = ProjectSite.TABLE_NAME)
public class ProjectSite extends BaseDaoEnabled<ProjectSite, Integer> implements XMLSerializable {

	public static final String TABLE_NAME = "sites";

	/**
	 * @uml.property  name="id"
	 */
	@DatabaseField(generatedId = true)
	protected int id;

	/**
	 * @uml.property  name="title"
	 */
	@DatabaseField
	protected String title;

	/**
	 * @uml.property  name="description"
	 */
	@DatabaseField
	protected String description;

	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	protected byte[] background;

	/**
	 * @uml.property  name="backgroundBitmap"
	 */
	protected Bitmap backgroundBitmap;

	/**
	 * @uml.property  name="width"
	 */
	@DatabaseField
	protected int width;

	/**
	 * @uml.property  name="height"
	 */
	@DatabaseField
	protected int height;

	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	protected byte[] image;

	/**
	 * @uml.property  name="imageBitmap"
	 */
	protected Bitmap imageBitmap;

	protected static final int quality = 100;

	/**
	 * @uml.property  name="project"
	 * @uml.associationEnd  
	 */
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	protected Project project;
	
	/**
	 * @uml.property  name="gridSpacingX"
	 */
	@DatabaseField
	protected float gridSpacingX=30;
	
	/**
	 * @uml.property  name="gridSpacingY"
	 */
	@DatabaseField
	protected float gridSpacingY=30;
	
	/**
	 * @uml.property  name="north"
	 */
	@DatabaseField
	protected float north=0;

	/**
	 * @uml.property  name="accessPoints"
	 */
	@ForeignCollectionField
	protected ForeignCollection<AccessPoint> accessPoints;

	/**
	 * @uml.property  name="scanResults"
	 */
	@ForeignCollectionField
	protected ForeignCollection<WifiScanResult> scanResults;
	
	@DatabaseField(dataType=DataType.SERIALIZABLE,columnName="selectedBssids2")
	protected ArrayList<String> unselectedBssids;


	/**
	 * @uml.property  name="lastLocation"
	 * @uml.associationEnd  
	 */
	@DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
	protected Location lastLocation;

	protected static final String XMLTAG = "location";

	protected static final String XMLTITLE = "title";

	public static final String UNTITLED = "untitled";

	// maybe we should use TableUtils
//	public static String FIELD_PROJECT_FK = Project.TABLE_NAME + "_" + Project.FIELD_ID;

	public ProjectSite() {
		this(null, null);
	}

	public ProjectSite(String title) {
		this(null, title);
	}

	public ProjectSite(Project project) {
		this(project, null);
	}

	public ProjectSite(Project project, String title) {
		super();
		this.title = title;
		this.project = project;
		if (this.title == null) {
			this.title = UNTITLED;
		}
		width = 0;
		height = 0;
		unselectedBssids=new ArrayList<String>();
	}

	/**
	 * copy constructor
	 * 
	 * @param copy
	 */
	public ProjectSite(ProjectSite copy) {
		title = copy.title;
		description = copy.description;
		if (copy.image != null)
			image = copy.image.clone();
		else
			image = null;
		
		if(copy.background!=null){
			background=copy.background.clone();
		}else
			background=null;
		
		project = copy.project;
		if (copy.lastLocation != null)
			lastLocation = new Location(copy.lastLocation);
		else
			lastLocation = null;
		width=copy.width;
		height=copy.height;
				
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

	/**
	 * @return
	 * @uml.property  name="title"
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 * @uml.property  name="title"
	 */
	public void setTitle(String title) {
		this.title = title;
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

	/**
	 * @return
	 * @uml.property  name="backgroundBitmap"
	 */
	public Bitmap getBackgroundBitmap() {
		if (backgroundBitmap == null && background != null) {
			backgroundBitmap = BitmapFactory.decodeByteArray(background, 0, background.length);
		}
		return backgroundBitmap;
	}

	/**
	 * @param backgroundBitmap
	 * @return
	 * @uml.property  name="backgroundBitmap"
	 */
	public boolean setBackgroundBitmap(Bitmap backgroundBitmap) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (backgroundBitmap.compress(Bitmap.CompressFormat.PNG, quality, baos)) {
			background = baos.toByteArray();
			this.backgroundBitmap = backgroundBitmap;
			return true;
		}
		return false;
	}

	/**
	 * @return
	 * @uml.property  name="imageBitmap"
	 */
	public Bitmap getImageBitmap() {
		if (imageBitmap == null && image != null) {
			imageBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
		}
		return imageBitmap;
	}

	/**
	 * @param imageBitmap
	 * @return
	 * @uml.property  name="imageBitmap"
	 */
	public boolean setImageBitmap(Bitmap imageBitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (imageBitmap.compress(Bitmap.CompressFormat.PNG, quality, baos)) {
			image = baos.toByteArray();
			this.imageBitmap = imageBitmap;
			return true;
		}
		return false;
	}

	/**
	 * @return
	 * @uml.property  name="project"
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project
	 * @uml.property  name="project"
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * @return
	 * @uml.property  name="id"
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return  the accessPoints
	 * @uml.property  name="accessPoints"
	 */
	public ForeignCollection<AccessPoint> getAccessPoints() {
		return accessPoints;
	}

	/**
	 * @return  the scanResults
	 * @uml.property  name="scanResults"
	 */
	public ForeignCollection<WifiScanResult> getScanResults() {
		return scanResults;
	}

	/**
	 * @return  the width
	 * @uml.property  name="width"
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return  the height
	 * @uml.property  name="height"
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * set the size of the project site
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * @return  the last known Location
	 * @uml.property  name="lastLocation"
	 */
	public Location getLastLocation() {
		return lastLocation;
	}

	/**
	 * @param lastLocation  the lastLocation to set
	 * @uml.property  name="lastLocation"
	 */
	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProjectSite(" + id + ") " + title + " " + width + "x" + height;
	}

	/**
	 * @return  the gridSpacingX
	 * @uml.property  name="gridSpacingX"
	 */
	public float getGridSpacingX() {
		return gridSpacingX;
	}

	/**
	 * @param gridSpacingX  the gridSpacingX to set
	 * @uml.property  name="gridSpacingX"
	 */
	public void setGridSpacingX(float gridSpacingX) {
		this.gridSpacingX = gridSpacingX;
	}

	/**
	 * @return  the gridSpacingY
	 * @uml.property  name="gridSpacingY"
	 */
	public float getGridSpacingY() {
		return gridSpacingY;
	}

	/**
	 * @param gridSpacingY  the gridSpacingY to set
	 * @uml.property  name="gridSpacingY"
	 */
	public void setGridSpacingY(float gridSpacingY) {
		this.gridSpacingY = gridSpacingY;
	}

	/**
	 * @return  the north
	 * @uml.property  name="north"
	 */
	public float getNorth() {
		return north;
	}

	/**
	 * <p>Define the the angle to the north of the map and the magnetic north</p> <p>The angle must be between 0 and 2*π</p>
	 * @param north  the north to set
	 * @uml.property  name="north"
	 */
	public void setNorth(float north) {
		this.north = (float) (north%(2*Math.PI));
		if(this.north<0)
			this.north+=2*Math.PI;
	}

	
	/* (non-Javadoc)
	 * @see com.j256.ormlite.misc.BaseDaoEnabled#delete()
	 */
	@Override
	public int delete() throws SQLException {
		if(lastLocation!=null&&lastLocation.getId()!=0) lastLocation.delete();
		for(AccessPoint ap: accessPoints){
			ap.delete();
		}
		for(WifiScanResult sr: scanResults){
			sr.delete();
		}
		
		return super.delete();
	}

	
	public boolean isBssidSelected(String bssid){
		if(unselectedBssids!=null){
			return !unselectedBssids.contains(bssid);
		}else 
			return true;
	}
	

	
	/**
	 * @param bssids
	 * @uml.property  name="unselectedBssids"
	 */
	public void setUnselectedBssids(ArrayList<String> bssids){
		this.unselectedBssids=bssids;
	}


	public ArrayList<String> getSelectedBssids() {
		return unselectedBssids;
	}

}
