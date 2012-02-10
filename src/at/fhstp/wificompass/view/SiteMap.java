/*
 * Created on Feb 10, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

public class SiteMap implements MultiTouchDrawable {
	
	protected static int counter=1;
	
	protected static int id;
	
	protected static Bitmap bmp;
	
	public SiteMap(){
		id=counter++;
		bmp=Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas bmpCanvas=new Canvas(bmp);
		bmpCanvas.drawColor(Color.BLUE);
	}

	@Override
	public Bitmap getDrawableBitmap() {
		
		return bmp;
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

}
