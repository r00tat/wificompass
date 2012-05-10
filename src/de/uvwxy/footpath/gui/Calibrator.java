package de.uvwxy.footpath.gui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.userlocation.StepDetectionProvider;
import de.uvwxy.footpath.core.StepDetection;
import de.uvwxy.footpath.core.StepTrigger;

/**
 * This Activity is used to calibrate the parameters concerning step detection
 * @author  Paul Smith
 */
public class Calibrator extends Activity implements StepTrigger {

	/**
	 * @uml.property  name="stepDetection"
	 * @uml.associationEnd  
	 */
	private StepDetection stepDetection;

	/**
	 * @uml.property  name="svHistory"
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
			tvPeak.setText(getString(R.string.calibrator_peak_text, value));
			break;
		case R.id.calibrator_sbFilter:
			filter = value;
			stepDetection.setA(filter);
			sbFilter.setProgress((int) (value * 100));
			tvFilter.setText(getString(R.string.calibrator_filter_text, value));
			break;
		case R.id.calibrator_sbTimeout:
			step_timeout_ms = (int) value;
			stepDetection.setStep_timeout_ms(step_timeout_ms);
			tvTimeout.setText(getString(R.string.calibrator_step_timeout_text, (int) value));
			break;
		case R.id.calibrator_step_size:
			stepSize = value;

			((TextView) (findViewById(R.id.calibrator_tv_step_size))).setText(getString(R.string.calibrator_step_size_text, value));
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
	public void dataHookAcc(long nowMs, double x, double y, double z) {
	}

	@Override
	public void dataHookComp(long nowMs, double x, double y, double z) {
	}

	@Override
	public void timedDataHook(long nowMs, double[] acc, double[] comp) {
		svHistory.addTriple(nowMs, acc);
	}

	@Override
	public void trigger(long nowMs, double compDir) {
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
	
	protected void initLogic(){
		stepDetection = new StepDetection(this, this, filter, peak, step_timeout_ms);
		
		long samples_per_second = 1000 / stepDetection.INTERVAL_MS;
		int history_in_seconds = 4;
		int samples_per_history = (int) (history_in_seconds * samples_per_second);

		// create PaintBox (-24.0 to 24.0, 100 entries)
		svHistory = new PaintBoxHistory(this, 48.0, samples_per_history, history_in_seconds);

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
		if(svHistory.getParent()!=null && svHistory.getParent() instanceof ViewGroup){
			((ViewGroup)svHistory.getParent()).removeView(svHistory);
		}
		linLayout.addView(svHistory, lpHistory); // add surface view clone to layout

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
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		loadSettings();
		stepDetection.load();
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

}