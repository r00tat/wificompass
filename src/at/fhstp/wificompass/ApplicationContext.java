/*
 * Created on Dec 8, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass;

import android.content.Context;

public class ApplicationContext {
	static public Context ctx=null;
	
	public static void setContext(Context context){
		ctx=context;
	}
	
	public static Context getContext(){
		return ctx;
	}
}
