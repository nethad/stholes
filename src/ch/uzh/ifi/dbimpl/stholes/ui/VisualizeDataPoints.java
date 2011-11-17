package ch.uzh.ifi.dbimpl.stholes.ui;

import java.awt.geom.Point2D.Double;
import java.util.List;

import javax.swing.JComponent;

import ch.uzh.ifi.dbimpl.stholes.DefaultDatabase;

public class VisualizeDataPoints extends VisualizationWindow {

	private DataCanvas canvas;

	public void drawHistogram(List<Double> dataPoints) {
		canvas.setDataPoints(dataPoints);
	}

	public static void main(String[] args) {

		VisualizeDataPoints visualizeDataPoints = new VisualizeDataPoints();

		DefaultDatabase defaultDatabase = new DefaultDatabase("db/random");
		List<Double> dataPoints = defaultDatabase.getAllDataPoints();

		visualizeDataPoints.drawHistogram(dataPoints);
	}

	@Override
	protected JComponent newCanvas() {
		canvas = new DataCanvas();
		return canvas;
	}

}
