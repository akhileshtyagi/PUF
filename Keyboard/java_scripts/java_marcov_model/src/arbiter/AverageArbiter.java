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
     */
    public Bit[] quantize(Challenge challenge){
        Bit[] quantization_bits = new Bit[challenge.get_challenge_string().length()];
        double average_probability = compute_next_state_average(challenge);

        // quantize each bit based on it's value compared to the average
        int bit_index = 0;
        for(char character : challenge.get_challenge_string().toCharArray()) {
            double next_state_average = compute_next_state_average(character);

            // set bit value 1 if this bit's average is
            // greater than equal average over all in the challenge
            // 0 otherwise
            quantization_bits[bit_index] = (next_state_average >= average_probability) ?
                    (new Bit(Bit.Value.ONE)):(new Bit(Bit.Value.ZERO));

            bit_index++;
        }

        return quantization_bits;
    }

    /**
     * compute the average next state probability for the challenge
     */
    private double compute_next_state_average(Challenge challenge){
        // for all bits in the challenge string
        List<Double> probability_list = new ArrayList<>();
        for(char character : challenge.get_challenge_string().toCharArray()){
            probability_list.add(compute_next_state_average(character));
        }

        // use Utility.average to preform the average computation
        return Utility.average(probability_list);
    }

    /**
     * compute the average next state probability for a single character
     * given it's TODO
     *
     * definition of next state probability:
     * 
     */
    private double compute_next_state_average(char character){
        // get the android code for this character
        int android_code = Utility.char_to_android_code(character);

        // generate a list to take the average of
        // list contains the next state probabilities for this character
        List<Double> next_state_probabilities = new ArrayList<>();
        //TODO which keycode corresponds to which character
//        for(challenge.get_user_input().get_next_state_probability_list()){
//
//        }
        //TODO
    }

    //TODO
    return 0.0;
}
