package at.fhstp.wificompass.view;

import java.sql.SQLException;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.BssidResult;
import at.fhstp.wificompass.model.Location;
import at.fhstp.wificompass.model.WifiScanResult;
import at.fhstp.wificompass.model.helper.DatabaseHelper;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * @author Paul Woelfel (paul@woelfel.at)
 */
public class MeasuringPointDrawable extends MultiTouchDrawable {

	protected static BitmapDrawable icon;

	/**
	 * @uml.property name="scanResult"
	 * @uml.associationEnd
	 */
	protected WifiScanResult scanResult;

	/**
	 * @uml.property name="deletePopup"
	 * @uml.associationEnd
	 */
	protected DeleteDrawable deletePopup;

	protected static final int padding = 5;

	/**
	 * @uml.property name="popup"
	 * @uml.associationEnd
	 */
	protected TextPopupDrawable popup;

	protected boolean isPopupActive = false;

	public MeasuringPointDrawable(Context ctx, MultiTouchDrawable superDrawable, WifiScanResult scanResult) {
		super(ctx, superDrawable);
		this.scanResult = scanResult;
		init();
	}

	protected void init() {
		icon = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.footprint_6);
		this.setPivot(0.5f, 0.94f);

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

		if (scanResult.getBssids() != null) {
			for (BssidResult bssid : scanResult.getBssids()) {
				sb.append(bssid.toString());
				sb.append("\n");
			}
		} else {
			sb.append(ctx.getString(R.string.measuringpoint_no_bssids));
		}

		popup = new TextPopupDrawable(ctx, this, sb.toString());
		popup.setWidth(400);

		popup.setActive(false);

		deletePopup = new DeleteDrawable(ctx, this, "Scan Result " + scanResult.getId()
				+ (scanResult.getLocation() != null ? " (" + scanResult.getLocation().getX() + "," + scanResult.getLocation().getY() + ")" : ""));
		deletePopup.setActive(false);

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
	public boolean isRotatable() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#onSingleTouch(org.metalev.multitouch.controller.MultiTouchController.PointInfo)
	 */
	@Override
	public boolean onSingleTouch(PointInfo pointinfo) {
		popup.setActive(!popup.isActive());
		deletePopup.setActive(popup.isActive());
		bringToFront();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhstp.wificompass.view.MultiTouchDrawable#onDelete()
	 */
	@Override
	public void onDelete() {
		try {
			// try to delete myself from the database
			DatabaseHelper databaseHelper = OpenHelperManager.getHelper(this.ctx, DatabaseHelper.class);

			if (scanResult.getLocation() != null)
				databaseHelper.getDao(Location.class).delete(scanResult.getLocation());
			databaseHelper.getDao(WifiScanResult.class).delete(scanResult);

			OpenHelperManager.releaseHelper();

		} catch (SQLException e) {
			Logger.w("could not delete myself from the database");
		}
	}

}
