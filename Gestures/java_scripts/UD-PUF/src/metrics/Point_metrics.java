package metrics;

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
public class Point_metrics {
    private ArrayList<Metric> point_metrics;

    public Point_metrics() {
        this.point_metrics = new ArrayList<Metric>();

        // initialize all values to null
        for(int i = 0; i < Metric.METRIC_TYPE.values().length; i++){
                this.point_metrics.add(null);
        }
    }

    /**
     * adds a metric, returns the index of that metric
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
}
