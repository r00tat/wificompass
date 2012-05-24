/*
 * Created on Dec 23, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.activities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.ToolBox;
import at.fhstp.wificompass.exceptions.SiteNotFoundException;
import at.fhstp.wificompass.exceptions.WifiException;
import at.fhstp.wificompass.model.AccessPoint;
import at.fhstp.wificompass.model.Bssid;
import at.fhstp.wificompass.model.BssidResult;
import at.fhstp.wificompass.model.Location;
import at.fhstp.wificompass.model.ProjectSite;
import at.fhstp.wificompass.model.WifiScanResult;
import at.fhstp.wificompass.model.helper.DatabaseHelper;
import at.fhstp.wificompass.model.helper.SelectBssdidsExpandableListAdapter;
import at.fhstp.wificompass.trilateration.WeightedCentroidTrilateration;
import at.fhstp.wificompass.userlocation.LocationChangeListener;
import at.fhstp.wificompass.userlocation.LocationServiceFactory;
import at.fhstp.wificompass.userlocation.StepDetectionProvider;
import at.fhstp.wificompass.view.AccessPointDrawable;
import at.fhstp.wificompass.view.MeasuringPointDrawable;
import at.fhstp.wificompass.view.MultiTouchDrawable;
import at.fhstp.wificompass.view.MultiTouchView;
import at.fhstp.wificompass.view.NorthDrawable;
import at.fhstp.wificompass.view.OkCallback;
import at.fhstp.wificompass.view.RefreshableView;
import at.fhstp.wificompass.view.ScaleLineDrawable;
import at.fhstp.wificompass.view.SiteMapDrawable;
import at.fhstp.wificompass.view.UserDrawable;
import at.fhstp.wificompass.wifi.WifiResultCallback;
import at.fhstp.wificompass.wifi.WifiScanner;
import at.woelfel.philip.filebrowser.FileBrowser;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;

/**
 * @author Paul Woelfel (paul@woelfel.at)
 */
public class ProjectSiteActivity extends Activity implements OnClickListener, WifiResultCallback, RefreshableView, LocationChangeListener {

	/**
	 * @uml.property name="log"
	 * @uml.associationEnd
	 */
	protected Logger log = new Logger(ProjectSiteActivity.class);

	public static final String SITE_KEY = "SITE";

	public static final String PROJECT_KEY = "PROJECT";

	public static final String SCAN_INTERVAL = "scan_interval";

	// public static final int START_NEW = 1, START_LOAD = 2;

	protected static final int DIALOG_TITLE = 1, DIALOG_SCANNING = 2, DIALOG_CHANGE_SIZE = 3, DIALOG_SET_BACKGROUND = 4, DIALOG_SET_SCALE_OF_MAP = 5,
			DIALOG_ADD_KNOWN_AP = 6, DIALOG_SELECT_BSSIDS = 7, DIALOG_FRESH_SITE = 8, DIALOG_ASK_CHANGE_SCALE = 9, DIALOG_ASK_FOR_NORTH = 10,
			DIALOG_CHANGE_SCAN_INTERVAL = 11;

	protected static final int MESSAGE_REFRESH = 1, MESSAGE_START_WIFISCAN = 2, MESSAGE_PERSIST_RESULT = 3;

	protected static final int FILEBROWSER_REQUEST = 1;

	/**
	 * how often should we start a wifi scan
	 */
	protected int schedulerTime = 10;

	/**
	 * @uml.property name="multiTouchView"
	 * @uml.associationEnd
	 */
	protected MultiTouchView multiTouchView;

	/**
	 * @uml.property name="map"
	 * @uml.associationEnd
	 */
	protected SiteMapDrawable map;

	/**
	 * @uml.property name="site"
	 * @uml.associationEnd
	 */
	protected ProjectSite site;

	/**
	 * @uml.property name="databaseHelper"
	 * @uml.associationEnd
	 */
	protected DatabaseHelper databaseHelper = null;

	protected Dao<ProjectSite, Integer> projectSiteDao = null;

	protected AlertDialog scanAlertDialog;

	protected ImageView scanningImageView;

	protected boolean ignoreWifiResults = false;

	protected BroadcastReceiver wifiBroadcastReceiver;

	/**
	 * @uml.property name="user"
	 * @uml.associationEnd
	 */
	protected UserDrawable user;

	/**
	 * @uml.property name="scaler"
	 * @uml.associationEnd
	 */
	protected ScaleLineDrawable scaler = null;

	protected final Context context = this;

	protected TextView backgroundPathTextView;

	protected float scalerDistance;

	/**
	 * @uml.property name="triangulationTask"
	 * @uml.associationEnd
	 */
	protected TriangulationTask triangulationTask = null;

