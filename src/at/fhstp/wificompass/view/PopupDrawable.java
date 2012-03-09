package at.fhstp.wificompass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

public class PopupDrawable extends MultiTouchDrawable {
	
	protected static final int padding=5;
	
	protected String popupText;
	
	protected TextPaint tp;

	public PopupDrawable(Context ctx,String text) {
		super(ctx);
		init();
		setPopupText(text);
	}
	
	public PopupDrawable(Context ctx,MultiTouchDrawable superDrawable,String text) {
		super(ctx,superDrawable);
		init();
		setPopupText(text);
	}
	
	protected void init() {
		this.setPivot(0.5f, 1.0f);
		this.width = 200;
		this.height = 100;
		
		tp = new TextPaint();

		tp.setColor(Color.BLACK);
		tp.setTextSize(14);
		
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
		
		
		
//		canvas.restore();
//		
//		canvas.save();
//		canvas.translate(minX-width/2, minY-height);
		
		canvas.translate(padding,padding);
		
		new StaticLayout(popupText.toString(), tp, width-2*padding, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, true).draw(canvas);
		canvas.restore();
		
		
	}
	
	@Override
	public Drawable getDrawable() {
		// we de not need getDrawable
		return null;
	}

	@Override
	public boolean isScalable() {
		return false;
	}

	@Override
	public boolean isRotateable() {
		return false;
	}

	@Override
	public boolean isDragable() {
		return false;
	}

	@Override
	public boolean isOnlyInSuper() {
		return true;
	}

	/**
	 * @return the popupText
	 */
	public String getPopupText() {
		return popupText;
	}

	/**
	 * @param popupText the popupText to set
	 */
	public void setPopupText(String popupText) {
		this.popupText = popupText;
		
	}

}
