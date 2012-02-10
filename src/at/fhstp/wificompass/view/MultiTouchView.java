/**
 * PhotoSorterView.java
 * 
 * (c) Luke Hutchison (luke.hutch@mit.edu)
 * 
 * TODO: Add OpenGL acceleration.
 * 
 * Released under the Apache License v2.
 */
package at.fhstp.wificompass.view;

import java.util.ArrayList;

import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import at.fhstp.wificompass.Logger;

public class MultiTouchView extends View implements MultiTouchObjectCanvas<MultiTouchViewObject> {
	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;

	private int mUIMode = UI_MODE_ROTATE;
	
	private ArrayList<MultiTouchViewObject> drawables = new ArrayList<MultiTouchViewObject>();

	// --

	private MultiTouchController<MultiTouchViewObject> multiTouchController = new MultiTouchController<MultiTouchViewObject>(this);

	// --

	private PointInfo currTouchPoint = new PointInfo();

	private boolean mShowDebugInfo = true;


	// --

	private Paint mLinePaintTouchPointCircle = new Paint();
	
	public boolean rearangable=true;

	// ---------------------------------------------------------------------------------------------------

	public MultiTouchView(Context context) {
		super(context);
		init();

	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MultiTouchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public MultiTouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	protected void init() {

		Logger.d("initializing MultiTouchView");
		 
		mLinePaintTouchPointCircle.setColor(Color.YELLOW);
		mLinePaintTouchPointCircle.setStrokeWidth(5);
		mLinePaintTouchPointCircle.setStyle(Style.STROKE);
		mLinePaintTouchPointCircle.setAntiAlias(true);
	}

	// ---------------------------------------------------------------------------------------------------
	/** Called by activity's onResume() method to load the images */
	public void loadImages(Context context) {
//		Resources res = context.getResources();
//		int n = drawables.size();
//		for (int i = 0; i < n; i++)
//			drawables.get(i).load(res);
	}

	/** Called by activity's onPause() method to free memory used for loading the images */
	public void unloadImages() {
		int n = drawables.size();
		for (int i = 0; i < n; i++)
			drawables.get(i).unload();
	}

	// ---------------------------------------------------------------------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int n = drawables.size();
		Logger.d("drawing "+n+" drawables");
		
		for (int i = 0; i < n; i++)
			drawables.get(i).draw(canvas);
		if (mShowDebugInfo)
			drawMultitouchDebugMarks(canvas);
	}

	// ---------------------------------------------------------------------------------------------------

	public void trackballClicked() {
		mUIMode = (mUIMode==UI_MODE_ROTATE?UI_MODE_ANISOTROPIC_SCALE:UI_MODE_ROTATE);
		invalidate();
	}

	private void drawMultitouchDebugMarks(Canvas canvas) {
		if (currTouchPoint.isDown()) {
			float[] xs = currTouchPoint.getXs();
			float[] ys = currTouchPoint.getYs();
			float[] pressures = currTouchPoint.getPressures();
			int numPoints = Math.min(currTouchPoint.getNumTouchPoints(), 2);
			for (int i = 0; i < numPoints; i++)
				canvas.drawCircle(xs[i], ys[i], 50 + pressures[i] * 80, mLinePaintTouchPointCircle);
			if (numPoints == 2)
				canvas.drawLine(xs[0], ys[0], xs[1], ys[1], mLinePaintTouchPointCircle);
		}
	}

	// ---------------------------------------------------------------------------------------------------

	/** Pass touch events to the MT controller */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return multiTouchController.onTouchEvent(event);
	}

	/** Get the image that is under the single-touch point, or return null (canceling the drag op) if none */
	public MultiTouchViewObject getDraggableObjectAtPoint(PointInfo pt) {
		float x = pt.getX(), y = pt.getY();
		int n = drawables.size();
		for (int i = n - 1; i >= 0; i--) {
			MultiTouchViewObject im = drawables.get(i);
			if (im.containsPoint(x, y)){
				if(!im.onTouch(pt)){
					return im;
				}else {
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * Select an object for dragging. Called whenever an object is found to be under the point (non-null is returned by getDraggableObjectAtPoint())
	 * and a drag operation is starting. Called with null when drag op ends.
	 */
	public void selectObject(MultiTouchViewObject drawable, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		if (drawable != null) {
			
			if(rearangable){
			// Move image to the top of the stack when selected
			drawables.remove(drawable);
			drawables.add(drawable);
			}
		} else {
			// Called with drawable == null when drag stops.
		}
		invalidate();
	}

	/** Get the current position and scale of the selected image. Called whenever a drag starts or is reset. */
	public void getPositionAndScale(MultiTouchViewObject drawable, PositionAndScale objPosAndScaleOut) {
		// FIXME affine-izem (and fix the fact that the anisotropic_scale part requires averaging the two scale factors)
		objPosAndScaleOut.set(drawable.getCenterX(), drawable.getCenterY(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
				(drawable.getScaleX() + drawable.getScaleY()) / 2, (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, drawable.getScaleX(), drawable.getScaleY(),
				(mUIMode & UI_MODE_ROTATE) != 0, drawable.getAngle());
	}

	/** Set the position and scale of the dragged/stretched image. */
	public boolean setPositionAndScale(MultiTouchViewObject drawable, PositionAndScale newImgPosAndScale, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		boolean ok = drawable.setPos(newImgPosAndScale);
		if (ok)
			invalidate();
		return ok;
	}
	


	public void resetAllXY(){
		for(int i=0;i<drawables.size();i++){
			drawables.get(i).resetXY();
		}
		invalidate();
	}
	
	public void resetAllAngle(){
		for(int i=0;i<drawables.size();i++){
			drawables.get(i).resetAngle();
		}
		invalidate();
	}
	
	public void resetAllScale(){
		for(int i=0;i<drawables.size();i++){
			drawables.get(i).resetScale();
		}
		invalidate();
	}
	
	public void addDrawable(MultiTouchDrawable drawable){
		Logger.d("added new drawable: "+drawable.getId());
		MultiTouchViewObject mtvo=new MultiTouchViewObject(drawable,this.getResources());
		drawables.add(mtvo);
	}
	
	public void addDrawable(MultiTouchDrawable drawable,MultiTouchDrawable superDrawable){
		Logger.d("added new drawable: "+drawable.getId());
		MultiTouchViewObject mtvo=new MultiTouchViewObject(drawable,this.getResources());
		drawables.add(mtvo);
		for(int i=0;i<drawables.size();i++){
			if(drawables.get(i).getDrawableId()==superDrawable.getId()){
				// this is the super drawable
				drawables.get(i).addSubViewObject(mtvo);
			}
		}
	}
	
	
	public void removeDrawable(MultiTouchDrawable drawable){
		for(int i=0;i<drawables.size();i++){
			if(drawables.get(i).getDrawableId()==drawable.getId()){
				drawables.remove(i);
			}
		}
	}

	/**
	 * @return the rearangable
	 */
	public boolean isRearangable() {
		return rearangable;
	}

	/**
	 * @param rearangable the rearangable to set
	 */
	public void setRearangable(boolean rearangable) {
		this.rearangable = rearangable;
	}
	
	
}