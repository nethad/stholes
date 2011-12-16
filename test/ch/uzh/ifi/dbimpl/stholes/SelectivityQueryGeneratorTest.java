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
	private final double[] expectedFirstInterval;

	@Parameters
	public static Collection<Object[]> parameters() {
		// double[] firstInterval
		// double upperBoundSecondInterval
		// double selectivity
		// double[] expectedFirstInterval
		// double[] expectedSecondInterval
		return Arrays.asList(new Object[][] {
				{ new double[] { 0.1, 0.2 }, 0.05D, 0.01D, new double[] { 0.1, 0.2 }, new double[] { 0.0, 0.1 } },
				{ new double[] { 0.9, 1.0 }, 1.0D, 0.01D, new double[] { 0.9, 1. }, new double[] { 0.9, 1.0 } },
				{ new double[] { 0.5, 0.55 }, 0.1D, 0.5D, new double[] { 0.275, 0.775 }, new double[] { 0.0, 1.0 } }
		});
	}

	public SelectivityQueryGeneratorTest(double[] firstInterval, double upperBoundSecondInterval, double selectivity,
			double[] expectedFirstInterval, double[] expectedSecondInterval) {
		this.firstInterval = firstInterval;
		this.expectedFirstInterval = expectedFirstInterval;
		this.expectedSecondInterval = expectedSecondInterval;
		this.upperBoundSecondInterval = upperBoundSecondInterval;
		this.selectivity = selectivity;
		generator = new SelectivityQueryGenerator(selectivity);
	}

	@Test
	public void testSecondIntervalCalculation() {
		double[][] calculatedIntervals = generator.calculateSecondInterval(this.firstInterval,
				this.upperBoundSecondInterval);

		assertThat(calculatedIntervals[0][0], closeTo(expectedFirstInterval[0], ERROR));
		assertThat(calculatedIntervals[0][1], closeTo(expectedFirstInterval[1], ERROR));
		assertThat(calculatedIntervals[1][0], closeTo(expectedSecondInterval[0], ERROR));
		assertThat(calculatedIntervals[1][1], closeTo(expectedSecondInterval[1], ERROR));
		assertSelectivitiy(calculatedIntervals);
	}

	@Test
	public void testValidSelectivity() {
		new SelectivityQueryGenerator(0.0);
		new SelectivityQueryGenerator(1.0);
	}

	@Test(expected = RuntimeException.class)
	public void testInvalidSelectivity_below0() {
		new SelectivityQueryGenerator(-0.01);
	}

	@Test(expected = RuntimeException.class)
	public void testInvalidSelectivity_above1() {
		new SelectivityQueryGenerator(1.01);
	}

	protected void assertSelectivitiy(double[][] intervals) {
		assertThat(selectivity(intervals), closeTo(this.selectivity, ERROR));
	}

	private double selectivity(double[][] intervals) {
		return new Query(intervals[0][0], intervals[0][1], intervals[1][0], intervals[1][1])
				.getSelectivity();
	}
}
