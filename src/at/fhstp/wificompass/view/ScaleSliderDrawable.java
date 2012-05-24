package at.fhstp.wificompass.view;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.R;

public class ScaleSliderDrawable extends MultiTouchDrawable {

	protected int id;
	
	public ScaleSliderDrawable(Context context, MultiTouchDrawable superDrawable, int id) {
		super(context, superDrawable);
		this.id = id;
		init();
	}

	protected static BitmapDrawable icon;
	
	protected void init() {
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.slider_arrow_up);
		this.setPivot(0.48f, 0.1f);

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
		if(superDrawable instanceof ScaleLineDrawable)
			((ScaleLineDrawable)superDrawable).onSliderMove(id);
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

}
