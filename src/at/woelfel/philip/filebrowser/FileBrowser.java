package at.woelfel.philip.filebrowser;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import at.fhstp.wificompass.R;

/**
 * @author Philip Woelfel (philip@woelfel.at)
 * modified by Paul Woelfel (paul@woelfel.at)
 */
public class FileBrowser extends Activity implements OnItemClickListener, OnClickListener {
	public static final int MODE_SAVE = 0;
	public static final int MODE_LOAD = 1;
	private static final int DIALOG_FILENAME = 0;
	
	public static final String EXTRA_MODE = "FileBrowserMode",EXTRA_PATH="path";
	protected static final int DIALOG_DIRECTORY = 1;
	
	protected static final String TAG="FileBrowser";
	
	protected FileAdapter adap;
	protected ListView lv;
	protected TextView header;
	protected Button but;
	
//	private ArrayList<Integer> colors;
	private int mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filebrowser);
		adap = new FileAdapter(this, Environment.getExternalStorageDirectory());

		header = (TextView) findViewById(R.id.dirPath);
		header.setText(getString(R.string.filebrowser_dir) + ": " + Environment.getExternalStorageDirectory().getAbsolutePath());

		lv = (ListView) findViewById(R.id.fileList);
		lv.setOnItemClickListener(this);
		lv.setAdapter(adap);

		but = (Button) findViewById(R.id.saveButton);
		but.setOnClickListener(this);
		
		Bundle xtra = getIntent().getExtras();
		if (xtra.containsKey(EXTRA_MODE)) {
			mode = xtra.getInt(EXTRA_MODE);
			switch (mode) {
				case MODE_SAVE:
					but.setText(R.string.filebrowser_save);
					break;

				case MODE_LOAD:
					but.setVisibility(View.GONE);
					break;
			}
		}
		
		((Button) findViewById(R.id.filebrowser_new_dir_button)).setOnClickListener(this);
		
	}



	private void loadFile(File f) {
		Intent in = new Intent();
//		in.putExtra(MainScreen.EXTRA_COLORLIST, SaveFileHandler.readFile(f));
		in.putExtra(EXTRA_PATH, f.getAbsolutePath());
		setResult(Activity.RESULT_OK, in);
		finish();
	}
	
	private void saveFile(File f){
		
		Intent in = new Intent();
//		in.putExtra(MainScreen.EXTRA_COLORLIST, SaveFileHandler.readFile(f));
		in.putExtra(EXTRA_PATH, f.getAbsolutePath());
		setResult(Activity.RESULT_OK, in);
		finish();
		
		
//		if(SaveFileHandler.saveFile(f, colors)){
//			Toast.makeText(this, R.string.filebrowser_saved, Toast.LENGTH_LONG).show();
//			setResult(Activity.RESULT_OK);
//			finish();
//		}
//		else{
//			Toast.makeText(this, R.string.filebrowser_error_save, Toast.LENGTH_LONG).show();
//		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// super.onListItemClick(l, v, position, id);
		File f = (File) adap.getItem(position);
		if (f.isDirectory()) {
			if(!f.getName().matches(".*\\."+getString(R.string.file_ending))){
			setFileAdapter(f);
			}else {
				loadFile(f);
			}
		}
		else {
//			loadFile(f);
		}

	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.saveButton:
			showDialog(DIALOG_FILENAME);
			break;
		case R.id.filebrowser_new_dir_button:
			showDialog(DIALOG_DIRECTORY);
			break;
		}
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_FILENAME:
				AlertDialog.Builder alert = new AlertDialog.Builder(this);

				alert.setTitle(R.string.filebrowser_dialog_filename_title);
				alert.setMessage(R.string.filebrowser_dialog_filename_message);

				// Set an EditText view to get user input
				final EditText input = new EditText(this);
				input.setSingleLine(true);
				alert.setView(input);

				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String filename = input.getText() + "";
						saveFile(new File(adap.getCurrentDir().getAbsolutePath()+"/"+filename+"." +getString(R.string.file_ending)));
						
					}
				});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

				return alert.create();
			case DIALOG_DIRECTORY:
				AlertDialog.Builder diralert = new AlertDialog.Builder(this);

				diralert.setTitle(R.string.filebrowser_dialog_directory_title);
				diralert.setMessage(R.string.filebrowser_dialog_directory_message);

				// Set an EditText view to get user input
				final EditText dirinput = new EditText(this);
				dirinput.setSingleLine(true);
				diralert.setView(dirinput);

				diralert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String filename = dirinput.getText() + "";
//						saveFile(new File(adap.getCurrentDir().getAbsolutePath()+"/"+filename+"." +getString(R.string.file_ending)));
						File f=new File(adap.getCurrentDir().getAbsolutePath()+"/"+filename);
						if(f.mkdir()){
						setFileAdapter(f);
						}else {
							Log.w(TAG,"Could not create directory: "+f.getAbsolutePath());
						}
					}
				});

				diralert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

				return diralert.create();
				
		}
		return super.onCreateDialog(id);
	}
	
	protected void setFileAdapter(File f){
		adap = new FileAdapter(this, f);
		lv.setAdapter(adap);
		header.setText(getString(R.string.filebrowser_dir) + ": " + f.getAbsolutePath());
	}
}
