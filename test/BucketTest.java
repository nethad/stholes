import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class BucketTest {

    private Bucket rootBucket;

    @Before
    public void setup() {
        rootBucket = new Bucket(0.0, 1.0, 0.0, 1.0);
    }

    @Test
    public void testVolume_forSingleFullBucket() {
        assertThat(rootBucket.getVolume(), is(1.0));
    }

}
