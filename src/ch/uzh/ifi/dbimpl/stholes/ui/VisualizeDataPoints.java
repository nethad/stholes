package ch.uzh.ifi.dbimpl.stholes.ui;



import java.awt.GridLayout;
import java.awt.geom.Point2D.Double;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ch.uzh.ifi.dbimpl.stholes.DefaultDatabase;

public class VisualizeDataPoints {

	private JFrame frame;
	private DataCanvas canvas;

	public VisualizeDataPoints() {
		initializeGui();
	}

	private void initializeGui() {
		this.frame = new JFrame();
		this.frame.setSize(800, 800);
		this.frame.setLayout(new GridLayout(1, 1));
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setTitle("STHoles");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1, 1));
		canvas = new DataCanvas();
		mainPanel.add(canvas);
		this.frame.add(mainPanel);
		this.frame.setVisible(true);
	}

	public void drawHistogram(List<Double> dataPoints) {
		canvas.setDataPoints(dataPoints);
	}

	public static void main(String[] args) {

		DefaultDatabase defaultDatabase = new DefaultDatabase("db/random");
		List<Double> dataPoints = defaultDatabase.getAllDataPoints();

		new VisualizeDataPoints().drawHistogram(dataPoints);
		// bucket = HistorgramFactory.CreateHistogram8();
		// new VisualizeBuckets().drawHistogram(bucket);
		// bucket = HistorgramFactory.CreateHistogram9();
		// new VisualizeBuckets().drawHistogram(bucket);
	}

}
