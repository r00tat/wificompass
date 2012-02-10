/*
 * Created on Feb 10, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.graphics.drawable.Drawable;

public interface MultiTouchDrawable {
	public Drawable getDrawable();
	public int getWidth();
	public int getHeight();
	public String getId();
	public boolean onTouch(PointInfo pointinfo);
	public void setAngle(float angle);
	public void setScale(float scaleX,float scaleY);
}
