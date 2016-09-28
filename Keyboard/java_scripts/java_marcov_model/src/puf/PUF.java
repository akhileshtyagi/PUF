package puf;

import arbiter.Arbiter;
import components.Chain;
import puf_analysis.Variability;

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

        Bit[] response_bits = new Bit[challenge.get_challenge_bits().length];
        Bit[][] quantized_bit_array = new Bit[arbiter_list.size()][];

        // for each arbiter, preform quantization
        for(int i=0; i<arbiter_list.size(); i++){
            quantized_bit_array[i] = arbiter_list.get(i).quantize(challenge);
        }

        // combine all bit arrays created by arbiters
        // result should be the same length as the original challenge
        for(int i=0; i<quantized_bit_array.length; i++){
            for(int j=0; j<quantized_bit_array[0].length-1; j++) {
                    response_bits[(i * (quantized_bit_array[0].length - 1)) + j] = quantized_bit_array[i][j];
            }
        }

        // add the last bits of the arbiter to the list if necessary
        int left_over_bits = challenge.get_challenge_bits().length % Challenge.BITS_PER_CHARACTER;

        // want to add all last arbiter bits if they divide evenly
        left_over_bits = (left_over_bits == 0) ? Challenge.BITS_PER_CHARACTER : left_over_bits;

        // for each bit which needs to be added
        for(int i=0; i<left_over_bits; i++){
            // add the last bit from the ith arbiter
            response_bits[response_bits.length - i - 1] = quantized_bit_array[i][quantized_bit_array[i].length-1];
        }

        // return a response which is the same length as the challenge
        //TODO this does not work if challenge is not a multiple of the number of bits per character
        //TODO in this case, the expected behavior is to use the first [number of bits per character] - x arbiters
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
