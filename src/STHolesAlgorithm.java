import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class STHolesAlgorithm {

    private final Bucket rootBucket;
    private int maxBucketCount;

    /**
     * Creates a new StHoles Algorithm.
     * @param maxBucketCount The number of buckets the algorithm may create.
     */
    public STHolesAlgorithm(int maxBucketCount) {
        this.maxBucketCount = maxBucketCount;
        
    	// Create a new root bucket over the complete range and initialize f(root) with 0.
        this.rootBucket = new Bucket(0.0, 1.0, 0.0, 1.0);
    }

    /**
     * Estimates the query.
     * @param q The query.
     * @return The estimate of the query.
     */
    public double getEstimateQuery(Query q) {
        return rootBucket.getEstimateForQuery(q);
    }

    public void updateHistogram(Query q, int actualResultCount) {
        // identify candidate holes
    	Map<Bucket, Bucket> candiates = IdentifyCandiateHoles(q, actualResultCount);
    	
        // drill candidate holes

        // merge superfluous buckets
    }
    
    private Map<Bucket, Bucket> IdentifyCandiateHoles(Query q, int actualResultCount)
    {
    	Map<Bucket, Bucket> candiates = new HashMap<Bucket, Bucket>();
    	Rectangle2D.Double queryBox = q.getRectangle2D(); 
  	  // The paper suggests that we have the actual count of all tuples in b.
  	  // Approximate tb by assuming the results are uniformly distributed across q.
  	  //int tb = actualResultCount * (int)(getVolume()/(queryBox.getHeight()*queryBox.getWidth()));
  	  
    	return candiates;    
    }
}
