/*
 * Created on Dec 5, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import at.fhstp.wificompass.R;
import at.woelfel.philip.filebrowser.FileBrowser;

public class ProjectActivity extends Activity implements OnClickListener {
	
	protected static final int REQ_SAVE = 1, REQ_LOAD = 2;
	
	public static final String START_MODE="START_MODE";
	public static final int START_NEW=1,START_LOAD=2;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project);
		
		((Button) findViewById(R.id.project_path_button)).setOnClickListener(this);
		((Button) findViewById(R.id.project_new_location_button)).setOnClickListener(this);
		
		Intent i=this.getIntent();
		if(i!=null){
			if(i.getExtras().getInt(START_MODE)==START_LOAD){
				Intent fbi=new Intent(this,FileBrowser.class);
				fbi.putExtra(FileBrowser.EXTRA_MODE, FileBrowser.MODE_LOAD);
				startActivityForResult(fbi, REQ_LOAD);
			}
		}
		// we didn't start load activity, so it's a new project
		((EditText) findViewById(R.id.project_title)).requestFocus();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQ_SAVE:
				String savePath=data.getStringExtra(FileBrowser.EXTRA_PATH);
				((TextView) findViewById(R.id.project_path_text)).setText(savePath);
				break;
			case REQ_LOAD:
				String loadPath=data.getStringExtra(FileBrowser.EXTRA_PATH);
				((TextView) findViewById(R.id.project_path_text)).setText(loadPath);
				
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.project_path_button:
			Intent fbi=new Intent(this,FileBrowser.class);
			fbi.putExtra(FileBrowser.EXTRA_MODE, FileBrowser.MODE_SAVE);
			startActivityForResult(fbi, REQ_LOAD);
			break;
		case R.id.project_new_location_button:
			
		}
		
	}

}
