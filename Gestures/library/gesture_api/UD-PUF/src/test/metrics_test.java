package test;

import metrics.Metric;
import metrics.Point_metrics;

/**
 * Created by element on 2/3/16.
 */
public class metrics_test {
    public static void main(String[] args){
        Point_metrics point_metrics = new Point_metrics();

        int pressure_metric = point_metrics.add_metric(new Metric<Double>(6.0));
        int integer_metric = point_metrics.add_metric(new Metric<Integer>(1));

        System.out.println(point_metrics.get_metric(pressure_metric).get_value());
        System.out.println(point_metrics.get_metric(integer_metric).get_value());
    }
}
