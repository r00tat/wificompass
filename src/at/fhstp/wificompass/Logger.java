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
 * <li><b>M</b>: log message, sent to debug/warn etc.</li>
 * </ul>
 * </p>
 * <p>
 * The default format string is "<i>.%m:%l: %M</i>". A debug message would look like this: <br/>
 * <i>
 * 
 * <pre>
 * 12-12 13:10:41.420: D/APLocActivity(31028): at.fhstp.aploc.activities.MainActivity.init[53]: display: 1280x800 orientation:0
 * </pre>
 * 
 * </i>
 * 
 * </p>
 * 
 * @author Paul Woelfel (paul@woelfel.at)
 * @see java.text.SimpleDateFormat, android.util.Log
 */
public class Logger {

	/**
	 * the log format to use for all messages
	 */
	protected static String logFormat = ".%m:%l: %M";

	/**
	 * date format to format dates
	 * 
	 * @see java.text.SimpleDateFormat
	 */
	protected static String dateFormat = "yyyy.MM.dd HH:mm:ss.S";

	/**
	 * android log message tag
	 */
	protected String tag;

	protected boolean[] enabled;

	/**
	 * default constructor, sets tag to <i>"Logger"</i>
	 */
	public Logger() {
		tag = "Logger";
		setLoggingEnabled();
	}

	protected void setLoggingEnabled() {
		enabled = new boolean[7];
		enabled[Log.VERBOSE] = Log.isLoggable(tag, Log.VERBOSE);
		enabled[Log.DEBUG] = Log.isLoggable(tag, Log.DEBUG);
		enabled[Log.INFO] = Log.isLoggable(tag, Log.INFO);
		enabled[Log.WARN] = Log.isLoggable(tag, Log.WARN);
		enabled[Log.ERROR] = Log.isLoggable(tag, Log.ERROR);

	}

	/**
	 * constructor with log Tag
	 * 
	 * @param logTag
	 *            tag to use in android log messages
	 */
	public Logger(String logTag) {
		tag = logTag;
		setLoggingEnabled();
	}

	/**
	 * constructor with class, uses the class name as android log tag.
	 * 
	 * @param className
	 *            class to use for android log tag
	 */
	public Logger(Class<?> className) {
		tag = className.getSimpleName();
	}


	/**
	 * debug log message
	 * 
	 * @param msg
	 *            log message
	 */
	public static void d(String msg) {

		logMessage(Log.DEBUG, null, msg, null);
	}

	/**
	 * debug log message
	 * 
	 * @param msg
	 *            log message
	 */
	public void debug(String msg) {
		logMessage(Log.DEBUG, tag, msg, null);
	}

	/**
	 * debug log message
	 * 
	 * @param msg
	 *            log message
	 * @param tr
	 *            Throwable to log
	 */
	public void debug(String msg, Throwable tr) {
		logMessage(Log.DEBUG, tag, msg, tr);
	}

	/**
	 * debug log message
	 * 
	 * @param msg
	 *            log message
	 * @param tr
	 *            Throwable to log
	 */
	public static void d(String msg, Throwable tr) {
		logMessage(Log.DEBUG, null, msg, tr);
	}

	/**
	 * verbose log message
	 * 
	 * @param msg
	 *            log message
	 */
	public void verbose(String msg) {
		logMessage(Log.VERBOSE, tag, msg, null);
	}

	/**
	 * verbose log message
	 * 
	 * @param msg
	 *            log message
	 */
	public static void v(String msg) {
		logMessage(Log.VERBOSE, null, msg, null);
	}

	/**
	 * verbose log message
	 * 
	 * @param msg
	 *            log message
	 * @param tr
	 *            Throwable to log
	 */
	public void verbose(String msg, Throwable tr) {
		logMessage(Log.VERBOSE, tag, msg, tr);
	}

	/**
	 * verbose log message
	 * 
	 * @param msg
	 *            log message
	 * @param tr
	 *            Throwable to log
	 */
	public static void v(String msg, Throwable tr) {
		logMessage(Log.VERBOSE, null, msg, tr);
	}

	/**
	 * info log message
	 * 
	 * @param msg
	 *            log message
	 */
	public void info(String msg) {
		logMessage(Log.INFO, tag, msg, null);
	}

	/**
	 * info log message
	 * 
	 * @param msg
	 *            log message
	 */
	public static void i(String msg) {
		logMessage(Log.INFO, null, msg, null);
	}

	/**
	 * info log message
	 * 
	 * @param msg
	 *            log message
	 * @param tr
	 *            Throwable to log
	 */
	public void info(String msg, Throwable tr) {
		logMessage(Log.INFO, tag, msg, tr);
	}

	/**
	 * info log message
	 * 
	 * @param msg
	 *            log message
	 * @param tr
	 *            Throwable to log
	 */
	public static void i(String msg, Throwable tr) {
		logMessage(Log.INFO, null, msg, tr);
	}

	/**
	 * warn log message
	 * 
	 * @param msg
	 *            log message
	 */
	public void warn(String msg) {
		logMessage(Log.WARN, tag, msg, null);
	}

	/**
	 * warn log message
	 * 
	 * @param msg
	 *            log message
	 */
	public static void w(String msg) {
		logMessage(Log.WARN, null, msg, null);
	}

	/**
	 * warn log message
	 * 
	 * @param msg
	 *            log message
	 * @param tr
	 *            Throwable to log
	 */
	public void warn(String msg, Throwable tr) {
		logMessage(Log.WARN, tag, msg, tr);
	}

