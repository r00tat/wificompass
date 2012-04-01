package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

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
import at.fhstp.wificompass.Logger;

public class TextPopupDrawable extends MultiTouchDrawable implements Popup {

	protected static final int padding = 5;

	protected String popupText;

	protected TextPaint tp;

	protected boolean isActive;

	protected StaticLayout layout;

	protected boolean persistent = false;

	// public PopupDrawable(Context ctx, String text) {
	// super(ctx);
	// init();
	// setPopupText(text);
	// }

	public TextPopupDrawable(Context ctx, MultiTouchDrawable superDrawable, String text) {
		super(ctx, superDrawable);
		init();
		setPopupText(text);
	}

	public TextPopupDrawable(Context ctx, MultiTouchDrawable superDrawable) {
		super(ctx, superDrawable);
		init();
	}

	protected void init() {
		isActive = false;

		this.setPivot(0.5f, 1.0f);
		this.width = 200;
		this.height = 100;

		tp = new TextPaint();

		tp.setColor(Color.BLACK);
		tp.setTextSize(14);

		// tv = new TextView(ctx);
		// tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		// tvLayout = new RelativeLayout(ctx);

		if (superDrawable != null) {
			this.setRelativePosition(superDrawable.width / 2, -10);
		}

	}

	@Override
	public void draw(Canvas canvas) {
		if (isActive) {
			canvas.save();

			canvas.translate(minX, minY);

			Paint paint = new Paint();
			RectF rect = new RectF(0, 0, this.width, this.height);

			paint.setColor(Color.rgb(230, 230, 230));
			paint.setStyle(Style.FILL);
			canvas.drawRoundRect(rect, 5, 5, paint);

			paint.setColor(Color.rgb(200, 200, 200));
			paint.setStrokeWidth(0);
			paint.setStyle(Style.STROKE);
			canvas.drawRoundRect(rect, 5, 5, paint);

			// canvas.restore();
			//
			// canvas.save();
			// canvas.translate(minX-width/2, minY-height);

			canvas.translate(padding, padding);

			layout.draw(canvas);

			// tv.draw(canvas);

			canvas.restore();
		}

	}

	@Override
	public Drawable getDrawable() {
		// we do not need getDrawable
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
		return false;
	}

	/**
	 * @return the popupText
	 */
	public String getPopupText() {
		return popupText;
	}

	/**
	 * @param popupText
	 *            the popupText to set
	 */
	public void setPopupText(String popupText) {
		this.popupText = popupText;
		layout = new StaticLayout(popupText, tp, width - 2 * padding, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, true);
		this.height = layout.getHeight() + 2 * padding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#onTouch(org.metalev.multitouch.controller.MultiTouchController.PointInfo)
	 */
	@Override
	public boolean onTouch(PointInfo pointinfo) {
		if (!isActive)
			return false;
		return super.onTouch(pointinfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#containsPoint(float, float)
	 */
	@Override
	public boolean containsPoint(float scrnX, float scrnY) {
		if (!isActive)
			return false;
		else {

			return super.containsPoint(scrnX, scrnY);
		}
	}

	@Override
	public void setActive(boolean isPopupActive) {
		if (!persistent)
			isActive = isPopupActive;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		this.setPopupText(popupText);
	}

	/**
	 * @return the persistent
	 */
	public boolean isPersistent() {
		return persistent;
	}

	/**
	 * @param persistent
	 *            the persistent to set
	 */
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#onSingleTouch(org.metalev.multitouch.controller.MultiTouchController.PointInfo)
	 */
	@Override
	public boolean onSingleTouch(PointInfo pointinfo) {
		if (!persistent) {
			Logger.d("disableing myself");
			isActive = false;
			return true;
		} else
			return super.onSingleTouch(pointinfo);
	}
}
