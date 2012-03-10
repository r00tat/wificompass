package at.fhstp.wificompass.view;

import java.util.Iterator;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.BssidResult;
import at.fhstp.wificompass.model.WifiScanResult;

public class MeasuringPointDrawable extends MultiTouchDrawable {

	protected static BitmapDrawable icon;

	protected WifiScanResult scanResult;

	

	protected static final int padding = 5;

	protected PopupDrawable popup;
	protected boolean isPopupActive = false;

	public MeasuringPointDrawable(Context ctx, WifiScanResult scanResult) {
		super(ctx);
		this.scanResult = scanResult;
		init();
	}

	public MeasuringPointDrawable(Context ctx, MultiTouchDrawable superDrawable, WifiScanResult scanResult) {
		super(ctx, superDrawable);
		this.scanResult = scanResult;
		init();
	}

	protected void init() {
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.red_dot);
		this.setPivot(0.5f, 0.5f);

		this.width = icon.getBitmap().getWidth();
		this.height = icon.getBitmap().getHeight();

		if (scanResult.getLocation() != null) {

			this.setRelativePosition(scanResult.getLocation().getX(), scanResult.getLocation().getY());
		} else {
			Logger.w("scanResult location is null!");
		}

		if (this.hasSuperDrawable()) {
			this.getSuperDrawable().recalculatePositions();
		}

		StringBuffer sb = new StringBuffer();

		for (Iterator<BssidResult> it = scanResult.getBssids().iterator(); it.hasNext();) {
			sb.append(it.next().toString());
			sb.append("\n");
		}

		popup = new PopupDrawable(ctx,sb.toString());
		popup.width=300;
		popup.setRelativePosition(this.width / 2 - popup.width / 2, popup.height * -0.5f);
		

	}

	@Override
	public Drawable getDrawable() {
		return icon;
	}

	@Override
	public boolean isScalable() {
		return false;
	}

	@Override
	public boolean isRotateable() {
		return false;
	}

	@Override
	public boolean isDragable() {
		return false;
	}

	@Override
	public boolean isOnlyInSuper() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		

	}

	/* (non-Javadoc)
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#onTouch(org.metalev.multitouch.controller.MultiTouchController.PointInfo)
	 */
	@Override
	public boolean onTouch(PointInfo pointinfo) {
		if (pointinfo.isMultiTouch() == false &&
				pointinfo.getNumTouchPoints() == 1 &&
				pointinfo.getAction() == 0
					) {
				this.setPopupActive(!isPopupActive);
				return true;
			}
		
		return super.onTouch(pointinfo);
	}

	
	public void setPopupActive(boolean isPopupActive) {
		this.isPopupActive = isPopupActive;
		
		if (this.isPopupActive)
			this.addSubDrawable(popup);
		else
			this.removeSubDrawable(popup);
	}
	
}
