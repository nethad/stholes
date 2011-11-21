package ch.uzh.ifi.dbimpl.stholes.ui;

import javax.swing.JComponent;

import ch.uzh.ifi.dbimpl.stholes.HistorgramFactory;
import ch.uzh.ifi.dbimpl.stholes.data.Bucket;
import ch.uzh.ifi.dbimpl.stholes.data.Query;

public class VisualizeBuckets extends VisualizationWindow {

	private BucketCanvas canvas;

	public void drawHistogram(Bucket bucket, Query query) {
		canvas.setRootBucket(bucket);
		canvas.setQuery(query);
	}

	public static void main(String[] args) {
		VisualizeBuckets visualizeBuckets = new VisualizeBuckets();
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
		visualizeBuckets.drawHistogram(bucket, query);
		// bucket = HistorgramFactory.CreateHistogram8();
		// new VisualizeBuckets().drawHistogram(bucket);
		// bucket = HistorgramFactory.CreateHistogram9();
		// new VisualizeBuckets().drawHistogram(bucket);

	}

	@Override
	protected JComponent newCanvas() {
		canvas = new BucketCanvas();
		return canvas;
	}

}
