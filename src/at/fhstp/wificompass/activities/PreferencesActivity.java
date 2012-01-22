/*
 * Created on Jan 22, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;

public class PreferencesActivity extends Activity implements OnItemSelectedListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
	}
	
	protected void initUI(){
		setContentView(R.layout.preferences);
		Spinner levels=(Spinner) findViewById(R.id.preferences_loglevel);
		 ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.preferences_loglevels, android.R.layout.simple_spinner_item);
		 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 levels.setAdapter(adapter);
		 levels.setSelection(Logger.getLogLevel()-2);
		 levels.setOnItemSelectedListener(this);
		 
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initUI();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent,View view, int pos, long id) {
		switch(parent.getId()){
		case R.id.preferences_loglevel:
			//VERBOSE = 2, DEBUG=3 aso.
			Logger.d("set log level to "+(pos+2));
			Logger.setLogLevel(pos+2);
			Logger.d("saved log level "+(Logger.saveLogLevel(this)?"OK":"NOT OK"));
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> paramAdapterView) {
	}
	

}
