package metrics;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by tim on 2/3/16.
 * <p>
 * holds differant metrics which can describe a point.
 * The idea is any number of metrics may be added.
 * <p>
 * also provides methods to manipulate metrics.
 * <p>
 * for example, pressure, distance, time are all differant metrics.
 */
public class PointMetrics implements Serializable {
    private ArrayList<Metric> point_metrics;

    public PointMetrics() {
        this.point_metrics = new ArrayList<Metric>();

        // initialize all values to null
        for (int i = 0; i < Metric.METRIC_TYPE.values().length; i++) {
            this.point_metrics.add(null);
        }
    }

    public PointMetrics(PointMetrics point_metrics) {
        this.point_metrics = new ArrayList<Metric>(point_metrics.point_metrics);
    }

    /**
     * adds a metric of the specified type
     */
    public void add_metric(Metric metric) {
        point_metrics.set(metric.get_type().id, metric);
    }

    /**
     * returns null when the metric has not been added yet
     */
    public Metric get_metric(Metric.METRIC_TYPE type) {
        return this.point_metrics.get(type.id);
    }

    /**
     * override .equals method.
     * Compare the values of each metric
     */
    @Override
    public boolean equals(Object point_metrics) {
        if( point_metrics == null){
            return false;
        }

        PointMetrics other = (PointMetrics) point_metrics;
        boolean equal = true;

        for (int i = 0; i < Metric.METRIC_TYPE.values().length; i++) {
            if(this.point_metrics.get(i) != null) {
                // compare the value of the metrics
                equal = equal && this.point_metrics.get(i).equals(other.point_metrics.get(i));
            } else {
                // if this point metrics has a null element
                // and the other does not, than they are not equal
                equal = equal && (other.point_metrics.get(i) != null);
            }
        }

        return equal;
    }
}
