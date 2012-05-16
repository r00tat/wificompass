/*
 * Created on Mar 19, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.ToolBox;
import at.fhstp.wificompass.model.ProjectSite;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public class NorthDrawable extends MultiTouchDrawable implements OkCallback, AngleChangeCallback {

	protected BitmapDrawable icon;

	/**
	 * @uml.property  name="okPopup"
	 * @uml.associationEnd  
	 */
	protected OkDrawable okPopup;
	

	protected TextDrawable tdCurrent;
	protected String textCurrent;
	

	protected TextDrawable tdCurrentAdjustment;
	protected String textCurrentAdjustment = "0 ¡";
	
	protected TextDrawable tdDescription;
	protected String textDescription;


	/**
	 * @uml.property  name="site"
	 * @uml.associationEnd  
	 */
	protected ProjectSite site;
	

	protected UserCompassDrawable compassIcon;
	protected static final int padding = 10;
	
	protected float mapAngle = 0.0f;
	protected float compassAngle = 0.0f;
	protected float adjustmentAngle = 0.0f;

	/**
	 * @param context
	 * @param superDrawable
	 */
	public NorthDrawable(Context context, MultiTouchDrawable superDrawable, ProjectSite site) {
		super(context, superDrawable);
		this.site = site;
		this.width = 250;
		
		this.getSuperDrawable().setAngleChangeCallback(this);
		
		compassIcon = new UserCompassDrawable(ctx, this, this, false);
		compassIcon.setRelativePosition(width / 2, compassIcon.getHeight() / 2 + padding);
		compassIcon.start();
		
		textCurrent = "Current adjustment:";
		tdCurrent = new TextDrawable(context, this, textCurrent, this.width - 2 * padding);
		tdCurrent.setRelativePosition(new PointF(padding, padding + compassIcon.height + padding));

		tdCurrentAdjustment = new TextDrawable(context, this, textDescription, this.width - 2 * padding, 20);
		tdCurrentAdjustment.setRelativePosition(new PointF(padding, padding + compassIcon.height + padding + tdCurrent.getHeight() + padding));

		textDescription = "Turn your device until the map rotation equals the real orientation. Once you're done, click the green tick below.";
		tdDescription = new TextDrawable(context, this, textDescription, this.width - 2 * padding);
		tdDescription.setRelativePosition(new PointF(padding, padding + compassIcon.height + padding + tdCurrent.getHeight() + padding + tdCurrentAdjustment.getHeight() + padding));
		
		okPopup = new OkDrawable(ctx, this);
		okPopup.setRelativePosition(width / 2, padding + compassIcon.height + padding + tdCurrent.getHeight() + padding + tdCurrentAdjustment.getHeight() + padding + tdDescription.getHeight() + padding);
		
		this.height = padding + compassIcon.height + padding + tdCurrent.getHeight() + padding + tdCurrentAdjustment.getHeight() + padding + tdDescription.getHeight() + padding + okPopup.getHeight();

	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();

		canvas.translate(minX, minY);

		Paint paint = new Paint();
		RectF rect = new RectF(0, 0, this.width, this.height);

		paint.setColor(Color.rgb(230, 230, 230));
		paint.setStyle(Style.FILL);
		canvas.drawRoundRect(rect, 5, 5, paint);

		paint.setColor(Color.rgb(200, 200, 200));
		paint.setStrokeWidth(0);
		paint.setStyle(Style.STROKE);
		canvas.drawRoundRect(rect, 5, 5, paint);

		canvas.translate(padding, padding);
		canvas.restore();

		this.drawSubdrawables(canvas);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#getDrawable()
	 */
	@Override
	public Drawable getDrawable() {
		return icon;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#isScalable()
	 */
	@Override
	public boolean isScalable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#isRotateable()
	 */
	@Override
	public boolean isRotateable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#isDragable()
	 */
	@Override
	public boolean isDragable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#isOnlyInSuper()
	 */
	@Override
	public boolean isOnlyInSuper() {
		return false;
	}

	@Override
	public void onOk() {
		if (this.getSuperDrawable() != null && this.getSuperDrawable() instanceof SiteMapDrawable) {
			((SiteMapDrawable)this.getSuperDrawable()).setAngleAdjustment(adjustmentAngle);
			site.setNorth(adjustmentAngle);
		}
		
		this.deleteDrawable();
	}

	@Override
	public void angleChanged(float angle, Object caller) {
		
		if (caller instanceof UserCompassDrawable) {
			compassAngle = ToolBox.normalizeAngle(angle);
		} else if (caller instanceof SiteMapDrawable) {
			mapAngle = ToolBox.normalizeAngle(angle);
		}
		
		adjustmentAngle = ToolBox.calculateAngleDifference(compassAngle, mapAngle);
		
		tdCurrentAdjustment.setText((int) Math.toDegrees(adjustmentAngle) + " ¡");
	}

}
