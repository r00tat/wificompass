/*
 * Created on Jan 21, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model.helper;

import java.sql.SQLException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.Project;
import at.fhstp.wificompass.model.ProjectSite;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public class ProjectSiteListAdapter extends BaseAdapter {

	protected Context context;

	/**
	 * @uml.property  name="sites"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	protected ProjectSite[] sites;

	/**
	 * @uml.property  name="databaseHelper"
	 * @uml.associationEnd  
	 */
	protected DatabaseHelper databaseHelper = null;

	protected Dao<ProjectSite, Integer> dao;
	
	protected LayoutInflater inflater;

	/**
	 * @uml.property  name="project"
	 * @uml.associationEnd  
	 */
	protected Project project;

	public ProjectSiteListAdapter(Context ctx, Project p) throws SQLException {
		this.context = ctx;
		project = p;
		databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
		dao = databaseHelper.getDao(ProjectSite.class);
		ForeignCollection<ProjectSite> fc = project.getSites();
		if (fc != null) {
			sites = fc.toArray(new ProjectSite[] {});
		} else {
			sites = new ProjectSite[] {};
		}
		
		inflater = (LayoutInflater)context.getSystemService
			      (Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return sites.length;
	}

	@Override
	public Object getItem(int position) {
		return sites[position];
	}

	@Override
	public long getItemId(int position) {
		return sites[position].getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewGroup v=(ViewGroup) inflater.inflate((position%2==0?R.layout.list_item_eq:R.layout.list_item_uneq), parent, false);
		((TextView)v.findViewById(R.id.list_view_item)).setText(sites[position].getTitle());
		
		return v;
		
		
//		LinearLayout layout = new LinearLayout(context);
//		layout.setOrientation(LinearLayout.HORIZONTAL);
//		//layout.setGravity(Gravity.CENTER);
//		layout.setPadding(3,3,3,3);
//
//		TextView tv = new TextView(context);
//		tv.setText(sites[position].getTitle());
//		layout.addView(tv);
//		
//		return layout;
	}

}
