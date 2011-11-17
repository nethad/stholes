import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

public class Bucket {

	private enum ShrinkDirection {
		H, V
	}

	private int frequency;
	private final Rectangle2D.Double box;
	private final List<Bucket> children;

	public Bucket(double x0, double x1, double y0, double y1) {
		this.box = new Rectangle2D.Double(x0, y0, x1 - x0, y1 - y0);
		this.children = new LinkedList<Bucket>();
	}

	private Bucket(Rectangle2D.Double box, int frequency) {
		this.box = box;
		this.frequency = frequency;
		this.children = new LinkedList<Bucket>();
	}

	/**
	 * Gets the volume v(b) for this bucket. (without the volume of the
	 * children)
	 * 
	 * @return
	 */
	public double getVolume() {
		double volume = volumeForRectangle(this.box);
		for (Bucket childBucket : this.children) {
			volume -= volumeForRectangle(childBucket.box);
		}
		return volume;
	}

	/**
	 * Calculate the intersection volume between the bucket and the query. = v(q
	 * UNION b)
	 * 
	 * @param queryIntersection
	 * @param q
	 * @return
	 */
	private double getIntersectionVolume(Query q) {
		Rectangle2D.Double queryBox = q.getRectangle2D();
		Rectangle2D.Double queryIntersection = (Rectangle2D.Double) this.box
				.createIntersection(queryBox);
		double intersectionVolume = volumeForRectangle(queryIntersection);
		for (Bucket childBucket : this.children) {
			if (childBucket.box.intersects(queryBox)) {
				Rectangle2D.Double intersection = (Rectangle2D.Double) queryBox
						.createIntersection(childBucket.box);
				intersectionVolume -= volumeForRectangle(intersection);
			}
		}

		return intersectionVolume;
	}

