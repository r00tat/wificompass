/*
 * Created on Apr 3, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass;

import android.hardware.SensorManager;

/**
 * @author Paul Woelfel (paul@woelfel.at)
 */
public class ToolBox {
	/**
	 * normalize the angle to the range 0 to 2π
	 * @param angle in radiant
	 * @return normalized angle
	 */
	public static float normalizeAngle(float angle){
		angle=(float) (angle%(2*Math.PI));
		return (float) (angle<0?angle+2*Math.PI:angle);
	}
	
	public static float normalizeAngleBetweenPI(float angle) {
		return (float) (angle % (2 * Math.PI));
	}

	public static double lowpassFilter(double oldValue, double newValue, double filter) {
		return oldValue + filter * (newValue - oldValue);
	}

	public static float lowpassFilter(float oldValue, float newValue, float filter) {
		return oldValue + filter * (newValue - oldValue);
	}

	public static float calculateAngleDifference(float angle1, float angle2) {
		float difference = 0.0f;
		
		difference = angle1 - angle2;
		
		difference = normalizeAngle(difference);
		
		if (difference > Math.PI) {
			difference = (float) (difference - Math.PI * 2);
		}
		
		return difference;
	}
	
	
	
	public static float getSmoothAngleFromSensorData(float oldAngle, float[] gravity, float[] geomag) {
		
		float newAngle = 0.0f;
		float smoothAngle = 0.0f;
		
		float[] orientVals = new float[3];
		float[] inR = new float[16];
		float[] I = new float[16];
		
		
		if (gravity != null && geomag != null) {
			boolean success = SensorManager.getRotationMatrix(inR, I, gravity, geomag);
		
			if (success) {				
				SensorManager.getOrientation(inR, orientVals);
				newAngle = ToolBox.normalizeAngle(orientVals[0]);
				
				float minimumAngleChange = (float) Math.toRadians(2.0f);
				float smoothFactorCompass = 0.8f;
				float smoothThresholdCompass = (float) Math.toRadians(30.0f);
				float halfCirle = (float) Math.PI;
				float wholeCircle = (float) (2 * Math.PI);
								
				if (Math.abs(newAngle - oldAngle) < minimumAngleChange) {
					smoothAngle = oldAngle;
				} else  if (Math.abs(newAngle - oldAngle) < halfCirle) {
				    if (Math.abs(newAngle - oldAngle) > smoothThresholdCompass) {
				    	smoothAngle = newAngle;
				    }
				    else {
				    	smoothAngle = oldAngle + smoothFactorCompass * (newAngle - oldAngle);
				    }
				}
				else {
				    if (wholeCircle - Math.abs(newAngle - oldAngle) > smoothThresholdCompass) {
				    	smoothAngle = newAngle;
				    }
				    else {
				        if (oldAngle > newAngle) {
				        	smoothAngle = (oldAngle + smoothFactorCompass * ((wholeCircle + newAngle - oldAngle) % wholeCircle) + wholeCircle) % wholeCircle;
				        } 
				        else {
				        	smoothAngle = (oldAngle - smoothFactorCompass * ((wholeCircle - newAngle + oldAngle) % wholeCircle) + wholeCircle) % wholeCircle;
				        }
				    }
				}
			}
		}
		
		return smoothAngle;
	}
}
