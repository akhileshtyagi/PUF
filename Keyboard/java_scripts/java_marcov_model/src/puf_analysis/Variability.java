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

import java.io.File;
import java.util.*;

/**
 * This quantifies the difference in response
 * given that there is a difference in challenge.
 * It answers the question,
 * If the challenge changes, how much does the response change on average.
 * This can be quantified though a metric which measures the
 * bitwise difference between responses.
 * For this reason,
 * the average hamming distance will be a good way to measure the variability.
 *
 * a good average hamming distance will be close to the size to the challenge.
 * ------
 * another way to quantify this would be:
 * the average change in the hamming distance given
 * some number of bits changed in the challenge
 *
 * in other words the goal might be stated as
 * quantifying how the responses depend on the challenge
 */
public class Variability {
    public static final String DATA_FOLDER = "data_sets";
    public static final int CHALLENGE_BITS = 128;
    public static final int CHALLENGE_NUMBER = 2;

    /**
     * provide a file name and device int for each chain involved
     */
    public static final String[] CHAIN_FILE_NAME = {
            "t_tim_d_ian.csv_4512"
    };
    public static final int[] CHAIN_DEVICE = {
        0
    };

    public static void main(String[] args){
        //////
        // arbiter definition
        //////
        List<Arbiter> arbiter_list = new ArrayList<>();
        arbiter_list.add(new AverageArbiter());
        arbiter_list.add(new AverageArbiter());
        arbiter_list.add(new AverageArbiter());
        arbiter_list.add(new AverageArbiter());
        arbiter_list.add(new AverageArbiter());

        PUF puf = new PUF(arbiter_list);

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
        for(int i=0; i<CHAIN_FILE_NAME.length; i++) {
            chain_list.add(Utility.read_chain(new File(DATA_FOLDER, CHAIN_FILE_NAME[i]).getPath()));
            device_list.add(CHAIN_DEVICE[i]);

            System.out.println("size: " + chain_list.size());
        }

        // make sure chains have computed everything
        //TODO is this necessary? think about it.
        for(int i=0; i<chain_list.size(); i++) {
            chain_list.get(i).compare_to(chain_list.get(i));
        }

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
            for(int j=0; j<challenge_list.size(); j++) {
                for (int k = 0; k < device_list.size(); k++) {
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
        // print out all responses
        //////
        for(Triple<Integer, Chain, Challenge> triple : response_map.keySet()){
            // print out the tripple and it's associated response
            System.out.println("tripple: " + triple + "\nresponse: " + response_map.get(triple));
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

    private static class Triple<X,Y,Z> {
        X x;
        Y y;
        Z z;

        public Triple(X x, Y y, Z z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return "<" + x + ", " + y + "\n, " + z + ">";
        }
    }
}