	/**
	 * warn log message
	 * 
	 * @param msg
	 *            log message
	 * @param tr
	 *            Throwable to log
	 */
	public static void w(String msg, Throwable tr) {
		logMessage(Log.WARN, null, msg, tr);
	}

	/**
	 * error log message
	 * 
	 * @param msg
	 *            log message
	 */
	public void error(String msg) {
		logMessage(Log.ERROR, tag, msg, null);
	}

	/**
	 * error log message
	 * 
	 * @param msg
	 *            log message
	 */
	public static void e(String msg) {
		logMessage(Log.ERROR, null, msg, null);
	}

	/**
	 * error log message
	 * 
	 * @param msg
	 *            log message
	 * @param tr
	 *            Throwable to log
	 */
	public void error(String msg, Throwable tr) {
		logMessage(Log.ERROR, tag, msg, tr);
	}

	/**
	 * error log message
	 * 
	 * @param msg
	 *            log message
	 * @param tr
	 *            Throwable to log
	 */
	public static void e(String msg, Throwable tr) {
		logMessage(Log.ERROR, null, msg, tr);
	}

	/**
	 * wtf log message
	 * 
	 * @param msg
	 *            log message
	 */
	public void wtf(String msg) {
		logMessage(Log.ASSERT, tag, msg, null);
	}

	/**
	 * wtf log message
	 * 
	 * @param msg
	 *            log message
	 */
	public static void f(String msg) {
		logMessage(Log.ASSERT, null, msg, null);
	}

	/**
	 * wtf log message
	 * 
	 * @param msg
	 *            log message
	 * @param tr
	 *            Throwable to log
	 */
	public void wtf(String msg, Throwable tr) {
		logMessage(Log.ASSERT, tag, msg, tr);
	}

	/**
	 * wtf log message
	 * 
	 * @param msg
	 *            log message
	 * @param tr
	 *            Throwable to log
	 */
	public static void f(String msg, Throwable tr) {
		logMessage(Log.ASSERT, null, msg, tr);
	}

	/**
	 * create an assert logging message
	 * 
	 * @param logIfTrue
	 *            write the log message if true
	 * @param msg
	 *            log message
	 */
	public void azzert(boolean logIfTrue, String msg) {
		if (logIfTrue)
			logMessage(Log.ASSERT, tag, msg, null);
	}

	/**
	 * format a message according to logFormat and dateFormat and log it
	 * 
	 * @param level
	 *            log level to use
	 * @param tag
	 *            tag, if existent
	 * @param msg
	 *            the log message
	 * @param exception
	 *            exception, if existent
	 */
	protected static void logMessage(int level, String tag, String msg, Throwable exception) {
		StringBuffer log = new StringBuffer();

		StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
		
		if (tag == null) {
			tag = caller.getClass().getSimpleName();
		}

		for (int i = 0; i < logFormat.length(); i++) {

			if (logFormat.charAt(i) == '%') {

				switch (logFormat.charAt(i + 1)) {
				case 'd':
					// date replacement
					log.append(new SimpleDateFormat(dateFormat).format(new Date()));
					break;

				case 'p':
					// package
					log.append(caller.getClass().getPackage().toString());
					break;

				case 'C':
					// full class name
					log.append(caller.getClassName());
					break;

				case 'c':
					// short class name

					log.append(caller.getClass().getSimpleName());
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
					log.append("%" + logFormat.charAt(i + 1));
					break;

				}

				// skip next character
				i++;
			} else {
				// just add the character
				log.append(logFormat.charAt(i));
			}

		}

		if (exception == null) {
			switch (level) {
			case Log.VERBOSE:
				Log.v(tag, log.toString());
				break;
			case Log.DEBUG:
				Log.d(tag, log.toString());
				break;
			case Log.INFO:
				Log.i(tag, log.toString());
				break;
			case Log.WARN:
				Log.w(tag, log.toString());
				break;
			case Log.ERROR:
				Log.e(tag, log.toString());
				break;
			case Log.ASSERT:
				Log.wtf(tag, log.toString());
				break;
			}
		} else {
			switch (level) {
			case Log.VERBOSE:
				Log.v(tag, log.toString(), exception);
				break;
			case Log.DEBUG:
				Log.d(tag, log.toString(), exception);
				break;
			case Log.INFO:
				Log.i(tag, log.toString(), exception);
				break;
			case Log.WARN:
				Log.w(tag, log.toString(), exception);
				break;
			case Log.ERROR:
				Log.e(tag, log.toString(), exception);
				break;
			case Log.ASSERT:
				Log.wtf(tag, log.toString(), exception);
				break;
			}
		}

		// return log.toString();
	}

	/**
	 * get the log format
	 * 
	 * @return logFormat
	 */
	public static String getLogFormat() {
		return logFormat;
	}

	/**
	 * set the log format
	 * 
	 * @param logFormat
	 *            new log format
	 */
	public static void setLogFormat(String logFormat) {
		Logger.logFormat = logFormat;
	}

	/**
	 * get the current date format
	 * 
	 * @return date format
	 */
	public static String getDateFormat() {
		return dateFormat;
	}

	/**
	 * set the date format
	 * 
	 * @param dateFormat
	 *            new date format
	 */
	public static void setDateFormat(String dateFormat) {
		Logger.dateFormat = dateFormat;
	}

}
