package at.fhstp.wificompass.model;

public class Bssid {
	protected String bssid;

	protected String ssid;

	protected String capabilities;

	protected int frequency;
	
	protected boolean isSelected = true;;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public Bssid(String bssid, String ssid, String capabilities, int frequency, boolean isSelected) {
		this.bssid = bssid;
		this.ssid = ssid;
		this.capabilities = capabilities;
		this.frequency = frequency;
		this.isSelected = isSelected;
	}
	
	public Bssid(String bssid, String ssid) {
		this.bssid = bssid;
		this.ssid = ssid;
		this.isSelected = true;
	}
	
	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
}
