/*
 * Created on Feb 22, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass.wifi;

import at.fhstp.wificompass.model.WifiScanResult;

public interface WifiResultCallback {
	public void scanFinished(WifiScanResult wr);
	public void scanFailed(Exception ex);
}
