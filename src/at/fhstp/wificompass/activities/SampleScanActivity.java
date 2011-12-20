/*
 * Created on Dec 5, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.activities;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.R;

public class SampleScanActivity extends Activity implements OnClickListener {
	protected boolean running;

	protected static final String logTag = "APLocActivity";
	
	protected static Logger log=new Logger(logTag);

	protected BroadcastReceiver wifiScanReceiver = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log.debug( "created sample scan");
		setContentView(R.layout.sample_scan);

		/* First, get the Display from the WindowManager */
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

		/* Now we can retrieve all display-related infos */
		int width = display.getWidth();
		int height = display.getHeight();
		int orientation = display.getOrientation();

		log.debug( "display: " + width + "x" + height + " orientation:" + orientation);

		running = false;
		((Button) findViewById(R.id.sample_scan_button)).setOnClickListener(this);
		// GridView gridview = (GridView) findViewById(R.id.sample_scan_result_grid);
		// gridview.setAdapter(new WiFiScanResultAdapter(this));
		// gridview.setEnabled(false);

		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		int state = wm.getWifiState();

		((TextView) findViewById(R.id.sample_scan_text)).append("\n" + getResources().getStringArray(R.array.wifi_states)[state]);

		updateResults();
	}

	@Override
	public void onClick(View v) {

		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if (!running) {

			TextView bodyText = ((TextView) findViewById(R.id.sample_scan_text));

			log.debug( "button clicked, trying to start a wifi scan");

			if (!wm.isWifiEnabled()) {
				bodyText.setText(getText(R.string.sample_scan_enableing_wifi));

				log.debug( "WiFi is disabled, trying to enable it");
				wm.setWifiEnabled(true);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {

				}

				if (wm.isWifiEnabled()) {
					bodyText.append("\n" + getText(R.string.sample_scan_enableing_wifi_failed));
					log.debug( "WiFi could not be enabled");
				} else {
					log.debug( "WiFI enabled successfully");
					bodyText.append("\n" + getText(R.string.enableingWiFiSucceed));

				}
			}

			if (wm.isWifiEnabled()) {
				log.debug( "WiFi is enabled");

				IntentFilter i = new IntentFilter();
				i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

				wifiScanReceiver = new BroadcastReceiver() {
					public void onReceive(Context c, Intent i) {

						log.debug( "received ScanResult");
						// Code to execute when SCAN_RESULTS_AVAILABLE_ACTION event occurs
						WifiManager w = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
						List<ScanResult> l = w.getScanResults(); // Returns a <list> of scanResults

						StringBuffer result = new StringBuffer();
						;

						for (Iterator<ScanResult> it = l.iterator(); it.hasNext();) {
							ScanResult sr = it.next();
							result.append(sr.BSSID + " " + sr.SSID + " " + sr.level + "dBm " + sr.frequency + "MHz " + sr.capabilities + "\n");
						}

						running = false;
						((Button) findViewById(R.id.sample_scan_button)).setText(getText(R.string.sample_scan_start_button_text));
//						((TextView) findViewById(R.id.sample_scan_text)).setText(result.toString());
						((TextView) findViewById(R.id.sample_scan_text)).append(getText(R.string.sample_scan_scan_finished));
						SampleScanActivity.this.unregisterReceiver(wifiScanReceiver);
						wifiScanReceiver = null;

						// gridview.setAdapter(new WiFiScanResultAdapter(SampleScanActivity.this));
						// gridview.setEnabled(false);

						updateResults();

					}
				};
				registerReceiver(wifiScanReceiver, i);

				log.debug( "starting scan");
				// Now you can call this and it should execute the broadcastReceiver's onReceive()
				running = wm.startScan();

				if (running) {
					bodyText.setText(getText(R.string.sample_scan_scanning_text));
					// ImageSpan is = new ImageSpan(this,android.R.drawable.)
					((Button) findViewById(R.id.sample_scan_button)).setText(getText(R.string.sample_scan_stop_button_text));
				} else {
					bodyText.setText(getText(R.string.sample_scan_start_scan_failed));
				}

			}

		} else {
			// stopping scanner
			log.debug( "stopping wifi scan reveiver");
			try {
				if (wifiScanReceiver != null)
					unregisterReceiver(wifiScanReceiver);
			} catch (Exception ex) {
			}
			// WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);

			((TextView) findViewById(R.id.sample_scan_text)).append(getText(R.string.sample_scan_stopped));
			((Button) findViewById(R.id.sample_scan_button)).setText(getText(R.string.sample_scan_start_button_text));
			running = false;
		}

		WifiInfo winfo = wm.getConnectionInfo();
		if (winfo != null) {
			((TextView) findViewById(R.id.sample_scan_text)).append("\n" + winfo.getSupplicantState().toString());
		}

	}

	@Override
	protected void onDestroy() {
		log.debug( this.getClass().getName() + " is destroyed!");
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		log.debug( this.getClass().getName() + " is stopped!");
		if (wifiScanReceiver != null) {
			unregisterReceiver(wifiScanReceiver);
		}
		super.onStop();
	}

	protected void updateResults() {

		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		WifiInfo winfo = wm.getConnectionInfo();
		if (winfo != null) {
			((TextView) findViewById(R.id.sample_scan_text)).append("\n" + getString(R.string.sample_scan_supplicant_state) + winfo.getSupplicantState().toString());
		}

		TableLayout table = (TableLayout) findViewById(R.id.sample_scan_result_table);
		while (table.getChildCount() > 1) {
			table.removeViewAt(1);
		}
		List<ScanResult> results = wm.getScanResults();
		for (Iterator<ScanResult> it = results.iterator(); it.hasNext();) {
			ScanResult sr = it.next();
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

			TextView tv;

			tv = new TextView(this);
			tv.setText(sr.SSID);
			tv.setPadding(5, 5, 5, 5);
			tr.addView(tv);
			tv = new TextView(this);
			tv.setText(sr.BSSID);
			tv.setPadding(5, 5, 5, 5);
			tr.addView(tv);
			tv = new TextView(this);
			tv.setText(sr.level + "dBm");
			tv.setPadding(5, 5, 5, 5);
			tr.addView(tv);
			tv = new TextView(this);
			tv.setText(sr.frequency + "MHz");
			tv.setPadding(5, 5, 5, 5);
			tr.addView(tv);
			tv = new TextView(this);
			tv.setText(sr.capabilities);
			tv.setPadding(5, 5, 5, 5);
			tr.addView(tv);

			table.addView(tr);
		}

	}

}
