package at.fhstp.wificompass.activities;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.SensorData;
import at.fhstp.wificompass.model.helper.DatabaseHelper;
import at.fhstp.wificompass.userlocation.StepDetection;
import at.fhstp.wificompass.userlocation.StepDetectionProvider;
import at.fhstp.wificompass.userlocation.StepDetector;
import at.fhstp.wificompass.userlocation.StepTrigger;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import de.uvwxy.footpath.gui.PaintBoxHistory;

/**
 * <p>
 * This Activity is used to calibrate the parameters concerning step detection
 * </p>
 * <p>
 * Original by Paul Smith, adjusted and extended by Paul Woelfel
 * </p>
 * 
 * @author Paul Smith, Paul Woelfel
 */
public class CalibratorActivity extends Activity implements StepTrigger, OnClickListener {

	/**
	 * @uml.property name="stepDetection"
	 * @uml.associationEnd
	 */
	private StepDetection stepDetection;

	/**
	 * @uml.property name="svHistory"
	 * @uml.associationEnd
	 */
	PaintBoxHistory svHistory;

	// GUI
	TextView tvPeak = null;

	TextView tvFilter = null;

	TextView tvTimeout = null;

	SeekBar sbPeak = null;

	SeekBar sbFilter = null;

	SeekBar sbTimeout = null;

	SeekBar sbStepSize = null;

	float peak; // threshold for step detection

	float filter; // value for low pass filter

	int step_timeout_ms; // distance in ms between each step

	float stepSize;

	protected boolean calibrating = false;

	protected DatabaseHelper databaseHelper;

	protected Dao<SensorData, Integer> sensorDao;

	protected AutoCalibrateTask calibrateTask;

	public static final String ACCELOREMETER_STRING = "acc", STEP_STRING = "step";

	public static final int STEP_TYPE = 42;

	// filter range 0.05 to 0.80 in 0.05 steps
	public static final float FILTER_MIN = 0.05f, FILTER_MAX = 0.8f, FILTER_INTERVAL = 0.05f;

	// peak range 0.2 to 3.5 in 0.1 steps
	public static final float PEAK_MIN = 0.2f, PEAK_MAX = 3.0f, PEAK_INTERVAL = 0.1f;

	// step timeout from 100ms to 500ms in 25ms steps
	public static final int TIMEOUT_MIN = 200, TIMEOUT_MAX = 500, TIMEOUT_INTERVAL = 50;
	
	public static final int STEP_DETECTED_REWARD=1,STEP_FALSE_DETECTED_PUNISH=-2,STEP_NOT_DETECTED_PUNISH=-1;

	protected int windowSize = 500;

	public static final String BUNDLE_SCORE = "score", BUNDLE_PCT = "percantage", BUNDLE_FILTER = "filter", BUNDLE_PEAK = "peak",
			BUNDLE_FOUND = "found", BUNDLE_NOTFOUND = "notfound", BUNDLE_FALSEFOUND = "falsefound", BUNDLE_ALLFOUND = "allfound",BUNDLE_TIMEOUT="timeout";

