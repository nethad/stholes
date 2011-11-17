package ch.uzh.ifi.dbimpl.stholes.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class DataCanvas extends JComponent {

	private static final int SCALE_FACTOR = 700;
	private static final int PADDING = 10;

	private static final Color BACKGROUND_COLOR = Color.white;
	private static final Color POINT_COLOR = Color.black;

	private List<Point> dataPoints;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		g.setColor(POINT_COLOR);
		g.drawRect(PADDING, PADDING, SCALE_FACTOR, SCALE_FACTOR);

		if (dataPoints != null) {
			for (Point point : dataPoints) {
				g.drawRect(point.x, point.y, 1, 1);
			}
		}
	}

	public void setDataPoints(List<Point2D.Double> oDataPoints) {
		this.dataPoints = new ArrayList<Point>();
		for (Point2D.Double dataPoint : oDataPoints) {
			this.dataPoints.add(scalePoint(dataPoint));
		}
	}

	private Point scalePoint(Point2D.Double dataPoint) {
		return new Point((int) (PADDING + (dataPoint.x * SCALE_FACTOR)),
				(int) (PADDING + (dataPoint.y * SCALE_FACTOR)));
	}

}
