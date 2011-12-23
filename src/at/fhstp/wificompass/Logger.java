/*
 * Created on Dec 12, 2011
 * Author: Paul Woelfel
 * Email: frig@frig.at
 */
package at.fhstp.wificompass;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

//TODO: comments and documentation
/**
 * <p>
 * Logging class to allow easy logging.
 * 
 * </p>
 * <p>
 * The output could be formated with the log format and with the date format. <br />
 * Each variable must be prepended with a % sign. Following modifiers are available:<br />
 * <ul>
 * <li><b>d</b>: Date, uses the dateFormat and a SimpleDateFormat to format the Date</li>
 * <li><b>p</b>: Package of the calling class</li>
 * <li><b>c</b>: short class name</li>
 * <li><b>C</b>: full class name</li>
 * <li><b>f</b>: file name</li>
 * <li><b>m</b>: method</li>
 * <li><b>l</b>: line number</li>
 * <li><b>M</b>: log message, sent to debug/warn etc. </li>
 * </ul>
 * </p>
 * <p>
 * The default format string is "<i>%C.%m:%l: %M</i>". A debug message would look like this: <br/>
 * <i><pre>12-12 13:10:41.420: D/APLocActivity(31028): at.fhstp.aploc.activities.MainActivity.init[53]: display: 1280x800 orientation:0</pre></i>
 * 
 * </p>
 * @author Paul Woelfel (paul@woelfel.at)
 * @see java.text.SimpleDateFormat, android.util.Log
 */
public class Logger {

	/**
	 * the log format to use for all messages
	 */
	protected static String logFormat = "%C.%m:%l: %M";

	/**
	 * date format to format dates
	 * @see java.text.SimpleDateFormat
	 */
	protected static String dateFormat = "yyyy.MM.dd HH:mm:ss.S";

	/**
	 * android log message tag
	 */
	protected String tag;

	/**
	 * default constructor, sets tag to <i>"Logger"</i>
	 */
	public Logger() {
		tag = "Logger";
	}

	/**
	 * constructor with log Tag
	 * @param logTag tag to use in android log messages
	 */
	public Logger(String logTag) {
		tag = logTag;
	}

	/**
	 * constructor with class, uses the class name as android log tag.
	 * @param className class to use for android log tag
	 */
	public Logger(Class<?> className) {
		tag = className.getName();
	}

	/**
	 * debug log message
	 * @param msg log message
	 */
	public void debug(String msg) {
		Log.d(tag, formatMessage(msg));
	}

	/**
	 * debug log message
	 * @param msg log message
	 * @param tr Throwable to log
	 */
	public void debug(String msg, Throwable tr) {
		Log.d(tag, formatMessage(msg), tr);
	}

	/**
	 * verbose log message
	 * @param msg log message
	 */
	public void verbose(String msg) {
		Log.v(tag, formatMessage(msg));
	}

	/**
	 * verbose log message
	 * @param msg log message
	 * @param tr Throwable to log
	 */
	public void verbose(String msg, Throwable tr) {
		Log.v(tag, formatMessage(msg), tr);
	}
	
	/**
	 * info log message
	 * @param msg log message
	 */
	public void info(String msg) {
		Log.i(tag, formatMessage(msg));
	}

	/**
	 * info log message
	 * @param msg log message
	 * @param tr Throwable to log
	 */
	public void info(String msg, Throwable tr) {
		Log.i(tag, formatMessage(msg), tr);
	}

	/**
	 * warn log message
	 * @param msg log message
	 */
	public void warn(String msg) {
		Log.w(tag, formatMessage(msg));
	}

	/**
	 * warn log message
	 * @param msg log message
	 * @param tr Throwable to log
	 */
	public void warn(String msg, Throwable tr) {
		Log.w(tag, formatMessage(msg), tr);
	}

	/**
	 * error log message
	 * @param msg log message
	 */
	public void error(String msg) {
		Log.e(tag, formatMessage(msg));
	}

	/**
	 * error log message
	 * @param msg log message
	 * @param tr Throwable to log
	 */
	public void error(String msg, Throwable tr) {
		Log.e(tag, formatMessage(msg), tr);
	}

	/**
	 * wtf log message
	 * @param msg log message
	 */
	public void wtf(String msg) {
		Log.wtf(tag, formatMessage(msg));
	}

	/**
	 * wtf log message
	 * @param msg log message
	 * @param tr Throwable to log
	 */
	public void wtf(String msg, Throwable tr) {
		Log.wtf(tag, formatMessage(msg), tr);
	}

	
	/**
	 * create an assert logging message
	 * @param logIfTrue write the log message if true
	 * @param msg log message
	 */
	public void azzert(boolean logIfTrue,String msg){
		if(logIfTrue)
			Log.wtf(tag, msg);
	}


	/**
	 * format a message according to logFormat and dateFormat
	 * @param msg the log message
	 * @return formated string
	 */
	protected static String formatMessage(String msg) {
		StringBuffer log = new StringBuffer();

		
		StackTraceElement caller = Thread.currentThread().getStackTrace()[4];

		for (int i = 0; i < logFormat.length(); i++) {

			if (logFormat.charAt(i) == '%') {

				switch (logFormat.charAt(i + 1)) {
				case 'd':
					// date replacement
					log.append(new SimpleDateFormat(dateFormat).format(new Date()));
					break;

				case 'p':
					// package
					log.append(caller.getClassName().replaceAll("\\..*$", ""));
					break;

				case 'C':
					// full class name
					log.append(caller.getClassName());
					break;

				case 'c':
					// short class name
					String cn = caller.getClassName();
					log.append(cn.contains(".") ? cn.substring(caller.getClassName().lastIndexOf(".") + 1) : cn);
					break;

				case 'f':
					// file name
					log.append(caller.getFileName());
					break;

				case 'm':
					// method name
					log.append(caller.getMethodName());
					break;

				case 'l':
					// line number
					log.append(caller.getLineNumber());
					break;

				case 'M':
					// log message
					log.append(msg);
					break;
					
				case '%':
					// % sign
					log.append("%");
					break;
					
				case 'n':
					// new line
					log.append("\n");
					break;
					
				default:
					log.append("%"+logFormat.charAt(i + 1));
					break;
						
				}

				// skip next character
				i++;
			} else {
				// just add the character
				log.append(logFormat.charAt(i));
			}

		}

		return log.toString();
	}

	/**
	 * get the log format
	 * @return logFormat
	 */
	public static String getLogFormat() {
		return logFormat;
	}

	/**
	 * set the log format
	 * @param logFormat new log format
	 */
	public static void setLogFormat(String logFormat) {
		Logger.logFormat = logFormat;
	}

	/**
	 * get the current date format
	 * @return date format
	 */
	public static String getDateFormat() {
		return dateFormat;
	}

	/**
	 * set the date format
	 * @param dateFormat new date format
	 */
	public static void setDateFormat(String dateFormat) {
		Logger.dateFormat = dateFormat;
	}

}
