package at.woelfel.philip.filebrowser;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.fhstp.wificompass.R;

/**
 * @author Philip Woelfel philp@woelfel.at
 */
public class FileAdapter extends BaseAdapter {

	protected Context context;
	protected File[] files;
	protected File parent;
	protected File currentDir;

	public FileAdapter(Context c, File f) {
		context = c;
		files = f.listFiles();
		//Arrays.sort(files);
		Arrays.sort(files, new Comparator<File>() {

			public int compare(File object1, File object2) {
				String myname = object1.getName();
				String othername = object2.getName();
				if(object1.isDirectory() && !object2.isDirectory()){
					return -1;
				}
				else if(!object1.isDirectory() && object2.isDirectory()){
					return 1;
				}
				return myname.toLowerCase().compareTo(othername.toLowerCase());
			}
		});
		parent = f.getParentFile();
		currentDir = f;
	}

	public int getCount() {
		if (parent != null) {
			return files.length + 1;
		}
		else {
			return files.length;
		}
	}

	public Object getItem(int position) {
		if (parent != null) {
			if (position > 0) {
				return files[position - 1];
			}
			else {
				return parent;
			}
		}
		else {
			return files[position];
		}
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		//layout.setGravity(Gravity.CENTER);

		TextView tv = new TextView(context);
		File file = (File) getItem(position);

		if (file == this.parent) {
			ImageView iv = new ImageView(context);
			iv.setImageResource(R.drawable.folder);
			layout.addView(iv);
			tv.setText(R.string.filebrowser_parent_dir);
		}
		else {
			if (file.isDirectory()) {
				ImageView iv = new ImageView(context);
				iv.setImageResource(R.drawable.folder);
				layout.addView(iv);
			}
			tv.setText(file.getName());
		}
		// tv.setTextColor(Color.BLACK);
		tv.setHeight((int) (35 * context.getResources().getDisplayMetrics().density));
		//tv.setGravity(Gravity.CENTER);

		layout.addView(tv);

		return layout;
	}

	/**
	 * @return the currentDir
	 */
	public File getCurrentDir() {
		return currentDir;
	}

	/**
	 * @param currentDir the currentDir to set
	 */
	public void setCurrentDir(File currentDir) {
		this.currentDir = currentDir;
	}

}