	/**
	 * @uml.property name="stepDetectionProvider"
	 * @uml.associationEnd
	 */
	protected StepDetectionProvider stepDetectionProvider = null;

	/**
	 * @uml.property name="northDrawable"
	 * @uml.associationEnd
	 */
	protected NorthDrawable northDrawable = null;

	protected Handler messageHandler;

	protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	protected Runnable wifiRunnable;

	protected ScheduledFuture<?> scheduledTask = null;

	protected ArrayList<WifiScanResult> unsavedScanResults;

	protected boolean walkingAndScanning = false;

	protected boolean freshSite = false;
	
	protected boolean trackSteps= true; 

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			this.setContentView(R.layout.project_site);
			super.onCreate(savedInstanceState);
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

			MultiTouchDrawable.setGridSpacing(site.getGridSpacingX(), site.getGridSpacingY());

			map = new SiteMapDrawable(this, this);
			map.setAngleAdjustment(site.getNorth());

			if (site.getWidth() == 0 || site.getHeight() == 0) {
				// the site has never been loaded
				freshSite = true;
				site.setSize(map.getWidth(), map.getHeight());
			} else {
				map.setSize(site.getWidth(), site.getHeight());
			}
			if (site.getBackgroundBitmap() != null) {
				map.setBackgroundImage(site.getBackgroundBitmap());
			}

			for (AccessPoint ap : site.getAccessPoints()) {
				new AccessPointDrawable(this, map, ap);
			}

			for (WifiScanResult wsr : site.getScanResults()) {
				new MeasuringPointDrawable(this, map, wsr);
			}

			user = new UserDrawable(this, map);

			if (site.getLastLocation() != null) {
				user.setRelativePosition(site.getLastLocation().getX(), site.getLastLocation().getY());
			} else {
				user.setRelativePosition(map.getWidth() / 2, map.getHeight() / 2);
			}

			LocationServiceFactory.getLocationService().setRelativeNorth(site.getNorth());
			LocationServiceFactory.getLocationService().setGridSpacing(site.getGridSpacingX(), site.getGridSpacingY());
			stepDetectionProvider = new StepDetectionProvider(this);
			stepDetectionProvider.setLocationChangeListener(this);

			messageHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case MESSAGE_REFRESH:
						/* Refresh UI */
						if (multiTouchView != null)
							multiTouchView.invalidate();
						break;
					case MESSAGE_START_WIFISCAN:
						// start a wifiscan
						startWifiBackgroundScan();
						break;

					case MESSAGE_PERSIST_RESULT:

