/*
 * Created on Dec 5, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import at.fhstp.wificompass.BuildInfo;
import at.fhstp.wificompass.R;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		((TextView)findViewById(R.id.about_revision)).setText(BuildInfo.revision);
		((TextView)findViewById(R.id.about_date)).setText(BuildInfo.commitDate);
		((TextView)findViewById(R.id.about_url)).setText(Html.fromHtml("<a href=\""+BuildInfo.repositoryURL+"\">"+BuildInfo.repositoryURL+"</a>"));
		((TextView)findViewById(R.id.about_description)).setMovementMethod(LinkMovementMethod.getInstance());
		((TextView)findViewById(R.id.about_url)).setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		log.debug("setting context");
		
	}

}
