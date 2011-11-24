package ch.uzh.ifi.dbimpl.stholes.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import ch.uzh.ifi.dbimpl.stholes.data.Bucket;
import ch.uzh.ifi.dbimpl.stholes.data.Query;

public class BucketCanvas extends JComponent {

	private static final long serialVersionUID = 1L;
	private static final Color BUCKET_COLOR = Color.black;
	private static final Color BACKGROUND_COLOR = Color.white;
	private static final Color QUERY_COLOR = Color.red;
	private static final int SCALE_FACTOR = 700;
	private static final int PADDING = 20;
	private static final int LABEL_X_PADDING = 5;
	private static final int LABEL_Y_PADDING = 15;
	final static float dash1[] = {10.0f};
	final static BasicStroke dashed = new BasicStroke(2.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);

	private ArrayList<DrawableRectangle> rectangles;
	private Query query;

	class DrawableRectangle {
		private final Rectangle rectangle;
		private final String labelText;
		private final Color color;

		public DrawableRectangle(Rectangle rectangle, String labelText, Color color) {
			this.rectangle = rectangle;
			this.labelText = labelText;
			this.color = color;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		drawRectangles(g);
		g.setColor(QUERY_COLOR);
		((Graphics2D)g).setStroke(dashed);
		drawQuery(g);
	}

	private void drawQuery(Graphics g) {
		if (this.query != null) {
			Rectangle rectangle = scaleRectangle(query.getRectangle2D());
			g.drawRect(rectangle.x, rectangle.y, rectangle.width,
					rectangle.height);
		}
	}

	private void drawRectangles(Graphics g) {
		if(this.rectangles != null) {
			for (DrawableRectangle dr : this.rectangles) {
				g.setColor(dr.color);
				g.drawRect(dr.rectangle.x, dr.rectangle.y, dr.rectangle.width,
						dr.rectangle.height);
				g.drawString(dr.labelText, dr.rectangle.x + LABEL_X_PADDING,
						dr.rectangle.y + LABEL_Y_PADDING);
			}
		}
	}

	public void setRootBucket(Bucket bucket) {
		this.rectangles = new ArrayList<DrawableRectangle>();
		DrawableRectangle drawableRectangle = new DrawableRectangle(
				scaleRectangle(bucket.getRectangle()), String.valueOf(bucket
						.getFrequency()), BUCKET_COLOR);
		this.rectangles.add(drawableRectangle);
		addRectangles(bucket.getChildren());
		repaint();
	}
	
	public void addAdditionalBucket(Bucket bucket, Color color) {
		DrawableRectangle drawableRectangle = new DrawableRectangle(
				scaleRectangle(bucket.getRectangle()), String.valueOf(bucket
						.getFrequency()), color);
		this.rectangles.add(drawableRectangle);
		repaint();		
	}

	public void setQuery(Query query) {
		this.query = query;
		repaint();
	}

	private void addRectangles(List<Bucket> buckets) {
		for (Bucket childBucket : buckets) {
			DrawableRectangle drawableRectangle = new DrawableRectangle(
					scaleRectangle(childBucket.getRectangle()),
					String.valueOf(childBucket.getFrequency()), BUCKET_COLOR);
			this.rectangles.add(drawableRectangle);
			addRectangles(childBucket.getChildren());
		}
	}

	private Rectangle scaleRectangle(Rectangle2D.Double rectangle) {
		int x = (int) (PADDING + rectangle.getX() * SCALE_FACTOR);
		int y = (int) (PADDING + rectangle.getY() * SCALE_FACTOR);
		int width = (int) (rectangle.getWidth() * SCALE_FACTOR);
		int height = (int) (rectangle.getHeight() * SCALE_FACTOR);
		return new Rectangle(x, y, width, height);
	}
}
