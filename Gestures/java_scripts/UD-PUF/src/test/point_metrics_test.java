package test;

import metrics.Metric;
import metrics.PointMetrics;
import metrics.PressureMetric;
import metrics.DistanceMetric;
import metrics.TimeMetric;
import metrics.VelocityMetric;
import metrics.AccelerationMetric;

/**
 * Created by element on 2/7/16.
 */
public class point_metrics_test {
    public static void main(String[] args) {
        PointMetrics point_metrics = new PointMetrics();

        // create the metrics
        //Metric<Double> PressureMetric = new PressureMetric(0.5);
        Metric<Double> DistanceMetric = new DistanceMetric(0.5);
        Metric<Double> TimeMetric = new TimeMetric(1.0);
        Metric<Double> VelocityMetric = new VelocityMetric(0.5);
        Metric<Double> AccelerationMetric = new AccelerationMetric(0.5);

        // add all the metrics to the PointMetrics class
        //point_metrics.add_metric(PressureMetric);
        point_metrics.add_metric(DistanceMetric);
        point_metrics.add_metric(TimeMetric);
        point_metrics.add_metric(VelocityMetric);
        point_metrics.add_metric(AccelerationMetric);

        // test access to values
        // must do null check on value returned to ensure it exists
        if( point_metrics.get_metric(Metric.METRIC_TYPE.PRESSURE) != null) {
            System.out.println(point_metrics.get_metric(Metric.METRIC_TYPE.PRESSURE).get_value());
        }

        // test copy constructor
        PointMetrics point_metrics_copy = new PointMetrics(point_metrics);

        point_metrics_copy.add_metric(new PressureMetric(1.0));
        System.out.println(point_metrics_copy.get_metric(Metric.METRIC_TYPE.PRESSURE).get_value());

        // now test each class after modificiation
        response_test();
        profile_test();
        user_device_pair_test();
    }

    /**
     * methods to test:
     * getTimeLength()
     *
     */
    private static void response_test() {

    }

    private static void profile_test() {

    }

    private static void user_device_pair_test() {

    }
}
