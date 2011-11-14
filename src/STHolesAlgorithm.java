public class STHolesAlgorithm {

    private final Bucket rootBucket;
    private int maxBucketCount;

    public STHolesAlgorithm() {
        rootBucket = new Bucket(0.0, 1.0, 0.0, 1.0);
    }

    public double getEstimateQuery(Query query) {
        return rootBucket.getEstimateForQuery(query);
    }

    public void updateHistogram(Query query, int actualResultCount) {
        // identify candidate holes

        // drill candidate holes

        // merge superfluous buckets
    }

}
