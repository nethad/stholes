package ch.uzh.ifi.dbimpl.stholes;

import ch.uzh.ifi.dbimpl.stholes.data.Query;

public class RandomQueryGenerator extends AbstractRandomQueryGenerator {

	// private final Random randomGenerator;

	@Override
	public Query nextQuery() {
		double[] numbers = new double[4];
		for (int i = 0; i < 4; i++) {
			numbers[i] = getRandom().nextDouble();
		}
		double[] firstInterval = sortedInterval(numbers[0], numbers[1]);
		double[] secondInterval = sortedInterval(numbers[2], numbers[3]);

		return buildQuery(firstInterval, secondInterval);
	}

	@Override
	public boolean hasNextQuery() {
		return true;
	}

}
