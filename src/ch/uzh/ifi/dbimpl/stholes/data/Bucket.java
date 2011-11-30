package ch.uzh.ifi.dbimpl.stholes.data;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

public class Bucket {

	private enum ShrinkDirection {
		H, V
	}

	private static final double MIN_INTERSECTION_DISTANCE = 0.000000001;

	private int frequency;

	// This factor is not included in the paper. The factor indicates with what factor the frequency was original multiplied.
	// By only updating the frequency if the update factor is "better" (high) we can achieve a better result.
	private double lastVFactor = 0;

	private final Rectangle2D.Double box;

	private final List<Bucket> children;

	// The parent link would not be required. however it makes the drill hole easier.
	private Bucket parent;

	public Bucket(double x0, double x1, double y0, double y1) {
		this.box = new Rectangle2D.Double(x0, y0, x1 - x0, y1 - y0);
		this.children = new LinkedList<Bucket>();
	}

	private Bucket(Rectangle2D.Double box, int frequency, Bucket parent, double vFactor) {
		this.box = box;
		this.frequency = frequency;
		this.parent = parent;
		this.lastVFactor = vFactor;
		this.children = new LinkedList<Bucket>();
	}

	public void setFrequency(int frequency) {
		if (frequency < 0) {
			throw new RuntimeException("Cannot set a negative frequency");
		}
		this.frequency = frequency;
	}

	public int getFrequency() {
		return (int) Math.round(this.frequency);
	}

	public Rectangle2D.Double getRectangle() {
		return box;
	}

	public Bucket getParent() {
		return this.parent;
	}

	public List<Bucket> getChildren() {
		return this.children;
	}

	public void addChildBucket(Bucket childBucket) {
		// Only does:
		// this.children.add(childBucket);
		// childBucket.parent = this;
		// The rest is debug code to prevent the creation of wrong histograms

		if (box.getMaxX() < childBucket.box.getMaxX() || box.getMinX() > childBucket.box.getMinX() || box.getMaxY() < childBucket.box.getMaxY()
				|| box.getMinY() > childBucket.box.getMinY()) {
			throw new RuntimeException("Trying to add child which is not contained in parten box." + "\n  Parent Box: " + box + "\n  Child Box: "
					+ childBucket.box);
		}

		for (Bucket other : children) {
			if (childBucket.box.intersects(other.box)) {
				Rectangle2D.Double intersection = (Rectangle2D.Double) childBucket.box.createIntersection(other.box);
				// :HACK: do not count very small overlaps (adjacent boxes) as intersection
				if (intersection.getWidth() >= MIN_INTERSECTION_DISTANCE && intersection.getHeight() >= MIN_INTERSECTION_DISTANCE) {
					throw new RuntimeException("Trying to add a child that would result in an overlap");
				}
			}
		}

		this.children.add(childBucket);

		childBucket.parent = this;
	}

	public void removeChildBucket(Bucket childBucket) {
		childBucket.parent = null;
		this.children.remove(childBucket);
	}

	/**
	 * @return Gets the number of buckets including all children.
	 */
	public int getHistogramSize() {
		int size = 1;
		for (Bucket childBucket : this.children) {
			size += childBucket.getHistogramSize();
		}
		return size;
	}

	/**
	 * @return Gets the total estimate including the estimate of all children.
	 */
	public int getTotalEstimate() {
		int estimate = this.frequency;
		for (Bucket childBucket : this.children) {
			estimate += childBucket.getTotalEstimate();
		}
		return estimate;
	}

	/**
	 * @return Gets the volume v(b) for this bucket. (without the volume of the children)
	 */
	public double getVolume() {
		double volume = volumeForRectangle(this.box);
		for (Bucket childBucket : this.children) {
			volume -= volumeForRectangle(childBucket.box);
		}
		return volume;
	}

