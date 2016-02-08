package metrics;

/**
 * Created by element on 2/7/16.
 */
public class PressureMetric extends Metric<Double> {
     public PressureMetric(Double value){
          super(value);

          this.type = METRIC_TYPE.PRESSURE;
     }
}
