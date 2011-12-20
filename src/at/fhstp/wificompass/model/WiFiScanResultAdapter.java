/*
 * Created on Dec 10, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.model;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WiFiScanResultAdapter extends BaseAdapter {
	
	protected Context ctx;
	protected List<ScanResult> scanResults;
	
	protected static final String logTag="APLocActivity";
	
	
	public WiFiScanResultAdapter(Context ctx){
		this.ctx=ctx;
		WifiManager wm = (WifiManager) this.ctx.getSystemService(Context.WIFI_SERVICE);
		scanResults=wm.getScanResults();
		if(scanResults==null){
			scanResults=new Vector<ScanResult>();
		}
	}

	@Override
	public int getCount() {
		return scanResults.size()*5;
	}

	@Override
	public Object getItem(int position) {
		String result=null;
		ScanResult sr=scanResults.get(position/5);
		switch(position%5){
		case 0:
			result=sr.BSSID;
			break;
		case 1:
			result=sr.SSID;
			break;
		case 2:
			result=sr.level+"dBm";
			break;
		case 3:
			result=sr.frequency+"MHz";
			break;
		case 4:
			result=sr.capabilities;
			break;
		}
//		Log.d(logTag,"get WiFiScanResult Item at "+position+": "+result);
		return result;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		Log.d(logTag, "get WiFiScanResult View at "+position);
		TextView tv=new TextView(ctx);
		tv.setText((String)getItem(position));
		return tv;
	}

}
