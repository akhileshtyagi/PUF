package dataTypes;

import metrics.Metric;
import metrics.PointMetrics;

import java.io.Serializable;

/**
 * This class represents one point of x, y, and pressure values
 */
public class Point implements Serializable {
    private static final long serialVersionUID = -6396773155505367546L;

    // point_metrics contain the metrics we will use to evaluate the trace.
    // examples are pressure, distance, time, velocity, acceleration
    private PointMetrics point_metrics;
    double x;
    double y;

    public Point(double x, double y) {
        this(x, y, new PointMetrics());
    }

    public Point(double x, double y, PointMetrics point_metrics) {
        this.point_metrics = point_metrics;
        this.x = x;
        this.y = y;
    }

    public Point(Point p) {
        this.point_metrics = new PointMetrics(p.point_metrics);
        this.x = p.x;
        this.y = p.y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Metric getPointMetric(Metric.METRIC_TYPE type) {
        return point_metrics.get_metric(type);
    }

    /**
     * returns the point metrics object.
     *
     * This is useful for making another point with the same point metrics
     */
    public PointMetrics getPointMetrics(){
        return this.point_metrics;
    }

    public void setPointMetric(Metric<? extends Metric> value){
        point_metrics.add_metric(value);
    }

    /**
     * equality does not check for distance or timestamp. This is done on
     * purpose. It doesn't make sense to check for time because all touchs are
     * separated by time. If we did check for time this would never return true.
     * It doesn't make sense to check for distance because this is a function of
     * the x/y values and the axis to which this is being normalized. Because we
     * already check x,y we know that if the touches were being normalized to
     * the same axis, the distance would be the same. And if they are not being
     * normalized to the same axis, we don't want this to affect equality, so we
     * don't check.
     */
    @Override
    public boolean equals(Object p) {
        Point other = (Point) p;
        boolean same = true;

        // check x,y values
        same = same && this.x == other.x;
        same = same && this.y == other.y;

        // check PointMetrics
        same = same && this.point_metrics.equals(other.point_metrics);

        return same;
    }

    @Override
    public String toString() {
        String string = "";

        string += "[";
        string += this.x + ", ";
        string += this.y + ", ";
        string += this.point_metrics.toString();

        return string;
    }
}
