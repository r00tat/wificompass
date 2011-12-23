/*
 * Created on Dec 22, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.activities;

import java.sql.SQLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import at.fhstp.wificompass.ApplicationContext;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.Project;
import at.fhstp.wificompass.model.ProjectListAdapter;

public class ProjectListActivity extends Activity implements OnItemClickListener {
	
	protected static final Logger log=new Logger(ProjectListAdapter.class);
	
	protected ProjectListAdapter adapter;
	
	public static final String PROJ_KEY="project_id";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project_list);
		ApplicationContext.setContext(this);
		
		ListView listView=((ListView)findViewById(R.id.project_list_view));
		log.azzert(listView==null, "list view is null??!?!?");
		log.azzert(this==null, "This could never be null!?!?!?!");
		try {
			adapter=new ProjectListAdapter(this);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
			
		} catch (SQLException e) {
			log.error("could not load project list", e);
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent i=new Intent();
		i.putExtra(PROJ_KEY,((Project)adapter.getItem(position)).getId());
		setResult(Activity.RESULT_OK, i);
		finish();
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		log.debug("setting context");
		ApplicationContext.setContext(this);
	}
}
