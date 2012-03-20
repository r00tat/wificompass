/*
 * Created on Mar 19, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;

/**
 * @author Paul Woelfel (paul@woelfel.at)
 */
public class NorthDrawable extends MultiTouchDrawable implements OkCallback {

	protected BitmapDrawable icon;
	
	protected OkDrawable okPopup;

	/**
	 * @param context
	 * @param superDrawable
	 */
	public NorthDrawable(Context context, MultiTouchDrawable superDrawable) {
		super(context, superDrawable);
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.north);
		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();
		this.setPivot(0.5f,0.5f);
		
//		okPopup=new OkDrawable(ctx,this);
		
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#getDrawable()
	 */
	@Override
	public Drawable getDrawable() {
		return icon;
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#isScalable()
	 */
	@Override
	public boolean isScalable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#isRotateable()
	 */
	@Override
	public boolean isRotateable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#isDragable()
	 */
	@Override
	public boolean isDragable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#isOnlyInSuper()
	 */
	@Override
	public boolean isOnlyInSuper() {
		return false;
	}

	@Override
	public void onOk() {
		// save north to site
		
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#setPos(float, float, float, float, float, int, boolean)
	 */
	@Override
	protected boolean setPos(float centerX, float centerY, float scaleX, float scaleY, float angle, int flags, boolean isDraggedOrPinched) {
		Logger.d("old: "+this.centerX+" "+this.centerY+" "+this.scaleX+" "+this.scaleY+" "+this.angle);
		Logger.d("new: "+centerX+" "+centerY+" "+scaleX+" "+scaleY+" "+angle+" "+Integer.toBinaryString(flags)+" "+(isDraggedOrPinched?"t":"f"));
		boolean ret= super.setPos(centerX, centerY, scaleX, scaleY, angle, flags, isDraggedOrPinched);
		Logger.d("set: "+this.centerX+" "+this.centerY+" "+this.scaleX+" "+this.scaleY+" "+this.angle);
		return ret;
	}
	
	

}
