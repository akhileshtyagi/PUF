package puf_analysis;

import arbiter.Arbiter;
import arbiter.AverageArbiter;
import components.Chain;
import puf.Challenge;
import puf.PUF;
import puf.Response;
import puf.UserInput;

import java.util.*;

/**
 * Created by element on 9/8/16.
 */
public class Variability {
    public static final int CHALLENGE_BITS = 128;

    public static void main(String[] args){
        //////
        // arbiter definition
        //////
        Arbiter arbiter = new AverageArbiter();
        PUF puf = new PUF(arbiter);

        // create lists of things whose combinations will be analyzed
        List<Chain> chain_list = new ArrayList<>(); //TODO
        List<Integer> device_list = new ArrayList<>(); //TODO

        //TODO each challenge needs to be generated and have user input associated with it
        List<Challenge> challenge_list = new ArrayList<>(); //TODO

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

                    // generate a response
                    Response response = puf.compute(chain, challenge);
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
        // take the hamming distance for each triple
        //TODO
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
}
