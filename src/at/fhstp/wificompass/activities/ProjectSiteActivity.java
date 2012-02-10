/*
 * Created on Dec 23, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import at.fhstp.wificompass.ApplicationContext;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.view.MultiTouchView;
import at.fhstp.wificompass.view.SiteMap;

public class ProjectSiteActivity extends Activity implements OnClickListener {
	
	protected Logger log=new Logger(ProjectSiteActivity.class);
	
	public static final String SITE_KEY="SITE",START_MODE="START_MODE",PROJECT_KEY="PROJECT";
	
	public static final int START_NEW=1,START_LOAD=2;
	
	protected MultiTouchView siteResultView;
	
	protected SiteMap map;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project_site);
		
		Button resetZoom=((Button)findViewById(R.id.project_site_reset_zoom_button));
		resetZoom.setOnClickListener(this);
		
		Button resetXY=((Button)findViewById(R.id.project_site_reset_pos_button));
		resetXY.setOnClickListener(this);
		
		siteResultView=((MultiTouchView)findViewById(R.id.project_site_resultview));
		map=new SiteMap(this);
		siteResultView.addDrawable(map);
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		siteResultView.unloadImages();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		log.debug("setting context");
		ApplicationContext.setContext(this);
		siteResultView.loadImages(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.project_site_reset_zoom_button:
				Logger.d("resetting Zoom");
				siteResultView.resetAllScale();
				break;
			
			case R.id.project_site_reset_pos_button:
				Logger.d("resetting position");
				siteResultView.resetAllXY();
				break;
				
		}
	}
	
}
