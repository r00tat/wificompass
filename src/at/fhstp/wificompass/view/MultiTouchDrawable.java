/*
 * Created on Feb 10, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.graphics.drawable.Drawable;

public abstract class MultiTouchDrawable {
	public static int counter=1;
	
	public abstract Drawable getDrawable();
	public abstract int getWidth();
	public abstract int getHeight();
	public abstract String getId();
	public abstract boolean onTouch(PointInfo pointinfo);
	public abstract void setAngle(float angle);
	public abstract void setScale(float scaleX,float scaleY);
	
}
