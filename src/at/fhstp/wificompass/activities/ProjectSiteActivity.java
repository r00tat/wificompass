/*
 * Created on Dec 23, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.activities;

import java.sql.SQLException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import at.fhstp.wificompass.ApplicationContext;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.exceptions.SiteNotFoundException;
import at.fhstp.wificompass.model.ProjectSite;
import at.fhstp.wificompass.model.helper.DatabaseHelper;
import at.fhstp.wificompass.view.AccessPointDrawable;
import at.fhstp.wificompass.view.MeasuringPointDrawable;
import at.fhstp.wificompass.view.MultiTouchView;
import at.fhstp.wificompass.view.SiteMap;
import at.fhstp.wificompass.view.UserDrawable;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class ProjectSiteActivity extends Activity implements OnClickListener {

	protected Logger log = new Logger(ProjectSiteActivity.class);

	public static final String SITE_KEY = "SITE", 
			PROJECT_KEY = "PROJECT";

//	public static final int START_NEW = 1, START_LOAD = 2;

	protected static final int DIALOG_TITLE=1;
	
	protected MultiTouchView multiTouchView;

	protected SiteMap map;
	
	protected ProjectSite site;
	
	protected DatabaseHelper databaseHelper = null;
	
	protected Dao<ProjectSite, Integer> projectSiteDao = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project_site);
		Intent intent = this.getIntent();
		
		int siteId=intent.getExtras().getInt(SITE_KEY, -1);
		if(siteId==-1){	
			throw new SiteNotFoundException("ProjectSiteActivity called without a correct site ID!");
		}
		
		databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		projectSiteDao = databaseHelper.getDao(ProjectSite.class);
		site=projectSiteDao.queryForId(siteId);
		
		if(site==null){
			throw new SiteNotFoundException("The ProjectSite Id could not be found in the database!");
		}
		
		
		Button resetZoom = ((Button) findViewById(R.id.project_site_reset_zoom_button));
		resetZoom.setOnClickListener(this);

		Button resetXY = ((Button) findViewById(R.id.project_site_reset_pos_button));
		resetXY.setOnClickListener(this);

		multiTouchView = ((MultiTouchView) findViewById(R.id.project_site_resultview));
		multiTouchView.setRearrangable(false);
		map = new SiteMap(this);

		AccessPointDrawable icon1 = new AccessPointDrawable(this, map);
		icon1.setRelativePosition(134, 57);
		AccessPointDrawable icon2 = new AccessPointDrawable(this, map);
		icon2.setRelativePosition(199, 301);
		AccessPointDrawable icon3 = new AccessPointDrawable(this, map);
		icon3.setRelativePosition(541, 332);
		AccessPointDrawable icon4 = new AccessPointDrawable(this, map);
		icon4.setRelativePosition(52, 81);
		AccessPointDrawable icon5 = new AccessPointDrawable(this, map);
		icon5.setRelativePosition(423, 214);
		
		UserDrawable user = new UserDrawable(this, map);
		user.setRelativePosition(320, 240);
		
		MeasuringPointDrawable point = new MeasuringPointDrawable(this, map);
		point.setRelativePosition(423, 293);
		
		multiTouchView.addDrawable(map);
//		multiTouchView.addDrawable(icon1);
//		multiTouchView.addDrawable(icon2);
//		multiTouchView.addDrawable(icon3);
//		multiTouchView.addDrawable(icon4);
//		multiTouchView.addDrawable(icon5);
		
		
		if(site.getTitle().equals(ProjectSite.UNTITLED)){
			showDialog(DIALOG_TITLE);
		}

		
		}catch(Exception ex){
			log.error("Failed to create ProjectSiteActivity: "+ex.getMessage(),ex);
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
		case R.id.project_site_reset_zoom_button:
			Logger.d("resetting Zoom");
			multiTouchView.resetAllScale();
			break;

		case R.id.project_site_reset_pos_button:
			Logger.d("resetting position");
			multiTouchView.resetAllXY();
			break;

		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DIALOG_TITLE:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle(R.string.project_site_dialog_title_title);
			alert.setMessage(R.string.project_site_dialog_title_message);

			// Set an EditText view to get user input
			final EditText input = new EditText(this);
			input.setSingleLine(true);
			alert.setView(input);

			alert.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					setSiteTitle(input.getText().toString());
					
				}
			});

			alert.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			return alert.create();
			
		default:
			return super.onCreateDialog(id);
		}
	}
	
	protected void setSiteTitle(String title){
		site.setTitle(title);
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
	
	protected void saveProjectSite(){
		log.debug("saveing project site");
		
		try {
			int changed=projectSiteDao.update(site);
			
			if(changed>0){
				Toast.makeText(this, R.string.project_site_saved, Toast.LENGTH_SHORT).show();
			}
			
			projectSiteDao.refresh(site);
		} catch (SQLException e) {
			log.error("could not save or refresh project site", e);
			Toast.makeText(this, R.string.project_site_save_failed, Toast.LENGTH_LONG).show();
		}
		
	}

	
}
