package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.R;

public class AccessPointDrawable extends MultiTouchDrawable {

	protected static BitmapDrawable icon;

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
	}

	public Drawable getDrawable() {
		return icon;
	}

	@Override
	public boolean onTouch(PointInfo pointinfo) {
		return true;
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
