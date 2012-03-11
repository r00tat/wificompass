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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class SiteMapDrawable extends MultiTouchDrawable {


	public SiteMapDrawable(Context ctx) {
		super(ctx);
		init();
	}

	public SiteMapDrawable(Context ctx, MultiTouchDrawable superDrawable) {
		super(ctx, superDrawable);
		init();
	}

	protected void init() {
		width = displayWidth;
		height = displayHeight;

		this.resetXY();
	}

	public Drawable getDrawable() {
		Bitmap bmp = Bitmap
				.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bmp);
		canvas.drawColor(Color.rgb(250, 250, 250));

//		Paint paint = new Paint();
//		paint.setStyle(Paint.Style.STROKE);
//		paint.setColor(Color.rgb(230, 230, 230));
//
//		for (int x = 0; x < displayWidth; x += gridSpacingX) {
//			canvas.drawLine(x, 0, x, displayHeight, paint);
//		}
//		
//		for (int y = 0; y < displayHeight; y += gridSpacingY) {
//			canvas.drawLine(0, y, displayWidth, y, paint);
//		}

		return new BitmapDrawable(ctx.getResources(), bmp);
	}
	
	@Override
	public void draw(Canvas canvas) {
		//Logger.d("Drawing " + this.toString());
		canvas.save();
		float dx = (maxX + minX) / 2;
		float dy = (maxY + minY) / 2;

		canvas.translate(dx, dy);
		canvas.rotate(angle * 180.0f / (float) Math.PI);
		canvas.translate(-dx, -dy);
	
		canvas.drawColor(Color.rgb(250, 250, 250));

		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.rgb(230, 230, 230));

		int counterX = 0;
		for (float x = minX; x < maxX; x += gridSpacingX * scaleX) {
			if (counterX % 10 == 0) {
				paint.setColor(Color.rgb(220, 220, 220));
				paint.setStrokeWidth(2);
			} else if (counterX % 5 == 0) {
				paint.setColor(Color.rgb(220, 220, 220));
			}
			
			canvas.drawLine(x, minY, x, maxY, paint);
			paint.setStrokeWidth(0);
			paint.setColor(Color.rgb(230, 230, 230));
			counterX ++;
		}
		
		int counterY = 0;
		for (float y = minY; y < maxY; y += gridSpacingY * scaleY) {
			if (counterY % 10 == 0) {
				paint.setColor(Color.rgb(220, 220, 220));
				paint.setStrokeWidth(2);
			} else if (counterY % 5 == 0) {
				paint.setColor(Color.rgb(220, 220, 220));
			}			
			
			canvas.drawLine(minX, y, maxX, y, paint);
			paint.setStrokeWidth(0);
			paint.setColor(Color.rgb(230, 230, 230));
			counterY ++;
		}
		
		canvas.restore();

		this.drawSubdrawables(canvas);
	}

	@Override
	public boolean onTouch(PointInfo pointinfo) {
		return super.onTouch(pointinfo);
	}

	@Override
	public void setAngle(float angle) {
		this.angle = angle;
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
	 * @param width	in pixels
	 * @param height in pixels
	 */
	public void setSize(int width,int height){
		this.width=width;
		this.height=height;
		this.recalculatePositions();
		
	}

}
