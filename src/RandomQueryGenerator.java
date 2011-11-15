import java.util.Random;

public class RandomQueryGenerator implements QueryGenerator {

	private final Random randomGenerator;

	public RandomQueryGenerator() {
		randomGenerator = new Random();
	}

	@Override
	public Query nextQuery() {
		double[] numbers = new double[4];
		for (int i = 0; i < 4; i++) {
			numbers[i] = randomGenerator.nextDouble();
		}
		double[] firstInterval = sortedInterval(numbers[0], numbers[1]);
		double[] secondInterval = sortedInterval(numbers[2], numbers[3]);

		return new Query(firstInterval[0], firstInterval[1], secondInterval[0],
				secondInterval[1]);
	}

	private double[] sortedInterval(double first, double second) {
		if (second > first) {
			return new double[] { first, second };
		} else {
			return new double[] { second, first };
		}
	}

	@Override
	public boolean hasNextQuery() {
		return true;
	}

}
