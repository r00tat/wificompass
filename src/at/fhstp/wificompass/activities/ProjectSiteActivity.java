/*
 * Created on Dec 23, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.activities;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import at.fhstp.wificompass.ApplicationContext;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.exceptions.SiteNotFoundException;
import at.fhstp.wificompass.exceptions.WifiException;
import at.fhstp.wificompass.model.BssidResult;
import at.fhstp.wificompass.model.Location;
import at.fhstp.wificompass.model.ProjectSite;
import at.fhstp.wificompass.model.WifiScanResult;
import at.fhstp.wificompass.model.helper.DatabaseHelper;
import at.fhstp.wificompass.triangulation.WeightedCentroidTriangulation;
import at.fhstp.wificompass.userlocation.LocationServiceFactory;
import at.fhstp.wificompass.view.AccessPointDrawable;
import at.fhstp.wificompass.view.MeasuringPointDrawable;
import at.fhstp.wificompass.view.MultiTouchView;
import at.fhstp.wificompass.view.SiteMapDrawable;
import at.fhstp.wificompass.view.UserDrawable;
import at.fhstp.wificompass.wifi.WifiResultCallback;
import at.fhstp.wificompass.wifi.WifiScanner;
import at.woelfel.philip.filebrowser.FileBrowser;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class ProjectSiteActivity extends Activity implements OnClickListener, WifiResultCallback {

	protected Logger log = new Logger(ProjectSiteActivity.class);

	public static final String SITE_KEY = "SITE", PROJECT_KEY = "PROJECT";

	// public static final int START_NEW = 1, START_LOAD = 2;

	protected static final int DIALOG_TITLE = 1, DIALOG_SCANNING = 2, DIALOG_CHANGE_SIZE = 3, DIALOG_SET_BACKGROUND = 4;

	protected static final int FILEBROWSER_REQUEST = 1;

	protected MultiTouchView multiTouchView;

	protected SiteMapDrawable map;

	protected ProjectSite site;

	protected DatabaseHelper databaseHelper = null;

	protected Dao<ProjectSite, Integer> projectSiteDao = null;

	protected AlertDialog scanAlertDialog;

	protected ImageView scanningImageView;

	protected boolean ignoreWifiResults = false;

	protected BroadcastReceiver wifiBroadcastReceiver;

	protected UserDrawable user;

	protected final Context context = this;

	protected TextView backgroundPathTextView;

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

			Button snapUser = ((Button) findViewById(R.id.project_site_snap_user_button));
			snapUser.setOnClickListener(this);

			Button startWifiScanButton = ((Button) findViewById(R.id.project_site_wifiscan_button));
			startWifiScanButton.setOnClickListener(this);
			
			Button calculateApPositions = ((Button) findViewById(R.id.project_site_calculate_ap_positions_button));
			calculateApPositions.setOnClickListener(this);

			multiTouchView = ((MultiTouchView) findViewById(R.id.project_site_resultview));
			multiTouchView.setRearrangable(false);
			map = new SiteMapDrawable(this);

			if (site.getWidth() == 0 || site.getHeight() == 0) {
				site.setSize(map.getWidth(), map.getHeight());
			} else {
				map.setSize(site.getWidth(), site.getHeight());
			}
			if(site.getBackgroundBitmap()!=null){
				map.setBackgroundImage(site.getBackgroundBitmap());
			}

			// do not add the testicons any more
			// AccessPointDrawable icon1 = new AccessPointDrawable(this, map);
			// icon1.setRelativePosition(134, 57);
			// AccessPointDrawable icon2 = new AccessPointDrawable(this, map);
			// icon2.setRelativePosition(199, 301);
			// AccessPointDrawable icon3 = new AccessPointDrawable(this, map);
			// icon3.setRelativePosition(541, 332);
			// AccessPointDrawable icon4 = new AccessPointDrawable(this, map);
			// icon4.setRelativePosition(52, 81);
			// AccessPointDrawable icon5 = new AccessPointDrawable(this, map);
			// icon5.setRelativePosition(423, 214);

			user = new UserDrawable(this, map);

			if (site.getLastLocation() != null) {
				user.setRelativePosition(site.getLastLocation().getX(), site.getLastLocation().getY());
			} else
				user.setRelativePosition(map.getWidth() / 2, map.getHeight() / 2);

			// MeasuringPointDrawable point = new MeasuringPointDrawable(this, map);
			// point.setRelativePosition(423, 293);

			for (Iterator<WifiScanResult> it = site.getScanResults().iterator(); it.hasNext();) {
				WifiScanResult wsr = it.next();
				new MeasuringPointDrawable(this, map, wsr);
			}

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
				wifiBroadcastReceiver = WifiScanner.startScan(this, this);
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
			multiTouchView.resetAllXY();
			multiTouchView.resetAllAngle();
			multiTouchView.recalculateDrawablePositions();
			multiTouchView.invalidate();
			break;

		case R.id.project_site_snap_user_button:
			Logger.d("Snapping user to grid");
			user.snapPositionToGrid();
			multiTouchView.invalidate();
			break;
			
		case R.id.project_site_calculate_ap_positions_button:
			WeightedCentroidTriangulation tri = new WeightedCentroidTriangulation(context, site);
			Vector<AccessPointDrawable> aps = tri.calculateAllAndGetDrawables();
			
			for (Iterator<AccessPointDrawable> it = aps.iterator(); it.hasNext();) {
				AccessPointDrawable ap = it.next();
				map.addSubDrawable(ap);
				map.recalculatePositions();
				multiTouchView.invalidate();
			}
			
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
			AlertDialog.Builder titleAlert = new AlertDialog.Builder(this);

			titleAlert.setTitle(R.string.project_site_dialog_title_title);
			titleAlert.setMessage(R.string.project_site_dialog_title_message);

			// Set an EditText view to get user input
			final EditText input = new EditText(this);
			input.setSingleLine(true);
			titleAlert.setView(input);

			titleAlert.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					setSiteTitle(input.getText().toString());

				}
			});

			titleAlert.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			return titleAlert.create();

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
					if (scanningImageView != null) {
						((AnimationDrawable) scanningImageView.getDrawable()).start();
					} else {
						Logger.w("why is scanningImageView null????");
					}
				}
			});

			return scanAlertDialog;

		case DIALOG_CHANGE_SIZE:
			AlertDialog.Builder sizeAlert = new AlertDialog.Builder(this);

			sizeAlert.setTitle(R.string.project_site_dialog_size_title);
			sizeAlert.setMessage(R.string.project_site_dialog_size_message);

			LinearLayout sizeLayout = new LinearLayout(this);
			sizeLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			sizeLayout.setGravity(Gravity.CENTER);

			// Set an EditText view to get user input
			final EditText widthInput = new EditText(this);
			widthInput.setSingleLine(true);
			widthInput.setText("" + map.getWidth());
			widthInput.setInputType(InputType.TYPE_CLASS_NUMBER);
			sizeLayout.addView(widthInput);

			TextView sizeMiddleText = new TextView(this);
			sizeMiddleText.setText(getString(R.string.project_site_dialog_size_middle_text));
			sizeLayout.addView(sizeMiddleText);

			final EditText heightInput = new EditText(this);
			heightInput.setSingleLine(true);
			heightInput.setText("" + map.getHeight());
			heightInput.setInputType(InputType.TYPE_CLASS_NUMBER);
			sizeLayout.addView(heightInput);

			sizeAlert.setView(sizeLayout);

			sizeAlert.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					try {
						int w = Integer.parseInt(widthInput.getText().toString()), h = Integer.parseInt(heightInput.getText().toString());
						if (w <= 0) {
							throw new NumberFormatException("width has to be larger than 0");
						}
						if (h <= 0) {
							throw new NumberFormatException("height has to be larger than 0");
						}

						map.setSize(w, h);
						site.setSize(w, h);

						multiTouchView.invalidate();
						Toast.makeText(context, context.getString(R.string.project_site_dialog_size_finished, w, h), Toast.LENGTH_SHORT).show();

						saveProjectSite();
					} catch (NumberFormatException e) {
						Logger.w("change size width or height not a number ", e);
						Toast.makeText(context, context.getString(R.string.project_site_dialog_size_nan), Toast.LENGTH_LONG).show();
					}

				}
			});

			sizeAlert.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			return sizeAlert.create();

		case DIALOG_SET_BACKGROUND:

			AlertDialog.Builder bckgAlert = new AlertDialog.Builder(this);
			bckgAlert.setTitle(R.string.project_site_dialog_background_title);
			bckgAlert.setMessage(R.string.project_site_dialog_background_message);

			LinearLayout bckgLayout = new LinearLayout(this);
			bckgLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			bckgLayout.setGravity(Gravity.CENTER);
			bckgLayout.setOrientation(LinearLayout.VERTICAL);
			bckgLayout.setPadding(5, 5, 5, 5);

			final TextView pathTextView = new TextView(this);
			backgroundPathTextView = pathTextView;
			pathTextView.setText(R.string.project_site_dialog_background_default_path);
			pathTextView.setPadding(10, 0, 10, 10);

			bckgLayout.addView(pathTextView);

			Button pathButton = new Button(this);
			pathButton.setText(R.string.project_site_dialog_background_path_button);
			pathButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(context, FileBrowser.class);
					i.putExtra(FileBrowser.EXTRA_MODE, FileBrowser.MODE_LOAD);
					startActivityForResult(i, FILEBROWSER_REQUEST);
				}

			});

			bckgLayout.addView(pathButton);

			bckgAlert.setView(bckgLayout);

			bckgAlert.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					setBackgroundImage(pathTextView.getText().toString());
				}
			});

			bckgAlert.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			Dialog bckgDialog = bckgAlert.create();
			bckgDialog.setCanceledOnTouchOutside(true);

			return bckgDialog;

		default:
			return super.onCreateDialog(id);
		}
	}

	protected void setSiteTitle(String title) {
		site.setTitle(title);
		saveProjectSite();
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

		case R.id.project_site_menu_change_size:
			showDialog(DIALOG_CHANGE_SIZE);
			return false;

		case R.id.project_site_menu_set_background:
			showDialog(DIALOG_SET_BACKGROUND);
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

	/**
	 * save current project site
	 */
	protected void saveProjectSite() {
		log.debug("saveing project site");

		try {

			Location curLocation = LocationServiceFactory.getLocationService().getLocation(), lastLocation = site.getLastLocation();

			if (lastLocation == null || (curLocation.getX() != lastLocation.getX() && curLocation.getY() != lastLocation.getY())) {
				site.setLastLocation(curLocation);

				Dao<Location, Integer> locDao = databaseHelper.getDao(Location.class);

				if (lastLocation != null) {
					// delete old location
					locDao.delete(lastLocation);
				}
				// and create new one
				locDao.create(curLocation);

			}

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

				projectSiteDao.refresh(site);

				new MeasuringPointDrawable(this, map, wr);

				StringBuffer sb = new StringBuffer();
				for (Iterator<BssidResult> it = wr.getBssids().iterator(); it.hasNext();) {
					BssidResult result = it.next();
					Logger.d("ScanResult: " + result.toString());
					sb.append(result.toString());
					sb.append("\n");
				}

				multiTouchView.invalidate();

				Toast.makeText(this, this.getString(R.string.project_site_wifiscan_finished, sb.toString()), Toast.LENGTH_SHORT).show();

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

	/**
	 * stop the wifi scan, if in progress
	 */
	protected void stopWifiScan() {
		hideWifiScanDialog();

		if (wifiBroadcastReceiver != null) {

			WifiScanner.stopScanner(this, wifiBroadcastReceiver);
			wifiBroadcastReceiver = null;

		}
		// stop scan
		// oh, wait, we can't stop the scan, it's asynchronous!
		// we just have to ignore the result!
		ignoreWifiResults = true;

	}

	/**
	 * hide the wifi scan dialog if shown
	 */
	protected void hideWifiScanDialog() {
		if (scanningImageView != null) {
			((AnimationDrawable) scanningImageView.getDrawable()).stop();
			// scanningImageView = null;
		}

		if (scanAlertDialog != null) {
			scanAlertDialog.cancel();
			// scanAlertDialog = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onWindowFocusChanged(boolean)
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		// if(scanAlertDialog!=null && scanningImageView!=null){
		// ((AnimationDrawable) scanningImageView.getDrawable()).start();
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Logger.d("Activity result of " + requestCode + " " + resultCode + " " + (data!=null?data.toString():""));

		switch (requestCode) {
		case FILEBROWSER_REQUEST:

			if (resultCode == Activity.RESULT_OK && data!=null) {
				String path = data.getExtras().getString(FileBrowser.EXTRA_PATH);

				if (backgroundPathTextView != null) {
					backgroundPathTextView.setText(path);
				} else {
					Logger.w("the background image dialog textview should not be null?!?");
				}
			}
			break;

		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}

	}

	protected void setBackgroundImage(String path) {
		

		try {
			Bitmap bmp = BitmapFactory.decodeFile(path);
			site.setBackgroundBitmap(bmp);
			map.setBackgroundImage(bmp);
			site.setSize(bmp.getWidth(), bmp.getHeight());
			map.setSize(bmp.getWidth(), bmp.getHeight());
			multiTouchView.invalidate();
			Toast.makeText(context, "set "+path +" as new background image!", Toast.LENGTH_LONG).show();
			saveProjectSite();

		} catch (Exception e) {
			Logger.e("could not set background", e);
			Toast.makeText(context, getString(R.string.project_site_set_background_failed, e.getMessage()), Toast.LENGTH_LONG).show();
		}
	}
}
