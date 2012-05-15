/*
 * Created on Feb 22, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.wifi;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import at.fhstp.wificompass.Logger;
import at.fhstp.wificompass.exceptions.WifiException;
import at.fhstp.wificompass.model.BssidResult;
import at.fhstp.wificompass.model.Location;
import at.fhstp.wificompass.model.WifiScanResult;
import at.fhstp.wificompass.model.helper.DatabaseHelper;
import at.fhstp.wificompass.userlocation.LocationServiceFactory;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class WifiScanner {
	
	protected static Vector<BroadcastReceiver> receivers=null;
	
	public static BroadcastReceiver startScan(Context ctx, WifiResultCallback callback) throws WifiException{
		if(receivers==null){
			receivers=new Vector<BroadcastReceiver>();
		}
		
		BroadcastReceiver wifiScanReceiver = null;
		final Context context=ctx;
		final WifiResultCallback resultCallback=callback;
		
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

//		Logger.d( "trying to start a wifi scan");

		if (!wm.isWifiEnabled()) {
			

			Logger.d( "WiFi is disabled, trying to enable it");
			wm.setWifiEnabled(true);
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {

			}

			if (wm.isWifiEnabled()) {
				
				Logger.d( "WiFi could not be enabled");
			} else {
				Logger.d( "WiFI enabled successfully");
				
			}
		}

		if (!wm.isWifiEnabled()) {
			
			throw new WifiException("WiFi could not be enabled, please enable it!");
		}
		
//			Logger.d( "WiFi is enabled");

			IntentFilter i = new IntentFilter();
			i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
			
			

			wifiScanReceiver = new BroadcastReceiver() {
				public void onReceive(Context c, Intent i) {

					Logger.d( "received ScanResult");
					// Code to execute when SCAN_RESULTS_AVAILABLE_ACTION event occurs
					WifiManager w = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
					List<ScanResult> l = w.getScanResults(); // Returns a <list> of scanResults

//					StringBuffer result = new StringBuffer();
					

//					for (Iterator<ScanResult> it = l.iterator(); it.hasNext();) {
//						ScanResult sr = it.next();
//						result.append(sr.BSSID + " " + sr.SSID + " " + sr.level + "dBm " + sr.frequency + "MHz " + sr.capabilities + "\n");
//					}

					context.unregisterReceiver(this);
					
					if(receivers.contains(this))
						receivers.remove(this);
					
					DatabaseHelper databaseHelper = null;
					try {
						
						databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
						Location curLocation=LocationServiceFactory.getLocationService().getLocation();
						
						databaseHelper.getDao(Location.class).create(curLocation);
						
						WifiScanResult wifiScanResult=new WifiScanResult(new Date().getTime(),curLocation,null);
						
						Dao<WifiScanResult,Integer> scanResultDao=databaseHelper.getDao(WifiScanResult.class);
						scanResultDao.create(wifiScanResult);
						
						Dao<BssidResult, Integer> bssidDao=databaseHelper.getDao(BssidResult.class);
						
						for (Iterator<ScanResult> it = l.iterator(); it.hasNext();) {
							ScanResult sr = it.next();
							BssidResult bssid=new BssidResult(sr,wifiScanResult);
							bssidDao.create(bssid);
							
						}
						
						scanResultDao.refresh(wifiScanResult);
						
						resultCallback.onScanFinished(wifiScanResult);
						
					} catch (SQLException e) {
						resultCallback.onScanFailed(e);
					} finally {
						if(databaseHelper!=null)
							OpenHelperManager.releaseHelper();
					}
					

					// gridview.setAdapter(new WiFiScanResultAdapter(SampleScanActivity.this));
					// gridview.setEnabled(false);

					

				}
			};
			
			
			context.registerReceiver(wifiScanReceiver, i);
			
			receivers.add(wifiScanReceiver);

			Logger.d( "starting Wifi Scan");
			// Now you can call this and it should execute the broadcastReceiver's onReceive()
			wm.startScan();

		
		return wifiScanReceiver;

	}
	
	public static void stopScanning(Context ctx){
		// we don't stop scanning, we just unregister all Broadcast Intent Receivers
		
		if(receivers!=null)
		for(BroadcastReceiver rcvr: receivers){
			ctx.unregisterReceiver(rcvr);
		}
		receivers=new Vector<BroadcastReceiver>();
		
		
	}

	
	public static void stopScanner(Context ctx,BroadcastReceiver receiver){
		ctx.unregisterReceiver(receiver);
		if(receivers.contains(receiver)){
			receivers.remove(receiver);
		}
	}
	
	
}
