package metrics;

/**
 * Created by element on 2/7/16.
 */
public class AccelerationMetric extends Metric<Double> {
    public AccelerationMetric(Double value){
        super(value);

        this.type = METRIC_TYPE.ACCELERATION;
    }
}
