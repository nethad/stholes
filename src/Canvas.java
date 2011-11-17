import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class Canvas extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Color BUCKET_COLOR = Color.black;
	private static final Color BACKGROUND_COLOR = Color.white;
	private static final Color QUERY_COLOR = Color.red;
	private static final int SCALE_FACTOR = 700;
	private static final int PADDING = 20;
	private static final int LABEL_X_PADDING = 5;
	private static final int LABEL_Y_PADDING = 15;

	private ArrayList<DrawableRectangle> rectangles;
	private Query query;

	class DrawableRectangle {
		private final Rectangle rectangle;
		private final String labelText;

		public DrawableRectangle(Rectangle rectangle, String labelText) {
			this.rectangle = rectangle;
			this.labelText = labelText;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(BUCKET_COLOR);
		drawRectangles(g);
		g.setColor(QUERY_COLOR);
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
		for (DrawableRectangle dr : this.rectangles) {
			g.drawRect(dr.rectangle.x, dr.rectangle.y, dr.rectangle.width,
					dr.rectangle.height);
			g.drawString(dr.labelText, dr.rectangle.x + LABEL_X_PADDING,
					dr.rectangle.y + LABEL_Y_PADDING);
		}
	}

	public void setRootBucket(Bucket bucket) {
		this.rectangles = new ArrayList<DrawableRectangle>();
		DrawableRectangle drawableRectangle = new DrawableRectangle(
				scaleRectangle(bucket.getRectangle()), String.valueOf(bucket
						.getFrequency()));
		this.rectangles.add(drawableRectangle);
		addRectangles(bucket.getChildren());
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	private void addRectangles(List<Bucket> buckets) {
		for (Bucket childBucket : buckets) {
			DrawableRectangle drawableRectangle = new DrawableRectangle(
					scaleRectangle(childBucket.getRectangle()),
					String.valueOf(childBucket.getFrequency()));
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
