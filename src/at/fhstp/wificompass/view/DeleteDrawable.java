/*
 * Created on Mar 12, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.R;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public class DeleteDrawable extends MultiTouchDrawable implements Popup{
	
	protected BitmapDrawable icon; 
	
	/**
	 * @uml.property  name="isActive"
	 */
	protected boolean isActive=false;
	
	protected String elementName;

	/**
	 * @param context
	 * @param superDrawable
	 */
	public DeleteDrawable(Context context, MultiTouchDrawable superDrawable, String elementName) {
		super(context, superDrawable);
		this.elementName=elementName;
		init();
	}

	protected void init(){
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.cross_delete);
		this.setPivot(0.7f, 0.3f);
		
		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();

		this.setRelativePosition(superDrawable.getWidth(), 0);
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
	public boolean isRotatable() {
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
		
		if(!isActive) return false;
		
		AlertDialog.Builder alertBuilder=new AlertDialog.Builder(ctx);
		alertBuilder.setTitle(R.string.project_site_delete_drawable_title);
		alertBuilder.setMessage(ctx.getString(R.string.project_site_delete_drawable_message,elementName));
		
		alertBuilder.setPositiveButton(ctx.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				forceDelete();
			}
		});

		alertBuilder.setNegativeButton(ctx.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});
		
		alertBuilder.create().show();
		
		return true;
	}

	/**
	 * @param isPopupActive
	 * @uml.property  name="isActive"
	 */
	@Override
	public void setActive(boolean isPopupActive) {
		this.isActive=isPopupActive;
	}

	/**
	 * @return
	 * @uml.property  name="isActive"
	 */
	@Override
	public boolean isActive() {
		return isActive;
	}
	
	protected void forceDelete(){
		if(superDrawable!=null){
			superDrawable.deleteDrawable();
			if(refresher!=null)
				refresher.invalidate();
		}
	}
	

}
