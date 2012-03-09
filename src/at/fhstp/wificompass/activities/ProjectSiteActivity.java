/*
 * Created on Dec 23, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.activities;

import java.sql.SQLException;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import at.fhstp.wificompass.ApplicationContext;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.exceptions.SiteNotFoundException;
import at.fhstp.wificompass.exceptions.WifiException;
import at.fhstp.wificompass.model.BssidResult;
import at.fhstp.wificompass.model.ProjectSite;
import at.fhstp.wificompass.model.WifiScanResult;
import at.fhstp.wificompass.model.helper.DatabaseHelper;
import at.fhstp.wificompass.view.AccessPointDrawable;
import at.fhstp.wificompass.view.MeasuringPointDrawable;
import at.fhstp.wificompass.view.MultiTouchView;
import at.fhstp.wificompass.view.SiteMap;
import at.fhstp.wificompass.view.UserDrawable;
import at.fhstp.wificompass.wifi.WifiResultCallback;
import at.fhstp.wificompass.wifi.WifiScanner;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class ProjectSiteActivity extends Activity implements OnClickListener, WifiResultCallback {

	protected Logger log = new Logger(ProjectSiteActivity.class);

	public static final String SITE_KEY = "SITE", PROJECT_KEY = "PROJECT";

	// public static final int START_NEW = 1, START_LOAD = 2;

	protected static final int DIALOG_TITLE = 1, DIALOG_SCANNING = 2;

	protected MultiTouchView multiTouchView;

	protected SiteMap map;

	protected ProjectSite site;

	protected DatabaseHelper databaseHelper = null;

	protected Dao<ProjectSite, Integer> projectSiteDao = null;

	protected AlertDialog scanAlertDialog;

	protected ImageView scanningImageView;

	protected boolean ignoreWifiResults = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.project_site);
			Intent intent = this.getIntent();

			int siteId = intent.getExtras().getInt(SITE_KEY, -1);
			if (siteId == -1) {
				throw new SiteNotFoundException("ProjectSiteActivity called without a correct site ID!");
			}

			databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
			projectSiteDao = databaseHelper.getDao(ProjectSite.class);
			site = projectSiteDao.queryForId(siteId);

			if (site == null) {
				throw new SiteNotFoundException("The ProjectSite Id could not be found in the database!");
			}

			Button resetZoom = ((Button) findViewById(R.id.project_site_reset_zoom_button));
			resetZoom.setOnClickListener(this);

			Button resetXY = ((Button) findViewById(R.id.project_site_reset_pos_button));
			resetXY.setOnClickListener(this);
			
			Button startWifiScanButton = ((Button) findViewById(R.id.project_site_wifiscan_button));
			startWifiScanButton.setOnClickListener(this);

			multiTouchView = ((MultiTouchView) findViewById(R.id.project_site_resultview));
			multiTouchView.setRearrangable(false);
			map = new SiteMap(this);

			AccessPointDrawable icon1 = new AccessPointDrawable(this, map);
			icon1.setRelativePosition(134, 57);
			AccessPointDrawable icon2 = new AccessPointDrawable(this, map);
			icon2.setRelativePosition(199, 301);
			AccessPointDrawable icon3 = new AccessPointDrawable(this, map);
			icon3.setRelativePosition(541, 332);
			AccessPointDrawable icon4 = new AccessPointDrawable(this, map);
			icon4.setRelativePosition(52, 81);
			AccessPointDrawable icon5 = new AccessPointDrawable(this, map);
			icon5.setRelativePosition(423, 214);

			UserDrawable user = new UserDrawable(this, map);
			user.setRelativePosition(320, 240);

			MeasuringPointDrawable point = new MeasuringPointDrawable(this, map);
			point.setRelativePosition(423, 293);

			multiTouchView.addDrawable(map);
			// multiTouchView.addDrawable(icon1);
			// multiTouchView.addDrawable(icon2);
			// multiTouchView.addDrawable(icon3);
			// multiTouchView.addDrawable(icon4);
			// multiTouchView.addDrawable(icon5);

			if (site.getTitle().equals(ProjectSite.UNTITLED)) {
				showDialog(DIALOG_TITLE);
			}

		} catch (Exception ex) {
			log.error("Failed to create ProjectSiteActivity: " + ex.getMessage(), ex);
			Toast.makeText(this, R.string.project_site_load_failed, Toast.LENGTH_LONG).show();
			this.finish();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		log.debug("setting context");
		ApplicationContext.setContext(this);
		multiTouchView.loadImages(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.project_site_wifiscan_button:
			Logger.d("start a wifiscan");
			try {
				WifiScanner.startScan(this, this);
				ignoreWifiResults = false;
				showDialog(DIALOG_SCANNING);

			} catch (WifiException e) {
				Logger.e("could not start wifi scan!", e);
				Toast.makeText(this, R.string.project_site_wifiscan_start_failed, Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.project_site_reset_zoom_button:
			Logger.d("resetting Zoom");
			multiTouchView.resetAllScale();
			break;

		case R.id.project_site_reset_pos_button:
			Logger.d("resetting position");
			multiTouchView.resetAllXY();
			break;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_TITLE:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle(R.string.project_site_dialog_title_title);
			alert.setMessage(R.string.project_site_dialog_title_message);

			// Set an EditText view to get user input
			final EditText input = new EditText(this);
			input.setSingleLine(true);
			alert.setView(input);

			alert.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					setSiteTitle(input.getText().toString());

				}
			});

			alert.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			return alert.create();

		case DIALOG_SCANNING:

			AlertDialog.Builder scanAlert = new AlertDialog.Builder(this);

			scanAlert.setTitle(R.string.project_site_dialog_scanning_title);
			scanAlert.setMessage(R.string.project_site_dialog_scanning_message);

			scanningImageView = new ImageView(this);
			scanningImageView.setImageResource(R.drawable.loading);
			

			scanAlert.setView(scanningImageView);

			scanAlert.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
					stopWifiScan();
				}
			});

			scanAlertDialog = scanAlert.create();
			
			scanAlertDialog.setOnShowListener(new OnShowListener() {
				
				@Override
				public void onShow(DialogInterface paramDialogInterface) {
					if(scanningImageView!=null){
					((AnimationDrawable) scanningImageView.getDrawable()).start();
					}else {
						Logger.w("why is scanningImageView null????");
					}
				}
			});
			

			return scanAlertDialog;

		default:
			return super.onCreateDialog(id);
		}
	}
	
	

	protected void setSiteTitle(String title) {
		site.setTitle(title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.project_site, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.project_site_menu_change_name:
			showDialog(DIALOG_TITLE);

			return false;

		case R.id.project_site_menu_save:
			saveProjectSite();
			return false;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		multiTouchView.unloadImages();
		saveProjectSite();
	}

	protected void saveProjectSite() {
		log.debug("saveing project site");

		try {
			int changed = projectSiteDao.update(site);

			if (changed > 0) {
				Toast.makeText(this, R.string.project_site_saved, Toast.LENGTH_SHORT).show();
			}

			projectSiteDao.refresh(site);
		} catch (SQLException e) {
			log.error("could not save or refresh project site", e);
			Toast.makeText(this, R.string.project_site_save_failed, Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void scanFinished(WifiScanResult wr) {
		hideWifiScanDialog();
		if (!ignoreWifiResults) {
			try {
				
				Logger.d("received a wifi scan result!");

				wr.setProjectLocation(site);

				Dao<WifiScanResult, Integer> scanResultDao = databaseHelper.getDao(WifiScanResult.class);
				scanResultDao.update(wr);
				
				
				StringBuffer sb=new StringBuffer();
				for(Iterator<BssidResult> it=wr.getBssids().iterator();it.hasNext();){
					BssidResult result=it.next();
					Logger.d("ScanResult: "+result.toString());
					sb.append(result.toString());
					sb.append("\n");
				}
				
				Toast.makeText(this, this.getString(R.string.project_site_wifiscan_finished,sb.toString()), Toast.LENGTH_SHORT).show();
				
				
				
			} catch (SQLException e) {
				Logger.e("could not update wifiscanresult!", e);
				Toast.makeText(this, this.getString(R.string.project_site_wifiscan_failed, e.getMessage()), Toast.LENGTH_LONG).show();
			}

		}
	}

	@Override
	public void scanFailed(Exception ex) {
		hideWifiScanDialog();
		if (!ignoreWifiResults) {

			Logger.e("Wifi scan failed!", ex);
			Toast.makeText(this, this.getString(R.string.project_site_wifiscan_failed, ex.getMessage()), Toast.LENGTH_LONG).show();

		}

	}

	protected void stopWifiScan() {
		hideWifiScanDialog();
		
		
		WifiScanner.stopScanning(this);

		// stop scan
		// oh, wait, we can't stop the scan, it's asynchronous!
		// we just have to ignore the result!
		ignoreWifiResults = true;

	}

	protected void hideWifiScanDialog() {
		if (scanningImageView != null) {
			((AnimationDrawable) scanningImageView.getDrawable()).stop();
//			scanningImageView = null;
		}

		if (scanAlertDialog != null) {
			scanAlertDialog.cancel();
//			scanAlertDialog = null;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onWindowFocusChanged(boolean)
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
//		if(scanAlertDialog!=null && scanningImageView!=null){
//			((AnimationDrawable) scanningImageView.getDrawable()).start();
//		}
	}

}
