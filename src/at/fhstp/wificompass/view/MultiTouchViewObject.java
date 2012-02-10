/*
 * Created on Feb 10, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.view;

import java.util.ArrayList;
import java.util.Iterator;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import at.fhstp.wificompass.Logger;

public class MultiTouchViewObject {

	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;

	private int mUIMode = UI_MODE_ROTATE;

	private boolean firstLoad;

	private int width, height, displayWidth, displayHeight;

	private float centerX, centerY, scaleX, scaleY, angle;

	private float minX, maxX, minY, maxY;

	private static final float SCREEN_MARGIN = 100;

	protected MultiTouchDrawable drawable;

	protected Resources resources;

	protected ArrayList<MultiTouchViewObject> subObjects;

	public MultiTouchViewObject(MultiTouchDrawable d, Resources res) {
		Logger.d("created MultiTouchObject for " + d.getId());
		this.firstLoad = true;
		this.drawable = d;
		this.resources = res;
		subObjects = new ArrayList<MultiTouchViewObject>();
		getMetrics();
		load();
	}

	private void getMetrics() {
		DisplayMetrics metrics = resources.getDisplayMetrics();
		// The DisplayMetrics don't seem to always be updated on screen rotate, so we hard code a portrait
		// screen orientation for the non-rotated screen here...
		// this.displayWidth = metrics.widthPixels;
		// this.displayHeight = metrics.heightPixels;
		
		//TODO remove randomnis
		
		this.displayWidth = resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels,
				metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
		this.displayHeight = resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels,
				metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);
	}

	/** Called by activity's onResume() method to load the images */
	public void load() {
		getMetrics();
		// this.drawable = res.getDrawable(resId);
		this.width = drawable.getWidth();
		this.height = drawable.getHeight();
		float cx, cy, sx, sy;
		if (firstLoad) {
			cx = SCREEN_MARGIN + (float) (Math.random() * (displayWidth - 2 * SCREEN_MARGIN));
			cy = SCREEN_MARGIN + (float) (Math.random() * (displayHeight - 2 * SCREEN_MARGIN));
			float sc = (float) (Math.max(displayWidth, displayHeight) / (float) Math.max(width, height) * Math.random() * 0.3 + 0.2);
			sx = sy = sc;
			firstLoad = false;
		} else {
			// Reuse position and scale information if it is available
			// FIXME this doesn't actually work because the whole activity is torn down and re-created on rotate
			cx = this.centerX;
			cy = this.centerY;
			sx = this.scaleX;
			sy = this.scaleY;
			// Make sure the image is not off the screen after a screen rotation
			if (this.maxX < SCREEN_MARGIN)
				cx = SCREEN_MARGIN;
			else if (this.minX > displayWidth - SCREEN_MARGIN)
				cx = displayWidth - SCREEN_MARGIN;
			if (this.maxY > SCREEN_MARGIN)
				cy = SCREEN_MARGIN;
			else if (this.minY > displayHeight - SCREEN_MARGIN)
				cy = displayHeight - SCREEN_MARGIN;
		}
		setPos(cx, cy, sx, sy, 0.0f);
	}

	public void resetXY() {
		this.centerX = SCREEN_MARGIN + (float) (Math.random() * (displayWidth - 2 * SCREEN_MARGIN));
		this.centerY = SCREEN_MARGIN + (float) (Math.random() * (displayHeight - 2 * SCREEN_MARGIN));

	}

	public void resetScale() {
		scaleX = scaleY = (float) (Math.max(displayWidth, displayHeight) / (float) Math.max(width, height) * Math.random() * 0.5 + 0.2);

	}

	public void resetAngle() {
		angle = 0.0f;
	}

	/** Called by activity's onPause() method to free memory used for loading the images */
	public void unload() {

	}

	/** Set the position and scale of an image in screen coordinates */
	public boolean setPos(PositionAndScale newImgPosAndScale) {
		return setPos(newImgPosAndScale.getXOff(), newImgPosAndScale.getYOff(),
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale.getScaleX() : newImgPosAndScale.getScale(),
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale.getScaleY() : newImgPosAndScale.getScale(),
				newImgPosAndScale.getAngle());
		// FIXME: anisotropic scaling jumps when axis-snapping
		// FIXME: affine-ize
		// return setPos(newImgPosAndScale.getXOff(), newImgPosAndScale.getYOff(), newImgPosAndScale.getScaleAnisotropicX(),
		// newImgPosAndScale.getScaleAnisotropicY(), 0.0f);
	}

	/** Set the position and scale of an image in screen coordinates */
	private boolean setPos(float centerX, float centerY, float scaleX, float scaleY, float angle) {

		// TODO adapt subobject position
		float ws = (width / 2) * scaleX, hs = (height / 2) * scaleY;
		float newMinX = centerX - ws, newMinY = centerY - hs, newMaxX = centerX + ws, newMaxY = centerY + hs;
		if (newMinX > displayWidth - SCREEN_MARGIN || newMaxX < SCREEN_MARGIN || newMinY > displayHeight - SCREEN_MARGIN || newMaxY < SCREEN_MARGIN)
			return false;
		
		float dCenterX=centerX-this.centerX;
		float dCenterY=centerY-this.centerY;
		
		float dScaleX=scaleX-this.scaleX;
		float dScaleY=scaleY-this.scaleY;
		
		float dAngle=angle-this.angle;
		
		if(drawable.isDragable()){
			
		this.centerX = centerX;
		this.centerY = centerY;
		}else {
			dCenterY=0;
			dCenterX=0;
		}

		if (drawable.isScalable()) {
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			drawable.setScale(scaleX, scaleY);
			
			this.minX = newMinX;
			this.minY = newMinY;
			this.maxX = newMaxX;
			this.maxY = newMaxY;
		}else {
			dScaleY=0;
			dScaleX=0;
		}
		if (drawable.isRotateable()) {
			this.angle = angle;
			drawable.setAngle(angle);
		}else {
			dAngle=0;
		}



		// Iterate through the subobjects and change their position (TODO: this doesn't work yet)
		Iterator<MultiTouchViewObject> iterator = subObjects.iterator();
		while (iterator.hasNext()) {
			MultiTouchViewObject subobject = iterator.next();

			Logger.d("Repositioning sub-drawable.");
			
			subobject.setPos(subobject.centerX+dCenterX, subobject.centerY+dCenterY, subobject.scaleX+dScaleX, subobject.scaleY+dScaleY, subobject.angle+dAngle);
		}

		return true;
	}

	/** Return whether or not the given screen coords are inside this image */
	public boolean containsPoint(float scrnX, float scrnY) {
		// FIXME: need to correctly account for image rotation
		return (scrnX >= minX && scrnX <= maxX && scrnY >= minY && scrnY <= maxY);
	}

	public boolean onTouch(PointInfo pointInfo) {
		// FIXME, TODO: calculate normalized point!
		return drawable.onTouch(pointInfo);
	}

	public void draw(Canvas canvas) {
		// Logger.d("drawing "+this.toString());
		canvas.save();
		float dx = (maxX + minX) / 2;
		float dy = (maxY + minY) / 2;
		Drawable d = drawable.getDrawable();
		d.setBounds((int) minX, (int) minY, (int) maxX, (int) maxY);
		canvas.translate(dx, dy);
		canvas.rotate(angle * 180.0f / (float) Math.PI);
		canvas.translate(-dx, -dy);
		d.draw(canvas);
		canvas.restore();

	}

	public MultiTouchDrawable getDrawable() {
		return drawable;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getCenterX() {
		return centerX;
	}

	public float getCenterY() {
		return centerY;
	}

	public float getScaleX() {
		return scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public float getAngle() {
		return angle;
	}

	// FIXME: these need to be updated for rotation
	public float getMinX() {
		return minX;
	}

	public float getMaxX() {
		return maxX;
	}

	public float getMinY() {
		return minY;
	}

	public float getMaxY() {
		return maxY;
	}

	/**
	 * @param centerX
	 *            the centerX to set
	 */
	public void setCenterX(float centerX) {
		this.centerX = centerX;
	}

	/**
	 * @param centerY
	 *            the centerY to set
	 */
	public void setCenterY(float centerY) {
		this.centerY = centerY;
	}

	/**
	 * @param angle
	 *            the angle to set
	 */
	public void setAngle(float angle) {
		this.angle = angle;
	}

	/**
	 * @param scaleX
	 *            the scaleX to set
	 */
	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	/**
	 * @param scaleY
	 *            the scaleY to set
	 */
	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public String getDrawableId() {
		return drawable.getId();
	}

	public String toString() {
		return "MultiTouchViewObject for " + drawable.getId() + " " + drawable.getWidth() + "x" + drawable.getHeight() + " center (" + centerX + ","
				+ centerY + ") min (" + minX + "," + minY + ") max (" + maxX + "," + maxY + ") scale (" + scaleX + "," + scaleY + ") angle " + angle;
	}

	public void addSubViewObject(MultiTouchViewObject subObject) {
		subObjects.add(subObject);
	}

	public void removeSubViewObject(MultiTouchViewObject subObject) {
		subObjects.remove(subObject);
	}
}
