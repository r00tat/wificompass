/*
 * Created on Mar 12, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.R;

public class DeleteDrawable extends MultiTouchDrawable implements Popup{
	
	protected BitmapDrawable icon; 
	
	protected boolean isActive=false;

	/**
	 * @param context
	 * @param superDrawable
	 */
	public DeleteDrawable(Context context, MultiTouchDrawable superDrawable) {
		super(context, superDrawable);
		init();
	}

	protected void init(){
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.cross_circle);
		this.setPivot(0, 0.7f);
		
		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();

		this.setRelativePosition(this.width/2+5, -5);
	}

	@Override
	public Drawable getDrawable() {
		if(isActive)
			return icon;
		else 
			return null;
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
		return false;
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#onSingleTouch(org.metalev.multitouch.controller.MultiTouchController.PointInfo)
	 */
	@Override
	public boolean onSingleTouch(PointInfo pointinfo) {
		if(superDrawable!=null){
			superDrawable.deleteDrawable();
		}
		return true;
	}

	@Override
	public void setActive(boolean isPopupActive) {
		this.isActive=isPopupActive;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}
	
	

}
