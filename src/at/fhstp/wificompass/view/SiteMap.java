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

public class SiteMap implements MultiTouchDrawable {
	
	protected static int counter=1;
	
	protected static int id;
	
	protected static Bitmap bmp;
	
	protected Context ctx;
	
	protected int drawed=0;
	
	protected float angle=0,scaleX=1.0f,scaleY=1.0f;
	
	public SiteMap(Context ctx){
		this.ctx=ctx;
		id=counter++;
		bmp=Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas bmpCanvas=new Canvas(bmp);
		bmpCanvas.drawColor(Color.BLUE);
	}

	@Override
	public Drawable getDrawable() {
		Bitmap bmpToDraw=Bitmap.createBitmap(bmp);
		Canvas cnv=new Canvas(bmpToDraw);
		Paint textPaint=new Paint();
		textPaint.setColor(Color.GREEN);
		cnv.save();
		cnv.rotate(angle * -180.0f / (float) Math.PI);
		cnv.drawText("Hallo "+drawed++, 40,40, textPaint);
		cnv.restore();
		return new BitmapDrawable(ctx.getResources(), bmpToDraw);
	}

	@Override
	public int getWidth() {
		return 400;
	}

	@Override
	public int getHeight() {
		return 300;
	}

	@Override
	public String getId() {
		return this.getClass().getCanonicalName()+":"+id;
	}

	@Override
	public boolean onTouch(PointInfo pointinfo) {
		return false;
	}

	@Override
	public void setAngle(float angle) {
		this.angle=angle;
	}

	@Override
	public void setScale(float scaleX, float scaleY) {
		this.scaleX=scaleX;
		this.scaleY=scaleY;
	}

}
