/*
 * Created on Dec 22, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model.helper;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.Project;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public class ProjectListAdapter extends BaseAdapter {
	
	protected Context context;
	
	protected List<Project> projects;
	
	protected LayoutInflater inflater;
	
	/**
	 * @uml.property  name="databaseHelper"
	 * @uml.associationEnd  
	 */
	protected DatabaseHelper databaseHelper = null;
	
	public ProjectListAdapter(Context context) throws SQLException{
		this.context=context;
		databaseHelper=OpenHelperManager.getHelper(context, DatabaseHelper.class);
		Dao<Project,Integer> projectDao=databaseHelper.getDao(Project.class);
		projects=projectDao.queryForAll();
		
		inflater = (LayoutInflater)context.getSystemService
			      (Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return projects.size();
	}

	@Override
	public Object getItem(int position) {
		return projects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return projects.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewGroup v=(ViewGroup) inflater.inflate((position%2==0?R.layout.list_item_eq:R.layout.list_item_uneq), parent, false);
		
//		LinearLayout layout = new LinearLayout(context);
//		layout.setOrientation(LinearLayout.HORIZONTAL);
//		//layout.setGravity(Gravity.CENTER);
//		layout.setPadding(3,3,3,3);
//
//		TextView tv = new TextView(context);
//		tv.setText(((Project)getItem(position)).getName());
//		
//		
//		layout.addView(tv);
		
		((TextView)v.findViewById(R.id.list_view_item)).setText(projects.get(position).getName());
		
		return v;
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
