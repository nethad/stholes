package ch.uzh.ifi.dbimpl.stholes;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import ch.uzh.ifi.dbimpl.stholes.data.Bucket;
import ch.uzh.ifi.dbimpl.stholes.data.Query;

public class STHolesAlgorithm {

	private final Bucket rootBucket;
	private int maxBucketCount;
	private Database db;

	/**
	 * Creates a new StHoles Algorithm.
	 */
	public STHolesAlgorithm(int maxBucketCount, Database db) {
		this.maxBucketCount = maxBucketCount;

		// Create a new root bucket over the complete range and initialize f(root) with 0.
		this.rootBucket = new Bucket(0.0, 1.0, 0.0, 1.0);
		this.db = db;
	}
	
	public Bucket getRootBucket() {
		return rootBucket;
	}

	/**
	 * Estimates the query.
	 */
	public double getEstimateForQuery(Query q) {
		return rootBucket.getEstimateForQuery(q);
	}

	/**
	 * Update the histogram according to the query q and the result of the query.
	 */
	public void updateHistogram(Query q, int actualResultCount) {
		// :NOTE: We do not perform the 1 step of the algorithm.
		// if q is not contained in H, expand H's root bucket so that it
		// contains q.
		// our implementation only support a fixed size root bucket. (0.0-1.0)

		// identify candidate holes
		List<Bucket> candidates = new LinkedList<Bucket>();
		identifyCandidateHoles(candidates, rootBucket, q, actualResultCount);
	
		// drill candidate holes
		for (Bucket candidate : candidates) {
			Query candidateQuery = new Query(candidate.getRectangle().getMinX(), 
					candidate.getRectangle().getMaxX(),
					candidate.getRectangle().getMinY(),
					candidate.getRectangle().getMaxY());
			
			// Only update if the current estimate with the current histogram is wrong
			if(Math.round(getEstimateForQuery(candidateQuery)) != candidate.getFrequency()) {
				Bucket parent = candidate.getParent();
				parent.drillHole(candidate);
			}
		}

		// merge superfluous buckets
		if(rootBucket.getHistogramSize() > maxBucketCount) {
			// TODO Merge
		}
	}

	/**
	 * Calculate the candidate list for the complete histogram.
	 */
	private void identifyCandidateHoles(List<Bucket> candidates, Bucket b, Query q, int actualResultCount) {

		Bucket candidate = b.identifyCandidate(q, actualResultCount);
		if (candidate != null) {
			if (db != null) {
				// :HACK: read the real values from the db as suggested by the paper.
				// In the paper the algorithm would count the number of tuples in b and calculate the value for the candidate
				// However reading the real results from the candidate directly results in more accurate histograms.
				// Read the real result directly for all candidates. --> all candidates have the correct frequency.
				Rectangle2D intersection = candidate.getRectangle();
				Query candidateQuery = new Query(intersection.getMinX(), intersection.getMaxX(), intersection.getMinY(), intersection.getMaxY());

				candidate.setFrequency(db.executeCountQuery(candidateQuery));
			}
			candidates.add(candidate);
		}

		if (b.getRectangle().intersects(q.getRectangle2D())) {
			for (Bucket childBucket : b.getChildren()) {
				identifyCandidateHoles(candidates, childBucket, q, actualResultCount);
			}
		}
	}
}
