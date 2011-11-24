package ch.uzh.ifi.dbimpl.stholes;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.awt.geom.Rectangle2D;

import org.junit.Test;

import ch.uzh.ifi.dbimpl.stholes.data.Bucket;
import ch.uzh.ifi.dbimpl.stholes.data.Query;

/**
 * Test the histogram volume, estimate and IdentifyCandiate functions. See
 * "DB Impl Scenarios" document for used scenarios.
 */
public class BucketTest {

	// Round estimate results to 6 decimals
	private final static double VOLUME_PRECISION = 1000000;

	// Round estimate results to 2 decimals
	private final static double ESTIMATE_PRECISION = 100;

	private static double RoundedVolume(double result) {
		return Math.round(result * VOLUME_PRECISION) / VOLUME_PRECISION;
	}

	private static double RoundedEstimate(double result) {
		return Math.round(result * ESTIMATE_PRECISION) / ESTIMATE_PRECISION;
	}

	public static boolean RectangleEquals(Rectangle2D.Double a, Rectangle2D.Double b) {
		if (Math.round(a.getX() * ESTIMATE_PRECISION) != Math.round(b.getX() * ESTIMATE_PRECISION)) {
			return false;
		}
		if (Math.round(a.getY() * ESTIMATE_PRECISION) != Math.round(b.getY() * ESTIMATE_PRECISION)) {
			return false;
		}
		if (Math.round(a.getWidth() * ESTIMATE_PRECISION) != Math.round(b.getWidth() * ESTIMATE_PRECISION)) {
			return false;
		}
		if (Math.round(a.getHeight() * ESTIMATE_PRECISION) != Math.round(b.getHeight() * ESTIMATE_PRECISION)) {
			return false;
		}
		return true;
	}

	@Test
	public void testHistogram_1() {
		Bucket root = HistorgramFactory.CreateHistogram1();
		Query q = HistorgramFactory.CreateTestQuery();

		assertThat(RoundedVolume(root.getVolume()), is(1.0));
		assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(600.0));

