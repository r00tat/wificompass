package at.fhstp.wificompass.model;

/**
 * @author  Paul Woelfel (paul@woelfel.at)
 */
public class Bssid {
	/**
	 * @uml.property  name="bssid"
	 */
	protected String bssid;

	/**
	 * @uml.property  name="ssid"
	 */
	protected String ssid;

	/**
	 * @uml.property  name="capabilities"
	 */
	protected String capabilities;

	/**
	 * @uml.property  name="frequency"
	 */
	protected int frequency;
	
	/**
	 * @uml.property  name="isSelected"
	 */
	protected boolean isSelected = true;;

	/**
	 * @return
	 * @uml.property  name="isSelected"
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param isSelected
	 * @uml.property  name="isSelected"
	 */
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
	
	/**
	 * @return
	 * @uml.property  name="bssid"
	 */
	public String getBssid() {
		return bssid;
	}

	/**
	 * @param bssid
	 * @uml.property  name="bssid"
	 */
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	/**
	 * @return
	 * @uml.property  name="ssid"
	 */
	public String getSsid() {
		return ssid;
	}

	/**
	 * @param ssid
	 * @uml.property  name="ssid"
	 */
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	/**
	 * @return
	 * @uml.property  name="capabilities"
	 */
	public String getCapabilities() {
		return capabilities;
	}

	/**
	 * @param capabilities
	 * @uml.property  name="capabilities"
	 */
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	/**
	 * @return
	 * @uml.property  name="frequency"
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency
	 * @uml.property  name="frequency"
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
}
