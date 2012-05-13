package at.fhstp.wificompass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import at.fhstp.wificompass.userlocation.StepDetection;
import de.uvwxy.footpath.ToolBox;

/**
 * 
 * @author Paul Smith, Paul Woelfel
 *
 */
public class PaintBoxHistory  extends View implements Handler.Callback {
	//private Context context;

	private int historySize;
	private double[] x_History;
	private double[] y_History;
	private double[] z_History;
	private long[] time_History;
	private int historyPtr = 0;
	private double valueRange = VALUE_RANGE_DEFAULT;
	private int drawWidth = 0;
	private int drawHeight = 0;
	//private double scale_x = 1;
	private double scale_y = 1;
	private int offset_y = 0;
	boolean once = true;
	
	
	protected Handler messageHandler;
	
	
	private int seconds = 1; // # of seconds to show on screen
	private int num_steps = 0;
	
	protected static final int HISTORY_SECONDS=4;
	protected static final double VALUE_RANGE_DEFAULT=48.0d;
	
	public static final int MESSAGE_REFRESH_HISTORY = 1;


	
	public PaintBoxHistory(Context context){
		this(context,null,0);
	}
	
	public PaintBoxHistory(Context context, AttributeSet attrs){
		this(context,attrs,0);
	}
	
	
	/**
	 * 
	 * @param context
	 *            the context under which is painted
	 */
	public PaintBoxHistory(Context context, AttributeSet attrs,int defStyle) {
		super(context,attrs,defStyle);
		
		// save to have e.g. access to asserts
//		this.context = context;
		this.valueRange = VALUE_RANGE_DEFAULT;
		this.historySize = (int) (HISTORY_SECONDS*1000 / StepDetection.INTERVAL_MS);
		x_History = new double[historySize];
		y_History = new double[historySize];
		z_History = new double[historySize];
		time_History = new long[historySize];
		this.seconds = HISTORY_SECONDS;
		
		messageHandler=new Handler(this);
		// set surface callback
		
//		getHolder().addCallback(this);
	}

	private int getPosOnScreen(long x_ms, long current_ms) {
		long diff_ms = current_ms - x_ms;

		// addition because diff_ms is negative!
		int res = (int) (drawWidth - (((double) drawWidth / (double) seconds) / 1000.0) * diff_ms);
		return res;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (once) {
			setDimensions();
		}
		
//		Logger.d("drawing paintboxhistory");
		
		canvas.drawColor(Color.WHITE);
		canvas.drawLine(0, offset_y, drawWidth, offset_y, ToolBox.myPaint(1, Color.BLACK));
		Paint paint = ToolBox.myPaint(2, Color.RED);
		paint.setTextSize(40.0f);
		canvas.drawText("Steps: " + num_steps, 10, getHeight()-40, paint);
		long uptime_ms = System.currentTimeMillis();
		
		for(long ts : tenLastSteps){
			canvas.drawLine(getPosOnScreen(ts, uptime_ms), 0,
					getPosOnScreen(ts, uptime_ms), drawHeight, ToolBox.myPaint(2, Color.RED));
		}
		
		drawDataSet(canvas, x_History, ToolBox.myPaint(2, Color.RED), uptime_ms);
		drawDataSet(canvas, y_History, ToolBox.myPaint(2, Color.GREEN), uptime_ms);
		drawDataSet(canvas, z_History, ToolBox.myPaint(2, Color.BLUE), uptime_ms);
		
		canvas.drawText("" + varianceOfSet(x_History), 10, 10, ToolBox.myPaint(2, Color.BLACK));
		canvas.drawText("" + varianceOfSet(y_History), 10, 32, ToolBox.myPaint(2, Color.BLACK));
		canvas.drawText("" + varianceOfSet(z_History), 10, 54, ToolBox.myPaint(2, Color.BLACK));
	}

	private void drawDataSet(Canvas canvas, double[] set, Paint paint, long uptime_ms) {
		int item0, item1;
		for (int i = 0; i < historySize - 1; i++) {
			item0 = (historyPtr + 1 + i) % historySize;
			item1 = (historyPtr + 2 + i) % historySize;
			double y1 = -set[item0] * scale_y + offset_y;
			double y2 = -set[item1] * scale_y + offset_y;

			canvas.drawLine(getPosOnScreen(time_History[item0], uptime_ms), (int) y1,
					getPosOnScreen(time_History[item1], uptime_ms), (int) y2, paint);
			
		}		
	}

	public void setDimensions() {
		this.drawWidth = this.getWidth();
		this.drawHeight = this.getHeight();
//		scale_x = this.drawWidth / historySize;
		scale_y = this.drawHeight / valueRange;
		offset_y = this.drawHeight / 2;
	}

	/**
	 * Call this timed
	 * @param t
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addTriple(long t, double[] acc) {
		x_History[(historyPtr + 1) % historySize] = acc[0];
		y_History[(historyPtr + 1) % historySize] = acc[1];
		z_History[(historyPtr + 1) % historySize] = acc[2];
		time_History[(historyPtr + 1) % historySize] = t;
		historyPtr++;
	}

	/**
	 * Calculate the mean of a given array
	 * 
	 * @param set
	 *            the input data
	 * @return mean of the input data
	 */
	private double meanOfSet(double[] set) {
		double res = 0.0;
		for (double i : set) {
			res += i;
		}
		return res / set.length;

	}

	/**
	 * Calculate the variance of agiven array
	 * 
	 * @param set
	 *            the input data
	 * @return variance of the input data
	 */
	private double varianceOfSet(double[] set) {
		double res = 0.0;
		double mean = meanOfSet(set);
		for (double i : set) {
			res += (i - mean) * (i - mean);
		}
		return res / set.length;
	}

	
	private int stepHistorySize = 10;
	private long[] tenLastSteps = new long[stepHistorySize];
	private int shPointer = 0;
	
	public void addStepTS(long ts){
		num_steps++;		
		tenLastSteps[shPointer % stepHistorySize] = ts;								// add value to values_history
		shPointer++;
		shPointer = shPointer % stepHistorySize;
	}



	/* (non-Javadoc)
	 * @see android.os.Handler.Callback#handleMessage(android.os.Message)
	 */
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MESSAGE_REFRESH_HISTORY:
			/* Refresh UI */
			invalidate();
			break;
		}
		return true;
	}

	/**
	 * @return the messageHandler
	 */
	public Handler getMessageHandler() {
		return messageHandler;
	}
}