		// Test candidate holes
		Bucket candiateR = root.identifyCandidate(q, 200);
		assertThat(RoundedVolume(candiateR.getVolume()), is(0.6));
		assertThat(candiateR.getFrequency(), is(200));
		assertThat(RectangleEquals(candiateR.getRectangle(), new Rectangle2D.Double(0.2, 0.0, 0.6, 1.0)), is(true));
	}

	@Test
	public void testHistogram_2() {
		Bucket root = HistorgramFactory.CreateHistogram2();
		Query q = HistorgramFactory.CreateTestQuery();

		assertThat(RoundedVolume(root.getVolume()), is(0.68));
		assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(604.41));

		// Test candidate holes
		Bucket candiateR = root.identifyCandidate(q, 200);
		assertThat(RoundedVolume(candiateR.getVolume()), is(0.18));
		assertThat(candiateR.getFrequency(), is(60));
		assertThat(RectangleEquals(candiateR.getRectangle(), new Rectangle2D.Double(0.2, 0.7, 0.6, 0.3)), is(true));

		Bucket candiateB1 = root.getChildren().get(0).identifyCandidate(q, 500);
		assertThat(RoundedVolume(candiateB1.getVolume()), is(0.24));
		assertThat(candiateB1.getFrequency(), is(199));
		assertThat(RectangleEquals(candiateB1.getRectangle(), new Rectangle2D.Double(0.2, 0.3, 0.6, 0.4)), is(true));
	}

	@Test
	public void testHistogram_3() {
		Bucket root = HistorgramFactory.CreateHistogram3();
		Query q = HistorgramFactory.CreateTestQuery();

		assertThat(RoundedVolume(root.getVolume()), is(0.68));
		assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(942.75));

		// Test candidate holes
		Bucket candiateR = root.identifyCandidate(q, 200);
		assertThat(RoundedVolume(candiateR.getVolume()), is(0.12));
		assertThat(candiateR.getFrequency(), is(40));
		// The shrink algorithm does not create an optimal solution! (Works as designed in the paper)
		assertThat(RectangleEquals(candiateR.getRectangle(), new Rectangle2D.Double(0.2, 0.7, 0.4, 0.3)), is(true));

		Bucket candiateB11 = root.getChildren().get(0).identifyCandidate(q, 500);
		assertThat(RoundedVolume(candiateB11.getVolume()), is(0.16));
		assertThat(candiateB11.getFrequency(), is(133));
		assertThat(RectangleEquals(candiateB11.getRectangle(), new Rectangle2D.Double(0.2, 0.3, 0.4, 0.4)), is(true));

		Bucket candiateB12 = root.getChildren().get(1).identifyCandidate(q, 300);
		assertThat(RoundedVolume(candiateB12.getVolume()), is(0.08));
		assertThat(candiateB12.getFrequency(), is(40));
		assertThat(RectangleEquals(candiateB12.getRectangle(), new Rectangle2D.Double(0.6, 0.3, 0.2, 0.4)), is(true));
	}

	@Test
	public void testHistogram_4() {
		Bucket root = HistorgramFactory.CreateHistogram4();
		Query q = HistorgramFactory.CreateTestQuery();

		assertThat(RoundedVolume(root.getVolume()), is(0.68));
		assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(8029.41));

		// Test candidate holes
		Bucket candiateR = root.identifyCandidate(q, 200);
		assertThat(RoundedVolume(candiateR.getVolume()), is(0.18));
		assertThat(candiateR.getFrequency(), is(60));
		assertThat(RectangleEquals(candiateR.getRectangle(), new Rectangle2D.Double(0.2, 0.7, 0.6, 0.3)), is(true));

		Bucket candiateB1 = root.getChildren().get(0).identifyCandidate(q, 500);
		assertThat(RoundedVolume(candiateB1.getVolume()), is(0.24));
		assertThat(candiateB1.getFrequency(), is(199));
		// b21 and b22 are both contained in the candidate hole of b1
		assertThat(RectangleEquals(candiateB1.getRectangle(), new Rectangle2D.Double(0.2, 0.3, 0.6, 0.4)), is(true));

		Bucket candiateB21 = root.getChildren().get(0).getChildren().get(0).identifyCandidate(q, 500);
		assertThat(RoundedVolume(candiateB21.getVolume()), is(0.06));
		assertThat(candiateB21.getFrequency(), is(49));
		assertThat(RectangleEquals(candiateB21.getRectangle(), new Rectangle2D.Double(0.2, 0.4, 0.3, 0.2)), is(true));

		Bucket candiateB22 = root.getChildren().get(0).getChildren().get(1).identifyCandidate(q, 500);
		assertThat(RoundedVolume(candiateB22.getVolume()), is(0.06));
		assertThat(candiateB22.getFrequency(), is(49));
		assertThat(RectangleEquals(candiateB22.getRectangle(), new Rectangle2D.Double(0.5, 0.4, 0.3, 0.2)), is(true));
	}

	@Test
	public void testHistogram_5() {
		Bucket root = HistorgramFactory.CreateHistogram5();
		Query q = HistorgramFactory.CreateTestQuery();

		assertThat(RoundedVolume(root.getVolume()), is(0.92));
		assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(886.96));

		// Test candidate holes
		Bucket candiateR = root.identifyCandidate(q, 200);
		assertThat(RoundedVolume(candiateR.getVolume()), is(0.3));
		assertThat(candiateR.getFrequency(), is(100));
		assertThat(RectangleEquals(candiateR.getRectangle(), new Rectangle2D.Double(0.2, 0.0, 0.3, 1.0)), is(true));

		Bucket candiateB1 = root.getChildren().get(0).identifyCandidate(q, 500);
		assertThat(RoundedVolume(candiateB1.getVolume()), is(0.06));
		assertThat(candiateB1.getFrequency(), is(49));
		assertThat(RectangleEquals(candiateB1.getRectangle(), new Rectangle2D.Double(0.5, 0.4, 0.3, 0.2)), is(true));
	}

	@Test
	public void testHistogram_6() {
		Bucket root = HistorgramFactory.CreateHistogram6();
		Query q = HistorgramFactory.CreateTestQuery();

		assertThat(RoundedVolume(root.getVolume()), is(0.9));
		assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(900.0));

		// Test candidate holes
		Bucket candiateR = root.identifyCandidate(q, 200);
		assertThat(RoundedVolume(candiateR.getVolume()), is(0.3));
		assertThat(candiateR.getFrequency(), is(100));
		assertThat(RectangleEquals(candiateR.getRectangle(), new Rectangle2D.Double(0.2, 0.0, 0.3, 1.0)), is(true));

		Bucket candiateB11 = root.getChildren().get(0).identifyCandidate(q, 500);
		// There is no candidate for b11
		assertThat(candiateB11, nullValue());

		Bucket candiateB12 = root.getChildren().get(1).identifyCandidate(q, 500);
		assertThat(RoundedVolume(candiateB12.getVolume()), is(0.06));
		assertThat(candiateB12.getFrequency(), is(49));
		assertThat(RectangleEquals(candiateB12.getRectangle(), new Rectangle2D.Double(0.5, 0.4, 0.3, 0.2)), is(true));
	}

	@Test
	public void testHistogram_7() {
		Bucket root = HistorgramFactory.CreateHistogram7();
		Query q = HistorgramFactory.CreateTestQuery();

		assertThat(RoundedVolume(root.getVolume()), is(0.68));
		assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(1006.33));

		// Test candidate holes
		Bucket candiateR = root.identifyCandidate(q, 200);
		assertThat(RoundedVolume(candiateR.getVolume()), is(0.18));
		assertThat(candiateR.getFrequency(), is(60));
		assertThat(RectangleEquals(candiateR.getRectangle(), new Rectangle2D.Double(0.2, 0.7, 0.6, 0.3)), is(true));

		Bucket candiateB1 = root.getChildren().get(0).identifyCandidate(q, 500);
		assertThat(RoundedVolume(candiateB1.getVolume()), is(0.24));
		assertThat(candiateB1.getFrequency(), is(199));
		// b21 is contained in the candidate hole of b1
		assertThat(RectangleEquals(candiateB1.getRectangle(), new Rectangle2D.Double(0.2, 0.3, 0.6, 0.4)), is(true));

		Bucket candiateB21 = root.getChildren().get(0).getChildren().get(0).identifyCandidate(q, 500);
		assertThat(RoundedVolume(candiateB21.getVolume()), is(0.04));
		assertThat(candiateB21.getFrequency(), is(33));
		assertThat(RectangleEquals(candiateB21.getRectangle(), new Rectangle2D.Double(0.3, 0.4, 0.2, 0.2)), is(true));

		Bucket candiateB22 = root.getChildren().get(0).getChildren().get(1).identifyCandidate(q, 500);
		// There is no candidate for b22
		assertThat(candiateB22, nullValue());
	}

	@Test
	public void testHistogram_8() {
		Bucket root = HistorgramFactory.CreateHistogram8();
		Query q = HistorgramFactory.CreateTestQuery();

		assertThat(RoundedVolume(root.getVolume()), is(0.84));
		assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(623.81));

		// Test candidate holes
		Bucket candiateR = root.identifyCandidate(q, 200);
		assertThat(RoundedVolume(candiateR.getVolume()), is(0.6));
		assertThat(candiateR.getFrequency(), is(200));
		// b1 is contained in the candidate hole of b1
		assertThat(RectangleEquals(candiateR.getRectangle(), new Rectangle2D.Double(0.2, 0.0, 0.6, 1.0)), is(true));

		Bucket candiateB1 = root.getChildren().get(0).identifyCandidate(q, 500);
		assertThat(RoundedVolume(candiateB1.getVolume()), is(0.16));
		assertThat(candiateB1.getFrequency(), is(133));
		assertThat(RectangleEquals(candiateB1.getRectangle(), new Rectangle2D.Double(0.3, 0.3, 0.4, 0.4)), is(true));
	}

	@Test
	public void testHistogram_9() {
		Bucket root = HistorgramFactory.CreateHistogram9();
		Query q = HistorgramFactory.CreateTestQuery();

		assertThat(RoundedVolume(root.getVolume()), is(0.84));
		assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(714.29));

		// Test candidate holes
		Bucket candiateR = root.identifyCandidate(q, 200);
		assertThat(RoundedVolume(candiateR.getVolume()), is(0.6));
		assertThat(candiateR.getFrequency(), is(200));
		assertThat(RectangleEquals(candiateR.getRectangle(), new Rectangle2D.Double(0.2, 0.0, 0.6, 1.0)), is(true));

		Bucket candiateB11 = root.getChildren().get(0).identifyCandidate(q, 500);
		// There is no candidate for b11
		assertThat(candiateB11, nullValue());

		Bucket candiateB12 = root.getChildren().get(1).identifyCandidate(q, 500);
		// There is no candidate for b11
		assertThat(candiateB12, nullValue());
	}
}
