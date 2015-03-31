#ChangeLog of WiFi Compass

# Introduction #

The current released version is 1.0. Minor changes and fixed will be released as 1.0.1, 1.0.2 aso.

We live the open source policy: release early, release often. So do not wonder, if you get frequent updates.


# Details #

## 1.0 ##


### 1.0.17 revision 8b6e8314f37b ###

fixed two NullPointerExceptions

### 1.0.16 revision c7091940c1de ###
Fixed NullPointerException in MeasuringPointDrawable if not BSSIDs are found.


### 1.0.15 Revision: cdc452e857f2 ###
  * tell the user how much steps to do with auto calibration ([issue 40](https://code.google.com/p/wificompass/issues/detail?id=40))
  * moved main icon left or right to the text ([issue 41](https://code.google.com/p/wificompass/issues/detail?id=41))
  * After auto calibration take the user to the main page ([issue 42](https://code.google.com/p/wificompass/issues/detail?id=42))

### 1.0.14 revision 2f77264d5575 ###
moved from svn to git (see [issue #43](https://code.google.com/p/wificompass/issues/detail?id=#43))


### 1.0.13 [r192](https://code.google.com/p/wificompass/source/detail?r=192) ###
  * path of a walk is now shown on the map
  * changed compass low pass filter from 0.3 to 0.5
  * fixed auto rotation
  * refactored class names from Triangulation to Trilateration
  * fixed some other small bugs


### 1.0.12  [r178](https://code.google.com/p/wificompass/source/detail?r=178) ###

extended auto configuration log message
fixed a bug on Android 4, when saving new results


### 1.0.11 [r176](https://code.google.com/p/wificompass/source/detail?r=176) ###
After defining north adjustment angle was not set on LocationService.
Async persistance task in ProjectSiteActivity does not require a ui any more, we also could save results in background
if a new WiFi scan is started, the old one is stopped first, otherwise we would get duplicate results.

### 1.0.10 [r175](https://code.google.com/p/wificompass/source/detail?r=175) ###
fixed initial config dialogs.

### 1.0.9 [r172](https://code.google.com/p/wificompass/source/detail?r=172) ###
When selecting a background image for a map, only files with valid extensions are displayed.


### 1.0.8 [r170](https://code.google.com/p/wificompass/source/detail?r=170) ###
UI Improvements:
  * if a site is loaded the first time, the user is guide through setup (loading a map, scaling, defining north).
  * auto rotation of the map, so it stays steady.
  * adjusting the magnetic north of the map is done by turning the device or the map and not the compass.
  * WiFi scan interval can now be changed in the UI

Under the hood:
  * created an CompassSensorWatcher, which calculates the correct azimuth and applies a low pass filter. Classes could be notified by registering a Listener with CompassMonitor.


### 1.0.7 [r157](https://code.google.com/p/wificompass/source/detail?r=157) ###
Changed List View to look more like a list, and not only like text. It should be more clear now, that the items can be clicked.

### 1.0.6 ###
WiFi Scan results are now saved asynchronous, after walking and scanning is finished.

### 1.0.5 ###
A few minor UI enhancements

### 1.0.4 ###
small UI improvements


### 1.0.3 ###
resolved a issue with auto calibrating.


### 1.0.2 ###
Sensor Auto Calibration is now also working on low performance devices, because the sensor data is just kept in memory and not written to the database.

### 1.0.1 ###
Added sensor auto calibrating support for low performance devices, by disabling the graph.