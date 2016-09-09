package arbiter;

import components.Chain;
import components.Touch;
import puf.Bit;
import puf.Challenge;
import utility.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by element on 9/9/16.
 *
 * arbiter is based on average pressure
 * bits:
 * 1 - if pressure is greater than equal average pressure
 * 0 - if pressure is less than average pressure
 */
public class AverageArbiter implements Arbiter{
    public Bit quantize_bit(Chain chain, Challenge challenge, Touch touch){
        // generate a list to take the average of
        List<Double> touch_pressure_list = new ArrayList<>();
        for(Touch t : challenge.get_user_input().get_input_list()){
            touch_pressure_list.add(t.get_pressure());
        }

        // find the average
        //TODO is this cast bad?
        double average = Utility.average((Double[])touch_pressure_list.toArray());

        // return 1 if this touch pressure is >= average
        return (touch.get_pressure() >= average)?(new Bit(Bit.Value.ONE)):(new Bit(Bit.Value.ZERO));
    }
}
