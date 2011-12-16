package ch.uzh.ifi.dbimpl.stholes;

import ch.uzh.ifi.dbimpl.stholes.data.Query;

public class SelectivityQueryGenerator extends AbstractRandomQueryGenerator {

	private static final double INTERVAL_MIN = 0.0;
	private static final double INTERVAL_MAX = 0.0;
	private double selectivity;

	public SelectivityQueryGenerator(double selectivity) {
		setSelectivity(selectivity);
	}

	@Override
	public Query nextQuery() {
		double[] numbers = new double[3];
		for (int i = 0; i < 3; i++) {
			numbers[i] = getRandom().nextDouble();
		}
		double[] fixedInterval = sortedInterval(numbers[0], numbers[1]);
		// double[] secondInterval = sortedInterval(numbers[2], numbers[3]);

		return queryWithSelectivity(fixedInterval, numbers[2]);
	}

	@Override
	public boolean hasNextQuery() {
		return true;
	}

	public void setSelectivity(double selectivity) {
		if (selectivity > 1.0 || selectivity < 0) {
			throw new RuntimeException("Selectivity must be in [0,1]");
		}
		this.selectivity = selectivity;
	}

	protected Query queryWithSelectivity(double[] firstInterval, double rangePointer) {
		double[][] intervals = calculateSecondInterval(firstInterval, rangePointer);

		boolean randomBoolean = getRandom().nextBoolean();
		if (randomBoolean) {
			return buildQuery(intervals[0], intervals[1]);
		} else {
			return buildQuery(intervals[1], intervals[0]);
		}
	}

	// protected for testing purpose
	protected double[][] calculateSecondInterval(double[] firstInterval, double rangePointer) {
		// in order to achieve to desired selectivity, we only need one degree
		// of freedom basic selectivity = (x2 - x1) * (y2 - y1) [the query's
		// area] selectivity: 0.03 formula: 0.03 = (x2 - x1) * (y2 - y1) for the
		// case that x1, x2, y2 are (randomly) set, we get:
		// y1 = -((0.03 / (x2 - x1)) - y2)
		// or x1 = -((0.03 / (y2 - y1)) - x2)
		// we randomly choose whether to adjust the x or y range

		double[][] intervals = new double[2][2];

		double firstIntervalLength = intervalLength(firstInterval);
		if (firstIntervalLength < this.selectivity) {
			firstInterval = adjustIntervalToMeetSelectivity(firstInterval);
		}
		double[] secondInterval = new double[2];
		double y1 = -((this.selectivity / (firstInterval[1] - firstInterval[0])) - rangePointer);
		if (y1 < INTERVAL_MIN) { // second interval needs to be shifted to [0,
									// 1]
			secondInterval[0] = INTERVAL_MIN;
			secondInterval[1] = rangePointer + Math.abs(y1);
		} else {
			secondInterval[0] = y1;
			secondInterval[1] = rangePointer;
		}

		intervals[0] = firstInterval;
		intervals[1] = secondInterval;
		return intervals;
	}

	private double[] adjustIntervalToMeetSelectivity(double[] interval) {
		double rangeToExtend = (this.selectivity - intervalLength(interval)) / 2;
		interval[0] -= rangeToExtend;
		interval[1] += rangeToExtend;
		if (!validInterval(interval)) {
			if (interval[0] < INTERVAL_MIN) {
				interval[1] += Math.abs(interval[0]);
				interval[0] = INTERVAL_MIN;
			} else if (interval[1] > INTERVAL_MAX) {
				interval[0] -= Math.abs(INTERVAL_MAX - interval[1]);
				interval[1] = INTERVAL_MAX;
			} else {
				throw new RuntimeException("Not reachable");
			}
		}
		return interval;
	}

	private boolean validInterval(double[] firstInterval) {
		for (double d : firstInterval) {
			if (d > 1.0 || d < 0.0) {
				return false;
			}
		}
		return true;
	}

	private double intervalLength(double[] interval) {
		return interval[1] - interval[0];
	}

}
