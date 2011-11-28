package ch.uzh.ifi.dbimpl.stholes.ui;

import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JComponent;

import ch.uzh.ifi.dbimpl.stholes.data.Bucket;
import ch.uzh.ifi.dbimpl.stholes.data.Query;

public class VisualizeSTHoles extends VisualizationWindow {

	private CompleteCanvas canvas;

	@Override
	protected JComponent newCanvas() {
		canvas = new CompleteCanvas();
		return canvas;
	}

	public void setDataPoints(List<Point2D.Double> dataPoints) {
		canvas.setDataPoints(dataPoints);
	}

	public void setCurrentQuery(Query query) {
		canvas.setQuery(query);
	}

	public void setRootBucket(Bucket bucket) {
		canvas.setRootBucket(bucket);
	}

	public void setQueryNumber(int queryNumber) {
		setTitle(DEFAULT_TITLE + " query #" + queryNumber);
	}

}
