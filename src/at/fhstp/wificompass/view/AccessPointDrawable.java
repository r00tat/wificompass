package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;

public class AccessPointDrawable extends MultiTouchDrawable {

	protected static BitmapDrawable icon;
	protected PopupDrawable popup;
	protected boolean isPopupActive = false;

	public AccessPointDrawable(Context ctx) {
		super(ctx);
		init();
	}
	
	public AccessPointDrawable(Context ctx,MultiTouchDrawable superDrawable) {
		super(ctx,superDrawable);
		init();
	}

	protected void init() {
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.access_point_icon);
		this.setPivot(0.5f, 0.716f);
		
		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();
		
		popup = new PopupDrawable(ctx);
		Logger.d("Popup width: " + popup.width);
		popup.setRelativePosition(this.width / 2 - popup.width / 2, popup.height * -0.5f);
	}

	public void setIsPopupActive(boolean isPopupActive) {
		this.isPopupActive = isPopupActive;
		
		if (this.isPopupActive)
			this.addSubDrawable(popup);
		else
			this.removeSubDrawable(popup);
	}
	
	public Drawable getDrawable() {
		return icon;
	}

	@Override
	public boolean onTouch(PointInfo pointinfo) {
		
		Logger.d("Touch event for AP " + this.getId() + ": " + pointinfo.isMultiTouch() + ", " + pointinfo.getNumTouchPoints() + ", " + pointinfo.getAction());
		
		if (pointinfo.isMultiTouch() == false &&
			pointinfo.getNumTouchPoints() == 1 &&
			pointinfo.getAction() == 0
				) {
			this.setIsPopupActive(!isPopupActive);
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

	

}