						if (msg.arg1 == RESULT_OK) {
							if (msg.getData().getInt(WifiScanResultPersistTask.RESULT_COUNT) > 0)
								Toast.makeText(context, R.string.project_site_scanresults_persisted, Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(
									context,
									context.getString(R.string.project_site_scanresults_not_persisted,
											msg.getData().getString(WifiScanResultPersistTask.RESULT_MESSAGE)), Toast.LENGTH_LONG).show();
						}

						break;
					}
				}
			};

			wifiRunnable = new Runnable() {

				@Override
				public void run() {
					messageHandler.sendEmptyMessage(MESSAGE_START_WIFISCAN);
				}

			};

			unsavedScanResults = new ArrayList<WifiScanResult>();

			schedulerTime = this.getPreferences(Activity.MODE_PRIVATE).getInt(SCAN_INTERVAL, schedulerTime);

			initUI();

		} catch (Exception ex) {
			log.error("Failed to create ProjectSiteActivity: " + ex.getMessage(), ex);
			Toast.makeText(this, R.string.project_site_load_failed, Toast.LENGTH_LONG).show();
			this.finish();
		}
	}

	protected void initUI() {

		// ((Button) findViewById(R.id.project_site_reset_zoom_button)).setOnClickListener(this);

		// ((Button) findViewById(R.id.project_site_snap_user_button)).setOnClickListener(this);

		((Button) findViewById(R.id.project_site_start_wifiscan_button)).setOnClickListener(this);

		// ((Button) findViewById(R.id.project_site_calculate_ap_positions_button)).setOnClickListener(this);

		// ((Button) findViewById(R.id.project_site_add_known_ap)).setOnClickListener(this);

		((Button) findViewById(R.id.project_site_step_detect)).setOnClickListener(this);

		((ToggleButton) findViewById(R.id.project_site_toggle_autorotate)).setOnClickListener(this);

		multiTouchView = ((MultiTouchView) findViewById(R.id.project_site_resultview));
		multiTouchView.setRearrangable(false);

		multiTouchView.addDrawable(map);

		if (site.getTitle().equals(ProjectSite.UNTITLED)) {
			showDialog(DIALOG_TITLE);
		} else {
			if (freshSite) {
				// start configuration dialog
				showDialog(DIALOG_FRESH_SITE);
			}
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
		WifiScanner.stopScanning(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		log.debug("setting context");

		multiTouchView.loadImages(this);
		map.load();
		// stepDetectionProvider.start();

		if (walkingAndScanning) {
			setWalkingAndScanning(true,true);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.project_site_start_wifiscan_button:
			Logger.d("start a wifiscan");
			try {
				startWifiScan();
				showDialog(DIALOG_SCANNING);

			} catch (WifiException e) {
				Logger.e("could not start wifi scan!", e);
				Toast.makeText(this, R.string.project_site_wifiscan_start_failed, Toast.LENGTH_LONG).show();
			}

			break;

		// case R.id.project_site_calculate_ap_positions_button:
		//
		// final ProgressDialog triangulationProgress = new ProgressDialog(this);
		// triangulationProgress.setTitle(R.string.project_site_triangulation_progress_title);
		// triangulationProgress.setMessage(getString(R.string.project_site_triangulation_progress_message));
		// triangulationProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// triangulationProgress.setButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// // Canceled.
		// if (triangulationTask != null) {
		// triangulationTask.cancel(true);
		// }
		// }
		// });
		//
		// triangulationTask = new TriangulationTask(this, triangulationProgress);
		//
		// triangulationProgress.show();
		// triangulationTask.execute();
		// break;

		case R.id.project_site_add_known_ap:
			showDialog(DIALOG_ADD_KNOWN_AP);
			break;

		case R.id.project_site_step_detect:

			setWalkingAndScanning(!walkingAndScanning,true);
			walkingAndScanning = !walkingAndScanning;

			break;

		case R.id.project_site_toggle_autorotate:

			ToggleButton button = (ToggleButton) findViewById(R.id.project_site_toggle_autorotate);

			if (button.isChecked()) {
				map.startAutoRotate();
				Logger.d("Started autorotate.");
			} else {
				map.stopAutoRotate();
				Logger.d("Stopped autorotate.");
			}

			break;
		}
	}

	protected void setWalkingAndScanning(boolean shouldRun,boolean ui) {
		if (!shouldRun) {
			// stop!

			if (stepDetectionProvider.isRunning())
				stepDetectionProvider.stop();
			if (scheduledTask != null) {
				scheduledTask.cancel(false);
				scheduledTask = null;
			}
			stopWifiScan();

			if(ui)
			((Button) findViewById(R.id.project_site_step_detect)).setText(R.string.project_site_start_step_detect);

			persistScanResults(ui);

		} else {
			// start
			unsavedScanResults = new ArrayList<WifiScanResult>();

			if (!stepDetectionProvider.isRunning()) {
				stepDetectionProvider.start();
			}

			if (scheduledTask == null) {
				scheduledTask = scheduler.scheduleWithFixedDelay(wifiRunnable, 0, schedulerTime, TimeUnit.SECONDS);
			}
			if(ui)
			((Button) findViewById(R.id.project_site_step_detect)).setText(R.string.project_site_stop_step_detect);
		}
	}

	/**
	 * 
	 */
	protected void persistScanResults(boolean dialog) {
		if (dialog) {
			final ProgressDialog persistProgress = new ProgressDialog(this);

			final WifiScanResultPersistTask persistTask = new WifiScanResultPersistTask(this, persistProgress);

			// create dialog and run asynctask
			persistProgress.setTitle(R.string.project_site_scanresults_persisting_title);
			persistProgress.setMessage(getString(R.string.project_site_scanresults_persisting_message));
			persistProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

			persistProgress.setButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
					if (persistTask != null) {
						persistTask.cancel(true);
					}
				}
			});

			persistProgress.show();
			persistTask.execute();
		} else {
			WifiScanResultPersistTask persistTask = new WifiScanResultPersistTask(this, null);
			persistTask.execute();
		}
	}

	protected void addKnownAP(String bssid, String ssid) {
		Location curLocation = LocationServiceFactory.getLocationService().getLocation();
		AccessPoint ap = new AccessPoint();
		ap.setBssid(bssid);
		ap.setSsid(ssid);
		ap.setLocation(curLocation);
		ap.setCapabilities("");
		ap.setCalculated(false);
		ap.setProjectSite(site);
		new AccessPointDrawable(this, map, ap);

		try {
			databaseHelper.getDao(Location.class).create(curLocation);
			databaseHelper.getDao(AccessPoint.class).create(ap);
		} catch (SQLException e) {
			Logger.e("could not create ap", e);

		}

		multiTouchView.invalidate();

	}

	protected void setCalculatedAccessPoints(Vector<AccessPointDrawable> aps) {
		// delete all old messurements
		for (int i = 0; i < map.getSubDrawables().size(); i++) {
			MultiTouchDrawable d = map.getSubDrawables().get(i);
			if (d instanceof AccessPointDrawable && ((AccessPointDrawable) d).isCalculated()) {
				map.removeSubDrawable(d);
				i--;
			}
		}

		try {
			Dao<AccessPoint, Integer> apDao = databaseHelper.getDao(AccessPoint.class);
			Dao<Location, Integer> locDao = databaseHelper.getDao(Location.class);

			for (AccessPoint ap : site.getAccessPoints()) {

				try {
					if (ap.isCalculated())
						apDao.delete(ap);
				} catch (Exception e) {

				}
			}

			for (AccessPointDrawable ap : aps) {

				locDao.createIfNotExists(ap.getAccessPoint().getLocation());
				ap.getAccessPoint().setProjectSite(site);
				apDao.createOrUpdate(ap.getAccessPoint());
			}

			projectSiteDao.refresh(site);

		} catch (SQLException e) {
			Logger.e("could not delete old or create new ap results", e);
		}

		for (AccessPointDrawable ap : aps) {
			map.addSubDrawable(ap);
			map.recalculatePositions();

		}
		multiTouchView.invalidate();
	}

	protected void setScaleOfMap(float scale) {
		float mapScale = scalerDistance / scale;
		site.setGridSpacingX(mapScale);
		site.setGridSpacingY(mapScale);
		LocationServiceFactory.getLocationService().setGridSpacing(site.getGridSpacingX(), site.getGridSpacingY());
		MultiTouchDrawable.setGridSpacing(mapScale, mapScale);
		multiTouchView.invalidate();
		Toast.makeText(this, getString(R.string.project_site_mapscale_changed, mapScale), Toast.LENGTH_SHORT).show();

		if (freshSite) {
			showDialog(DIALOG_ASK_FOR_NORTH);
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
					if (freshSite) {
						showDialog(DIALOG_FRESH_SITE);
					}
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

			sizeAlert.setView(getLayoutInflater().inflate(R.layout.project_site_dialog_change_size, (ViewGroup) getCurrentFocus()));

			sizeAlert.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					try {
						int w = Integer.parseInt(((EditText) ((AlertDialog) dialog).findViewById(R.id.project_site_dialog_change_size_width))
								.getText().toString()), h = Integer.parseInt(((EditText) ((AlertDialog) dialog)
								.findViewById(R.id.project_site_dialog_change_size_height)).getText().toString());
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
					i.putExtra(FileBrowser.EXTRA_ALLOWED_EXTENSIONS, "jpg,png,gif,jpeg,bmp");
					startActivityForResult(i, FILEBROWSER_REQUEST);
				}

			});

			bckgLayout.addView(pathButton);

			bckgAlert.setView(bckgLayout);

			bckgAlert.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					setBackgroundImage(pathTextView.getText().toString());
					if (freshSite) {
						showDialog(DIALOG_ASK_CHANGE_SCALE);
					}
				}
			});

			bckgAlert.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
					freshSite = false;
				}
			});

			Dialog bckgDialog = bckgAlert.create();
			bckgDialog.setCanceledOnTouchOutside(true);

			return bckgDialog;

		case DIALOG_SET_SCALE_OF_MAP:
			AlertDialog.Builder scaleOfMapDialog = new AlertDialog.Builder(this);

			scaleOfMapDialog.setTitle(R.string.project_site_dialog_scale_of_map_title);
			scaleOfMapDialog.setMessage(R.string.project_site_dialog_scale_of_map_message);

			// Set an EditText view to get user input
			final EditText scaleInput = new EditText(this);
			scaleInput.setSingleLine(true);
			scaleInput.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			scaleOfMapDialog.setView(scaleInput);

			scaleOfMapDialog.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					try {
						float value = Float.parseFloat(scaleInput.getText().toString());
						setScaleOfMap(value);
					} catch (NumberFormatException nfe) {
						Logger.w("Wrong number format format!");
						Toast.makeText(context, getString(R.string.not_a_number, scaleInput.getText()), Toast.LENGTH_SHORT).show();
					}
				}
			});

			scaleOfMapDialog.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			return scaleOfMapDialog.create();

		case DIALOG_ADD_KNOWN_AP:

			AlertDialog.Builder addAPAlert = new AlertDialog.Builder(this);

			addAPAlert.setTitle(R.string.project_site_dialog_add_known_ap_title);
			addAPAlert.setMessage(R.string.project_site_dialog_add_known_ap_message);

			addAPAlert.setView(getLayoutInflater().inflate(R.layout.project_site_dialog_add_known_ap, (ViewGroup) getCurrentFocus()));

			addAPAlert.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					String ssid = ((EditText) ((AlertDialog) dialog).findViewById(R.id.project_site_dialog_add_known_ap_ssid)).getText().toString();
					String bssid = ((EditText) ((AlertDialog) dialog).findViewById(R.id.project_site_dialog_add_known_ap_bssid)).getText().toString();
					addKnownAP(bssid, ssid);

				}
			});

			addAPAlert.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			return addAPAlert.create();

		case DIALOG_SELECT_BSSIDS:

			AlertDialog.Builder selectBssidsDialog = new AlertDialog.Builder(this);

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.project_site_dialog_select_bssids,
					(ViewGroup) findViewById(R.id.project_site_dialog_select_bssids_root_layout));
			selectBssidsDialog.setView(layout);

			final SelectBssdidsExpandableListAdapter adapter = new SelectBssdidsExpandableListAdapter();

			selectBssidsDialog.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					site.setUnselectedBssids(adapter.getSelectedBssids(false));
					try {
						site.update();
					} catch (SQLException e) {
						Logger.e("Could not update project site", e);
					}
				}
			});
			selectBssidsDialog.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			AlertDialog dialog = selectBssidsDialog.create();

			ExpandableListView listView = (ExpandableListView) layout.findViewById(R.id.project_site_dialog_select_bssids_list_view);
			adapter.initialize(dialog.getContext(), new ArrayList<String>(), new ArrayList<ArrayList<Bssid>>());

			Button selectAll = (Button) layout.findViewById(R.id.project_site_dialog_select_bssids_select_all_button);
			Button deselectAll = (Button) layout.findViewById(R.id.project_site_dialog_select_bssids_deselect_all_button);

			OnClickListener selectAllListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean state = true;

					if (v.getId() == R.id.project_site_dialog_select_bssids_select_all_button)
						state = true;
					else
						state = false;

					adapter.selectAllChildren(state);
				}

			};

			selectAll.setOnClickListener(selectAllListener);
			deselectAll.setOnClickListener(selectAllListener);

			// Set this blank adapter to the list view
			listView.setAdapter(adapter);

			ForeignCollection<WifiScanResult> scanResults = site.getScanResults();
			ArrayList<Bssid> bssids = new ArrayList<Bssid>();

			for (WifiScanResult scanResult : scanResults) {
				Collection<BssidResult> bssidResults = scanResult.getBssids();

				for (BssidResult bssidResult : bssidResults) {
					Bssid bssid = new Bssid(bssidResult.getBssid(), bssidResult.getSsid());

					boolean alreadyAdded = false;

					for (Bssid tmpBssid : bssids) {
						if (tmpBssid.getBssid().equals(bssid.getBssid()) && tmpBssid.getSsid().equals(bssid.getSsid()))
							alreadyAdded = true;
					}

					if (!alreadyAdded) {
						bssid.setSelected(site.isBssidSelected(bssid.getBssid()));
						bssids.add(bssid);
					}
				}
			}

			adapter.addItems(bssids);

			return dialog;

		case DIALOG_FRESH_SITE:

			AlertDialog.Builder freshBuilder = new Builder(context);
			freshBuilder.setTitle(R.string.project_site_dialog_fresh_site_title);
			freshBuilder.setMessage(R.string.project_site_dialog_fresh_site_message);

			freshBuilder.setPositiveButton(getString(R.string.button_yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					showDialog(DIALOG_SET_BACKGROUND);
				}

			});

			freshBuilder.setNegativeButton(getString(R.string.button_no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
					freshSite = false;
				}
			});

			return freshBuilder.create();

		case DIALOG_ASK_CHANGE_SCALE:

			AlertDialog.Builder askScaleBuilder = new Builder(context);
			askScaleBuilder.setTitle(R.string.project_site_dialog_ask_change_scale_title);
			askScaleBuilder.setMessage(R.string.project_site_dialog_ask_change_scale_message);

			askScaleBuilder.setPositiveButton(getString(R.string.button_yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					scaleOfMap();
				}

			});

			askScaleBuilder.setNegativeButton(getString(R.string.button_no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
					freshSite = false;
				}
			});

			return askScaleBuilder.create();

		case DIALOG_ASK_FOR_NORTH:

			AlertDialog.Builder askNorthBuilder = new Builder(context);
			askNorthBuilder.setTitle(R.string.project_site_dialog_ask_north_title);
			askNorthBuilder.setMessage(R.string.project_site_dialog_ask_north_message);

			askNorthBuilder.setPositiveButton(getString(R.string.button_yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					setMapNorth();
					freshSite = false;
				}

			});

			askNorthBuilder.setNegativeButton(getString(R.string.button_no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
					freshSite = false;
				}
			});

			return askNorthBuilder.create();

		case DIALOG_CHANGE_SCAN_INTERVAL:
			AlertDialog.Builder changeScanIntervalBuilder = new Builder(context);
			changeScanIntervalBuilder.setTitle(R.string.project_site_dialog_change_scan_interval_title);
			changeScanIntervalBuilder.setMessage(getString(R.string.project_site_dialog_change_scan_interval_message, schedulerTime));

			final SeekBar sb = new SeekBar(this);
			sb.setMax(60);
			sb.setProgress(schedulerTime);

			changeScanIntervalBuilder.setView(sb);

			changeScanIntervalBuilder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					schedulerTime = sb.getProgress();
					getPreferences(MODE_PRIVATE).edit().putInt(SCAN_INTERVAL, schedulerTime).commit();
					if (walkingAndScanning) {
						// timer must be updated
						setWalkingAndScanning(false,true);
						setWalkingAndScanning(true,true);
					}
				}

			});

			changeScanIntervalBuilder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.

				}
			});

			final AlertDialog changeScanIntervalDialog = changeScanIntervalBuilder.create();

			sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					changeScanIntervalDialog.setMessage(context.getString(R.string.project_site_dialog_change_scan_interval_message, progress));
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

			});

			return changeScanIntervalDialog;

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

		Logger.d("id: " + item.getItemId() + " != " + R.id.project_site_calculate_ap_positions_option);

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

		case R.id.project_site_menu_set_scale_of_map:

			if (scaler == null) {
				scaleOfMap();
			} else {
				// just hide the scalers, don't change the scaleing
				scaler.removeScaleSliders();
				map.removeSubDrawable(scaler);
				scaler = null;
				invalidate();
			}

			return false;

		case R.id.project_site_menu_set_north:

			setMapNorth();
			return false;

		case R.id.project_site_menu_select_bssids:

			this.showDialog(DIALOG_SELECT_BSSIDS);

			return false;

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

		case R.id.project_site_calculate_ap_positions_option:

			final ProgressDialog triangulationProgress = new ProgressDialog(this);
			triangulationProgress.setTitle(R.string.project_site_triangulation_progress_title);
			triangulationProgress.setMessage(getString(R.string.project_site_triangulation_progress_message));
			triangulationProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			triangulationProgress.setButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
					if (triangulationTask != null) {
						triangulationTask.cancel(true);
					}
				}
			});

			triangulationTask = new TriangulationTask(this, triangulationProgress);

			triangulationProgress.show();
			triangulationTask.execute();
			break;

		case R.id.project_site_add_known_ap:
			showDialog(DIALOG_ADD_KNOWN_AP);
			break;

		case R.id.project_site_menu_change_scan_interval:
			showDialog(DIALOG_CHANGE_SCAN_INTERVAL);
			break;
			
		case R.id.project_site_menu_track_steps:
			trackSteps=!trackSteps;
			if(trackSteps==false){
				// tracking disabled
				map.deleteAllSteps();
			}
			break;

		default:
			return super.onOptionsItemSelected(item);
		}

		return false;
	}

	/**
	 * 
	 */
	protected void setMapNorth() {
		if (northDrawable == null) {
			// Stop auto-rotate when map north is set
			((ToggleButton) findViewById(R.id.project_site_toggle_autorotate)).setChecked(false);
			map.stopAutoRotate();

			// create the icon the set the north
			northDrawable = new NorthDrawable(this, map, site) {

				/*
				 * (non-Javadoc)
				 * 
				 * @see at.fhstp.wificompass.view.NorthDrawable#onOk()
				 */
				@Override
				public void onOk() {
					super.onOk();
					northDrawable = null;
					site.setNorth(ToolBox.normalizeAngle(adjustmentAngle));
					map.setAngleAdjustment(site.getNorth());
					
					LocationServiceFactory.getLocationService().setRelativeNorth(site.getNorth());
					Logger.d("set adjustment angle of map to "+site.getNorth());
					Toast.makeText(ctx, R.string.project_site_nort_set, Toast.LENGTH_SHORT).show();
					saveProjectSite();
				}

			};
			northDrawable.setRelativePosition(site.getWidth() / 2, site.getHeight() / 2);
			northDrawable.setAngle(map.getAngle() + site.getNorth());

		} else {
			map.removeSubDrawable(northDrawable);
			// do not set the angle, if the menu option is clicked
			// site.setNorth(northDrawable.getAngle());
			// LocationServiceFactory.getLocationService().setRelativeNorth(site.getNorth());
			northDrawable = null;
		}

		multiTouchView.invalidate();

	}

	/**
	 * 
	 */
	protected void scaleOfMap() {
		if (scaler == null) {
			scaler = new ScaleLineDrawable(context, map, new OkCallback() {

				@Override
				public void onOk() {
					onMapScaleSelected();
				}
			});
			scaler.getSlider(1).setRelativePosition(user.getRelativeX() - 80, user.getRelativeY());
			scaler.getSlider(2).setRelativePosition(user.getRelativeX() + 80, user.getRelativeY());
			multiTouchView.invalidate();
		} else {
			onMapScaleSelected();
		}
	}

	protected void onMapScaleSelected() {
		scalerDistance = scaler.getSliderDistance();
		scaler.removeScaleSliders();
		map.removeSubDrawable(scaler);
		scaler = null;
		invalidate();
		showDialog(DIALOG_SET_SCALE_OF_MAP);
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
		map.unload();

		setWalkingAndScanning(false,false);

		saveProjectSite();
	}

	/**
	 * save current project site
	 */
	protected void saveProjectSite() {
		log.debug("saveing project site");

		try {

			Location curLocation = new Location(LocationServiceFactory.getLocationService().getLocation()), lastLocation = site.getLastLocation();

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

			for (MultiTouchDrawable d : map.getSubDrawables()) {

				if (d instanceof AccessPointDrawable) {
					AccessPoint ap = ((AccessPointDrawable) d).getAccessPoint();
					// id is not 0, so this location was never saved
					if (!ap.isCalculated() && ap.getLocation() != null) {
						try {
							databaseHelper.getDao(Location.class).create(ap.getLocation());
							databaseHelper.getDao(AccessPoint.class).update(ap);
						} catch (SQLException e) {
							log.error("could not save location data for an ap: " + ap.toString(), e);
						}
					}
				}
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

	protected void deleteProjectSite() {
		log.debug("saveing project site");

		try {
			int rows = site.delete();

			if (rows > 0) {
				Toast.makeText(this, R.string.project_site_deleted, Toast.LENGTH_SHORT).show();
			} else {
				Logger.w("Tried to delete a project site, but it did not exist?!?");

			}
			finish();

		} catch (SQLException e) {
			log.error("could not delete project site", e);
			Toast.makeText(this, getString(R.string.project_site_delete_failed, e.getMessage()), Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public synchronized void onScanFinished(WifiScanResult wr) {
		hideWifiScanDialog();
		if (!ignoreWifiResults) {
			try {

				Logger.d("received a wifi scan result!");
				ignoreWifiResults = true;

				wr.setProjectLocation(site);

				if (walkingAndScanning) {
					unsavedScanResults.add(wr);
				} else {
					wr.save(databaseHelper);
					site.getScanResults().refreshCollection();
				}

				// Dao<WifiScanResult, Integer> scanResultDao = databaseHelper.getDao(WifiScanResult.class);
				// scanResultDao.update(wr);

				// projectSiteDao.refresh(site);

				new MeasuringPointDrawable(this, map, wr);

				// StringBuffer sb = new StringBuffer();
				HashMap<String, Integer> ssids = new HashMap<String, Integer>();
				for (BssidResult result : wr.getBssids()) {
					ssids.put(result.getSsid(), (ssids.get(result.getSsid()) == null ? 1 : ssids.get(result.getSsid()) + 1));
					// BssidResult result = it.next();
					// Logger.d("ScanResult: " + result.toString());
					// sb.append(result.toString());
					// sb.append("\n");
				}

				user.bringToFront();

				multiTouchView.invalidate();

				// it's not necessary to show the result as Toast, but we can show a summary
				// Toast.makeText(this, this.getString(R.string.project_site_wifiscan_finished, sb.toString()), Toast.LENGTH_SHORT).show();
				Toast.makeText(this, this.getString(R.string.project_site_wifiscan_finished, ssids.size(), wr.getBssids().size()), Toast.LENGTH_SHORT)
						.show();

				// if(stepDetectionProvider.isRunning()){
				// // we are walking and finished a scan, why don't we start a new one
				// startWifiBackgroundScan();
				//
				// }

			} catch (SQLException e) {
				Logger.e("could not update wifiscanresult!", e);
				Toast.makeText(this, this.getString(R.string.project_site_wifiscan_failed, e.getMessage()), Toast.LENGTH_LONG).show();
			}

		}
	}

	@Override
	public void onScanFailed(Exception ex) {
		hideWifiScanDialog();
		if (!ignoreWifiResults) {

			Logger.e("Wifi scan failed!", ex);
			Toast.makeText(this, this.getString(R.string.project_site_wifiscan_failed, ex.getMessage()), Toast.LENGTH_LONG).show();

		}

	}

	protected void startWifiScan() throws WifiException {
		log.debug("starting WiFi Scan");

		wifiBroadcastReceiver = WifiScanner.startScan(this, this);
		ignoreWifiResults = false;
	}

	protected void startWifiBackgroundScan() {
		
			try {
				// we first stop the old receiver, so we wont receive duplicate results
//				stopWifiScan();
				
				if(wifiBroadcastReceiver!=null){
//					wifiBroadcastReceiver.
				}
				
				startWifiScan();
				// Toast.makeText(this, R.string.project_site_wifiscan_started, Toast.LENGTH_SHORT).show();
			} catch (WifiException e) {
				Logger.e("could not start wifi scan", e);
				Toast.makeText(this, getString(R.string.project_site_wifiscan_start_failed, e.getMessage()), Toast.LENGTH_LONG).show();
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

		Logger.d("Activity result of " + requestCode + " " + resultCode + " " + (data != null ? data.toString() : ""));

		switch (requestCode) {
		case FILEBROWSER_REQUEST:

			if (resultCode == Activity.RESULT_OK && data != null) {
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
			user.setRelativePosition(bmp.getWidth() / 2, bmp.getHeight() / 2);
			multiTouchView.invalidate();
			Toast.makeText(context, "set " + path + " as new background image!", Toast.LENGTH_LONG).show();
			saveProjectSite();

		} catch (Exception e) {
			Logger.e("could not set background", e);
			Toast.makeText(context, getString(R.string.project_site_set_background_failed, e.getMessage()), Toast.LENGTH_LONG).show();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration )
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		this.setContentView(R.layout.project_site);
		initUI();
	}

	@Override
	public void invalidate() {
		if (multiTouchView != null) {
			multiTouchView.invalidate();
		}
	}

	/**
	 * @author Thomas Konrad (is101503@fhstp.ac.at)
	 */
	protected class TriangulationTask extends AsyncTask<Void, Integer, Vector<AccessPointDrawable>> {

		/**
		 * @uml.property name="parent"
		 * @uml.associationEnd
		 */
		private final ProjectSiteActivity parent;

		private final ProgressDialog progress;

		public TriangulationTask(final ProjectSiteActivity parent, final ProgressDialog progress) {
			this.parent = parent;
			this.progress = progress;
		}

		@Override
		protected Vector<AccessPointDrawable> doInBackground(Void... params) {
			WeightedCentroidTrilateration wc = new WeightedCentroidTrilateration(context, site, progress);
			return wc.calculateAllAndGetDrawables();
		}

		@Override
		protected void onPostExecute(final Vector<AccessPointDrawable> result) {
			progress.dismiss();
			parent.setCalculatedAccessPoints(result);
		}
	}

	@Override
	public void onLocationChange(Location loc) {
		// info from StepDetectionProvider, that the location changed.
		user.setRelativePosition(loc.getX(), loc.getY());
		map.addStep(new PointF(loc.getX(),loc.getY()));
		messageHandler.sendEmptyMessage(MESSAGE_REFRESH);
	}

	protected class WifiScanResultPersistTask extends AsyncTask<Void, Integer, Bundle> {

		protected ProjectSiteActivity parent;

		protected ProgressDialog progressDialog;

		protected boolean running = true;

		protected DatabaseHelper databaseHelper;

		static final String RESULT_CODE = "result", RESULT_MESSAGE = "message", RESULT_COUNT = "count";

		public WifiScanResultPersistTask(ProjectSiteActivity parent, ProgressDialog progress) {
			this.parent = parent;
			this.progressDialog = progress;
			databaseHelper = OpenHelperManager.getHelper(parent, DatabaseHelper.class);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Bundle doInBackground(Void... params) {
			Bundle result = new Bundle();
			result.putInt(RESULT_CODE, RESULT_CANCELED);

			int progress = 0;

			synchronized (unsavedScanResults) {

				try{
				this.progressDialog.setMax(unsavedScanResults.size());
				}catch (Exception ex){}

				// save all wifiscan results
				try {

					for (WifiScanResult sr : unsavedScanResults) {
						if (!running) {
							break;
						}
						sr.save(databaseHelper);
						this.publishProgress(++progress);
					}

					result.putInt(RESULT_COUNT, progress);
					if (running) {
						Logger.d("saved " + unsavedScanResults.size() + " WiFi scan results");
						unsavedScanResults = new ArrayList<WifiScanResult>();
						result.putInt(RESULT_CODE, RESULT_OK);
					} else {
						// remove the saved results

						while (progress > 0) {
							unsavedScanResults.remove(0);
							progress--;
						}
					}
				} catch (SQLException e) {
					Logger.e("Could not save temporary results", e);
					result.putInt(RESULT_CODE, RESULT_CANCELED);
					result.putString(RESULT_MESSAGE, RESULT_MESSAGE);
				}
			}

			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Bundle result) {
			if (progressDialog != null)
				progressDialog.dismiss();
			OpenHelperManager.releaseHelper();
			if (running && messageHandler != null) {
				Message msg = new Message();
				msg.what = MESSAGE_PERSIST_RESULT;
				msg.arg1 = result.getInt(RESULT_CODE);
				msg.setData(result);
				messageHandler.sendMessage(msg);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			if (progressDialog != null)
				progressDialog.setProgress(values[0]);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onCancelled()
		 */
		@Override
		protected void onCancelled() {
			running = false;
			super.onCancelled();
		}

	}
}
