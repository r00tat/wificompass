/*
 * Created on Feb 10, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.view;

import android.graphics.Bitmap;

public interface MultiTouchDrawable {
	public Bitmap getDrawableBitmap();
	public int getWidth();
	public int getHeight();
	public String getId();
}
