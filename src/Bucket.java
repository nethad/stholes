import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.List;

public class Bucket {

    private int frequency;
    // private double x0, x1, y0, y1;
    private final Rectangle2D.Double box;
    private List<Bucket> children;

    public Bucket(double x0, double x1, double y0, double y1) {
        box = new Rectangle2D.Double(x0, y0, x1 - x0, y1 - y0);
    }

    /**
     * get the volume for this bucket (without the volume of the children)
     * 
     * @return
     */
    public double getVolume() {
        double volume = volumeForRectangle(box);
        for (Bucket childBucket : children) {
            volume -= volumeForRectangle(childBucket.box);
        }
        return volume;
    }

    /**
     * The volume of the overlapping area (query and bucket) multiplied with the bucket frequency, including the
     * estimates for all children
     * <p>
     * est(H,q) = f(b) * ( v(q UNION b) / v(b) ) + est(for all children, q)
     * 
     * @param query
     * @return
     */
    public double getEstimateForQuery(Query query) {
        Double queryBox = query.getRectangle2D();
        if (queryIsInRange(query)) {
            Rectangle2D.Double queryIntersection = (Rectangle2D.Double) box.createIntersection(queryBox);
            double intersectionVolume = volumeForRectangle(queryIntersection);
            for (Bucket childBucket : children) {
                intersectionVolume -= childBucket.getIntersectionVolume(query);
            }
            double estimate = intersectionVolume / getVolume() * frequency;
            for (Bucket childBucket : children) {
                estimate += childBucket.getEstimateForQuery(query);
            }
            return estimate;
        } else {
            return 0D;
        }
    }

    private double getIntersectionVolume(Query query) {
        Rectangle2D.Double intersection = (Rectangle2D.Double) query.getRectangle2D().createIntersection(box);
        return volumeForRectangle(intersection);
    }

    private boolean queryIsInRange(Query query) {
        return box.intersects(query.getRectangle2D());
    }

    public void addChildBucket(Bucket childBucket) {
        children.add(childBucket);
    }

    public void removeChildBucket(Bucket childBucket) {
        children.remove(childBucket);
    }

    private double volumeForRectangle(Rectangle2D.Double box) {
        return box.getWidth() * box.getHeight();
    }

}
