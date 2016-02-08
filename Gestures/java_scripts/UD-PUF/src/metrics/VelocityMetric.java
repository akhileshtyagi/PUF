package metrics;

/**
 * Created by element on 2/7/16.
 */
public class VelocityMetric extends Metric<Double>{
    public VelocityMetric(Double value){
        super(value);

        this.type = Metric.METRIC_TYPE.VELOCITY;
    }
}
