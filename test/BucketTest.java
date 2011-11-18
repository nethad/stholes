import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Test the histogram volume, estimate and IdentifyCandiate functions.
 * See "DB Impl Scenarios" document for used scenarios.
 */
public class BucketTest {
	
	// Round estimate results to 6 decimals 
	private final static double VOLUME_PRECISION = 1000000;

	// Round estimate results to 2 decimals 
	private final static double ESTIMATE_PRECISION = 100;
	
	private static double RoundedVolume(double result)
	{
		return Math.round(result * VOLUME_PRECISION) / VOLUME_PRECISION;
	}
	
	private static double RoundedEstimate(double result)
	{
		return Math.round(result * ESTIMATE_PRECISION) / ESTIMATE_PRECISION;
	}
    
    @Test
    public void testHistogram_1()
    {
    	Bucket root = HistorgramFactory.CreateHistogram1();
    	Query q = HistorgramFactory.CreateTestQuery();
    	assertThat(RoundedVolume(root.getVolume()), is(1.0));
    	assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(600.0));
    	
    	Bucket candiateR = root.IdentifyCandiate(q, 200);
    	assertThat(RoundedVolume(candiateR.getVolume()), is(0.6));
    	assertThat(candiateR.getFrequency(), is(200));
    }
    
    @Test
    public void testHistogram_2()
    {
    	Bucket root = HistorgramFactory.CreateHistogram2();
    	Query q = HistorgramFactory.CreateTestQuery();
    	
    	assertThat(RoundedVolume(root.getVolume()), is(0.68));
    	assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(604.41));
    	
    	
    	Bucket candiateR = root.IdentifyCandiate(q, 200);
    	assertThat(RoundedVolume(candiateR.getVolume()), is(0.18));
    	assertThat(candiateR.getFrequency(), is(99));
    	
    	Bucket candiateB1 = root.getChildren().get(0).IdentifyCandiate(q, 500);
    	assertThat(RoundedVolume(candiateB1.getVolume()), is(0.24));
    	assertThat(candiateB1.getFrequency(), is(500));
    }
    
    @Test
    public void testHistogram_3()
    {
    	Bucket root = HistorgramFactory.CreateHistogram3();
    	Query q = HistorgramFactory.CreateTestQuery();
    	
    	assertThat(RoundedVolume(root.getVolume()), is(0.68));
    	assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(942.75));
    	
    	Bucket candiateR = root.IdentifyCandiate(q, 200);
    	// The shrink algorithm doesn't create a optimal solution for this setup. (Expected)
    	assertThat(RoundedVolume(candiateR.getVolume()), is(0.06));
    	assertThat(candiateR.getFrequency(), is(33));
    	
    	Bucket candiateB11 = root.getChildren().get(0).IdentifyCandiate(q, 500);
    	assertThat(RoundedVolume(candiateB11.getVolume()), is(0.16));
    	assertThat(candiateB11.getFrequency(), is(500)); 
    	
    	Bucket candiateB12 = root.getChildren().get(0).IdentifyCandiate(q, 300);
    	assertThat(RoundedVolume(candiateB12.getVolume()), is(0.16));
    	assertThat(candiateB12.getFrequency(), is(300));
    }
    
    @Test
    public void testHistogram_4()
    {
    	Bucket root = HistorgramFactory.CreateHistogram4();
    	Query q = HistorgramFactory.CreateTestQuery();
    	
    	assertThat(RoundedVolume(root.getVolume()), is(0.68));
    	assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(8029.41));
    }
    
    @Test
    public void testHistogram_5()
    {
    	Bucket root = HistorgramFactory.CreateHistogram5();
    	Query q = HistorgramFactory.CreateTestQuery();
    	
    	assertThat(RoundedVolume(root.getVolume()), is(0.92));
    	assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(886.96));
    }
    
    @Test
    public void testHistogram_6()
    {
    	Bucket root = HistorgramFactory.CreateHistogram6();
    	Query q = HistorgramFactory.CreateTestQuery();
    	
    	assertThat(RoundedVolume(root.getVolume()), is(0.9));
    	assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(900.0));
    }
    
    @Test
    public void testHistogram_7()
    {
    	Bucket root = HistorgramFactory.CreateHistogram7();
    	Query q = HistorgramFactory.CreateTestQuery();
    	
    	assertThat(RoundedVolume(root.getVolume()), is(0.68));
    	assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(1006.33));
    }
    
    @Test
    public void testHistogram_8()
    {
    	Bucket root = HistorgramFactory.CreateHistogram8();
    	Query q = HistorgramFactory.CreateTestQuery();
    	
    	assertThat(RoundedVolume(root.getVolume()), is(0.84));
    	assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(623.81));
    }
    
    @Test
    public void testHistogram_9()
    {
    	Bucket root = HistorgramFactory.CreateHistogram9();
    	Query q = HistorgramFactory.CreateTestQuery();
    	
    	assertThat(RoundedVolume(root.getVolume()), is(0.84));
    	assertThat(RoundedEstimate(root.getEstimateForQuery(q)), is(714.29));
    }
}
