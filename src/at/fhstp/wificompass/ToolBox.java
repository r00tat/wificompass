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

}
