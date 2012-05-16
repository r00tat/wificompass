/*
 * Created on Apr 3, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.CompassListener;
import at.fhstp.wificompass.CompassMonitor;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.ToolBox;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public class UserCompassDrawable extends MultiTouchDrawable implements CompassListener {

	protected static BitmapDrawable icon;

	

	/**
	 * @uml.property  name="popup"
	 * @uml.associationEnd  
	 */
	protected TextPopupDrawable popup;
	
	int popupAngle=0;

	 double azimuth = 0;
	
	// double pitch = 0;
	//
	// double roll = 0;
	 
	protected AngleChangeCallback compassAngleCallback = null;
	
	protected boolean withPopup = true;

	/**
	 * <p>
	 * Only change the angle, if the compass changes more than minAngleChange degrees. Per default 3°.
	 * </p>
	 */
	protected static final float minAngleChange = (float) Math.toRadians(3d);

	/**
	 * <p>
	 * Only change the angle, if the compass changes more than minAngleChange degrees. Per default 3°.
	 * </p>
	 */
	protected static final float minAngleChangeForPopup = (float) Math.toRadians(1);

	/**
	 * @param context
	 * @param superDrawable
	 */
	public UserCompassDrawable(Context context, MultiTouchDrawable superDrawable) {
		super(context, superDrawable);
		init();
	}
	
	public UserCompassDrawable(Context context, MultiTouchDrawable superDrawable, AngleChangeCallback compassAngleCallback) {
		super(context, superDrawable);
		this.compassAngleCallback = compassAngleCallback;
		init();
	}
	
	public UserCompassDrawable(Context context, MultiTouchDrawable superDrawable, AngleChangeCallback compassAngleCallback, boolean withPopup) {
		super(context, superDrawable);
		this.compassAngleCallback = compassAngleCallback;
		this.withPopup = withPopup;
		init();
	}

	protected void init() {
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.north_small);
		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();



		if (withPopup) {
			popup = new TextPopupDrawable(ctx, this.superDrawable);
			popup.setText("0°");
			popup.setActive(true);
			popup.setPersistent(true);
			popup.setWidth(40);
			popup.setRelativePosition(60,0);
		}
	}
	
	public void start() {
		CompassMonitor.registerListener(ctx, this);
	}

	public void stop() {
		CompassMonitor.unregisterListener(this);
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
		return false;
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

	


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		stop();
		super.finalize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#load()
	 */
	@Override
	public void load() {
		super.load();
		start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#unload()
	 */
	@Override
	public void unload() {
		stop();
		super.unload();
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.CompassListener#onCompassChanged(float, java.lang.String)
	 */
	@Override
	public void onCompassChanged(float azimuth, String direction) {
		float newAngle = (float) -Math.toRadians(azimuth) ;
		this.azimuth=azimuth;
//		if ((int)azimuth!=popupAngle) {
//			popupAngle=(int)azimuth;
//			popup.setText(ctx.getString(R.string.user_compass_degrees, popupAngle));
//			refresher.invalidate();
//		}
		
		if (Math.abs(angle - newAngle) > minAngleChange) {
			this.angle=ToolBox.normalizeAngle(newAngle);
			// we do not have to set the angle, the angle of the popup is always 0.
//			popup.setAngle(-newAngle);

			popupAngle=(int)this.azimuth;
			
			if (withPopup) {
				popup.setText(ctx.getString(R.string.user_compass_degrees, popupAngle));
				
			}
			
			if (compassAngleCallback != null) {
				compassAngleCallback.angleChanged(azimuth, this);
			}
			refresher.invalidate();
			
		}
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#setAngle(float)
	 */
	@Override
	public void setAngle(float angle) {
//		super.setAngle(angle);
		// we do not want the angle to be changed from somewhere else, we change the angle ourself
		
	}

}
