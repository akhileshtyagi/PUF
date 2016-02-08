package metrics;

import java.io.Serializable;

/**
 * Created by element on 2/3/16.
 */
public abstract class Metric<T> implements Serializable {
    /* enumerate the PointMetrics types
     * type id will correspond to the index
     */
    public enum METRIC_TYPE {
        PRESSURE(0),
        DISTANCE(1),
        TIME(2),
        VELOCITY(3),
        ACCELERATION(4);

        public int id;

        METRIC_TYPE(int id) {
            this.id = id;
        }
    }

    protected T value;
    protected METRIC_TYPE type;

    public Metric(T thing){
        value = thing;
        this.type = null;
    }

    public Metric(Metric<? extends Cloneable> metric){
        // This should never fail because we require that all metrics implement cloneable
        try {
            this.value = ((Metric<T>)(metric.clone())).value;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        this.type = metric.type;
    }

    public void set_value(T value){
        this.value = value;
    }

    public T get_value(){
        return this.value;
    }

    public METRIC_TYPE get_type(){
        return this.type;
    }
}
