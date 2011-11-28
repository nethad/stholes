package ch.uzh.ifi.dbimpl.stholes;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.uzh.ifi.dbimpl.stholes.data.Query;

@RunWith(Parameterized.class)
public class SelectivityQueryGeneratorTest {

	// private static final double SELECTIVITY = 0.01;
	private static final double ERROR = 1E-6;
	private final SelectivityQueryGenerator generator;
	private final double[] firstInterval;
	private final double[] expectedSecondInterval;
	private final double upperBoundSecondInterval;
	private final double selectivity;

	@Parameters
	public static Collection<Object[]> parameters() {
		// double[] firstInterval
		// double upperBoundSecondInterval
		// double selectivity
		// double[] expectedSecondInterval
		return Arrays.asList(new Object[][] {
				{ new double[] { 0.1, 0.2 }, 0.05D, 0.01D, new double[] { 0.0, 0.1 } },
				{ new double[] { 0.9, 1.0 }, 1.0D, 0.01D, new double[] { 0.9, 1.0 } },
				// { new double[] { 0.5, 0.55 }, 0.1D, 0.5D, new double[] { 0.0,
				// 1.0 } }
		});
	}

	public SelectivityQueryGeneratorTest(double[] firstInterval, double upperBoundSecondInterval, double selectivity,
			double[] secondInterval) {
		this.firstInterval = firstInterval;
		this.expectedSecondInterval = secondInterval;
		this.upperBoundSecondInterval = upperBoundSecondInterval;
		this.selectivity = selectivity;
		generator = new SelectivityQueryGenerator(selectivity);
	}

	@Test
	public void testSecondIntervalCalculation() {
		double[] calculatedSecondInterval = generator.calculateSecondInterval(this.firstInterval,
				this.upperBoundSecondInterval);
		assertThat(calculatedSecondInterval[0], closeTo(expectedSecondInterval[0], ERROR));
		assertThat(calculatedSecondInterval[1], closeTo(expectedSecondInterval[1], ERROR));
		assertSelectivitiy(this.firstInterval, calculatedSecondInterval);
	}

	protected void assertSelectivitiy(double[] firstInterval, double[] secondInterval) {
		assertThat(selectivity(firstInterval, secondInterval), closeTo(this.selectivity, ERROR));
	}

	private double selectivity(double[] firstInterval, double[] secondInterval) {
		return new Query(firstInterval[0], firstInterval[1], secondInterval[0], secondInterval[1]).getSelectivity();
	}
}
