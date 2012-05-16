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

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.ToolBox;

/**
 * Abstract Class for drawable objects for the MultiTouchView
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public abstract class MultiTouchDrawable {

	/**
	 * static counter for a unique drawable Id
	 */
	protected static int counter = 0;

	/**
	 * unique numeric id
	 * @uml.property  name="id"
	 */
	protected int id;

	/**
	 * current angle of drawable
	 * @uml.property  name="angle"
	 */
	protected float angle = 0;

	/**
	 * x and y scale factors
	 * @uml.property  name="scaleX"
	 */
	protected float scaleX = 1.0f;

	/**
	 * x and y scale factors
	 * @uml.property  name="scaleY"
	 */
	protected float scaleY = 1.0f;

	/**
	 * relative x and y position
	 */
	protected float relX = 0;

	/**
	 * relative x and y position
	 */
	protected float relY = 0;

	/**
	 * The pivot point needed for rotation (value between 0 and 1), default center
	 * @uml.property  name="pivotX"
	 */
	protected float pivotX = 0.5f;

	/**
	 * The pivot point needed for rotation (value between 0 and 1), default center
	 * @uml.property  name="pivotY"
	 */
	protected float pivotY = 0.5f;

	/**
	 * Super drawable
	 * @uml.property  name="superDrawable"
	 * @uml.associationEnd  
	 */
	protected MultiTouchDrawable superDrawable = null;

	/**
	 * @uml.property  name="gridSpacingX"
	 */
	protected static float gridSpacingX = 30;

	/**
	 * @uml.property  name="gridSpacingY"
	 */
	protected static float gridSpacingY = 30;

	/**
	 * Context
	 */
	protected Context ctx;

	protected static final int UI_MODE_ROTATE = 1;

	protected static final int UI_MODE_ANISOTROPIC_SCALE = 2;

	protected static final int FLAG_FORCEXY = 1;

	protected static final int FLAG_FORCESCALE = 2;

	protected static final int FLAG_FORCEROTATE = 4;

	protected static final int FLAG_FORCEALL = 7;

	protected int mUIMode = UI_MODE_ROTATE;

	protected static boolean firstLoad= true;

	/**
	 * @uml.property  name="width"
	 */
	protected int width;

	/**
	 * @uml.property  name="height"
	 */
	protected int height;
	
	protected static int displayWidth=0;

	protected static int displayHeight=0;

	/**
	 * @uml.property  name="centerX"
	 */
	protected float centerX;

	/**
	 * @uml.property  name="centerY"
	 */
	protected float centerY;

	/**
	 * @uml.property  name="minX"
	 */
	protected float minX;

	/**
	 * @uml.property  name="maxX"
	 */
	protected float maxX;

	/**
	 * @uml.property  name="minY"
	 */
	protected float minY;

	/**
	 * @uml.property  name="maxY"
	 */
	protected float maxY;

	protected static final float SCREEN_MARGIN = 0;

	protected Resources resources;

	/**
	 * @uml.property  name="subDrawables"
	 */
	protected ArrayList<MultiTouchDrawable> subDrawables;

	/**
	 * @uml.property  name="refresher"
	 * @uml.associationEnd  
	 */
	protected RefreshableView refresher;

	protected AngleChangeCallback angleChangeCallback = null;
	
	/**
	 * default constructor
	 * 
	 * @param context
	 */
	public MultiTouchDrawable(Context context, RefreshableView containingView) {
		id = counter++;
		this.ctx = context;
		
		this.resources = context.getResources();
		subDrawables = new ArrayList<MultiTouchDrawable>();
		this.refresher = containingView;
		load();
	}

	/**
	 * constructor with superDrawable
	 * 
	 * @param context
	 * @param superDrawable
	 *            super Drawable Object
	 */
	public MultiTouchDrawable(Context context, MultiTouchDrawable superDrawable) {
		id = counter++;

		this.ctx = context;
		this.superDrawable = superDrawable;

		
		this.resources = context.getResources();
		subDrawables = new ArrayList<MultiTouchDrawable>();

		superDrawable.addSubDrawable(this);
	}

	/**
	 * <p>
	 * returns a Drawable object, to be painted on the MultiTochView.
	 * </p>
	 * <p>
	 * This function is called by the MultiTouchView onDraw method. This should return a BitmapDrawable or any other Drawable which should represent the content
	 * </p>
	 * 
	 * @return Drawable to be painted
	 * @see android.graphics.drawable.Drawable
	 */
	public abstract Drawable getDrawable();

	/**
	 * get the width in pixel of the Drawable
	 * @return  width in pixel
	 * @uml.property  name="width"
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * get the height in pixel of the Drawable
	 * @return  height in pixel
	 * @uml.property  name="height"
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * get an unique ID of one instance
	 * @return  id
	 * @uml.property  name="id"
	 */
	public String getId() {
		return this.getClass().getName() + ":" + id;
	}

	/**
	 * callback method for onTouch events
	 * 
	 * @param pointinfo
	 *            info about the touch event
	 * @return <p>
	 *         <b>true</b> if the touch event is handled by this event<br />
	 *         <b>false</b> if this touch event is not for this element
	 *         </p>
	 *         <p>
	 *         This is intended, that a subobject can check, if it should handle a click or so, or if the event should be sent to the underlying object.
	 *         </p>
	 * 
	 */
	public boolean onTouch(PointInfo pointinfo) {
		boolean handleEvent = false;

		// first ask our subobjects to handle it, otherwise let us handle it

		for (int i = subDrawables.size() - 1; i >= 0 && !handleEvent; i--) {

			MultiTouchDrawable sub = subDrawables.get(i);

			if (sub.containsPoint(pointinfo.getX(), pointinfo.getY())) {
				handleEvent = sub.onTouch(pointinfo);
			}
		}

		// Logger.d("touch action: "+this.getClass().getSimpleName()+" " +
		// pointinfo.getAction()+
		// " 0x"+Integer.toHexString(pointinfo.getAction()));

		// it's hard to find MOTION_UP events, because if the finger does not
		// stay exactly on the correct position,
		// this will change to dragging and we wont't get the drag end as
		// MOTION_UP

		if (!handleEvent && pointinfo.isMultiTouch() == false && pointinfo.getNumTouchPoints() == 1
				&& pointinfo.getAction() == MotionEvent.ACTION_DOWN) {
			handleEvent = this.onSingleTouch(pointinfo);
		}

		return handleEvent;
	}

	/**
	 * called if a single touch event is found
	 * 
	 * @param pointinfo
	 * @return
	 */
	public boolean onSingleTouch(PointInfo pointinfo) {
		return false;
	}

	/**
	 * set Angle of the Drawable
	 * @param  angle
	 * @uml.property  name="angle"
	 */
	public void setAngle(float angle) {
		// this.angle = angle;
		// we only want positive angles, so calculation are easier
		// angle must be in the range from 0 to 2*PI or in degrees 0° to 359°
		// to convert the angle from radiants to degrees use angle*180/Math.PI
		this.angle = ToolBox.normalizeAngle(angle);
		
		if (this.angleChangeCallback != null) {
			angleChangeCallback.angleChanged(angle, this);
		}
	}

	/**
	 * set scaleing
	 * 
	 * @param scaleX
	 * @param scaleY
	 */
	public void setScale(float scaleX, float scaleY) {
		if (isScalable()) {
			this.scaleX = scaleX;
			this.scaleY = scaleY;
		}
	}

	/**
	 * set relative position to super object if set
	 * 
	 * @param relativePosition
	 */
	public void setRelativePosition(PointF relativePosition) {
		this.setRelativePosition(relativePosition.x, relativePosition.y);
	}

	/**
	 * set relative position to super object if set
	 * 
	 * @param xPos
	 *            relative x position
	 * @param yPos
	 *            relative y position
	 */
	public void setRelativePosition(float relX, float relY) {
		this.relX = relX;
		this.relY = relY;
		if (superDrawable != null) {
			superDrawable.recalculatePositions();
		}
		onRelativePositionUpdate();
	}

	/**
	 * Get the x position of the drawable relative to it's super-drawable (if set)
	 * 
	 * @return The x position relative to it's super drawable
	 */
	public float getRelativeX() {
		return this.relX;
	}

	/**
	 * Get the y position of the drawable relative to it's super-drawable (if set)
	 * 
	 * @return The y position relative to it's super drawable
	 */
	public float getRelativeY() {
		return this.relY;
	}

	/**
	 * Returns whether a custom pivot point is used for this drawable. If not, the image center will be used.
	 * 
	 * @return Whether a custom pivot is used or not
	 */
	public boolean isCustomPivotUsed() {
		return (this.pivotX != 0.5f || this.pivotY != 0.5f) ? true : false;
	}

	/**
	 * Sets the pivot point (value between 0 and 1) or, in other words, the semantic center of the image. This is the point relative to which sub-drawables will be positioned when the super-drawable
	 * is scaled and rotated.
	 * 
	 * @param pivotX
	 *            The relative x value (between 0 and 1) of the pivot point
	 * @param pivotY
	 *            The relative x value (between 0 and 1) of the pivot point
	 */
	public void setPivot(float pivotX, float pivotY) {
		this.pivotX = pivotX;
		this.pivotY = pivotY;
	}

	/**
	 * Returns the the x coordinate of the pivot point
	 * @return  The x coordinate of the pivot point
	 * @uml.property  name="pivotX"
	 */
	public float getPivotX() {
		return this.pivotX;
	}

	/**
	 * Returns the the y coordinate of the pivot point
	 * @return  The y coordinate of the pivot point
	 * @uml.property  name="pivotY"
	 */
	public float getPivotY() {
		return this.pivotY;
	}

	/**
	 * Returns the the x coordinate of the pivot point relative to the image's center. This is useful when the image is by default positioned relative to it's center and has to be moved from there.
	 * 
	 * @return The x coordinate of the pivot point relative to the image's center
	 */
	public float getPivotXRelativeToCenter() {
		return this.getWidth() * this.pivotX - this.getWidth() / 2;
	}

	/**
	 * Returns the the y coordinate of the pivot point relative to the image's center. This is useful when the image is by default positioned relative to it's center and has to be moved from there.
	 * 
	 * @return The y coordinate of the pivot point relative to the image's center
	 */
	public float getPivotYRelativeToCenter() {
		return this.getHeight() * this.pivotY - this.getHeight() / 2;
	}

	/**
	 * does this object support scaleing, or should it stay the same size all the time
	 * 
	 * @return true if scalable
	 */
	public abstract boolean isScalable();

	/**
	 * does this object support rotateing, or should it have the same angle all the time
	 * 
	 * @return true if rotateable
	 */
	public abstract boolean isRotateable();

	/**
	 * does this object support dragging, or should it stay where it is
	 * 
	 * @return true if dragable
	 */
	public abstract boolean isDragable();

	/**
	 * is this object only allowed to be positioned in super object
	 * 
	 * @return true, if this object must be on the super object
	 */
	public abstract boolean isOnlyInSuper();

	/**
	 * does this object have a Drawable, which it corresponds to and should be modified with its super Drawable, i.e. scaled or moved.
	 * 
	 * @return true, if it suppports and has a super Drawable
	 */
	public boolean hasSuperDrawable() {
		return (superDrawable == null ? false : true);
	}

	/**
	 * get the super Drawable
	 * @return  MultiTouchDrawable if existent, otherwise null
	 * @uml.property  name="superDrawable"
	 */
	public MultiTouchDrawable getSuperDrawable() {
		return superDrawable;
	}

	// imported from MultiTouchDrawable

	protected void getMetrics() {
		DisplayMetrics metrics = resources.getDisplayMetrics();
		// The DisplayMetrics don't seem to always be updated on screen rotate,
		// so we hard code a portrait
		// screen orientation for the non-rotated screen here...
		displayWidth = metrics.widthPixels;
		displayHeight = metrics.heightPixels;

		displayWidth = resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels,
				metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
		displayHeight = resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels,
				metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);
	}

	/** Called by activity's onResume() method to load the images */
	public void load() {
		if(firstLoad){
			getMetrics();
			firstLoad=false;
		}
		
		for(MultiTouchDrawable sub:subDrawables){
			sub.load();
		}
			

//		float cx, cy, sx, sy;
//		if (firstLoad) {
//			cx = 0;
//			cy = 0;
//
//			float sc = 1;
//			sx = sy = sc;
//			firstLoad = false;
//		} else {
//			// Reuse position and scale information if it is available
//			// FIXME this doesn't actually work because the whole activity is
//			// torn down and re-created on rotate
//			cx = this.centerX;
//			cy = this.centerY;
//			sx = this.scaleX;
//			sy = this.scaleY;
//		}
//		setPos(cx, cy, sx, sy, 0.0f, FLAG_FORCEALL, false);
	}

	public void resetXY() {
		this.centerX = this.width / 2;
		this.centerY = this.height / 2;
	}

	public void resetScale() {
		scaleX = scaleY = 1;
	}

	public void resetAngle() {
		this.setAngle(0.0f);
	}

	/**
	 * Called by activity's onPause() method to free memory used for loading the images
	 */
	public void unload() {
		for(MultiTouchDrawable sub: subDrawables){
			sub.unload();
		}
	}

	/** Set the position and scale of an image in screen coordinates */
	public boolean setPos(PositionAndScale newImgPosAndScale, boolean isDraggedOrPinched) {
		return setPos(newImgPosAndScale.getXOff(), newImgPosAndScale.getYOff(),
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale.getScaleX() : newImgPosAndScale.getScale(),
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale.getScaleY() : newImgPosAndScale.getScale(),
				newImgPosAndScale.getAngle(), isDraggedOrPinched);
		// FIXME: anisotropic scaling jumps when axis-snapping
		// FIXME: affine-ize
		// return setPos(newImgPosAndScale.getXOff(),
		// newImgPosAndScale.getYOff(),
		// newImgPosAndScale.getScaleAnisotropicX(),
		// newImgPosAndScale.getScaleAnisotropicY(), 0.0f);
	}

	protected boolean setPos(float centerX, float centerY, float scaleX, float scaleY, float angle, boolean isDraggedOrPinched) {
		return setPos(centerX, centerY, scaleX, scaleY, angle, 0, isDraggedOrPinched);
	}

	/** Set the position and scale of an image in screen coordinates */
	protected boolean setPos(float centerX, float centerY, float scaleX, float scaleY, float angle, int flags, boolean isDraggedOrPinched) {

		// Reset the scale if the object is not scalable (otherwise the ws and
		// hs calculations below are not 100% accurate and will result in a
		// slight but noticable scaling of non-scalable objects
		if (!isScalable()) {
			scaleX = 1.0f;
			scaleY = 1.0f;
		}

		// Reset the angle if the drawable is not rotatable (for the same reason
		// as above)
		if (!isRotateable()) {
			this.setAngle(0.0f);
		}

		float ws = (width / 2) * scaleX, hs = (height / 2) * scaleY;
		float newMinX = centerX - ws, newMinY = centerY - hs, newMaxX = centerX + ws, newMaxY = centerY + hs;

		float scaleXChange = 1.0f, scaleYChange = 1.0f, angleChange = 0.0f;

		// Min and max values need to be set when item is dragged or scaled
		if ((flags & FLAG_FORCEXY) != 0 || this.isDragable() || (flags & FLAG_FORCESCALE) != 0 || this.isScalable()) {
			this.minX = newMinX;
			this.minY = newMinY;
			this.maxX = newMaxX;
			this.maxY = newMaxY;
		}

		if ((flags & FLAG_FORCEXY) != 0 || this.isDragable()) {

			this.centerX = centerX;
			this.centerY = centerY;
		}

		if ((flags & FLAG_FORCESCALE) != 0 || this.isScalable()) {
			scaleXChange = scaleX / this.scaleX;
			scaleYChange = scaleY / this.scaleY;
			this.setScale(scaleX, scaleY);
		}

		if ((flags & FLAG_FORCEROTATE) != 0 || this.isRotateable()) {
			angleChange = angle - this.angle;
			// this.angle = angle;
			this.setAngle(angle);
		}

		if (isDraggedOrPinched && this.hasSuperDrawable()) {
			PointF relativePosition = getRelativePositionToSuperobject();
			// we do not want to use setRelativePosition, because this will
			// overwrite the angle
			// this.setRelativePosition(relativePosition.x, relativePosition.y);
			this.setRelativePosition(relativePosition);
		}

		// Iterate through the subobjects and change their position
		for (MultiTouchDrawable subobject : subDrawables) {
			PointF absolutePosition = this.getAbsolutePositionOfSubobject(subobject);
			subobject.setPos(absolutePosition.x, absolutePosition.y, subobject.scaleX * scaleXChange, subobject.scaleY * scaleYChange,
					subobject.angle + angleChange, FLAG_FORCEXY, false);
		}

		return true;
	}

	protected void recalculateSubDrawables() {

	}

	protected void onRelativePositionUpdate() {

	}

	protected PointF getAbsolutePositionOfSubobject(MultiTouchDrawable subobject) {

		float xBeforeRotate = this.minX + subobject.getRelativeX() * scaleX;
		float yBeforeRotate = this.minY + subobject.getRelativeY() * scaleY;

		float radius = (float) Math.sqrt(Math.pow(Math.abs(centerX - xBeforeRotate), 2) + Math.pow(Math.abs(centerY - yBeforeRotate), 2));

		float angleBeforeRotate = (float) Math.atan2(yBeforeRotate - centerY, xBeforeRotate - centerX);

		float newAngle = angle + angleBeforeRotate;

		float newY = (float) (centerY + radius * Math.sin(newAngle));
		float newX = (float) (centerX + radius * Math.cos(newAngle));

		// Move the drawable according to it's pivot point if one is set
		if (subobject.isCustomPivotUsed()) {
			if (subobject.angle == 0.0f) {
				newX -= subobject.getPivotXRelativeToCenter() * subobject.scaleX;
				newY -= subobject.getPivotYRelativeToCenter() * subobject.scaleY;
			} else {
				PointF pivotPosition = subobject.getPivotPointPositionConsideringScalingAndAngle();

				newX -= pivotPosition.x;
				newY -= pivotPosition.y;
			}
		}

		return new PointF(newX, newY);
	}

	protected PointF getRelativePositionToSuperobject() {

		float x = centerX;
		float y = centerY;

		if (this.isCustomPivotUsed()) {
			if (this.angle == 0.0f) {
				x += this.getPivotXRelativeToCenter() * this.scaleX;
				y += this.getPivotYRelativeToCenter() * this.scaleY;
			} else {
				PointF pivotPosition = this.getPivotPointPositionConsideringScalingAndAngle();

				x += pivotPosition.x;
				y += pivotPosition.y;
			}
		}

		float superAngle = superDrawable.angle;
		float angleToCenter = (float) Math.atan2(y - superDrawable.centerY, x - superDrawable.centerX);

		float angle = superAngle - angleToCenter;

		float radius = (float) Math.sqrt(Math.pow(Math.abs(x - superDrawable.centerX), 2) + Math.pow(Math.abs(y - superDrawable.centerY), 2))
				/ superDrawable.scaleX;

		float newX = (float) (radius * Math.cos(angle) + superDrawable.getWidth() / 2);
		float newY = (float) (radius * Math.sin(angle * -1) + superDrawable.getHeight() / 2);

		return new PointF(newX, newY);
	}

	public PointF getPivotPointPositionConsideringScalingAndAngle() {
		float absolutePivotX = this.getPivotXRelativeToCenter() * this.scaleX;
		float absolutePivotY = this.getPivotYRelativeToCenter() * this.scaleY;

		float pivotRadius = (float) Math.sqrt(Math.pow(absolutePivotX, 2) + Math.pow(absolutePivotY, 2));

		float pivotAngleAfterRotation = this.angle + (float) Math.atan2(absolutePivotY, absolutePivotX);

		float x = (float) (pivotRadius * Math.cos(pivotAngleAfterRotation));
		float y = (float) (pivotRadius * Math.sin(pivotAngleAfterRotation));

		return new PointF(x, y);
	}

	public void recalculatePositions() {
		this.setPos(centerX, centerY, scaleX, scaleY, angle, false);
	}

	/** Return whether or not the given screen coords are inside this image */
	public boolean containsPoint(float scrnX, float scrnY) {
		// If this is a subdrawable, then don't let the controller think this is
		// the item to be dragged. Otherwise non-draggable subdrawables will not
		// allow to drag the superdrawable when the user's finger is exactly on
		// them. FIXME: Is this the right place to do this?
		// if (this.hasSuperDrawable())
		// return false;
		// else
		// FIXME: need to correctly account for image rotation
		boolean inside = (scrnX >= minX && scrnX <= maxX && scrnY >= minY && scrnY <= maxY);

		if (inside)
			return true;

		Iterator<MultiTouchDrawable> it = this.subDrawables.iterator();
		while (it.hasNext()) {
			MultiTouchDrawable sub = it.next();
			if (sub.containsPoint(scrnX, scrnY)) {
				return true;
			}
		}

		return false;
	}

	public MultiTouchDrawable getDraggableObjectAtPoint(PointInfo pt) {
		float x = pt.getX(), y = pt.getY();
		int n = subDrawables.size();
		for (int i = n - 1; i >= 0; i--) {
			MultiTouchDrawable im = subDrawables.get(i);

			if (im.isDragable() && im.containsPoint(x, y)) {
				return im.getDraggableObjectAtPoint(pt);
			}
		}

		if (this.containsPoint(pt.getX(), pt.getY())) {
			return this;
		}

		return null;
	}

	public void draw(Canvas canvas) {

		drawFromDrawable(canvas);
		
		this.drawSubdrawables(canvas);
	}

	protected void drawFromDrawable(Canvas canvas) {
		// Logger.d("Drawing " + this.toString());
		canvas.save();

		// hmm, why did we calculate dx and dy, this should be the same as
		// centerX and centerY?

		// float dx = (maxX + minX) / 2;
		// float dy = (maxY + minY) / 2;
		Drawable d = this.getDrawable();
		if (d != null) {
			d.setBounds((int) minX, (int) minY, (int) maxX, (int) maxY);
			// canvas.translate(dx, dy);
			canvas.translate(centerX, centerY);
			canvas.rotate((float) Math.toDegrees(angle));
			canvas.translate(-centerX, -centerY);
			d.draw(canvas);

		}
		canvas.restore();
	}

	public void drawSubdrawables(Canvas canvas) {
		// Logger.d(subDrawables.toString());
		for (int i = 0; i < subDrawables.size(); i++) {
			subDrawables.get(i).draw(canvas);
		}
	}

	/**
	 * @return
	 * @uml.property  name="centerX"
	 */
	public float getCenterX() {
		return centerX;
	}

	/**
	 * @return
	 * @uml.property  name="centerY"
	 */
	public float getCenterY() {
		return centerY;
	}

	/**
	 * @return
	 * @uml.property  name="scaleX"
	 */
	public float getScaleX() {
		return scaleX;
	}

	/**
	 * @return
	 * @uml.property  name="scaleY"
	 */
	public float getScaleY() {
		return scaleY;
	}

	/**
	 * @return
	 * @uml.property  name="angle"
	 */
	public float getAngle() {
		return angle;
	}

	// FIXME: these need to be updated for rotation
	/**
	 * @return
	 * @uml.property  name="minX"
	 */
	public float getMinX() {
		return minX;
	}

	/**
	 * @return
	 * @uml.property  name="maxX"
	 */
	public float getMaxX() {
		return maxX;
	}

	/**
	 * @return
	 * @uml.property  name="minY"
	 */
	public float getMinY() {
		return minY;
	}

	/**
	 * @return
	 * @uml.property  name="maxY"
	 */
	public float getMaxY() {
		return maxY;
	}

	/**
	 * @param centerX  the centerX to set
	 * @uml.property  name="centerX"
	 */
	public void setCenterX(float centerX) {
		this.centerX = centerX;
	}

	/**
	 * @param centerY  the centerY to set
	 * @uml.property  name="centerY"
	 */
	public void setCenterY(float centerY) {
		this.centerY = centerY;
	}

	/**
	 * @param scaleX  the scaleX to set
	 * @uml.property  name="scaleX"
	 */
	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	/**
	 * @param scaleY  the scaleY to set
	 * @uml.property  name="scaleY"
	 */
	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getId() + " " + this.getWidth() + "x" + this.getHeight() + "px (" + centerX + "[" + minX + "-" + maxX + "]," + centerY + "["
				+ minY + "-" + maxY + "]) scale (" + scaleX + "," + scaleY + ") angle " + angle * 180.0f / Math.PI
				+ (superDrawable != null ? " super: " + superDrawable.id + " rel: (" + relX + "," + relY + ")" : "");
	}

	public void addSubDrawable(MultiTouchDrawable subObject) {
		subDrawables.add(subObject);
		subObject.refresher = this.refresher;
		this.setPos(centerX, centerY, scaleX, scaleY, angle, false);
	}

	public void removeSubDrawable(MultiTouchDrawable subObject) {
		subDrawables.remove(subObject);
	}

	public void snapPositionToGrid() {
		float unfittingX = this.relX % gridSpacingX;
		float unfittingY = this.relY % gridSpacingY;

		float newRelX = this.relX;
		float newRelY = this.relY;

		if (unfittingX >= (gridSpacingX / 2.0f))
			newRelX += gridSpacingX - unfittingX;
		else
			newRelX -= unfittingX;

		if (unfittingY >= (gridSpacingY / 2.0f))
			newRelY += gridSpacingY - unfittingY;
		else
			newRelY -= unfittingY;

		this.setRelativePosition(newRelX, newRelY);
	}

	/**
	 * @return  the subDrawables
	 * @uml.property  name="subDrawables"
	 */
	public ArrayList<MultiTouchDrawable> getSubDrawables() {
		return subDrawables;
	}

	/**
	 * tries to bring the current element to the front.
	 * 
	 * @return true, if this element could be brought to front
	 */
	public boolean bringToFront() {
		if (superDrawable != null) {
			superDrawable.bringSubDrawableToFront(this);
			return true;
		} else {
			Logger.d("we can't bring ourselfs to front, because we are not attached to a super drawable");
		}
		return false;
	}

	protected void bringSubDrawableToFront(MultiTouchDrawable drawable) {
		// do we need the clean way?
		// this.removeSubDrawable(drawable);
		// this.addSubDrawable(drawable);

		// or is the dirty one sufficient?
		subDrawables.remove(drawable);
		subDrawables.add(drawable);
	}

	public void deleteDrawable() {
		if (this.superDrawable == null) {
			Logger.d("don't know how to delete myself, if I have not super Drawable");
		} else {
			superDrawable.subDrawables.remove(this);

		}
		onDelete();
	}

	/**
	 * this method is called, if this method is deleted
	 */
	public void onDelete() {

	}

	/**
	 * hide all open popups
	 */
	public void hidePopups() {
		if (this instanceof Popup) {
			((Popup) this).setActive(false);
		}

		for (MultiTouchDrawable d : subDrawables) {
			d.hidePopups();
		}
	}

	/**
	 * Sets the grid spacing. If, for example, grid spacing is 10, then 10 pixels of the map are one meter in reality.
	 * 
	 * @param gridSpacingX
	 *            The grid spacing in <b>x</b> direction
	 * @param gridSpacingY
	 *            The grid spacing in <b>y</b> direction
	 */
	public static void setGridSpacing(float gridSpacingX, float gridSpacingY) {
		MultiTouchDrawable.gridSpacingX = gridSpacingX;
		MultiTouchDrawable.gridSpacingY = gridSpacingY;
	}

	/**
	 * Returns the grid spacing in x direction. If, for example, grid spacing is 10, then 10 pixels of the map are one meter in reality.
	 * @return  The grid spacing in <b>x</b> direction
	 * @uml.property  name="gridSpacingX"
	 */
	public static float getGridSpacingX() {
		return gridSpacingX;
	}

	/**
	 * Returns the grid spacing in y direction. If, for example, grid spacing is 10, then 10 pixels of the map are one meter in reality.
	 * @return  The grid spacing in <b>y</b> direction
	 * @uml.property  name="gridSpacingY"
	 */
	public static float getGridSpacingY() {
		return gridSpacingY;
	}

	public void setAngleChangeCallback(AngleChangeCallback callback) {
		this.angleChangeCallback = callback;
	}
	
}
