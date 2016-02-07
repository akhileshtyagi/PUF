package metrics;

/**
 * Created by element on 2/3/16.
 */
public class Metric<T> {
    /* enumerate the Point_metrics types
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
