package at.fhstp.wificompass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.Logger;

public class ScaleLineDrawable extends MultiTouchDrawable {

	protected ScaleSliderDrawable slider1;
	protected ScaleSliderDrawable slider2;
	protected float slider1RelX;
	protected float slider1RelY;
	protected float slider2RelX;
	protected float slider2RelY;
	
	public ScaleLineDrawable(Context context, MultiTouchDrawable superDrawable) {
		super(context, superDrawable);
		init();
	}
	
	protected void init() {
		slider1 = new ScaleSliderDrawable(this.ctx, this.getSuperDrawable(), this, 1);
		slider2 = new ScaleSliderDrawable(this.ctx, this.getSuperDrawable(), this, 2);
	}
	
	public void setSliderPosition(int id, float relX, float relY) {
		if (id == 1) {
			this.slider1RelX = relX;
			this.slider1RelY = relY;
		}
		else if (id == 2) {
			this.slider2RelX = relX;
			this.slider2RelY = relY;
		}
		
		Logger.d("Slider position was set: slider1(" + slider1RelX + ", " + slider1RelY + "), slider2(" + slider2RelX + ", " + slider2RelY + ")"); 
	}
	
	public float getSliderDistance() {
		return (float) Math.sqrt(Math.pow(slider2RelX - slider1RelX, 2) + Math.pow(slider2RelY - slider1RelY, 2));
	}
	
	public void removeScaleSliders() {
		this.getSuperDrawable().removeSubDrawable(slider1);
		this.getSuperDrawable().removeSubDrawable(slider2);
	}
	
	@Override
	public Drawable getDrawable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		float dx = (maxX + minX) / 2;
		float dy = (maxY + minY) / 2;

		canvas.translate(dx, dy);
		canvas.rotate(this.getSuperDrawable().getAngle() * 180.0f / (float) Math.PI);
		
		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.rgb(255, 0, 0));
		
		float scaleX = this.getSuperDrawable().getScaleX();
		float scaleY = this.getSuperDrawable().getScaleY();
		
		canvas.drawLine(slider1RelX*scaleX, slider1RelY*scaleY, slider2RelX*scaleX, slider2RelY*scaleY, paint);
		
		canvas.restore();

		this.drawSubdrawables(canvas);
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
		return true;
	}

}
