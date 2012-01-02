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
import android.widget.ToggleButton;
import at.fhstp.wificompass.ApplicationContext;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.DatabaseHelper;
import at.fhstp.wificompass.model.SensorData;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.LineGraphView;

public class SensorsActivity extends Activity implements SensorEventListener, OnClickListener {

	protected static final Logger log = new Logger(SensorsActivity.class);

	protected SensorManager sensorManager;

	protected Sensor accelerometer, gyroscope;
	
	protected float[] gravity;

	protected float[] linear_acceleration;

	protected float[] rotation;

	protected final float alpha = 0.8f;

	protected DatabaseHelper databaseHelper = null;

	protected Dao<SensorData, String> sensorDataDao;

	protected boolean scanning = false;

	protected GraphView graphView =null;
	
	protected long startTime=0L,lastUpdate=0L;
	
	protected static final String FIELD_TYPE="sensorType",FIELD_NAME="sensorName",FIELD_TIMESTAMP="timestamp";
	
	protected static final int[] COLORS={Color.RED,Color.BLUE,Color.GREEN,Color.YELLOW,Color.CYAN,Color.MAGENTA,Color.WHITE};
	
	protected static final long UPDATE_INTERVAL=5000L;
	
	


	public SensorsActivity() throws SQLException {
		gravity = new float[3];
		linear_acceleration = new float[3];
		rotation = new float[3];
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
		setContentView(R.layout.sensors);

		// log.debug("registering sensor listener");
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		// sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

		ToggleButton toggle = ((ToggleButton) findViewById(R.id.sensors_scan_toggle));
		toggle.setOnClickListener(this);
		
				updateGraph();
		
	}
	
	protected void updateGraph(){
		lastUpdate=new Date().getTime();
		log.debug("updateing graph");
		
		try {
			
			if(graphView!=null){
				((LinearLayout) findViewById(R.id.sensors_layout)).removeView(graphView);
			}

			graphView = new LineGraphView(this, getString(R.string.sensors_graphview_title));
			
			graphView.setScrollable(true);  
			// optional - activate scaling / zooming  
			graphView.setScalable(true);  
			
			// optional - legend  
			graphView.setShowLegend(true);  
			graphView.setLegendAlign(LegendAlign.BOTTOM); 

			int length=0;
			
			List<SensorData> types = sensorDataDao.queryBuilder().selectColumns(FIELD_TYPE,FIELD_NAME).distinct().query();
			
			Iterator<SensorData> typeIt = types.iterator();
			for (int j=0; typeIt.hasNext();j++) {
				
				SensorData type=typeIt.next();

				List<SensorData> data = sensorDataDao.queryBuilder().orderBy(FIELD_TIMESTAMP, false).limit(500L).where().eq(FIELD_TYPE, type.getSensorType()).and().gt(FIELD_TIMESTAMP, startTime).query();
				
				if(data.size()>length)
					length=data.size();

				GraphViewData[] graphData = new GraphViewData[data.size()];
				Iterator<SensorData> it = data.iterator();
				for (int i = 0; it.hasNext(); i++) {
					SensorData element = it.next();
					graphData[i] = new GraphViewData(i, element.getValue0() + element.getValue1() + element.getValue2()
							+ element.getValue3());
				}

				
				graphView.addSeries(new GraphViewSeries(type.getSensorName(),COLORS[j%COLORS.length],graphData));

			}
			
			graphView.setViewPort(0, length);

			
			((LinearLayout) findViewById(R.id.sensors_layout)).addView(graphView);
		} catch (Throwable e) {
			log.error("could not create graph",e);
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
		
		

		 ToggleButton toggle=((ToggleButton)findViewById(R.id.sensors_scan_toggle));
		 toggle.setChecked(true);
	}

	protected void stopScan() {
		log.debug("unregistering sensor listener");
		sensorManager.unregisterListener(this);
		scanning = false;
		
		updateGraph();

		 ToggleButton toggle=((ToggleButton)findViewById(R.id.sensors_scan_toggle));
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
		ApplicationContext.setContext(this);
//		startScan();

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(startTime==0){
			startTime=event.timestamp;
			log.debug("set start time to "+startTime);
		}
		
			
		
		SensorData sd = new SensorData();
		sd.setSensorName(event.sensor.getName());
		sd.setSensorType(event.sensor.getType());
		sd.setTimestamp(event.timestamp);
		sd.setAccuracy(event.accuracy);

		if (event.values.length >= 1)
			sd.setValue0(event.values[0]);
		if (event.values.length >= 2)
			sd.setValue1(event.values[1]);
		if (event.values.length >= 3)
			sd.setValue2(event.values[2]);
		if (event.values.length >= 4)
			sd.setValue3(event.values[3]);

		// don't write to db yet, let the if type do something with the object

		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			log.debug("accelerometer sensor changed! "+sd.toString());

			// gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
			// gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
			// gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
			//
			// linear_acceleration[0] = event.values[0] - gravity[0];
			// linear_acceleration[1] = event.values[1] - gravity[1];
			// linear_acceleration[2] = event.values[2] - gravity[2];

			linear_acceleration[0] = event.values[0];
			linear_acceleration[1] = event.values[1];
			linear_acceleration[2] = event.values[2];

//			((TextView) findViewById(R.id.sensors_accelerometer_text)).setText(getString(R.string.sensors_accelerometer_format,
//					linear_acceleration[0], linear_acceleration[1], linear_acceleration[2]));

		} else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			log.debug("gyroscope sensor changed "+sd.toString());
			rotation[0] = event.values[0];
			rotation[1] = event.values[1];
			rotation[2] = event.values[2];

//			((TextView) findViewById(R.id.sensors_gyroscope_text)).setText(getString(R.string.sensors_gyroscope_format, rotation[0], rotation[1],
//					rotation[2]));

		} else {
			log.debug("sensor unkown");
		}

		try {
			sensorDataDao.createIfNotExists(sd);
		} catch (SQLException e) {
			log.error("could not save sensor data to database", e);
		}
		
		if(new Date().getTime()>lastUpdate+UPDATE_INTERVAL){
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

}
