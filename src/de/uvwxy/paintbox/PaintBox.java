package de.uvwxy.paintbox;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import at.fhstp.wificompass.Logger;

/**
 * A class managing the creation of a canvas to draw on. Usage: Create a class which overrides onDraw. Once the surface is created onDraw() is called in an infinite loop from a PaintThread. Destroying the surface stops the background thread calling onDraw().
 * @author  Paul Smith
 */
public abstract class PaintBox extends SurfaceView implements SurfaceHolder.Callback {

	/**
	 * @uml.property  name="pThread"
	 * @uml.associationEnd  
	 */
	PaintThread pThread;
	
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PaintBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Logger.i("Constructing PaintBox");
		getHolder().addCallback(this);
		Logger.i("Constructing finished");
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public PaintBox(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		
	}
	
	public PaintBox(Context context) {
		this(context,null,0);		
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		pThread = new PaintThread(getHolder(), this);
		pThread.setRunning(true);
		pThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean retry = true;
		pThread.setRunning(false);
		while (retry) {
			try {
				pThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	protected abstract void onDraw(Canvas canvas);




}
