package puf;

import arbiter.Arbiter;
import components.Chain;

import java.util.Random;

/**
 * Created by element on 9/8/16.
 */
public class PUF {
    protected Arbiter arbiter;

    public PUF(Arbiter arbiter){
        this.arbiter = arbiter;
    }

    /**
     * computes a response given a challenge
     * number of response bits generated depends on the size of the challenge,
     * one byte generated from each letter
     */
    public Response compute(Chain chain, Challenge challenge){
        int response_length = challenge.get_challenge_string().length();
        Bit[] response_bits = new Bit[response_length];

        for(int i=0; i<response_length; i++){
            response_bits[i] = arbiter.quantize_bit(chain, challenge, challenge.get_user_input().touch_input_list.get(i));
        }

        return new Response(response_bits);
    }

    /**
     * construct an arbitrary challenge
     *
     * only constructs alphabetic challenges
     */
    public static Challenge construct_arbitrary_challenge(int challenge_size, long seed){
        //TODO different sizes of challenge strings might yeild different results
        String alphabet = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        String challenge_string = "";
        Random random = new Random(seed);

        // this will always generate the same challenge string unless given a seed
        for(int i=0; i<challenge_size; i++){
            challenge_string += alphabet.charAt(random.nextInt()%alphabet.length());
        }

        return new Challenge(challenge_string);
    }
}
