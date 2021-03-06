package ch.uzh.ifi.dbimpl.stholes.data;

import java.awt.geom.Rectangle2D;

public class Query {

	private final double xMin, xMax, yMin, yMax;

	public Query(double xMin, double xMax, double yMin, double yMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}

	@Override
	public String toString() {
		return "[Query xMin: " + xMin + ", xMax: " + xMax + ", yMin: " + yMin + ", yMax: " + yMax + "]";
	}

	public Rectangle2D.Double getRectangle2D() {
		return new Rectangle2D.Double(xMin, yMin, xMax - xMin, yMax - yMin);
	}

	public double getXMin() {
		return xMin;
	}

	public double getXMax() {
		return xMax;
	}

	public double getYMin() {
		return yMin;
	}

	public double getYMax() {
		return yMax;
	}

	public double getSelectivity() {
		return (xMax - xMin) * (yMax - yMin);
	}
}
