package ch.uzh.ifi.dbimpl.stholes;

import ch.uzh.ifi.dbimpl.stholes.data.Query;

public class SelectivityQueryGenerator extends AbstractRandomQueryGenerator {

	private double selectivity;
	private double bounds;

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
		this.selectivity = selectivity;
		// this.bounds = 1.0D - Math.sqrt(selectivity);
	}

	protected Query queryWithSelectivity(double[] firstInterval, double rangePointer) {
		double[] secondInterval = calculateSecondInterval(firstInterval, rangePointer);

		boolean randomBoolean = getRandom().nextBoolean();
		if (randomBoolean) {
			return buildQuery(firstInterval, secondInterval);
		} else {
			return buildQuery(secondInterval, firstInterval);
		}
	}

	// protected for testing purpose
	protected double[] calculateSecondInterval(double[] firstInterval, double rangePointer) {
		// in order to achieve to desired selectivity, we only need one degree
		// of freedom basic selectivity = (x2 - x1) * (y2 - y1) [the query's
		// area] selectivity: 0.03 formula: 0.03 = (x2 - x1) * (y2 - y1) for the
		// case that x1, x2, y2 are (randomly) set, we get:
		// y1 = -((0.03 / (x2 - x1)) - y2)
		// or x1 = -((0.03 / (y2 - y1)) - x2)
		// we randomly choose whether to adjust the x or y range
		double[] secondInterval = new double[2];
		double y1 = -((this.selectivity / (firstInterval[1] - firstInterval[0])) - rangePointer);
		if (y1 < 0) { // second interval needs to be shifted to [0, 1]
			secondInterval[0] = 0D;
			secondInterval[1] = rangePointer + Math.abs(y1);
		} else {
			secondInterval[0] = y1;
			secondInterval[1] = rangePointer;
		}
		return secondInterval;
	}

}
