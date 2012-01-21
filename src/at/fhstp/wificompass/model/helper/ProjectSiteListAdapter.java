/*
 * Created on Jan 21, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model.helper;

import java.sql.SQLException;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.fhstp.wificompass.model.Project;
import at.fhstp.wificompass.model.ProjectSite;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;

public class ProjectSiteListAdapter extends BaseAdapter {

	protected Context context;

	protected ProjectSite[] sites;

	protected DatabaseHelper databaseHelper = null;

	protected Dao<ProjectSite, Integer> dao;

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
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		//layout.setGravity(Gravity.CENTER);
		layout.setPadding(3,3,3,3);

		TextView tv = new TextView(context);
		tv.setText(sites[position].getTitle());
		layout.addView(tv);
		
		return layout;
	}

}
