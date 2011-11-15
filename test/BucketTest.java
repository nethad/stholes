import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class BucketTest {

    private Bucket rootBucketSingle;
    private Bucket rootBucket;

    @Before
    public void setup() {
        rootBucketSingle = new Bucket(0.0, 1.0, 0.0, 1.0);
        rootBucket = new Bucket(0.0, 1.0, 0.0, 1.0);
        Bucket childBucket = new Bucket(0.5, 1.0, 0.5, 1.0);
        rootBucket.addChildBucket(childBucket);
    }

    @Test
    public void testVolume_forSingleFullBucket() {
        assertThat(rootBucketSingle.getVolume(), is(1.0));
    }

    @Test
    public void testVolume_singleBucketWithChild() {
        assertThat(rootBucket.getVolume(), is(0.75));
    }

    @Test
    public void testEstimateForQuery() {
        Query query = new Query(0.3, 0.7, 0.0, 1.0);
        rootBucket.getEstimateForQuery(query);
    }

}
