/*
 * Created on Dec 5, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.activities;

import java.sql.SQLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import at.fhstp.wificompass.ApplicationContext;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.DatabaseHelper;
import at.fhstp.wificompass.model.Project;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class ProjectActivity extends Activity implements OnClickListener {

	protected static final int REQ_SAVE = 1, REQ_LOAD = 2;

	public static final String START_MODE = "START_MODE", PROJ_KEY = "PROJECT";

	public static final int START_NEW = 1, START_LOAD = 2;

	protected Project project;

	protected DatabaseHelper databaseHelper = null;

	protected static Logger log = new Logger(ProjectActivity.class);

	protected Dao<Project, String> dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project);
		ApplicationContext.setContext(this);

		try {
			dao = getHelper().getDao(Project.class);
		} catch (SQLException e1) {
			log.wtf("could not create dao!", e1);
			Toast.makeText(this, R.string.database_failure, Toast.LENGTH_LONG).show();
			finish();
		}

		Intent i = this.getIntent();
		if (i != null) {
			if (i.getExtras().getInt(START_MODE) == START_LOAD) {
				// Intent fbi = new Intent(this, FileBrowser.class);
				// fbi.putExtra(FileBrowser.EXTRA_MODE, FileBrowser.MODE_LOAD);
				// startActivityForResult(fbi, REQ_LOAD);
				try {
					Dao<Project, String> dao = databaseHelper.getDao(Project.class);
					log.debug("Searching for project: " + i.getExtras().getInt(PROJ_KEY));
					project = dao.queryForId(""+i.getExtras().getInt(PROJ_KEY));

				} catch (SQLException e) {
					log.error("could not find project", e);
					project = null;
				}

				if (project == null) {
					log.error("empty project");
					Toast.makeText(this, R.string.project_not_found, Toast.LENGTH_LONG).show();
				}
			}
		}

		if (project == null) {
			project = new Project();
		} else {
			((EditText) findViewById(R.id.project_title)).setText(project.getName());
			((EditText) findViewById(R.id.project_description)).setText(project.getDescription());
		}

		// ((Button) findViewById(R.id.project_path_button)).setOnClickListener(this);
		((Button) findViewById(R.id.project_save)).setOnClickListener(this);
		((Button) findViewById(R.id.project_delete)).setOnClickListener(this);
		((Button) findViewById(R.id.project_new_location_button)).setOnClickListener(this);

		// ((EditText) findViewById(R.id.project_title)).requestFocus();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ApplicationContext.setContext(this);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			// case REQ_SAVE:
			// String savePath = data.getStringExtra(FileBrowser.EXTRA_PATH);
			// ((TextView) findViewById(R.id.project_path_text)).setText(savePath);
			// break;
			// case REQ_LOAD:
			// String loadPath = data.getStringExtra(FileBrowser.EXTRA_PATH);
			// ((TextView) findViewById(R.id.project_path_text)).setText(loadPath);
			//
			// break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		log.debug("clicked.");
		switch (v.getId()) {
		// case R.id.project_path_button:
		// Intent fbi = new Intent(this, FileBrowser.class);
		// fbi.putExtra(FileBrowser.EXTRA_MODE, FileBrowser.MODE_SAVE);
		// startActivityForResult(fbi, REQ_LOAD);
		// break;
		case R.id.project_save:
			log.debug("saving project");
			project.setName(((EditText) findViewById(R.id.project_title)).getText().toString());
			project.setDescription(((EditText) findViewById(R.id.project_description)).getText().toString());
			try {

				dao.createOrUpdate(project);
				Toast.makeText(this, R.string.project_saved, Toast.LENGTH_SHORT).show();
			} catch (SQLException e) {
				log.error("could not save project", e);
				Toast.makeText(this, R.string.project_save_failed, Toast.LENGTH_LONG).show();
			}
			break;
			
		case R.id.project_delete:
			log.debug("Delete project");
			
			try {
				dao.delete(project);
				Toast.makeText(this, R.string.project_delete_success, Toast.LENGTH_LONG).show();
				finish();
			} catch (SQLException e) {
				log.error("could not delete project", e);
				Toast.makeText(this, R.string.project_delete_failed, Toast.LENGTH_LONG).show();
			}
			
			break;
			
		case R.id.project_new_location_button:
			log.debug("new project button");
			
			break;
			
		default:
			log.wtf("clicked but not catched??" + v.getId());
			break;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	protected DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return databaseHelper;
	}

	@Override
	protected void onResume() {
		super.onResume();
		log.debug("setting context");
		ApplicationContext.setContext(this);
	}
}
