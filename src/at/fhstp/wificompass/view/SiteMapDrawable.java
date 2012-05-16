/*
 * Created on Feb 10, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.view;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.CompassListener;
import at.fhstp.wificompass.CompassMonitor;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.ToolBox;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public class SiteMapDrawable extends MultiTouchDrawable implements CompassListener {

	/**
	 * @uml.property  name="backgroundImage"
	 */
	protected Bitmap backgroundImage;

	float angleAdjustment = 0.0f;
	
	public SiteMapDrawable(Context ctx, RefreshableView refresher) {
		super(ctx,refresher);
		init();
	}

	public SiteMapDrawable(Context ctx, MultiTouchDrawable superDrawable) {
		super(ctx, superDrawable);
		init();
	}

	protected void init() {
		width = displayWidth;
		height = displayHeight;
		backgroundImage = null;
		this.resetXY();
	}

	public void startAutoRotate() {
		CompassMonitor.registerListener(ctx, this);
		Logger.d("Auto rotate started. North value: " + angleAdjustment);
	}

	public void stopAutoRotate() {
		CompassMonitor.unregisterListener(this);
	}
	
	public Drawable getDrawable() {
		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bmp);
		canvas.drawColor(Color.rgb(250, 250, 250));

		return new BitmapDrawable(ctx.getResources(), bmp);
	}

	@Override
	public void draw(Canvas canvas) {
		// Logger.d("Drawing " + this.toString());
		canvas.save();
		float dx = (maxX + minX) / 2;
		float dy = (maxY + minY) / 2;

		canvas.translate(dx, dy);
		canvas.rotate(angle * 180.0f / (float) Math.PI);
		canvas.translate(-dx, -dy);

		// fill the canvas with nearly white colur
		canvas.drawColor(Color.rgb(250, 250, 250));

		// draw backgroundimage
		if (backgroundImage != null)
			canvas.drawBitmap(backgroundImage, new Rect(0, 0, backgroundImage.getWidth(), backgroundImage.getHeight()), new Rect((int) minX,
					(int) minY, (int) maxX, (int) maxY), null);

		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.rgb(230, 230, 230));

		int counterX = 0;
		for (float x = minX; Math.floor(x) <= maxX; x += gridSpacingX * scaleX) {
			if (counterX % 10 == 0) {
				paint.setColor(Color.rgb(220, 220, 220));
				paint.setStrokeWidth(2);
			} else if (counterX % 5 == 0) {
				paint.setColor(Color.rgb(220, 220, 220));
			}

			canvas.drawLine(x, minY, x, maxY, paint);
			paint.setStrokeWidth(0);
			paint.setColor(Color.rgb(230, 230, 230));
			counterX++;
		}

		int counterY = 0;
		for (float y = minY; Math.floor(y) <= maxY; y += gridSpacingY * scaleY) {
			if (counterY % 10 == 0) {
				paint.setColor(Color.rgb(220, 220, 220));
				paint.setStrokeWidth(2);
			} else if (counterY % 5 == 0) {
				paint.setColor(Color.rgb(220, 220, 220));
			}

			canvas.drawLine(minX, y, maxX, y, paint);
			paint.setStrokeWidth(0);
			paint.setColor(Color.rgb(230, 230, 230));
			counterY++;
		}

		canvas.restore();

		this.drawSubdrawables(canvas);
	}


	@Override
	public void setAngle(float angle) {
		super.setAngle(angle);
	}

	@Override
	public void setScale(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	@Override
	public void setRelativePosition(float xPos, float yPos) {
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
		return false;
	}

	@Override
	public boolean hasSuperDrawable() {
		return false;
	}

	@Override
	public MultiTouchDrawable getSuperDrawable() {
		return null;
	}

	/**
	 * set the size of the map
	 * 
	 * @param width
	 *            in pixels
	 * @param height
	 *            in pixels
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		this.recalculatePositions();

	}

	/**
	 * @return  the backgroundImage
	 * @uml.property  name="backgroundImage"
	 */
	public Bitmap getBackgroundImage() {
		return backgroundImage;
	}

	/**
	 * @param backgroundImage  the backgroundImage to set
	 * @uml.property  name="backgroundImage"
	 */
	public void setBackgroundImage(Bitmap backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#bringSubDrawableToFront(at.fhstp.wificompass.view.MultiTouchDrawable)
	 */
	@Override
	protected void bringSubDrawableToFront(MultiTouchDrawable drawable) {
		super.bringSubDrawableToFront(drawable);

		if (!(drawable instanceof UserDrawable)) {
			// user should be one of the last drawables, so we search the vector reverse
			for (int i = subDrawables.size() - 1; i >= 0; i--) {
				if (subDrawables.get(i) instanceof UserDrawable) {
					// the user should always be in front
					subDrawables.get(i).bringToFront();
					break;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#onSingleTouch(org.metalev.multitouch.controller.MultiTouchController.PointInfo)
	 */
	@Override
	public boolean onSingleTouch(PointInfo pointinfo) {
		hidePopups();
		return true;
	}

	public void setAngleAdjustment(float adjustment) {
		this.angleAdjustment = adjustment;
		this.angleChangeCallback = null;
	}

	@Override
	public void onCompassChanged(float azimuth, String direction) {
		//azimuth = (float) Math.toRadians(azimuth);
		float adjusted = ToolBox.normalizeAngle((azimuth - angleAdjustment) * -1.0f);
		this.setAngle(adjusted);
		this.recalculatePositions();
	}
	
}
