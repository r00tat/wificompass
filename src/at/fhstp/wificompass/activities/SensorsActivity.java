/*
 * Created on Dec 29, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.activities;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.SensorData;
import at.fhstp.wificompass.model.helper.DatabaseHelper;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.LineGraphView;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public class SensorsActivity extends Activity implements SensorEventListener, OnClickListener, OnSeekBarChangeListener {

	/**
	 * @uml.property  name="log"
	 * @uml.associationEnd  
	 */
	protected static final Logger log = new Logger(SensorsActivity.class);

	protected SensorManager sensorManager;

	protected Sensor accelerometer;

	protected Sensor gyroscope;

//	protected float[] gravity;
//
//	protected float[] linear_acceleration;
//
//	protected float[] rotation;

	protected final float alpha = 0.8f;

	/**
	 * @uml.property  name="databaseHelper"
	 * @uml.associationEnd  
	 */
	protected DatabaseHelper databaseHelper = null;

	protected Dao<SensorData, Integer> sensorDataDao;

	protected boolean scanning = false;

	protected GraphView graphView = null;

	protected long startTime = 0L;

	protected long lastUpdate = 0L;

	
	protected static final int[] COLORS = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.WHITE };

	protected int updateInterval = 5000;

	protected int valueLimit=500;
	
	protected static final float LEGEND_WITH=200f;
	

	public SensorsActivity() throws SQLException {
//		gravity = new float[3];
//		linear_acceleration = new float[3];
//		rotation = new float[3];
		log.debug("creating dao");
		databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		try {
			sensorDataDao = databaseHelper.getDao(SensorData.class);
		} catch (SQLException e) {
			log.wtf("could not get sensord data dao!", e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log.debug("created sensors activity");

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		initUI();

	}
	
	protected void initUI(){
		setContentView(R.layout.sensors);

		ToggleButton toggle = ((ToggleButton) findViewById(R.id.sensors_scan_toggle));
		toggle.setOnClickListener(this);
		toggle.setChecked(scanning);
		
		SeekBar updateSeekbar=((SeekBar)findViewById(R.id.sensors_update_seekbar));
		updateSeekbar.setMax(60);
		updateSeekbar.setKeyProgressIncrement(100);
		updateSeekbar.setOnSeekBarChangeListener(this);
		updateSeekbar.setProgress(updateInterval/1000);
		
		
		SeekBar valueSeekbar=((SeekBar)findViewById(R.id.sensors_valuecount_seekbar));
		valueSeekbar.setMax(2000);
		valueSeekbar.setKeyProgressIncrement(100);
		valueSeekbar.setOnSeekBarChangeListener(this);
		valueSeekbar.setProgress(valueLimit);
		
		
		((TextView)findViewById(R.id.sensors_update_label)).setText(getString(R.string.sensors_update_label,updateInterval/1000));
		((TextView)findViewById(R.id.sensors_valuecount_label)).setText(getString(R.string.sensors_valuecount_label,valueLimit));

		updateGraph();
	}

	protected void updateGraph() {
		lastUpdate = new Date().getTime();
		log.debug("updateing graph");

		try {

			if (graphView != null) {
				((LinearLayout) findViewById(R.id.sensors_layout)).removeView(graphView);
			}

			graphView = new LineGraphView(this, getString(R.string.sensors_graphview_title));

			int length = 0;

			List<SensorData> types = sensorDataDao.queryBuilder().selectColumns(SensorData.FIELD_TYPE, SensorData.FIELD_NAME).distinct().query();
			
						Iterator<SensorData> typeIt = types.iterator();
			for (int j = 0; typeIt.hasNext(); j++) {

				SensorData type = typeIt.next();

				List<SensorData> data = sensorDataDao.queryBuilder().orderBy(SensorData.FIELD_TIMESTAMP, false).limit((long)valueLimit).where()
						.eq(SensorData.FIELD_TYPE, type.getSensorType()).query();

				if (data.size() > length)
					length = data.size();

				GraphViewData[] graphData = new GraphViewData[data.size()];
				Iterator<SensorData> it = data.iterator();
				for (int i = 0; it.hasNext(); i++) {
					SensorData element = it.next();
					graphData[i]=new GraphViewData(i, element.getNormalizedValue());
				}

				graphView.addSeries(new GraphViewSeries(type.getSensorName(), COLORS[j % COLORS.length], graphData));

			}

			if (length > 0 && types.size() > 0) {
				graphView.setViewPort(0, length);
				graphView.setScrollable(true);
				// optional - activate scaling / zooming
				graphView.setScalable(true);

				// optional - legend
				graphView.setShowLegend(true);
				graphView.setLegendAlign(LegendAlign.BOTTOM);
				graphView.setLegendWidth(LEGEND_WITH);
				
				((LinearLayout) findViewById(R.id.sensors_layout)).addView(graphView);
			}else {
				graphView=null;
			}
			
			
		} catch (Throwable e) {
			log.error("could not create graph", e);
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
	}

	protected void startScan() {
		log.debug("registering sensor listener");
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
		scanning = true;

		ToggleButton toggle = ((ToggleButton) findViewById(R.id.sensors_scan_toggle));
		toggle.setChecked(true);
	}

	protected void stopScan() {
		log.debug("unregistering sensor listener");
		sensorManager.unregisterListener(this);
		scanning = false;

		updateGraph();

		ToggleButton toggle = ((ToggleButton) findViewById(R.id.sensors_scan_toggle));
		toggle.setChecked(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		stopScan();
	}

	@Override
	protected void onResume() {
		super.onResume();
		log.debug("setting context");
		
		// startScan();

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (startTime == 0) {
			startTime = event.timestamp;
			log.debug("set start time to " + startTime);
		}

		SensorData sd = new SensorData(event);
		// don't write to db yet, let the if type do something with the object
		
		log.debug("Sensor "+event.sensor.getName()+" changed");

		try {
			sensorDataDao.createIfNotExists(sd);
		} catch (SQLException e) {
			log.error("could not save sensor data to database", e);
		}

		if (new Date().getTime() > lastUpdate + updateInterval) {
			updateGraph();
		}

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

	@Override
	protected void finalize() throws Throwable {
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
		super.finalize();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sensors_scan_toggle:
			log.debug("toggle button");
			if (scanning)
				stopScan();
			else
				startScan();
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//		log.debug("seekbar "+seekBar.getId()+" changed to "+progress);
		switch(seekBar.getId()){
		case R.id.sensors_update_seekbar: 
			updateInterval=progress*1000;
			if(updateInterval<1000) updateInterval=1000;
			((TextView)findViewById(R.id.sensors_update_label)).setText(getString(R.string.sensors_update_label,updateInterval/1000));
			break;
		case R.id.sensors_valuecount_seekbar:
			valueLimit=progress;
			if(valueLimit<10) valueLimit=10;
			((TextView)findViewById(R.id.sensors_valuecount_label)).setText(getString(R.string.sensors_valuecount_label,valueLimit));
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

}
