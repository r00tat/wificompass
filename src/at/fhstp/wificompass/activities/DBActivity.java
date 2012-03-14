/*
 * Created on Dec 29, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import at.fhstp.wificompass.ApplicationContext;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.AccessPoint;
import at.fhstp.wificompass.model.BssidResult;
import at.fhstp.wificompass.model.Location;
import at.fhstp.wificompass.model.Project;
import at.fhstp.wificompass.model.ProjectSite;
import at.fhstp.wificompass.model.WifiScanResult;
import at.fhstp.wificompass.model.helper.DatabaseHelper;
import at.woelfel.philip.filebrowser.FileBrowser;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class DBActivity extends Activity implements OnClickListener {

	protected static final Logger log = new Logger(SensorsActivity.class);

	protected static final int FILEBROWSER_REQUEST = 12345, FILEBROWSER_IMPORT = 12346,REFRESH=123;

	protected DatabaseHelper databaseHelper = null;

	protected TextView statusView = null;

	protected Thread importThread = null;

	protected StringBuffer statusMessages = null;
	
	protected Handler hRefresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		log.debug("created sensors activity");
		setContentView(R.layout.export_db);

		databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		((Button) findViewById(R.id.export_db_button)).setOnClickListener(this);
		((Button) findViewById(R.id.export_db_drop_button)).setOnClickListener(this);
		((Button) findViewById(R.id.export_db_import_button)).setOnClickListener(this);

		statusView = ((TextView) findViewById(R.id.export_db_message));

		statusMessages = new StringBuffer();
		
		hRefresh = new Handler(){
			@Override
			public void handleMessage(Message msg) {
			switch(msg.what){
			     case REFRESH:
			            /*Refresh UI*/
			            updateStatusView();
			            break;
			   }
			}
			};
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		log.debug("setting context");
		ApplicationContext.setContext(this);

	}

	@Override
	public void onClick(View v) {
		final TextView tv = ((TextView) findViewById(R.id.export_db_message));

		switch (v.getId()) {
		case R.id.export_db_button:
			Intent i = new Intent(this, FileBrowser.class);
			i.putExtra(FileBrowser.EXTRA_MODE, FileBrowser.MODE_SAVE);
			startActivityForResult(i, FILEBROWSER_REQUEST);
			break;

		case R.id.export_db_import_button:

			Intent importIntent = new Intent(this, FileBrowser.class);
			importIntent.putExtra(FileBrowser.EXTRA_MODE, FileBrowser.MODE_LOAD);
			startActivityForResult(importIntent, FILEBROWSER_IMPORT);

			// tv.setText(R.string.not_implemented);

			break;

		case R.id.export_db_drop_button:

			// we should first ask the user, if he's sure
			try {
				databaseHelper.recreateDatabase();
				tv.setText(R.string.export_db_drop_finished);
			} catch (SQLException e) {
				log.error("could not recreate database", e);
				tv.setText(getString(R.string.export_db_drop_failed, e.getMessage()));
			}

			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ApplicationContext.setContext(this);

		switch (requestCode) {
		case FILEBROWSER_REQUEST:

			if (resultCode == Activity.RESULT_OK && data != null) {

				String path = data.getExtras().getString(FileBrowser.EXTRA_PATH);
				TextView tv = ((TextView) findViewById(R.id.export_db_message));
				tv.setText(getString(R.string.export_db_save_start_message, path));

				File dbFile = new File(Environment.getDataDirectory() + "/data/" + getString(R.string.app_package) + "/databases/"
						+ DatabaseHelper.DATABASE_NAME);
				File backup = new File(path);

				if (backup.exists() && backup.isFile()) {
					// TODO: show dialog to overerwrite or not
				}

				FileChannel inChannel = null, outChannel = null;
				try {
					backup.createNewFile();
					inChannel = new FileInputStream(dbFile).getChannel();
					outChannel = new FileOutputStream(backup).getChannel();
					inChannel.transferTo(0, inChannel.size(), outChannel);
					tv.append(getString(R.string.export_db_save_finished_message));

				} catch (IOException e) {
					log.error("could not create backup", e);
					tv.append(getString(R.string.export_db_save_failed_message, e.getMessage()));

				} finally {
					if (inChannel != null)
						try {
							inChannel.close();
						} catch (IOException e) {
							log.error("could not close database input channel", e);
						}
					if (outChannel != null)
						try {
							outChannel.close();
						} catch (IOException e) {
							log.error("could not close database output channel", e);
						}
				}
			}
			break;
		case FILEBROWSER_IMPORT:
			if (resultCode == Activity.RESULT_OK && data != null) {
				TextView tv = ((TextView) findViewById(R.id.export_db_message));
				tv.setText(R.string.export_db_starting_import);
				try {
					String path = data.getExtras().getString(FileBrowser.EXTRA_PATH);
					
					if(!path.endsWith(".wcdb")){
						Toast.makeText(this, getString(R.string.export_db_wrong_file,path), Toast.LENGTH_LONG).show();
					}
					else
					importDataFromFile(path);

				} catch (Exception e) {
					Logger.e("could not import database", e);
					printStatusLine(getString(R.string.export_db_import_failed, e.getMessage()));
				}
			}

			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	protected void importDataFromFile(String path) throws SQLException {

		final String importPath = path;
		final Context context = this;
		
		final ProgressDialog progress=new ProgressDialog(this);
		progress.setTitle(R.string.export_db_progress_title);
		progress.setMessage(getString(R.string.export_db_progress_message));
		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progress.setButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
				if(importThread!=null){
					importThread.interrupt();
					printStatusLine(getString(R.string.export_db_import_interrupted));
					hRefresh.sendEmptyMessage(REFRESH);
				}
			}
		});
//		progress.setMax(max)

		importThread = new Thread() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				try {
					// FIXME: Android deletes files if this is not a correct database, VERY BAD!!!

//					SQLiteDatabase importDB = SQLiteDatabase.openDatabase(importPath, null, SQLiteDatabase.OPEN_READONLY|SQLiteDatabase.CONFLICT_FAIL);
//					AndroidConnectionSource importSource = new AndroidConnectionSource(importDB);

					// TODO: hmm, we should check, which databaseVersion is the import database, or we might have an issue
					
					// a try: use databaseHelper
					DatabaseHelper importSource=new DatabaseHelper(context,importPath);

//					Dao<Project, Integer> importProjectDao = DaoManager.createDao(importSource, Project.class);
					Dao<Project, Integer> importProjectDao = importSource.getDao(Project.class);
					Dao<Project, Integer> targetProjectDao = databaseHelper.getDao(Project.class);
					Dao<Location, Integer> targetLocationDao = databaseHelper.getDao(Location.class);
					Dao<ProjectSite, Integer> targetSiteDao = databaseHelper.getDao(ProjectSite.class);
					Dao<AccessPoint, Integer> targetAPDao = databaseHelper.getDao(AccessPoint.class);
					Dao<WifiScanResult, Integer> targetScanResultDao = databaseHelper.getDao(WifiScanResult.class);
					Dao<BssidResult, Integer> targetBssidDao = databaseHelper.getDao(BssidResult.class);
					
					progress.setMax((int) importProjectDao.countOf());
					progress.setProgress(0);

					int projectCount=0;
					// loop through projects
					for (Project importProject : importProjectDao) {
						

						Project targetProject = new Project(importProject);

						printStatusLine(getString(R.string.export_db_importing, importProject.toString()));

						// create the project
						targetProjectDao.create(targetProject);

						// loop through sites
						for (ProjectSite importSite : importProject.getSites()) {
							printStatusLine(getString(R.string.export_db_importing, importSite.toString()));

							ProjectSite targetSite = new ProjectSite(importSite);

							// create last known location
							if (targetSite.getLastLocation() != null) {
								targetLocationDao.create(targetSite.getLastLocation());
							}

							// make sure, we've got the right references
							targetSite.setProject(targetProject);

							// create site
							targetSiteDao.create(targetSite);

							for (AccessPoint importAP : importSite.getAccessPoints()) {
								printStatusLine(getString(R.string.export_db_importing, importAP.toString()));
								AccessPoint targetAP = new AccessPoint(importAP);

								if (targetAP.getLocation() != null) {
									targetLocationDao.create(targetAP.getLocation());
								}

								// make sure, we've got the right references
								targetAP.setProjectSite(targetSite);

								// create ap
								targetAPDao.create(targetAP);
							}

							for (WifiScanResult importScanResult : importSite.getScanResults()) {
								printStatusLine(getString(R.string.export_db_importing, importScanResult.toString()));
								WifiScanResult targetScanResult = new WifiScanResult(importScanResult);

								// create location
								if (targetScanResult.getLocation() != null) {
									targetLocationDao.create(targetScanResult.getLocation());
								}

								// set reference
								targetScanResult.setProjectLocation(targetSite);

								// create
								targetScanResultDao.create(targetScanResult);

								for (BssidResult importBssid : importScanResult.getBssids()) {
									// printStatusLine(getString(R.string.export_db_importing,importBssid.toString()));
									BssidResult targetBssid = new BssidResult(importBssid);

									// set reference
									targetBssid.setScanResult(targetScanResult);

									// craete
									targetBssidDao.create(targetBssid);
								}

							}
							
						
						}
						progress.setProgress(++projectCount);
					}
				} catch (Exception ex) {
					Logger.e("could not import database", ex);
					printStatusLine(getString(R.string.export_db_import_failed, ex.getMessage()));
					
					
				}finally {
					hRefresh.sendEmptyMessage(REFRESH);
					progress.dismiss();
				}
			}

		};
		
		progress.show();
		importThread.start();

	}

	protected void updateStatusView() {
		statusView.append(statusMessages.toString());
		statusMessages=new StringBuffer();
	}

	@Override
	protected void finalize() throws Throwable {
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
		super.finalize();
	}

	protected void printStatusLine(String line) {
		printStatus(line + "\n");
	}

	protected void printStatus(String line) {
		if (statusMessages != null)
			statusMessages.append(line);
	}

}
