package at.fhstp.wificompass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class ScaleLineDrawable extends MultiTouchDrawable {

	protected ScaleSliderDrawable slider1;

	protected ScaleSliderDrawable slider2;

	// protected float slider1.getRelativeX();
	//
	// protected float slider1.getRelativeY();
	//
	// protected float slider2.getRelativeX();
	//
	// protected float slider2.getRelativeY();

	protected OkDrawable okDrawable;

	public ScaleLineDrawable(Context context, MultiTouchDrawable superDrawable, OkCallback okCallback) {
		super(context, superDrawable);
		slider1 = new ScaleSliderDrawable(ctx, this, 1);
		slider2 = new ScaleSliderDrawable(ctx, this, 2);

		okDrawable = new OkDrawable(ctx, this, okCallback);
		this.setRelativePosition(0, 0);
	}

	protected void init() {

	}

	public void onSliderMove(int id) {

		if (okDrawable != null) {
			okDrawable.setRelativePosition((slider1.getRelativeX() + slider2.getRelativeX()) / 2,
					(slider1.getRelativeY() + slider2.getRelativeY()) / 2);
		}

		// Logger.d("Slider position was set: slider1(" + slider1.getRelativeX() + ", " + slider1.getRelativeY() + "), slider2(" + slider2.getRelativeX() + ", " + slider2.getRelativeY() + ")");
	}

	public float getSliderDistance() {
		return (float) Math.sqrt(Math.pow(slider2.getRelativeX() - slider1.getRelativeX(), 2)
				+ Math.pow(slider2.getRelativeY() - slider1.getRelativeY(), 2));
	}

	public void removeScaleSliders() {
		this.removeSubDrawable(slider1);
		this.removeSubDrawable(slider2);
	}

	@Override
	public Drawable getDrawable() {
		return null;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		float dx = (maxX + minX) / 2;
		float dy = (maxY + minY) / 2;

		canvas.translate(dx, dy);
		canvas.rotate((float) Math.toDegrees(this.getAngle()));

		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.rgb(255, 0, 0));

		float scaleX = this.getScaleX();
		float scaleY = this.getScaleY();

		canvas.drawLine(slider1.getRelativeX() * scaleX, slider1.getRelativeY() * scaleY, slider2.getRelativeX() * scaleX, slider2.getRelativeY()
				* scaleY, paint);

		canvas.restore();

		this.drawSubdrawables(canvas);
	}

	@Override
	public boolean isScalable() {
		return true;
	}

	@Override
	public boolean isRotateable() {
		return true;
	}

	@Override
	public boolean isDragable() {
		return true;
	}

	@Override
	public boolean isOnlyInSuper() {
		return true;
	}

	public ScaleSliderDrawable getSlider(int id) {
		switch (id) {
		case 1:
			return slider1;
		case 2:
			return slider2;
		default:
			return null;
		}
	}

}
