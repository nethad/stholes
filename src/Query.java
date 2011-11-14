import java.awt.geom.Rectangle2D;

public class Query {

    private double xMin, xMax, yMin, yMax;

    @Override
    public String toString() {
        return "[xMin: " + xMin + ",xMax: " + xMax + ",yMin: " + yMin + ",yMax: " + yMax + "]";
    }

    public Rectangle2D.Double getRectangle2D() {
        return new Rectangle2D.Double(xMin, yMin, xMax - xMin, yMax - yMin);
    }
}
