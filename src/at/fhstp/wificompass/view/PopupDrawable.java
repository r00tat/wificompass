package at.fhstp.wificompass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class PopupDrawable extends MultiTouchDrawable {

	public PopupDrawable(Context ctx) {
		super(ctx);
		init();
	}
	
	public PopupDrawable(Context ctx,MultiTouchDrawable superDrawable) {
		super(ctx,superDrawable);
		init();
	}
	
	protected void init() {
		this.setPivot(0.5f, 1.0f);
		this.width = 200;
		this.height = 100;
	}
	
	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		
		float dx = (maxX + minX) / 2;
		float dy = (maxY + minY) / 2;
		canvas.translate(dx, dy);
		
		Paint paint = new Paint();
		RectF rect = new RectF(0, 0, this.width, this.height);
		
		paint.setColor(Color.rgb(230, 230, 230));
		paint.setStyle(Style.FILL);
		canvas.drawRoundRect(rect, 5, 5, paint);
		
		paint.setColor(Color.rgb(200, 200, 200));
		paint.setStrokeWidth(0);
		paint.setStyle(Style.STROKE);
		canvas.drawRoundRect(rect, 5, 5, paint);
		
		canvas.restore();
	}
	
	@Override
	public Drawable getDrawable() {
		// TODO Auto-generated method stub
		return null;
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
		return true;
	}

}
