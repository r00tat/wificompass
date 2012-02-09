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

import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SiteResultView extends View implements MultiTouchObjectCanvas<View> {

	private MultiTouchController<View> multiTouchController = new MultiTouchController<View>(this);

	private PointInfo currTouchPoints = new PointInfo();

	private Paint mLinePaintTouchPointCircle = new Paint();

	private Bitmap img;

	private float x = 100;

	private float y = 100;;

	private float scale = 1;

	private float angle = 0;

	// ---------------------------------------------------------------------------------------------------

	public SiteResultView(Context context) {
		super(context);
		init();

	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SiteResultView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public SiteResultView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	protected void init() {
		img = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888);
//		Paint paint = new Paint();
//		paint.setColor(Color.WHITE);
		Canvas canvas = new Canvas(img);
		 
		// Fill the entire canvas with a red color.
		canvas.drawColor(Color.WHITE);
		 
		mLinePaintTouchPointCircle.setColor(Color.YELLOW);
		mLinePaintTouchPointCircle.setStrokeWidth(5);
		mLinePaintTouchPointCircle.setStyle(Style.STROKE);
		mLinePaintTouchPointCircle.setAntiAlias(true);
	}

	// ---------------------------------------------------------------------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		canvas.scale(scale, scale, x, y);
		// canvas.rotate(angle * 180.0f / (float) Math.PI, x, y);
		canvas.drawBitmap(img, x, y, null);
		canvas.restore();
		drawMultitouchDebugMarks(canvas);
	}

	// ---------------------------------------------------------------------------------------------------

	private void drawMultitouchDebugMarks(Canvas canvas) {
		if (currTouchPoints.isDown()) {
			float[] xs = currTouchPoints.getXs();
			float[] ys = currTouchPoints.getYs();
			float[] pressures = currTouchPoints.getPressures();
			int numPoints = currTouchPoints.getNumTouchPoints();
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
	public View getDraggableObjectAtPoint(PointInfo pt) {
		return this;
	}

	/**
	 * Select an object for dragging. Called whenever an object is found to be under the point (non-null is returned by getDraggableObjectAtPoint()) and a drag operation is starting. Called with null
	 * when drag op ends.
	 */
	public void selectObject(View obj, PointInfo touchPoint) {
		currTouchPoints.set(touchPoint);
		invalidate();
	}

	/** Get the current position and scale of the selected image. Called whenever a drag starts or is reset. */
	public void getPositionAndScale(View obj, PositionAndScale objPosAndScaleOut) {
		objPosAndScaleOut.set(x, y, true, scale, false, scale, scale, true, angle);
	}

	/** Set the position and scale of the dragged/stretched image. */
	public boolean setPositionAndScale(View obj, PositionAndScale newPosAndScale, PointInfo touchPoint) {
		currTouchPoints.set(touchPoint);
		x = newPosAndScale.getXOff();
		y = newPosAndScale.getYOff();
		scale = newPosAndScale.getScale();
		// angle = newPosAndScale.getAngle();
		invalidate();
		return true;
	}

	/**
	 * @return the img
	 */
	public Bitmap getImg() {
		return img;
	}

	/**
	 * @param img
	 *            the img to set
	 */
	public void setImg(Bitmap img) {
		this.img = img;
	}
}