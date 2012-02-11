package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.R;

public class AccessPointDrawable extends MultiTouchDrawable {

	protected static Drawable icon;

	public AccessPointDrawable(Context ctx) {
		super(ctx);
		init();
	}
	
	public AccessPointDrawable(Context ctx,MultiTouchDrawable superDrawable) {
		super(ctx,superDrawable);
		init();
	}

	protected void init() {
		icon = ctx.getResources().getDrawable(R.drawable.access_point_icon);
		
//		icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
	}

	public Drawable getDrawable() {
//		Logger.d("get ap drawable");
		return icon;
	}

	public int getWidth() {
		return icon.getIntrinsicWidth();
	}

	public int getHeight() {
		return icon.getIntrinsicHeight();
	}


	@Override
	public boolean onTouch(PointInfo pointinfo) {
		return false;
	}

	@Override
	public void setRelativePosition(float xPos, float yPos) {
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
