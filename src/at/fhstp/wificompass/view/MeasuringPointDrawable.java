package at.fhstp.wificompass.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.R;

public class MeasuringPointDrawable extends MultiTouchDrawable {

	protected static BitmapDrawable icon;
	
	public MeasuringPointDrawable(Context ctx) {
		super(ctx);
		init();
	}
	
	public MeasuringPointDrawable(Context ctx,MultiTouchDrawable superDrawable) {
		super(ctx,superDrawable);
		init();
	}
	
	protected void init() {
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.red_dot);
		this.setPivot(0.5f, 0.5f);
		
		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();
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
