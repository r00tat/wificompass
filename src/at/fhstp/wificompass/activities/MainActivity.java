package at.fhstp.wificompass.activities;

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
import android.widget.Button;
import at.fhstp.wificompass.ApplicationContext;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;

public class MainActivity extends Activity implements OnClickListener {

	protected boolean running;

	protected static final String logTag = "APLocActivity";
	
	protected static final Logger log=new Logger(logTag);

	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log.debug( "MainActivity onCreate");

		init();
	}

	protected void init() {
		log.debug( "init");

		ApplicationContext.setContext(getApplicationContext());

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
		((Button) findViewById(R.id.load_project_button)).setOnClickListener(this);
		((Button) findViewById(R.id.sample_scan_button)).setOnClickListener(this);
		Button about_button = ((Button) findViewById(R.id.aboutButton));
		if (about_button != null) {
			about_button.setOnClickListener(this);
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
		case R.id.load_project_button:
			log.debug( "load project");
			Intent lpi = new Intent(this, ProjectActivity.class);
			lpi.putExtra(ProjectActivity.START_MODE, ProjectActivity.START_LOAD);
			startActivity(lpi);
			
			break;
		case R.id.sample_scan_button:
			log.debug( "starting sample scan activity");
			Intent i = new Intent(this, SampleScanActivity.class);
			startActivity(i);
			break;
		case R.id.aboutButton:
			log.debug( "show About");
			Intent aboutIntent = new Intent(this, AboutActivity.class);
			startActivity(aboutIntent);
			break;
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

			return false;
		case R.id.quitOption:
			log.debug( "quitting app");

			finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	

}