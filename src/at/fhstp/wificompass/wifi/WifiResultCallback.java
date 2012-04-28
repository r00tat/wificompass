/*
 * Created on Feb 22, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.wifi;

import at.fhstp.wificompass.model.WifiScanResult;

public interface WifiResultCallback {
	public void onScanFinished(WifiScanResult wr);
	public void onScanFailed(Exception ex);
}
