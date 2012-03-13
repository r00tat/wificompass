package at.fhstp.wificompass.view;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.R;

public class ScaleSliderDrawable extends MultiTouchDrawable {

	protected int id;
	protected ScaleLineDrawable line;
	
	public ScaleSliderDrawable(Context context, MultiTouchDrawable superDrawable, ScaleLineDrawable line, int id) {
		super(context, superDrawable);
		this.line = line;
		this.id = id;
		init();
	}

	protected static BitmapDrawable icon;
	
	protected void init() {
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.slider_arrow_up);
		this.setPivot(0.5f, 0.0222f);

		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();
		
		this.forwardRelativePositionToScaleLine();
	}
	
	@Override
	public void setRelativePosition(PointF relativePosition) {
		super.setRelativePosition(relativePosition.x, relativePosition.y);
		this.forwardRelativePositionToScaleLine();
	}

	@Override
	public void setRelativePosition(float relX, float relY) {
		super.setRelativePosition(relX, relY);
		this.forwardRelativePositionToScaleLine();
	}
	
	protected void forwardRelativePositionToScaleLine() {
		line.setSliderPosition(this.id, this.getRelativeX(), this.getRelativeY());
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
		// TODO Auto-generated method stub
		return true;
	}

}
