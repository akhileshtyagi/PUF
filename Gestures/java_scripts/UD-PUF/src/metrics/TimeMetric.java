package metrics;

/**
 * Created by element on 2/7/16.
 */
public class TimeMetric extends Metric<Double> {
    public TimeMetric(Double value){
        super(value);

        this.type = Metric.METRIC_TYPE.TIME;
    }
}
