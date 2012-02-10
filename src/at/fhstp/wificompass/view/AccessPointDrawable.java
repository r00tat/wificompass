package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import at.fhstp.wificompass.R;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class AccessPointDrawable implements MultiTouchDrawable {

	protected static int counter=0;
	protected int id;
	protected static Drawable icon;
	protected Context ctx;
	protected float angle = 0, scaleX = 1.0f, scaleY = 1.0f;

	public AccessPointDrawable(Context ctx) {
		this.ctx = ctx;
		id=counter++;
		icon = ctx.getResources().getDrawable(R.drawable.access_point_icon);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(),
				icon.getIntrinsicHeight());
	}

	public Drawable getDrawable() {
		return icon;
	}

	public int getWidth() {
		return icon.getIntrinsicWidth();
	}

	public int getHeight() {
		return icon.getIntrinsicHeight();
	}

	public String getId() {
		return Integer.toString(id);
	}

	public boolean onTouch(PointInfo pointinfo) {
		return false;
	}

	public void setAngle(float angle) {
		this.angle=angle;
	}

	public void setScale(float scaleX, float scaleY) {
		this.scaleX=scaleX;
		this.scaleY=scaleY;
	}

}
