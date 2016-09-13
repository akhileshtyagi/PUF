package puf_analysis;

import arbiter.Arbiter;
import arbiter.AverageArbiter;
import components.Chain;
import components.Touch;
import generator.AverageGenerator;
import generator.Generator;
import puf.Challenge;
import puf.PUF;
import puf.Response;
import utility.Utility;

import java.util.*;

/**
 * Created by element on 9/8/16.
 *
 * TODO make sure the chains have computed all their values
 */
public class Variability {
    public static final int CHALLENGE_BITS = 128;
    public static final int CHALLENGE_NUMBER = 1;

    public static void main(String[] args){
        //////
        // arbiter definition
        //////
        Arbiter arbiter = new AverageArbiter();
        PUF puf = new PUF(arbiter);

        //////
        // generator definition
        //////
        Generator generator = new AverageGenerator();

        //////
        // Chain generation
        //////
        // create lists of things whose combinations will be analyzed
        List<Chain> chain_list = new ArrayList<>();
        List<Integer> device_list = new ArrayList<>();

        // describe the chain and the devcie from which it comes from
        //TODO adjust these to add and remove chains
        chain_list.add(read_chain(null)); device_list.add(0);

        // each challenge needs to be generated and have user input associated with it
        List<Challenge> challenge_list = new ArrayList<>();

        for(int i=0; i<CHALLENGE_NUMBER; i++){
            // generate a challenge and add to list
            Challenge challenge = PUF.construct_arbitrary_challenge(CHALLENGE_BITS, i);

            // these Challenges do no have have UserInput associated with them
            challenge_list.add(challenge);
        }

        // map each set of Device, Chain, [Challenge, UserInput]
        Map<Triple<Integer, Chain, Challenge>, Response> response_map = new HashMap<>();

        // compute the responses
        for(int i=0; i<chain_list.size(); i++){
            for(int j=0; j<chain_list.size(); j++) {
                for (int k = 0; k < chain_list.size(); k++) {
                    // grab this particular combination of things
                    Chain chain = chain_list.get(i);
                    Challenge challenge = challenge_list.get(j);
                    Integer device = device_list.get(k);

                    // ask the challenge to take information in chain as input
                    if(!challenge.compute_user_input(chain_list.get(i), generator)){
                        System.out.println("Failed creating user input");
                        continue;
                    }

                    // generate a response
                    Response response = puf.compute(challenge);
                    Triple<Integer, Chain, Challenge> triple = new Triple<>(device, chain, challenge);

                    // put the response in the map
                    response_map.put(triple, response);
                }
            }
        }

        //////
        // analyze the responses
        //////
        // find average hamming distance between responses
        //
        // take the hamming distance for each triple to every other tripple
        ArrayList<Double> hamming_distance_list = new ArrayList<>();
        for(Response response_0 : response_map.values()){
            for(Response response_1 : response_map.values()){
                // do not compare responses to themselves
                if(response_0 == response_1) continue;

                hamming_distance_list.add((double)response_0.hamming_distance(response_1));
            }
        }

        // take the average hamming distance
        double average_hamming_distance = Utility.average(hamming_distance_list);

        // print out results from taking hamming distance
        System.out.println("average hamming distance: " + average_hamming_distance);
    }

    private static class Triple<X,Y,Z>{
        X x;
        Y y;
        Z z;

        public Triple(X x, Y y, Z z){
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static Chain read_chain(String file_name){
        // if now file name is provided
        if(file_name == null){
            return generate_test_chain();
        }else{
            // use file_name to read a chain from the disk
            //TODO
            return null;
        }
    }

    private static Chain generate_test_chain(){
        //////
        // Chain parameters
        //////
        //TODO adjust chain parameters
        int window_size = 3;
        int token_number = 5;
        int time_threshold = 1000;
        int chain_size = 4000;
        Chain chain = new Chain(window_size, token_number, time_threshold, chain_size);

        // add touches to the chain
        for (int i = 0; i < chain_size; i++) {
            chain.add_touch(new Touch(Utility.char_to_android_code((char)('a' + (i % 26))), (i % 11) * .1, 100));
        }

        return chain;
    }
}
