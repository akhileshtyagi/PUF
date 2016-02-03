package metrics;

/**
 * Created by element on 2/3/16.
 */
public class Metric<T> {
    T variable;

    public Metric(T thing){
        variable = thing;
    }

    public void set_value(T value){
        variable = value;
    }

    public T get_value(){
        return variable;
    }
}
