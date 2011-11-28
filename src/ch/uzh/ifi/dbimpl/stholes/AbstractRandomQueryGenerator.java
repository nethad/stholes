package ch.uzh.ifi.dbimpl.stholes;

import java.util.Random;

import ch.uzh.ifi.dbimpl.stholes.data.Query;

public abstract class AbstractRandomQueryGenerator implements QueryGenerator {

	private final Random random;

	public AbstractRandomQueryGenerator() {
		random = new Random();
	}

	protected Query buildQuery(double[] xInterval, double[] yInterval) {
		return new Query(xInterval[0], xInterval[1], yInterval[0], yInterval[1]);
	}

	protected double[] sortedInterval(double first, double second) {
		if (second > first) {
			return new double[] { first, second };
		} else {
			return new double[] { second, first };
		}
	}

	protected Random getRandom() {
		return random;
	}

}
