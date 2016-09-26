package puf;

import arbiter.Arbiter;
import components.Chain;

import java.util.List;
import java.util.Random;

/**
 * Created by element on 9/8/16.
 */
//TODO do quantization based on next state probabilities instead of averages
public class PUF {
    protected List<Arbiter> arbiter_list;

    public PUF(List<Arbiter> arbiter){
        this.arbiter_list = arbiter;
    }

    /**
     * computes a response given a challenge
     * number of response bits generated depends on the size of the challenge,
     * one byte generated from each letter
     */
    public Response compute(Challenge challenge){
        // bits per character should be the same as the number of characters
        // each arbiter will decide one bit
        int bits_per_character = 5;

        Bit[] response_bits = new Bit[challenge.get_challenge_bits().length];
        Bit[][] quantized_bit_array = new Bit[arbiter_list.size()][];

        // for each arbiter, preform quantization
        for(int i=0; i<arbiter_list.size(); i++){
            quantized_bit_array[i] = arbiter_list.get(i).quantize(challenge);
        }

        // combine all bit arrays created by arbiters
        // result should be the same length as the original challenge
        for(int i=0; i<quantized_bit_array.length; i++){
            for(int j=0; j<quantized_bit_array[0].length; j++) {
                response_bits[(i*quantized_bit_array[0].length) + j] = quantized_bit_array[i][j];
            }
        }

        // return a response which is the same length as the challenge
        //TODO this does not work if challenge is not a multiple of the number of bits per character
        return new Response(response_bits);
    }

    /**
     * construct an arbitrary challenge
     *
     * only constructs alphabetic challenges
     */
    public static Challenge construct_arbitrary_challenge(int challenge_size, long seed){
//        String alphabet = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//
//        String challenge_string = "";
//        Random random = new Random(seed);
//
//        // this will always generate the same challenge string unless given a seed
//        for(int i=0; i<challenge_size; i++){
//            challenge_string += alphabet.charAt(Math.abs(random.nextInt()%alphabet.length()));
//        }
//
//        return new Challenge(challenge_string);

        Random random = new Random(seed);
        Bit[] bit_array = new Bit[challenge_size];

        for(int i=0; i<challenge_size; i++){
            // randomly choose 1 or zero
            Bit.Value value = (random.nextInt()%2 == 0) ? Bit.Value.ONE : Bit.Value.ZERO;

            bit_array[i] = new Bit(value);
        }

        return new Challenge(bit_array);
    }
}
