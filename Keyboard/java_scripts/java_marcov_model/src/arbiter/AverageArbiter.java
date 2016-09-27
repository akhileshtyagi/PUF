package arbiter;

import components.Chain;
import components.Touch;
import components.Window;
import puf.Bit;
import puf.Challenge;
import utility.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Float.NaN;

/**
 * Created by element on 9/9/16.
 *
 * arbiter is based on average pressure
 * bits:
 * 1 - if pressure is greater than equal average [next state probability]
 * 0 - if pressure is less than average [next state probability]
 */
public class AverageArbiter implements Arbiter{
//    public Bit quantize_bit(Chain chain, Challenge challenge, Touch touch){
//        // generate a list to take the average of
//        List<Double> touch_pressure_list = new ArrayList<>();
//        for(Touch t : challenge.get_user_input().get_input_list()){
//            touch_pressure_list.add(t.get_pressure());
//        }
//
//        // find the average
//        //TODO is this cast bad?
//        double average = Utility.average((Double[])touch_pressure_list.toArray());
//
//        // return 1 if this touch pressure is >= average
//        return (touch.get_pressure() >= average)?(new Bit(Bit.Value.ONE)):(new Bit(Bit.Value.ZERO));
//    }

    /**
     * take the average of next state probabilities for each touch
     * use this to quantize each bit
     *
     * There are a list of touches which correspond to each character
     * for each of those touches,
     * there is a Map<Window, Double> which gives the
     * probability with which a Window predicted that Touch
     *
     * mabe I don't need to map because each touch holds information
     * about the probability with which it is predicted
     */
    public Bit[] quantize(Challenge challenge){
        Bit[] quantization_bits = new Bit[challenge.get_challenge_string().length()];
        double average_probability = compute_next_state_average(challenge);

        String error = "";

        // quantize each bit based on it's value compared to the average
        for(int bit_index=0; bit_index<challenge.get_challenge_string().length(); bit_index++) {
            double next_state_average = compute_next_state_average(challenge.get_user_input().get_input_list().get(bit_index));

            // set bit value 1 if this bit's average is
            // greater than equal average over all in the challenge
            // 0 otherwise
            quantization_bits[bit_index] = (next_state_average >= average_probability) ?
                    (new Bit(Bit.Value.ONE)):(new Bit(Bit.Value.ZERO));

            error += "[" + next_state_average + ", " + quantization_bits[bit_index] + "] ";
        }

        //System.out.println("average "+average_probability + "\t|\t" + error);

        return quantization_bits;
    }

    /**
     * compute the average next state probability for the challenge
     * in other average(average \forall characters)
     */
    private double compute_next_state_average(Challenge challenge){
        // for all bits in the challenge string
        List<Double> probability_list = new ArrayList<>();
        for(int i=0; i<challenge.get_challenge_string().length(); i++){
            // compute next state average for a single character in the challenge
            probability_list.add(compute_next_state_average(challenge.get_user_input().get_input_list().get(i)));
        }

        // use Utility.average to preform the average computation
        double average = Utility.average(probability_list);
        // test for NaN
        return average != average ? 0 : average;
    }

    /**
     * compute the average next state probability for a single character
     * given it's associated touch list
     *
     * prediction_map maps Windows to Doubles.
     * Window predicts C with some Double probability
     *
     * definition of next state probability of character C:
     *  the average probability with which
     *  a token with keycode C is predicted
     *
     * The probability of being predicted is assumed to be 0
     * if every touch in the list is never predicted
     *
     *  TODO it might also make sense to weight the prediction probabilities
     *  TODO by the relative frequency of the Window
     */
    private double compute_next_state_average(List<Touch> touch_list) {

        //TODO touch_list is empty
        //System.out.println(touch_list);

        // list contains the next state probabilities for this character
        List<Double> next_state_probabilities = new ArrayList<Double>();

        // take an average of all probabilities which this touch has been predicted with
        //TODO this doesn't exactly make sense, does it? more thinking required.
        //TODO if it does make sense, it might also make sense to weight
        //TODO the average by X
        for(Touch touch : touch_list) {
            next_state_probabilities.addAll(touch.get_probability_list());
        }

        //System.out.println("next state probabilities: " + next_state_probabilities);

        double average = Utility.average(next_state_probabilities);
        // test for NaN
        return average != average ? 0 : average;
    }
}
