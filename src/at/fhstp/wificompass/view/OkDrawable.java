/*
 * Created on Mar 19, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.R;

/**
 * @author Paul Woelfel (paul@woelfel.at)
 */
public class OkDrawable extends MultiTouchDrawable {

	protected BitmapDrawable icon;

	protected boolean active = true;

	/**
	 * @param context
	 * @param superDrawable
	 */
	public OkDrawable(Context context, MultiTouchDrawable superDrawable) {
		super(context, superDrawable);
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.tick);
		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();
		this.setPivot(0.5f, 0.5f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#getDrawable()
	 */
	@Override
	public Drawable getDrawable() {
		if (active)
			return icon;
		else
			return null;
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

	public void setActive(boolean isPopupActive) {
		active = isPopupActive;
	}

	public boolean isActive() {
		return active;
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#onSingleTouch(org.metalev.multitouch.controller.MultiTouchController.PointInfo)
	 */
	@Override
	public boolean onSingleTouch(PointInfo pointinfo) {
		
		if(!active)
		return super.onSingleTouch(pointinfo);
		else {
			if(superDrawable !=null && superDrawable instanceof OkCallback){
				((OkCallback)superDrawable).onOk();
				return true;
			}else {
				return super.onSingleTouch(pointinfo);
			}
		}
	}

}
