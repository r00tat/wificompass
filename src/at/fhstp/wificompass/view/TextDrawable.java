package at.fhstp.wificompass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * @author  Thomas Konrad (tkonrad@gmx.net)
 */
public class TextDrawable extends MultiTouchDrawable {

	protected String text = "";

	protected TextPaint tp;

	protected int maxWidth = 100;
	protected int fontSize = 14;

	protected StaticLayout layout;

	protected boolean persistent = false;


	public TextDrawable(Context ctx, MultiTouchDrawable superDrawable, String text, int maxWidth) {
		super(ctx, superDrawable);
		this.text = text;
		this.maxWidth = maxWidth;
		init();
	}
	
	public TextDrawable(Context ctx, MultiTouchDrawable superDrawable, String text, int maxWidth, int fontSize) {
		super(ctx, superDrawable);
		this.text = text;
		this.maxWidth = maxWidth;
		this.fontSize = fontSize;
		init();
	}

	public TextDrawable(Context ctx, MultiTouchDrawable superDrawable) {
		super(ctx, superDrawable);
		init();
	}

	protected void init() {
		this.setPivot(0, 0);
		tp = new TextPaint();

		tp.setColor(Color.BLACK);
		tp.setTextSize(fontSize);
		setText(text);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		canvas.translate(minX, minY);
		layout.draw(canvas);
		canvas.restore();
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
	public String getText() {
		return text;
	}

	/**
	 * @param popupText
	 *            the popupText to set
	 */
	public void setText(String text) {
		if (text == null)
			this.text = "";
		else 
			this.text = text;
		
		layout = new StaticLayout(this.text, tp, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, true);
		this.width  = layout.getWidth();
		this.height = layout.getHeight();
	}

	public int getWidth() {
		return width;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
		this.setText(text);
	}

	/**
	 * @return  the persistent
	 * @uml.property  name="persistent"
	 */
	public boolean isPersistent() {
		return persistent;
	}

	/**
	 * @param persistent  the persistent to set
	 * @uml.property  name="persistent"
	 */
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

}
