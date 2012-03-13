package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.AccessPoint;

public class AccessPointDrawable extends MultiTouchDrawable {

	protected static BitmapDrawable icon;

	protected PopupDrawable popup;

	protected AccessPoint accessPoint;

	public AccessPointDrawable(Context ctx, AccessPoint accessPoint) {
		super(ctx,(RefreshableView)null);
		this.accessPoint = accessPoint;
		init();
	}

	public AccessPointDrawable(Context ctx, MultiTouchDrawable superDrawable) {
		super(ctx, superDrawable);
		init();
	}

	public AccessPointDrawable(Context ctx, MultiTouchDrawable superDrawable, AccessPoint accessPoint) {
		super(ctx, superDrawable);
		this.accessPoint = accessPoint;
		init();
	}

	protected void init() {
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.wifi_red_2);
		this.setPivot(0.5f, 0.94f);

		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();

		if(accessPoint!=null&&accessPoint.getLocation()!=null)
			this.setRelativePosition(accessPoint.getLocation().getX(), accessPoint.getLocation().getY());
		
		popup = new PopupDrawable(ctx, this, this.getPopupText());
		popup.setWidth(250);
		popup.setActive(false);
		
	}

	public Drawable getDrawable() {
		return icon;
	}

	protected String getPopupText() {
		if (accessPoint != null)
			return accessPoint.getSsid() + " [" + accessPoint.getBssid() + "]\nPosition: " + this.getRelativeX()/gridSpacingX + " / " + this.getRelativeY()/gridSpacingY;
		else
			return "";
	}


	@Override
	public boolean isScalable() {
		return false;
	}

	@Override
	public boolean isRotateable() {
		return false;
	}

	@Override
	public boolean isDragable() {
		return false;
	}

	@Override
	public boolean isOnlyInSuper() {
		return true;
	}

	/**
	 * @return the accessPoint
	 */
	public AccessPoint getAccessPoint() {
		return accessPoint;
	}

	/**
	 * @param accessPoint
	 *            the accessPoint to set
	 */
	public void setAccessPoint(AccessPoint accessPoint) {
		this.accessPoint = accessPoint;
		popup.setPopupText(getPopupText());
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#onSingleTouch(org.metalev.multitouch.controller.MultiTouchController.PointInfo)
	 */
	@Override
	public boolean onSingleTouch(PointInfo pointinfo) {
		popup.setPopupText(this.getPopupText());
		popup.setActive(!popup.isActive());
		bringToFront();
		return true;
	}

}
