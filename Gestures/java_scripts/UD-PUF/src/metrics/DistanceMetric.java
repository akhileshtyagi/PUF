package metrics;

/**
 * Created by element on 2/7/16.
 */
public class DistanceMetric extends Metric<Double> {
    public DistanceMetric(Double value){
        super(value);

        this.type = Metric.METRIC_TYPE.DISTANCE;
    }
}
