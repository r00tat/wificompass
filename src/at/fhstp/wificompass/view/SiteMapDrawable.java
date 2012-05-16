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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.ToolBox;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public class SiteMapDrawable extends MultiTouchDrawable implements SensorEventListener {

	/**
	 * @uml.property  name="backgroundImage"
	 */
	protected Bitmap backgroundImage;

	protected SensorManager sensorManager;
	protected Sensor compass;
	protected Sensor accelerometer;
	float[] geomag = new float[3];
	float[] gravity = new float[3];
	float angleAdjustment = 0.0f;
	//int angleUpdateTreshold = 100;
	//long lastAngleUpdate = 0;
	
	float oldSensorAngle = 0.0f; 
	
	public SiteMapDrawable(Context ctx,RefreshableView refresher) {
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
		
		sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
		compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
	}

	public void startAutoRotate() {
		try {
			sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_UI);
			sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		} catch (Exception e) {
			Logger.w("could not register listener", e);
		}
	}

	public void stopAutoRotate() {
		try {
			sensorManager.unregisterListener(this);
		} catch (Exception e) {
			Logger.w("could not unregister listener", e);
		}
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

	@Override
	public void onSensorChanged(SensorEvent event) {
		// we use TYPE_MAGNETIC_FIELD to get changes in the direction, but use SensorManager to get directions
		if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
			return;
		
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			gravity = event.values.clone();
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			geomag = event.values.clone();
			break;
		}
		
		//long now = new Date().getTime();
		
		//Logger.d("Angle update time. Now: " + now + ", last: " + lastAngleUpdate + ", difference: " + (now - lastAngleUpdate));
		
		//if ((now - lastAngleUpdate) > angleUpdateTreshold) {
		float angle = ToolBox.getSmoothAngleFromSensorData(oldSensorAngle, gravity, geomag);
		
		Logger.d("New angle: " + angle);
		
		oldSensorAngle = angle;
				
		float adjusted = ToolBox.normalizeAngle((angle - angleAdjustment) * -1.0f);
				
		//Logger.d("Angle goodness: angle " + Math.toDegrees(angle) + ", adjustment " + Math.toDegrees(angleAdjustment) + " adjusted: " + Math.toDegrees(adjusted));
				
		this.setAngle(adjusted);
		this.recalculatePositions();
			
			//lastAngleUpdate = new Date().getTime();
		//}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void setAngleAdjustment(float adjustment) {
		this.angleAdjustment = adjustment;
		this.angleChangeCallback = null;
	}
	
}
