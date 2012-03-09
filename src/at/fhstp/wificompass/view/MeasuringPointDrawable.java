package at.fhstp.wificompass.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.WifiScanResult;

public class MeasuringPointDrawable extends MultiTouchDrawable {

	protected static BitmapDrawable icon;
	
	protected WifiScanResult scanResult;
	
	public MeasuringPointDrawable(Context ctx,WifiScanResult scanResult) {
		super(ctx);
		this.scanResult=scanResult;
		init();
	}
	
	public MeasuringPointDrawable(Context ctx,MultiTouchDrawable superDrawable,WifiScanResult scanResult) {
		super(ctx,superDrawable);
		this.scanResult=scanResult;
		init();
	}
	
	protected void init() {
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.red_dot);
		this.setPivot(0.5f, 0.5f);
		
		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();
		
		if(scanResult.getLocation()!=null){
			
		this.setRelativePosition(scanResult.getLocation().getX(), scanResult.getLocation().getY());
		}else {
			Logger.w("scanResult location is null!");
		}
		
		if(this.hasSuperDrawable()){
			this.getSuperDrawable().recalculateSubdrawablePositions();
		}
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
		return false;
	}

	@Override
	public boolean isOnlyInSuper() {
		// TODO Auto-generated method stub
		return false;
	}

}
