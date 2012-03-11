package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.AccessPoint;

public class AccessPointDrawable extends MultiTouchDrawable {

	protected static BitmapDrawable icon;

	protected PopupDrawable popup;

	protected AccessPoint accessPoint;

	public AccessPointDrawable(Context ctx, AccessPoint accessPoint) {
		super(ctx);
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
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.wifi_green_2);
		this.setPivot(0.5f, 1f);

		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();

		popup = new PopupDrawable(ctx, this, this.getPopupText());

		Logger.d("Popup width: " + popup.width);

		popup.setActive(false);
		if(accessPoint!=null&&accessPoint.getLocation()!=null)
			this.setRelativePosition(accessPoint.getLocation().getX(), accessPoint.getLocation().getY());
	}

	public Drawable getDrawable() {
		return icon;
	}

	protected String getPopupText() {
		if (accessPoint != null)
			return accessPoint.getSsid() + "\n[" + accessPoint.getBssid() + "]\nPosition: " + this.getRelativeX() + " / " + this.getRelativeY();
		else
			return "";
	}

	@Override
	public boolean onTouch(PointInfo pointinfo) {

		// Logger.d("Touch event for AP " + this.getId() + ": " + pointinfo.isMultiTouch() + ", " + pointinfo.getNumTouchPoints() + ", " + pointinfo.getAction());

		if (pointinfo.isMultiTouch() == false && pointinfo.getNumTouchPoints() == 1 && pointinfo.getAction() == 0) {
//			popup.setPopupText(this.getPopupText());
			popup.setActive(!popup.isActive());
			return true;
		}

		return false;
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

}
