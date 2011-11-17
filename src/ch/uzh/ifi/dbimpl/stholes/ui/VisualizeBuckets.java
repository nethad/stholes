package ch.uzh.ifi.dbimpl.stholes.ui;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ch.uzh.ifi.dbimpl.stholes.HistorgramFactory;
import ch.uzh.ifi.dbimpl.stholes.data.Bucket;
import ch.uzh.ifi.dbimpl.stholes.data.Query;

public class VisualizeBuckets {

	private JFrame frame;
	private BucketCanvas canvas;

	public VisualizeBuckets() {
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
		canvas = new BucketCanvas();
		mainPanel.add(canvas);
		this.frame.add(mainPanel);
		this.frame.setVisible(true);
	}

	public void drawHistogram(Bucket bucket, Query query) {
		canvas.setRootBucket(bucket);
		canvas.setQuery(query);
	}

	public static void main(String[] args) {
		Bucket bucket;
		Query query = new Query(0.2, 0.8, 0.0, 1.0);

		// bucket = HistorgramFactory.CreateHistogram1();
		// new VisualizeBuckets().drawHistogram(bucket);
		// bucket = HistorgramFactory.CreateHistogram2();
		// new VisualizeBuckets().drawHistogram(bucket);
		// bucket = HistorgramFactory.CreateHistogram3();
		// new VisualizeBuckets().drawHistogram(bucket);
		// bucket = HistorgramFactory.CreateHistogram4();
		// new VisualizeBuckets().drawHistogram(bucket);
		// bucket = HistorgramFactory.CreateHistogram5();
		// new VisualizeBuckets().drawHistogram(bucket);
		// bucket = HistorgramFactory.CreateHistogram6();
		// new VisualizeBuckets().drawHistogram(bucket);
		bucket = HistorgramFactory.CreateHistogram7();
		new VisualizeBuckets().drawHistogram(bucket, query);
		// bucket = HistorgramFactory.CreateHistogram8();
		// new VisualizeBuckets().drawHistogram(bucket);
		// bucket = HistorgramFactory.CreateHistogram9();
		// new VisualizeBuckets().drawHistogram(bucket);
	}

}
