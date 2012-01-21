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

import com.j256.ormlite.android.apptools.OpenHelperManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import at.fhstp.wificompass.ApplicationContext;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.helper.DatabaseHelper;
import at.woelfel.philip.filebrowser.FileBrowser;

public class DBActivity extends Activity implements OnClickListener {

	protected static final Logger log = new Logger(SensorsActivity.class);

	protected static final int FILEBROWSER_REQUEST = 12345;
	
	protected DatabaseHelper databaseHelper = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		log.debug("created sensors activity");
		setContentView(R.layout.export_db);
		
		databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		((Button) findViewById(R.id.export_db_button)).setOnClickListener(this);
		((Button) findViewById(R.id.export_db_drop_button)).setOnClickListener(this);
		((Button) findViewById(R.id.export_db_import_button)).setOnClickListener(this);
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
		TextView tv = ((TextView) findViewById(R.id.export_db_message));

		switch (v.getId()) {
		case R.id.export_db_button:
			Intent i = new Intent(this, FileBrowser.class);
			i.putExtra(FileBrowser.EXTRA_MODE, FileBrowser.MODE_SAVE);
			startActivityForResult(i, FILEBROWSER_REQUEST);
			break;
		
		case R.id.export_db_import_button:
			//TODO import data
			tv.setText(R.string.not_implemented);
			break;
			
		case R.id.export_db_drop_button:
			
			try {
				databaseHelper.recreateDatabase();
				tv.setText(R.string.export_db_drop_finished);
			} catch (SQLException e) {
				log.error("could not recreate database", e);
				tv.setText(getString(R.string.export_db_drop_failed,e.getMessage()));
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

		if (resultCode == Activity.RESULT_OK && requestCode == FILEBROWSER_REQUEST) {
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

		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
		super.finalize();
	}

}
