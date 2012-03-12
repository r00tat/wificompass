package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.userlocation.ManualLocationProvider;

public class UserDrawable extends MultiTouchDrawable {

	protected static BitmapDrawable icon;
	
	protected ManualLocationProvider locProvider;
	
	public UserDrawable(Context ctx) {
		super(ctx);
		init();
	}
	
	public UserDrawable(Context ctx,MultiTouchDrawable superDrawable) {
		super(ctx,superDrawable);
		init();
	}

	protected void init() {
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.user_icon);
		this.setPivot(0.272727f, 0.7436f);
		
		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();
		
		// create a Location Provider to update the LocationService
		locProvider=new ManualLocationProvider(ctx);
	}
	
	@Override
	public Drawable getDrawable() {
		return icon;
	}

	@Override
	public boolean isScalable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRotateable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDragable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isOnlyInSuper() {
		return true;
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#setRelativePosition(float, float)
	 */
	@Override
	public void setRelativePosition(float relX, float relY) {
		super.setRelativePosition(relX, relY);
		locProvider.updateCurrentPosition(relX, relY);
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#onSingleTouch(org.metalev.multitouch.controller.MultiTouchController.PointInfo)
	 */
	@Override
	public boolean onSingleTouch(PointInfo pointinfo) {
		bringToFront();
		return true;
	}

}
