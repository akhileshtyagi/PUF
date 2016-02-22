package metrics;

import java.util.ArrayList;

/**
 * Created by tim on 2/3/16.
 *
 * holds differant metrics which can describe a point.
 * The idea is any number of metrics may be added.
 *
 * also provides methods to manipulate metrics.
 *
 * for example, pressure, distance, time are all differant metrics.
 */
public class Point_metrics {
    public enum METRIC_TYPE{

    }

    private ArrayList<Metric> point_metrics;

    public Point_metrics(){
        this.point_metrics = new ArrayList<Metric>();
    }

    /**
     * adds a metric, returns the index of that metric
     */
    public int add_metric(Metric metric){
        point_metrics.add(metric);

        return point_metrics.size() - 1;
    }

    public Metric get_metric(int metric_index){
        return this.point_metrics.get(metric_index);
    }
}
