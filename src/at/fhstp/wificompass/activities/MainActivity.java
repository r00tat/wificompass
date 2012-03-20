package at.fhstp.wificompass.activities;

import java.sql.SQLException;

import de.uvwxy.footpath.gui.Calibrator;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.helper.DatabaseHelper;
import at.fhstp.wificompass.model.helper.ProjectListAdapter;

public class MainActivity extends Activity implements OnClickListener, OnItemClickListener {

	protected boolean running;

	protected static final String logTag = "MainActivity";
	
	protected static final Logger log=new Logger(logTag);
	
	protected static final int REQ_PROJECT_LIST=3;
	
	protected DatabaseHelper databaseHelper = null;

	
	protected ProjectListAdapter adapter;
	

	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.setLogLevelFromPreferences(this);
//		log.debug( "MainActivity onCreate");
//		Logger.i("TEST!");

		init();
	}

	protected void init() {
		log.debug( "init");

		

		setContentView(R.layout.main);

		/* First, get the Display from the WindowManager */
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

		/* Now we can retrieve all display-related infos */
		int width = display.getWidth();
		int height = display.getHeight();
		int orientation = display.getOrientation();

		log.debug( "display: " + width + "x" + height + " orientation:" + orientation);

		running = false;
		((Button) findViewById(R.id.new_project_button)).setOnClickListener(this);
//		((Button) findViewById(R.id.load_project_button)).setOnClickListener(this);
//		((Button) findViewById(R.id.sample_scan_button)).setOnClickListener(this);
//		Button about_button = ((Button) findViewById(R.id.aboutButton));
//		if (about_button != null) {
//			about_button.setOnClickListener(this);
//		}
		
		ListView project_list=((ListView)findViewById(R.id.main_project_list));
		try {
			adapter=new ProjectListAdapter(this);
			project_list.setAdapter(adapter);
			project_list.setOnItemClickListener(this);
			
		} catch (SQLException e) {
			log.error("could not load project list", e);
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_project_button:
			log.debug( "new project");
			Intent npi = new Intent(this, ProjectActivity.class);
			npi.putExtra(ProjectActivity.START_MODE, ProjectActivity.START_NEW);
			startActivity(npi);
			break;
//		case R.id.load_project_button:
//			log.debug( "load project");
//			Intent lpi = new Intent(this, ProjectListActivity.class);
//			
//			startActivityForResult(lpi, REQ_PROJECT_LIST);
//			
//			break;
//		case R.id.sample_scan_button:
//			log.debug( "starting sample scan activity");
//			Intent i = new Intent(this, SampleScanActivity.class);
//			startActivity(i);
//			break;
//		case R.id.aboutButton:
//			log.debug( "show About");
//			Intent aboutIntent = new Intent(this, AboutActivity.class);
//			startActivity(aboutIntent);
//			break;
		default:
			log.warn("could not identify sender = " + v.getId());
			break;
		}

	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// startActivity(getIntent());
		// finish();
		log.debug( "Config changed " + newConfig.toString());
		/* First, get the Display from the WindowManager */
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.aboutOption:
			log.debug( "show About");
			Intent i = new Intent(this, AboutActivity.class);
			startActivity(i);

			break;
//		case R.id.quitOption:
//			log.debug( "quitting app");
//
//			finish();
//
//			return true;
//			
		case R.id.main_new_project_option:
			log.debug( "new project");
			Intent npi = new Intent(this, ProjectActivity.class);
			npi.putExtra(ProjectActivity.START_MODE, ProjectActivity.START_NEW);
			startActivity(npi);
			break;
			
		case R.id.main_wifi_scan:
			log.debug( "starting sample scan activity");
			Intent scanIntent = new Intent(this, SampleScanActivity.class);
			startActivity(scanIntent);
			
			break;
			
		case R.id.main_sensors_option:
			log.debug("starting sensors test activity");
			Intent sensorsIntent=new Intent(this,SensorsActivity.class);
			startActivity(sensorsIntent);
			
			break;
			
			
		case R.id.main_export_option:
			log.debug("starting export db activity");
			Intent exportIntent=new Intent(this,DBActivity.class);
			startActivity(exportIntent);
			
			
			break;
			
		case R.id.main_settings_option:
			log.debug("starting preferences activity");
			Intent prefsIntent=new Intent(this,PreferencesActivity.class);
			startActivity(prefsIntent);
			break;
			
		case R.id.main_menu_step_calibrate:
			log.debug("calibrating sensors");
			Intent calibrateIntent=new Intent(this,Calibrator.class);
			startActivity(calibrateIntent);
			break;
			
		default:
			log.debug("could not identify sender: "+item.getItemId());
			return super.onOptionsItemSelected(item);
		}
		
		return false;

	}

	@Override
	protected void onResume() {
		super.onResume();
		log.debug("setting context");
		
		
		
		log.debug("refreshing project list");
		// TODO is there a better way?
		ListView project_list=((ListView)findViewById(R.id.main_project_list));
		try {
			adapter=new ProjectListAdapter(this);
			project_list.setAdapter(adapter);
			project_list.setOnItemClickListener(this);
			
		} catch (SQLException e) {
			log.error("could not load project list", e);
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		

		if (resultCode == Activity.RESULT_OK&&requestCode==REQ_PROJECT_LIST) {
			int project=data.getExtras().getInt(ProjectListActivity.PROJ_KEY);
			Intent projectIntent=new Intent(this,ProjectActivity.class);
			projectIntent.putExtra(ProjectActivity.PROJ_KEY, project);
			projectIntent.putExtra(ProjectActivity.START_MODE, ProjectActivity.REQ_LOAD);
			startActivity(projectIntent);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent projectIntent=new Intent(this,ProjectActivity.class);
		projectIntent.putExtra(ProjectActivity.PROJ_KEY, (int)id);
		projectIntent.putExtra(ProjectActivity.START_MODE, ProjectActivity.REQ_LOAD);
		startActivity(projectIntent);
		
	}

}