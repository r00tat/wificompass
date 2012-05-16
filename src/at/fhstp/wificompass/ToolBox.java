/*
 * Created on Apr 3, 2012
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass;

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

	public static float calculateAngleDifference(float angle1, float angle2) {
		float difference = 0.0f;
		
		difference = angle1 - angle2;
		
		difference = normalizeAngle(difference);
		
		if (difference > Math.PI) {
			difference = (float) (difference - Math.PI * 2);
		}
		
		return difference;
	}
}
