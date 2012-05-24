package at.fhstp.wificompass.trilateration;

import java.util.Iterator;
import java.util.Vector;

import Jama.Matrix;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PointF;
import at.fhstp.wificompass.ToolBox;
import at.fhstp.wificompass.model.AccessPoint;
import at.fhstp.wificompass.model.ProjectSite;
import at.fhstp.wificompass.view.MultiTouchDrawable;

public class LocalSignalStrengthGradientTrilateration extends
		AccessPointTrilateration {

	/**
	 * Half the size of the square (in meters) in which measurement points are
	 * considered to have similar attenuation
	 */
	protected static float windowSize = 2;
	protected static float g = -0.4f;

	public LocalSignalStrengthGradientTrilateration(Context context,
			ProjectSite projectSite) {
		super(context, projectSite);
	}
	
	public LocalSignalStrengthGradientTrilateration(Context context,
			ProjectSite projectSite, ProgressDialog progressDialog) {
		super(context, projectSite, progressDialog);
	}

	@Override
	public PointF calculateAccessPointPosition(AccessPoint ap) {

		Vector<MeasurementDataSet> originalData = this.measurementData.get(ap);
		Vector<GradientArrow> arrows = new Vector<GradientArrow>();
		Vector<MeasurementDataSet> data = new Vector<MeasurementDataSet>();

		if (originalData.size() > 3) {

			float sumRssi = 0;

			for (Iterator<MeasurementDataSet> it = originalData.iterator(); it
					.hasNext();) {
				MeasurementDataSet dataSet = it.next();

				float newRssi = (float) Math.pow(
						Math.pow(10, dataSet.getRssi() / 20), g);
				sumRssi += newRssi;

				data.add(new MeasurementDataSet(dataSet.getX(), dataSet.getY(),
						newRssi));
			}

			for (Iterator<MeasurementDataSet> it = data.iterator(); it
					.hasNext();) {
				MeasurementDataSet currentDataSet = it.next();

				Vector<MeasurementDataSet> dataSetsWithinWindow = this
						.getMeasurementDataWithinWindow(data, currentDataSet,
								windowSize);

				// Make sure we can fit a plane, so we need at least 3 points.
				// And make sure the measurement points are not collinear
				if (dataSetsWithinWindow.size() >= 3
						&& !areAllMeasurementsCollinear(dataSetsWithinWindow)) {

					Matrix matrixA = new Matrix(dataSetsWithinWindow.size(), 3);
					Matrix matrixZ = new Matrix(dataSetsWithinWindow.size(), 1);

					for (int i = 0; i < dataSetsWithinWindow.size(); i++) {
						MeasurementDataSet dataSet = dataSetsWithinWindow
								.get(i);
						matrixA.set(i, 0, dataSet.getX());
						matrixA.set(i, 1, dataSet.getY());
						matrixA.set(i, 2, 1);

						matrixZ.set(i, 0, dataSet.getRssi());
					}

					// Least squares solution
					Matrix matrixC = matrixA.solve(matrixZ);

					float weight = currentDataSet.rssi / sumRssi;
					// Logger.d("Weight: " + weight + " (current RSSI: "
					// + currentDataSet.rssi + ", sum: " + sumRssi + ")");

					GradientArrow arrow = new GradientArrow(currentDataSet.x,
							currentDataSet.y, (float) matrixC.get(0, 0),
							(float) matrixC.get(1, 0), weight);
					arrows.add(arrow);
				}
			}

			// Steps (in meters) by which the area is divided in order to
			// calculate the heat map
			float step = 1.0f * MultiTouchDrawable.getGridSpacingX();
			float areaFactor = 2.0f; // Factor by which the area is expanded

			float minX = roundToGridSpacing(MeasurementDataSet.getMinimumValue(
					data, MeasurementDataSet.VALUE_X));
			float maxX = roundToGridSpacing(MeasurementDataSet.getMaximumValue(
					data, MeasurementDataSet.VALUE_X));
			float minY = roundToGridSpacing(MeasurementDataSet.getMinimumValue(
					data, MeasurementDataSet.VALUE_Y));
			float maxY = roundToGridSpacing(MeasurementDataSet.getMaximumValue(
					data, MeasurementDataSet.VALUE_Y));

			float centerX = (maxX - minX) / 2.0f + minX;
			float dX = Math.abs(maxX - centerX);
			float centerY = (maxY - minY) / 2.0f + minY;
			float dY = Math.abs(maxY - centerY);

			float areaMinX = centerX - dX * areaFactor;
			float areaMaxX = centerX + dX * areaFactor;
			float areaMinY = centerY - dY * areaFactor;
			float areaMaxY = centerY + dY * areaFactor;

			float sumP = 0;
			Vector<MeasurementDataSet> probabilities = new Vector<MeasurementDataSet>();

			for (float x = areaMinX; x <= areaMaxX; x += step) {
				for (float y = areaMinY; y <= areaMaxY; y += step) {
					for (GradientArrow arrow : arrows) {
						float arrowAngle = ToolBox.normalizeAngle((float) Math
								.atan2(arrow.directionY, arrow.directionX));
						float pointAngle = ToolBox.normalizeAngle((float) Math
								.atan2(y - arrow.y, x - arrow.x));

						float angleDifference = Math.abs(arrowAngle
								- pointAngle);

						// Always use the minimum difference of the two angles
						// (190 degrees would be 170 then)
						if (angleDifference > Math.PI)
							angleDifference = (float) (2 * Math.PI - angleDifference);

						// Logger.d("Angle difference: " + x + ", " + y + ", " +
						// angleDifference);

						sumP += (float) Math.pow(angleDifference, 2)
								* arrow.weight;
					}

					probabilities.add(new MeasurementDataSet(x, y, sumP));
					sumP = 0.0f;
				}
				// Logger.d("Done with " + (x - areaMinX) / (areaMaxX -
				// areaMinX) * 100 + " %");
			}

			float minProb = 0.0f;
			float minProbX = 0.0f;
			float minProbY = 0.0f;

			for (MeasurementDataSet probability : probabilities) {

				// Logger.d("Probability: x(" + probability.getX() + "), y(" +
				// probability.getY() + "), prob(" + probability.getRssi() +
				// ")");

				if ((probabilities.indexOf(probability) == 0)
						|| (probability.getRssi() < minProb)) {
					minProb = probability.getRssi();
					minProbX = probability.getX();
					minProbY = probability.getY();
				}
			}

			return new PointF(minProbX, minProbY);
		} else {
			return null;
		}
	}

	/**
	 * Returns a vector of all points that are located within the statically
	 * configured window.
	 * 
	 * @param data
	 *            The data to search
	 * @param dataSet
	 *            The center point
	 * @param windowSize
	 *            Half the size of the square to search
	 * @return A vector of all points that are inside the window
	 */
	protected Vector<MeasurementDataSet> getMeasurementDataWithinWindow(
			Vector<MeasurementDataSet> data, MeasurementDataSet dataSet,
			float windowSize) {

		Vector<MeasurementDataSet> result = new Vector<MeasurementDataSet>();

		// We walk through all the measuring points given and return only those
		// which are inside the window
		for (MeasurementDataSet currentDataSet : data) {
			// Let's see if the current point is located within the window
			if (currentDataSet.getX() >= dataSet.getX() - windowSize
					* MultiTouchDrawable.getGridSpacingX()
					&& currentDataSet.getX() <= dataSet.getX() + windowSize
							* MultiTouchDrawable.getGridSpacingX()
					&& currentDataSet.getY() >= dataSet.getY() - windowSize
							* MultiTouchDrawable.getGridSpacingY()
					&& currentDataSet.getY() <= dataSet.getY() + windowSize
							* MultiTouchDrawable.getGridSpacingY())
				// If so, add it to the results
				result.add(currentDataSet);
		}

		return result;
	}

	/**
	 * Returns whether a set of points is collinear, or in other words, in one
	 * line.
	 * 
	 * @param points
	 *            The points to check
	 * @return <b>true</b> if the points are collinear, <b>false</b> if not
	 */
	protected boolean areAllMeasurementsCollinear(
			Vector<MeasurementDataSet> measurements) {

		if (measurements.size() < 3) {
			return true;
		} else {
			PointF p1 = measurements.get(0).getPointF();
			PointF p2 = measurements.get(1).getPointF();

			for (int i = 2; i < measurements.size(); i++)
				if (!arePointsCollinear(p1, p2, measurements.get(i).getPointF()))
					return false;

			return true;
		}
	}

	/**
	 * Checks whether three points are collinear, or in other words, in one
	 * line. Mathematically, it calculates the area of the triangle of the three
	 * points and checks whether this value lies within a certain threshold.
	 * 
	 * @param p1
	 *            The first point
	 * @param p2
	 *            The second point
	 * @param p3
	 *            The third point
	 * 
	 * @return Whether or not the three points are in one line
	 */
	protected boolean arePointsCollinear(PointF p1, PointF p2, PointF p3) {

		float threshold = 0.1f;
		float area = Math
				.abs((p1.x * (p2.y - p3.y) + p2.x * (p3.y - p1.y) + p3.x
						* (p1.y - p2.y)) / 2.0f);

		return (area <= threshold) ? true : false;
	}

	protected float roundToGridSpacing(float value) {
		return Math.round(value / MultiTouchDrawable.getGridSpacingX())
				* MultiTouchDrawable.getGridSpacingX();
	}

	/**
	 * This class holds an arrow needed for the Local Signal Strength Gradient
	 * algorithm
	 * 
	 * @author tom
	 * 
	 */
	protected class GradientArrow {
		public float x;
		public float y;
		public float directionX;
		public float directionY;
		public float weight;

		public GradientArrow() {

		}

		public GradientArrow(float x, float y, float directionX,
				float directionY, float weight) {
			this.x = x;
			this.y = y;
			this.directionX = directionX;
			this.directionY = directionY;
			this.weight = weight;
		}
	}
}