	/**
	 * The volume of the overlapping area (query and bucket) multiplied with the
	 * bucket frequency, including the estimates for all children
	 * <p>
	 * est(H,q) = f(b) * ( v(q UNION b) / v(b) ) + est(for all children, q)
	 * </p>
	 * 
	 * @param query
	 *            The query q
	 * @return The estimate for the query q.
	 */
	public double getEstimateForQuery(Query q) {
		Rectangle2D.Double queryBox = q.getRectangle2D();
		if (this.box.intersects(queryBox)) {
			// Calculate the estimate for the bucket. = f(b) * ( v(q UNION b) /
			// v(b) )
			double estimate = this.getIntersectionVolume(q) / this.getVolume()
					* this.frequency;

			// Estimate all children buckets and add them to the current
			// estimate.
			for (Bucket childBucket : children) {
				estimate += childBucket.getEstimateForQuery(q);
			}

			return estimate;
		} else {
			// No intersection with the query. -> Children will also not
			// intersect.
			return 0D;
		}
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getFrequency() {
		return this.frequency;
	}

	public void addChildBucket(Bucket childBucket) {
		if (!box.contains(childBucket.box)) {
			throw new RuntimeException(
					"Trying to add child which is not contained in parten box."
							+ "\n  Parent Box: " + box + "\n  Child Box: "
							+ childBucket.box);
		}
		this.children.add(childBucket);
	}

	public void removeChildBucket(Bucket childBucket) {
		this.children.remove(childBucket);
	}

	public List<Bucket> getChildren() {
		return this.children;
	}

	private static double volumeForRectangle(Rectangle2D.Double box) {
		return box.getWidth() * box.getHeight();
	}

	public Bucket IdentifyCandiate(Query q, int tb) {
		Rectangle2D.Double queryBox = q.getRectangle2D();

		if (this.box.intersects(queryBox)) {
			Rectangle2D.Double c = (Rectangle2D.Double) this.box
					.createIntersection(queryBox);
			List<Bucket> participants = new LinkedList<Bucket>();
			UpdateParticipants(participants, c);

			do {
				double minShrink = Double.MAX_VALUE;
				ShrinkDirection direction = ShrinkDirection.H;

				// Determine the shrink length
				for (Bucket participant : participants) {
					if (c.getMaxX() > participant.box.getMaxX()) {
						// Could Shrink by reducing from the right.
						double rightShrink = participant.box.getMaxX()
								- c.getMinX();
						if (rightShrink > 0 && rightShrink < minShrink) {
							minShrink = -rightShrink;
							direction = ShrinkDirection.H;
						}
					}

					if (c.getMinX() < participant.box.getMinX()) {
						// Could Shrink by reducing from the left.
						double leftShrink = c.getMaxX()
								- participant.box.getMinX();
						if (leftShrink > 0 && leftShrink < minShrink) {
							minShrink = leftShrink;
							direction = ShrinkDirection.H;
						}
					}

					if (c.getMaxY() > participant.box.getMaxY()) {
						// Could Shrink by reducing from the top.
						double topShrink = participant.box.getMaxY()
								- c.getMinY();
						if (topShrink > 0 && topShrink < minShrink) {
							minShrink = -topShrink;
							direction = ShrinkDirection.V;
						}
					}

					if (c.getMinY() < participant.box.getMinY()) {
						// Could Shrink by reducing from the bottom.
						double bottomShrink = c.getMaxY()
								- participant.box.getMinY();
						if (bottomShrink > 0 && bottomShrink < minShrink) {
							minShrink = bottomShrink;
							direction = ShrinkDirection.V;
						}
					}
					/*
					 * if(participant.box.getMaxX()c.getMinX() >=
					 * participant.box.getMinX() && c.getMinX() <
					 * participant.box.getMaxX()) { double startHSrink =
					 * participant.box.getMaxX() - c.getMinX(); if(minHShrink >
					 * startHSrink) { minHShrink = startHSrink; hDirection =
					 * ShrinkDirection.Start; } } if(c.getMaxX() >=
					 * participant.box.getMinX() && c.getMaxX() <
					 * participant.box.getMaxX()) { double endHSrink =
					 * c.getMaxX() - participant.box.getMinX(); if(minHShrink >
					 * endHSrink) { minHShrink = endHSrink; hDirection =
					 * ShrinkDirection.End; } } if(c.getMinY() >=
					 * participant.box.getMinY() && c.getMinY() <
					 * participant.box.getMaxY()) { double startVSrink =
					 * participant.box.getMaxY() - c.getMinY(); if(minVShrink >
					 * startVSrink) { minHShrink = startVSrink; vDirection =
					 * ShrinkDirection.Start; } } if(c.getMaxY() >=
					 * participant.box.getMinY() && c.getMaxY() <
					 * participant.box.getMaxY()) { double endVSrink =
					 * c.getMaxY() - participant.box.getMinY(); if(minVShrink >
					 * endVSrink) { minHShrink = endVSrink; vDirection =
					 * ShrinkDirection.End; } }
					 */
				}

				/*
				 * if(minHShrink != Double.MAX_VALUE) { c.width -= minHShrink;
				 * if(hDirection == ShrinkDirection.Start) { c.x += minHShrink;
				 * } }
				 * 
				 * if(minVShrink != Double.MAX_VALUE) { c.height -= minVShrink;
				 * if(vDirection == ShrinkDirection.Start) { c.y += minVShrink;
				 * } }
				 */

				// Shrink
				if (minShrink != Double.MAX_VALUE) {
					if (direction == ShrinkDirection.H) {
						c.width -= Math.abs(minShrink);
						if (minShrink > 0) {
							c.x += minShrink;
						}
					} else {
						c.height -= Math.abs(minShrink);
						if (minShrink > 0) {
							c.y += minShrink;
						}
					}
				} else {
					throw new RuntimeException("No Shrink found");
				}

				UpdateParticipants(participants, c);
			} while (!participants.isEmpty());

			if (c.getHeight() != 0 && c.getWidth() != 0) {
				double vC = c.getHeight() * c.getWidth();
				double vQuB = this.getIntersectionVolume(q);
				int tc = (int) (tb * vC / vQuB);
				return new Bucket(c, tc);
			}
		}

		return null;
	}

	private void UpdateParticipants(List<Bucket> participants,
			Rectangle2D.Double c) {
		participants.clear();
		for (Bucket childBucket : children) {
			if (childBucket.box.intersects(c)) {
				if (!c.contains(childBucket.box)) {
					participants.add(childBucket);
				}
			}
		}
	}

	public java.awt.geom.Rectangle2D.Double getRectangle() {
		return box;
	}
}