	/**
	 * The volume of the overlapping area (query and bucket) multiplied with the bucket frequency, including the estimates for all children
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

			// Calculate the estimate for the bucket. = f(b) * ( v(q UNION b) / v(b) )
			double estimate;
			if (this.getVolume() < -MIN_INTERSECTION_DISTANCE) {
				estimate = 0;
			} else {
				estimate = this.getIntersectionVolume(q) / this.getVolume() * this.frequency;
			}

			// Estimate all children buckets and add them to the current estimate.
			for (Bucket childBucket : children) {
				estimate += childBucket.getEstimateForQuery(q);
			}

			return estimate;
		} else {
			// No intersection with the query. -> Children will also not intersect.
			return 0D;
		}
	}

	/**
	 * Calculate the intersection volume between the bucket and the query. = v(q UNION b)
	 */
	private double getIntersectionVolume(Query q) {
		Rectangle2D.Double queryBox = q.getRectangle2D();
		Rectangle2D.Double queryIntersection = (Rectangle2D.Double) this.box.createIntersection(queryBox);
		double intersectionVolume = volumeForRectangle(queryIntersection);
		for (Bucket childBucket : this.children) {
			if (childBucket.box.intersects(queryBox)) {
				Rectangle2D.Double intersection = (Rectangle2D.Double) queryIntersection.createIntersection(childBucket.box);
				intersectionVolume -= volumeForRectangle(intersection);
			}
		}

		return intersectionVolume;
	}

	public Bucket identifyCandidate(Query q, int actualResultCount) {
		Rectangle2D.Double queryBox = q.getRectangle2D();

		if (this.box.intersects(queryBox)) {
			Rectangle2D.Double c = (Rectangle2D.Double) this.box.createIntersection(queryBox);
			List<Bucket> participants = new LinkedList<Bucket>();
			updateParticipants(participants, c);

			while (!participants.isEmpty()) {
				// :TODO: What is the smallest reduction of c? Volume or length. The paper does not mention this. > Use length only.
				double minShrink = Double.MAX_VALUE;
				ShrinkDirection direction = ShrinkDirection.H;

				// Determine the minimal shrink length
				for (Bucket participant : participants) {
					if ((c.getMaxX() - participant.box.getMaxX()) > MIN_INTERSECTION_DISTANCE) {
						// Could Shrink by reducing from the right.
						double rightShrink = participant.box.getMaxX() - c.getMinX();
						if (rightShrink > 0 && rightShrink < Math.abs(minShrink)) {
							minShrink = rightShrink;
							direction = ShrinkDirection.H;
						}
					}

					if ((c.getMinX() - participant.box.getMinX()) < MIN_INTERSECTION_DISTANCE) {
						// Could Shrink by reducing from the left.
						double leftShrink = c.getMaxX() - participant.box.getMinX();
						if (leftShrink > 0 && leftShrink < Math.abs(minShrink)) {
							minShrink = -leftShrink;
							direction = ShrinkDirection.H;
						}
					}

					if ((c.getMaxY() - participant.box.getMaxY()) > MIN_INTERSECTION_DISTANCE) {
						// Could Shrink by reducing from the top.
						double topShrink = participant.box.getMaxY() - c.getMinY();
						if (topShrink > 0 && topShrink < Math.abs(minShrink)) {
							minShrink = topShrink;
							direction = ShrinkDirection.V;
						}
					}

					if ((c.getMinY() - participant.box.getMinY()) < MIN_INTERSECTION_DISTANCE) {
						// Could Shrink by reducing from the bottom.
						double bottomShrink = c.getMaxY() - participant.box.getMinY();
						if (bottomShrink > 0 && bottomShrink < Math.abs(minShrink)) {
							minShrink = -bottomShrink;
							direction = ShrinkDirection.V;
						}
					}
				}

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
					// No shrink values could be found in an iteration -> abort and do not create a candidate
					return null;
				}

				updateParticipants(participants, c);
			}

			if (c.getHeight() != 0 && c.getWidth() != 0) {
				// Only add non 0 buckets
				double vFactor = (c.getHeight() * c.getWidth() / (queryBox.getHeight() * queryBox.getWidth()));
				int tc = (int) ((double) actualResultCount * vFactor);

				return new Bucket(c, tc, this, vFactor);
			}
		}