	OnSeekBarChangeListener sbListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar sb, int arg1, boolean arg2) {
			switch (sb.getId()) {
			case R.id.calibrator_sbPeak:
				setProgressValue(sb.getId(), sbPeak.getProgress() / 10.0f);
				break;
			case R.id.calibrator_sbFilter:
				setProgressValue(sb.getId(), sbFilter.getProgress() / 100.0f);
				break;
			case R.id.calibrator_sbTimeout:
				setProgressValue(sb.getId(), sbTimeout.getProgress());
				break;
			case R.id.calibrator_step_size:
				setProgressValue(sb.getId(), sbStepSize.getProgress() / 100.0f);
				break;
			}

		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
		}

	};

	private void loadSettings() {
		filter = getSharedPreferences(StepDetectionProvider.CALIB_DATA, 0).getFloat(StepDetectionProvider.FILTER,
				StepDetectionProvider.FILTER_DEFAULT);
		peak = getSharedPreferences(StepDetectionProvider.CALIB_DATA, 0).getFloat(StepDetectionProvider.PEAK, StepDetectionProvider.PEAK_DEFAULT);
		step_timeout_ms = getSharedPreferences(StepDetectionProvider.CALIB_DATA, 0).getInt(StepDetectionProvider.TIMEOUT,
				StepDetectionProvider.TIMEOUT_DEFAULT);
		stepSize = getSharedPreferences(StepDetectionProvider.CALIB_DATA, 0).getFloat(StepDetectionProvider.STEP, StepDetectionProvider.STEP_DEFAULT);

		// Update GUI elements
		setProgressValue(R.id.calibrator_sbPeak, peak);
		setProgressValue(R.id.calibrator_sbFilter, filter);
		setProgressValue(R.id.calibrator_sbTimeout, step_timeout_ms);
		setProgressValue(R.id.calibrator_step_size, stepSize);
	}

	protected boolean setProgressValue(int id, float value) {
		boolean ret = true;
		switch (id) {
		case R.id.calibrator_sbPeak:
			peak = value;
			stepDetection.setPeak(peak);
			sbPeak.setProgress((int) (value * 10));
			tvPeak.setText(getString(R.string.calibrator_peak_text, value,StepDetectionProvider.PEAK_DEFAULT));
			break;
		case R.id.calibrator_sbFilter:
			filter = value;
			stepDetection.setA(filter);
			sbFilter.setProgress((int) (value * 100));
			tvFilter.setText(getString(R.string.calibrator_filter_text, value,StepDetectionProvider.FILTER_DEFAULT));
			break;
		case R.id.calibrator_sbTimeout:
			step_timeout_ms = (int) value;
			stepDetection.setStep_timeout_ms(step_timeout_ms);
			tvTimeout.setText(getString(R.string.calibrator_step_timeout_text, (int) value,StepDetectionProvider.TIMEOUT_DEFAULT));
			break;
		case R.id.calibrator_step_size:
			stepSize = value;

			((TextView) (findViewById(R.id.calibrator_tv_step_size))).setText(getString(R.string.calibrator_step_size_text, value,StepDetectionProvider.STEP_DEFAULT));
			break;
		default:
			ret = false;
			break;
		}
		if (ret) {
			saveSettings();
		}

		return ret;
	}

	private void saveSettings() {
		// Save current values to settings
		SharedPreferences settings = getSharedPreferences(StepDetectionProvider.CALIB_DATA, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(StepDetectionProvider.FILTER, filter);
		editor.putFloat(StepDetectionProvider.PEAK, peak);
		editor.putInt(StepDetectionProvider.TIMEOUT, step_timeout_ms);
		editor.putFloat(StepDetectionProvider.STEP, stepSize);
		// Apply changes
		editor.commit();
	}

	@Override
	public void onAccelerometerDataReceived(long nowMs, double x, double y, double z) {
		if (calibrating && sensorDao != null) {
			// save data for calculations.
			try {
				sensorDao.create(new SensorData(ACCELOREMETER_STRING, Sensor.TYPE_ACCELEROMETER, (float) x, (float) y, (float) z, 0));
			} catch (SQLException e) {
				Logger.e("could not save sensor data", e);
			}
		}
	}

	@Override
	public void onCompassDataReceived(long nowMs, double x, double y, double z) {
	}

	@Override
	public void onTimerElapsed(long nowMs, double[] acc, double[] comp) {
		svHistory.addTriple(nowMs, acc);
	}

	@Override
	public void onStepDetected(long nowMs, double compDir) {
		if (!calibrating)
			svHistory.addStepTS(nowMs);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load settings after creation of GUI-elements, to set their values
		initLogic();
		initUI();

	}

	protected void initLogic() {
		stepDetection = new StepDetection(this, this, filter, peak, step_timeout_ms);

		long samples_per_second = 1000 / StepDetection.INTERVAL_MS;
		int history_in_seconds = 4;
		int samples_per_history = (int) (history_in_seconds * samples_per_second);

		// create PaintBox (-24.0 to 24.0, 100 entries)
		svHistory = new PaintBoxHistory(this, 48.0, samples_per_history, history_in_seconds);

		databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		try {
			sensorDao = databaseHelper.getDao(SensorData.class);
		} catch (SQLException e) {
			Logger.e("could not initialize dao for sensorData", e);
		}
	}

	protected void initUI() {
		setContentView(R.layout.calibrator);

		tvPeak = (TextView) findViewById(R.id.calibrator_tvPeak);
		tvFilter = (TextView) findViewById(R.id.calibrator_tvFilter);
		tvTimeout = (TextView) findViewById(R.id.calibrator_tvTimeout);

		sbPeak = (SeekBar) findViewById(R.id.calibrator_sbPeak);
		sbFilter = (SeekBar) findViewById(R.id.calibrator_sbFilter);
		sbTimeout = (SeekBar) findViewById(R.id.calibrator_sbTimeout);
		sbStepSize = (SeekBar) findViewById(R.id.calibrator_step_size);

		// Add OnSeekBarChangeListener after creation of step detection, because object is used
		sbPeak.setOnSeekBarChangeListener(sbListener);
		sbFilter.setOnSeekBarChangeListener(sbListener);
		sbTimeout.setOnSeekBarChangeListener(sbListener);
		sbStepSize.setOnSeekBarChangeListener(sbListener);

		LinearLayout linLayout = (LinearLayout) findViewById(R.id.calibrator_LinearLayout01); // get pointer to layout
		SurfaceView svOld = (SurfaceView) findViewById(R.id.calibrator_svHistory); // get SurfaceView defined in xml
		LayoutParams lpHistory = svOld.getLayoutParams(); // get its layout params

		linLayout.removeView(svOld); // and remove surface view from layout
		if (svHistory.getParent() != null && svHistory.getParent() instanceof ViewGroup) {
			((ViewGroup) svHistory.getParent()).removeView(svHistory);
		}
		linLayout.addView(svHistory, lpHistory); // add surface view clone to layout
		svHistory.setOnClickListener(this);

		ToggleButton autoScanning = (ToggleButton) findViewById(R.id.calibrator_auto_calibrate);
		autoScanning.setOnClickListener(this);
		autoScanning.setChecked(calibrating);

		((Button) findViewById(R.id.calibrator_analyze_data)).setOnClickListener(this);

		loadSettings();
	}

	@Override
	public void onPause() {
		super.onPause();
		saveSettings();
		stepDetection.unload();
	}

	@Override
	public void onDestroy() {
		OpenHelperManager.releaseHelper();
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		loadSettings();
		stepDetection.load(SensorManager.SENSOR_DELAY_FASTEST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initUI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View paramView) {
		switch (paramView.getId()) {
		case R.id.calibrator_auto_calibrate:
			if (calibrating) {
				// stop calibration

				showCalibrationDialog();
				calibrating = false;
			} else {
				// start calibration
				// clear saved sensor data
				try {
					sensorDao.delete(sensorDao.deleteBuilder().prepare());
				} catch (SQLException e) {
					Logger.e("could not delete all sensordata entries", e);
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.calibrator_auto_config_info_title);
				builder.setMessage(R.string.calibrator_auto_config_info_message);

				final Activity activity = this;

				builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						calibrating = true;
						((ToggleButton) activity.findViewById(R.id.calibrator_auto_calibrate)).setChecked(calibrating);
					}
				});

				builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});

				builder.create().show();
			}

			((ToggleButton) findViewById(R.id.calibrator_auto_calibrate)).setChecked(calibrating);

			break;

		case R.id.calibrator_analyze_data:
			showCalibrationDialog();
			break;

		}

		if (paramView == svHistory && calibrating) {
			try {
				sensorDao.create(new SensorData(STEP_STRING, STEP_TYPE));
			} catch (SQLException e) {
				Logger.e("could not save step", e);
			}
			svHistory.addStepTS(System.currentTimeMillis());
		}

	}

	protected void showCalibrationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.calibrator_auto_config_setting_title);
		builder.setMessage(getString(R.string.calibrator_auto_config_setting_message, windowSize));

		final SeekBar sb = new SeekBar(this);
		sb.setMax(1500);
		sb.setProgress(windowSize);

		builder.setView(sb);

		builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				windowSize = sb.getProgress();
				startCalibrationCalculation();
			}
		});

		builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		final AlertDialog dialog = builder.create();

		final Context ctx = this;

		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				dialog.setMessage(ctx.getString(R.string.calibrator_auto_config_setting_message, progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

		});

		dialog.show();
	}

	/**
	 * 
	 */
	protected void startCalibrationCalculation() {
		// do the magic
		final ProgressDialog calibratingProgress = new ProgressDialog(this);
		calibratingProgress.setTitle(R.string.calibrator_auto_config_progress_title);
		calibratingProgress.setMessage(getString(R.string.calibrator_auto_config_progress_message));
		calibratingProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		calibratingProgress.setButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
				if (calibrateTask != null) {
					calibrateTask.cancel(true);
				}
			}
		});

		calibrateTask = new AutoCalibrateTask(this, calibratingProgress);

		calibratingProgress.show();
		calibrateTask.execute();
	}

	protected class AutoCalibrateTask extends AsyncTask<Void, Integer, Bundle> {

		protected CalibratorActivity parent;

		protected ProgressDialog progressDialog;

		protected boolean running = true;

		public AutoCalibrateTask(final CalibratorActivity calibrator, final ProgressDialog progress) {
			this.parent = calibrator;
			this.progressDialog = progress;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Bundle doInBackground(Void... paramArrayOfParams) {
			Bundle result = new Bundle();

			try {
				QueryBuilder<SensorData, Integer> accQuery = sensorDao.queryBuilder();
				accQuery.where().eq(SensorData.FIELD_TYPE, Sensor.TYPE_ACCELEROMETER);
				accQuery.orderBy(SensorData.FIELD_TIMESTAMP, true);
				List<SensorData> accelerometerValues = accQuery.query();

				QueryBuilder<SensorData, Integer> stepQuery = sensorDao.queryBuilder();
				stepQuery.where().eq(SensorData.FIELD_TYPE, STEP_TYPE);
				stepQuery.orderBy(SensorData.FIELD_TIMESTAMP, true);

				List<SensorData> steps = stepQuery.query();

				progressDialog
						.setMax((int) (((PEAK_MAX - PEAK_MIN) / PEAK_INTERVAL) * ((FILTER_MAX - FILTER_MIN) / FILTER_INTERVAL) ));
				progressDialog.setProgress(0);

				float bestFilter = 0f, bestPeak = 0f;
				int bestScore = Integer.MIN_VALUE, bestStepFound = 0, bestStepNotFound = 0, bestStepFalseFound = 0, bestTimeout=0;

				// long halfWindow = StepDetection.INTERVAL_MS * StepDetector.WINDOW / 2;

				// the user must tap on the screen in less than a half second
				long halfWindow = windowSize / 2;

				int allDetected = 1;

				int progress = 0;

				// calculate the best values for lowpass filter l and peak p
				// filter range 0.05 to 0.80 in 0.05 steps
				// peak range 0.2 to 3 in 0.05 steps

				if (accelerometerValues.size() > 0)

//					for (int t = TIMEOUT_MIN; running && t <= TIMEOUT_MAX; t += TIMEOUT_INTERVAL) 

						// cycle over peak values
						for (float p = PEAK_MIN; running && p <= PEAK_MAX; p += PEAK_INTERVAL) {

							// cycle over filter values
							for (float l = FILTER_MIN; running && l <= FILTER_MAX; l += FILTER_INTERVAL) {

								// Logger.d("searching for steps with peak p=" + p + " and filter l=" + l);

								int score = 0;
								StepDetector detector = new StepDetector(l, p, step_timeout_ms);
								detector.setLogSteps(false);
								Iterator<SensorData> stepIterator = steps.iterator();
								long stepTime = 0;

								if (stepIterator.hasNext())
									stepTime = stepIterator.next().getTimestamp();

								long lastTimer = 0;

								int stepFound = 0, stepNotFound = 0, stepFalseFound = 0;

								// cycle through accelerometerValues
								for (SensorData acc : accelerometerValues) {

									// add sensor values
									detector.addSensorValues(acc.getTimestamp(), new float[] { acc.getValue0(), acc.getValue1(), acc.getValue2() });

									// only all INTERVAL_MS
									if (acc.getTimestamp() > lastTimer + StepDetection.INTERVAL_MS) {
										lastTimer = acc.getTimestamp();

										// check if a step has been detected
										if (detector.checkForStep()) {

											// check if we have missed some steps:
											// are more steps saved?
											// is the current StepTime before the sensor value timesteamp minus half the window size
											while (stepIterator.hasNext() && stepTime < acc.getTimestamp() - halfWindow) {
												// Logger.d("step "+ stepTime +" has not been found, getting next one");
												stepTime = stepIterator.next().getTimestamp();
												stepNotFound++;
												score+=STEP_NOT_DETECTED_PUNISH;

											}

											// is there a step in the current database
											// is the step timer in the interval sensor timestamp - halfwindow and sensortime + halfwindow

											// Logger.d("stepTime="+stepTime+" acc="+acc.getTimestamp()+" diff="+(stepTime-acc.getTimestamp())+" matched: "+(Math.abs(stepTime - acc.getTimestamp()) <
											// halfWindow?"true":"false"));

											if (Math.abs(stepTime - acc.getTimestamp()) < halfWindow) {
												// that's fine, we found one

												// Logger.d("matched step");
												score+=STEP_DETECTED_REWARD;
												stepFound++;
												if (stepIterator.hasNext()) {
													stepTime = stepIterator.next().getTimestamp();
												} else {
													stepTime = 0;
												}
											} else {
												// no, there is none
												score+=STEP_FALSE_DETECTED_PUNISH;
												stepFalseFound++;
												// Logger.d("step not matched");
											}
										}

									}
								}
								// finished analyzing all sensor values

								// are there some steps missing?

								while (stepTime!=0) {
									// Logger.d("missed a step="+stepTime);
									// step missing, bad for the score
									if(stepIterator.hasNext())
										stepTime = stepIterator.next().getTimestamp();
									else
										stepTime=0;
									score+=STEP_NOT_DETECTED_PUNISH;
									stepNotFound++;

								}

								
								if (Logger.isVerboseEnabled())
									Logger.v((score > bestScore ? "better" : "worse") + " score found: " + score + (score > bestScore ? ">" : "<")
											+ bestScore + " : p=" + p + " l=" + l + " found=" + stepFound + " notFound=" + stepNotFound
											+ " falseFound=" + stepFalseFound);

								if (score == bestScore) {
									allDetected++;
								}

								// have we found a better score?
								if (score > bestScore || (score == bestScore && stepFound > bestStepFound)) {
									bestFilter = l;
									bestPeak = p;
									bestTimeout=step_timeout_ms;
									bestScore = score;
									bestStepFound = stepFound;
									bestStepNotFound = stepNotFound;
									bestStepFalseFound = stepFalseFound;
									allDetected = 1;
								}

								// progressDialog.setProgress((int)progressCur);
								this.publishProgress(++progress);
							}
						}
					

				Logger.i("Best score is: " + bestScore + " " + (((float) bestScore) / steps.size() * 100) + "% filter l=" + bestFilter + " peak p="
						+ bestPeak);
				result.putInt(BUNDLE_SCORE, bestScore);
				result.putFloat(BUNDLE_PCT, ((float) bestScore) / steps.size());
				result.putFloat(BUNDLE_FILTER, bestFilter);
				result.putFloat(BUNDLE_PEAK, bestPeak);
				result.putInt(BUNDLE_FOUND, bestStepFound);
				result.putInt(BUNDLE_NOTFOUND, bestStepNotFound);
				result.putInt(BUNDLE_FALSEFOUND, bestStepFalseFound);
				result.putInt(BUNDLE_ALLFOUND, allDetected);
				result.putInt(BUNDLE_TIMEOUT, bestTimeout);

			} catch (SQLException e) {
				Logger.e("could not access saved data", e);
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
			progressDialog.dismiss();
			if (running)
				parent.setCalibrationResult(result);
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			progressDialog.setProgress(values[0]);
		}

		// @Override
		// protected void onPostExecute(final Vector<AccessPointDrawable> result) {
		// progress.dismiss();
		// parent.setCalculatedAccessPoints(result);
		// }
	}

	/**
	 * @param result
	 */
	protected void setCalibrationResult(Bundle result) {
		setProgressValue(R.id.calibrator_sbFilter, result.getFloat(BUNDLE_FILTER, StepDetectionProvider.FILTER_DEFAULT));
		setProgressValue(R.id.calibrator_sbPeak, result.getFloat(BUNDLE_PEAK, StepDetectionProvider.FILTER_DEFAULT));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.calibrator_auto_config_title);
		builder.setMessage(getString(R.string.calibrator_auto_config_message, result.getInt(BUNDLE_SCORE, 0), result.getFloat(BUNDLE_PCT, 0) * 100,
				result.getFloat(BUNDLE_FILTER, StepDetectionProvider.FILTER_DEFAULT),
				result.getFloat(BUNDLE_PEAK, StepDetectionProvider.PEAK_DEFAULT), result.getInt(BUNDLE_FOUND, 0), result.getInt(BUNDLE_NOTFOUND, 0),
				result.getInt(BUNDLE_FALSEFOUND, 0), result.getInt(BUNDLE_ALLFOUND, 0),result.getInt(BUNDLE_TIMEOUT,0)));
		builder.setCancelable(false);
		builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				paramDialogInterface.dismiss();
			}

		});
		builder.create().show();
	}

}