package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.userlocation.ManualLocationProvider;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public class UserDrawable extends MultiTouchDrawable {

	protected static BitmapDrawable icon;
	
	/**
	 * @uml.property  name="locProvider"
	 * @uml.associationEnd  
	 */
	protected ManualLocationProvider locProvider;
	
	/**
	 * @uml.property  name="compassIcon"
	 * @uml.associationEnd  
	 */
	protected UserCompassDrawable compassIcon;

	
	public UserDrawable(Context ctx,MultiTouchDrawable superDrawable) {
		super(ctx,superDrawable);
		init();
	}

	protected void init() {
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.user_icon);
		this.setPivot(0.5f,66f/69f);
		
		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();
		
		compassIcon=new UserCompassDrawable(ctx, this);
//		compassIcon.setRelativePosition(width/2,30+compassIcon.height/2);
		compassIcon.setRelativePosition(10,10);
		
		// create a Location Provider to update the LocationService
		locProvider=new ManualLocationProvider(ctx);
//		locProvider.setLocationChangeListener(new LocationChangeListener() {
//			
//			@Override
//			public void locationChanged(Location loc) {
//				Logger.d("updated location to "+loc);
//			}
//		});
	}
	
	@Override
	public Drawable getDrawable() {
		return icon;
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
		return true;
	}

	@Override
	public boolean isOnlyInSuper() {
		return true;
	}

	

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#onSingleTouch(org.metalev.multitouch.controller.MultiTouchController.PointInfo)
	 */
	@Override
	public boolean onSingleTouch(PointInfo pointinfo) {
		bringToFront();
		return true;
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#onRelativePositionUpdate()
	 */
	@Override
	protected void onRelativePositionUpdate() {
//		super.onRelativePositionUpdate();
		locProvider.updateCurrentPosition(relX, relY);
		if(superDrawable instanceof SiteMapDrawable){
			superDrawable.setPivot((relX<0?0:relX/superDrawable.width),(relY<0?0: relY/superDrawable.height));
			Logger.d("updated pivot of sitemap to "+(relX<0?0:relX/superDrawable.width)+","+(relY<0?0: relY/superDrawable.height));
		}
	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#draw(android.graphics.Canvas)
	 */
//	@Override
//	public void draw(Canvas canvas) {
//		// draw the compass icon first, then the user icon
//		this.drawSubdrawables(canvas);
//		this.drawFromDrawable(canvas);
//	}

}