		return null;
	}

	private void updateParticipants(List<Bucket> participants, Rectangle2D.Double c) {
		participants.clear();
		for (Bucket childBucket : children) {
			if (childBucket.box.intersects(c)) {
				if (!c.contains(childBucket.box)) {
					Rectangle2D.Double intersection = (Rectangle2D.Double) childBucket.box.createIntersection(c);
					// :HACK: do not count very small overlaps (adjacent boxes) as intersection
					if (intersection.getWidth() >= MIN_INTERSECTION_DISTANCE && intersection.getHeight() >= MIN_INTERSECTION_DISTANCE) {
						participants.add(childBucket);
					}
				} else if (rectangleEquals(c, childBucket.box)) {
					// Add the childBucket if it is equal to the query box. -> No shrink will be found and no candidate will be created
					// This is correct since the candidate will be created in the child
					participants.add(childBucket);
				}
			}
		}
	}

	public void drillHole(Bucket candidate) {
		if (boxEquals(candidate)) {
			// Case 1: both buckets reference the same box.

			// :TODO: The paper does not mention to change the frequency in this case!
			for (Bucket childBucket : children) {
				if (candidate.box.contains(childBucket.box)) {
					candidate.frequency -= childBucket.frequency;
				}
			}

			if (candidate.frequency < 0) {
				candidate.frequency = 0;
			}

			//if (this.lastVFactor < candidate.lastVFactor) {
				// Only update the frequency if the candidate frequency was calculated from a better approximation.
				// :TODO: THIS IS NOT PART OF THE PAPER! (Only works when the data is static)
				this.frequency = candidate.frequency;
			//	this.lastVFactor = candidate.lastVFactor;
			//}
		} else if (isRemainingSpace(this, candidate)) {
			eliminateBucket(this, candidate);
		} else {
			// Case 3: Default
			List<Bucket> toMove = new LinkedList<Bucket>();

			// Move all contained children into the candidate
			for (Bucket childBucket : children) {
				if (candidate.box.contains(childBucket.box)) {
					toMove.add(childBucket);
				}
			}

			for (Bucket m : toMove) {
				this.removeChildBucket(m);
				if (isRemainingSpace(candidate, m)) {
					return; // :TODO: The paper does not mention who to handle this rare case. Just forget about the candidate.
				} else {
					candidate.addChildBucket(m);
					// :TODO: The paper does not mention to change the frequency in this case!
					candidate.frequency -= m.getTotalEstimate();
				}
			}

			if (candidate.frequency < 0) {
				candidate.frequency = 0;
			}

			// Reevaluate case 2 after modifying the children.
			// :TODO: The paper does not mention this case!
			if (isRemainingSpace(this, candidate)) {
				eliminateBucket(this, candidate);
			} else {
				// Case 3: Default (cont.)
				// Add the candidate
				this.addChildBucket(candidate);

				// Update the frequency
				int newFrequency = this.frequency - candidate.frequency;
				if (newFrequency >= 0) {
					this.frequency = newFrequency;
				} else {
					this.frequency = 0;
				}
			}
		}
	}

	private static boolean isRemainingSpace(Bucket bucket, Bucket candidate) {
		// :TODO: The paper does not specify what to do when this case happens for the root bucket! > Never remove the root bucket.
		boolean case2 = bucket.parent != null;
		// Check if the remaining volume is 0
		case2 = case2 && Math.abs(bucket.getVolume() - volumeForRectangle(candidate.box)) <= MIN_INTERSECTION_DISTANCE;
		if (case2) {
			for (Bucket childBucket : bucket.children) {
				// The candidate must not be contained in any other children.
				case2 = case2 && !childBucket.box.contains(candidate.box);
			}
		}

		return case2;
	}

	private static void eliminateBucket(Bucket bucketToRemove, Bucket candidate) {
		// Case 2: candidate hole covers all the remaining space

		// Remove the own bucket from the parent
		Bucket oldParent = bucketToRemove.parent;
		oldParent.removeChildBucket(bucketToRemove);

		// Move all children from the bucket to the parent
		for (Bucket childBucket : bucketToRemove.children) {
			oldParent.addChildBucket(childBucket);
		}
		bucketToRemove.children.clear();

		// Add the candidate to the parent
		oldParent.addChildBucket(candidate);
	}

	/**
	 * Returns the best possible parent child merge with this bucket as parent.
	 */
	public Merge idetifyBestParentChildMerge() {
		Bucket bp = this;
		Merge m = null;
		double minPenalty = Double.MAX_VALUE;
		for (Bucket bc : bp.children) {
			int fBN = bp.frequency + bc.frequency;
			double vBN = bp.getVolume() + bc.getVolume();
			double penalty = 
					Math.abs(bp.frequency - fBN * (bp.getVolume() / vBN)) + 
					Math.abs(bc.frequency - fBN * (bc.getVolume() / vBN));

			if (penalty < minPenalty) {
				minPenalty = penalty;
				m = new ParentMerge(this, bc, penalty);
			}
		}

		return m;
	}

	/**
	 * Returns the best possible sibling sibling merge among the children of this bucket.
	 */
	public Merge idetifyBestSiblingMerge() {
		double minPenalty = Double.MAX_VALUE;
		Merge m = null;
		for (Bucket b1 : this.children) {
			for (Bucket b2 : this.children) {
				if (b1 == b2) {
					continue;
				}

				Rectangle2D.Double bn = new Rectangle2D.Double();
				Rectangle2D.Double.union(b1.box, b2.box, bn);

				// Calculate the box of the new bucket bn
				boolean intersectionFound = false;
				do {
					intersectionFound = false;
					for (Bucket i : this.children) {
						if (bn.intersects(i.box)) {
							if (!bn.contains(i.box)) {
								Rectangle2D.Double intersection = (Rectangle2D.Double) i.box.createIntersection(bn);
								// :HACK: do not count very small overlaps (adjacent boxes) as intersection
								if (intersection.getWidth() >= MIN_INTERSECTION_DISTANCE && intersection.getHeight() >= MIN_INTERSECTION_DISTANCE) {
									// Update the rectangle to include the overlapping bucket;
									Rectangle2D.Double.union(bn, i.box, bn);
									intersectionFound = true; // Need to perform at least one additional run
								}
							}
						}
					}
				} while (intersectionFound && !rectangleEquals(this.box, bn));

				// If bn was extended to the size of the parent return no merge candidate for s1, s2
				// This case is handled by the parent child merge
				if (!rectangleEquals(this.box, bn)) {
					// Calculate the participants (Here the children that are contained in bn)
					double vOld = volumeForRectangle(bn) - volumeForRectangle(b1.box) - volumeForRectangle(b2.box);
					for (Bucket i : this.children) {
						if (i != b1 && i != b2 && bn.contains(i.box)) {
							vOld -= volumeForRectangle(i.box);
						}
					}

					// Calculate the penalty
					int fBN = (int) Math.round((b1.frequency + b2.frequency + this.frequency * (vOld / this.getVolume())));
					int fBP = (int) Math.round((this.frequency * (1 - (vOld / this.getVolume()))));
					double vBN = vOld + volumeForRectangle(b1.box) + volumeForRectangle(b2.box);
					double vBP = this.getVolume() - vOld;

					double penalty = 
							Math.abs(fBN * (vOld / vBN) - fBP * (vOld / vBP)) + 
							Math.abs(b1.frequency - fBN * (b1.getVolume() / vBN)) + 
							Math.abs(b2.frequency - fBN * (b2.getVolume() / vBN));

					if (penalty < minPenalty) {
						minPenalty = penalty;
						Bucket candidate = new Bucket(bn, fBN, null, 0.0);

						m = new SiblingMerge(this, candidate, b1, b2, penalty, fBP);
					}
				}
			}
		}

		return m;
	}

	private static double volumeForRectangle(Rectangle2D.Double box) {
		return box.getWidth() * box.getHeight();
	}

	private boolean boxEquals(Bucket other) {
		return rectangleEquals(this.box, other.box);
	}

	private static boolean rectangleEquals(Rectangle2D.Double a, Rectangle2D.Double b) {
		if (Math.round(a.getX() * 1 / MIN_INTERSECTION_DISTANCE) != Math.round(b.getX() * 1 / MIN_INTERSECTION_DISTANCE)) {
			return false;
		}
		if (Math.round(a.getY() * 1 / MIN_INTERSECTION_DISTANCE) != Math.round(b.getY() * 1 / MIN_INTERSECTION_DISTANCE)) {
			return false;
		}
		if (Math.round(a.getWidth() * 1 / MIN_INTERSECTION_DISTANCE) != Math.round(b.getWidth() * 1 / MIN_INTERSECTION_DISTANCE)) {
			return false;
		}
		if (Math.round(a.getHeight() * 1 / MIN_INTERSECTION_DISTANCE) != Math.round(b.getHeight() * 1 / MIN_INTERSECTION_DISTANCE)) {
			return false;
		}
		return true;
	}
}
