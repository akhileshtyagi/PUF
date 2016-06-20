package computation;

import components.Chain;
import components.Token;
import components.Touch;
import components.Window;
import trie.TrieList;

import java.util.List;

/**
 * describes the difference between two tokens
 */
public class TokenDistance {
    private Touch user_touch;
    private Touch auth_touch;

    private double distance;
    private double weight;

    public TokenDistance(Touch auth_touch, List<Touch> successor_list_user, List<Touch> successor_list_auth, int auth_index, List<Token> auth_tokens, Window auth_window, TrieList base_window_list) {
        super();

        this.auth_touch = auth_touch;

        // determine the corresponding user touch
        this.user_touch = null;

        this.distance = distance(auth_touch, successor_list_user, successor_list_auth, auth_index, auth_tokens, auth_window, base_window_list);
        this.weight = weight(auth_touch, successor_list_auth, auth_index, auth_tokens, auth_window);
    }

    /**
     * returns the weighted difference between the tokens
     */
    public double get_weighted_distance() {
        return this.distance * this.weight;
    }

    /**
     * returns the unweighted difference between the tokens
     */
    public double get_unweighted_distance() {
        //System.out.println("distance: " + distance);

        return this.distance;
    }

    /**
     * returns the weight of the window
     */
    public double get_weight(){
        return this.weight;
    }

    /**
     * returns the distance between two touches
     */
    private double distance(Touch auth_touch, List<Touch> successor_list_user, List<Touch> successor_list_auth, int auth_index, List<Token> auth_tokens, Window auth_window, TrieList base_window_list) {
        // compute auth probability
        double auth_probability = successor_list_auth.get(auth_index).get_probability(auth_tokens, auth_window);

        // get the unique successors of base
        List<Integer> index_list_base = Chain.compute_unique_successors(auth_tokens, successor_list_user, base_window_list.get_index_list(auth_window));

        // compute base probability
        double base_probability = 0;
        // getting base_probability is incorrect, the index_lists don't necessarily correspond on i
        // want the probability of getting the same successor touch in the base model
        // for all successor touches of base
        int base_touch_index = -1;
        for(int j=0; j<index_list_base.size(); j++){
            // determine if there is a touch which matches the auth touch
            if( successor_list_user.get(index_list_base.get(j))
                    .compare_with_token(auth_tokens, successor_list_auth.get(auth_index)) ){
                // they do match, this the index in index_list_base which corresponds to the index in index_list_auth
                base_touch_index = j;
                break;
            }
        }

        // if there is no such touch, base_probability is 0
        if(base_touch_index == -1){
            // no touch was found
            base_probability = 0.0;
        }else{
            // matching touch was found
            base_probability = successor_list_user.get(index_list_base.get(base_touch_index)).get_probability(auth_tokens, auth_window);
        }

        //System.out.println("auth_probability: " + auth_probability + "\tbase_probability: " + base_probability);

        // compute absolute difference
        return Math.abs(base_probability - auth_probability);
    }

    /**
     * returns the weight of this difference
     */
    private double weight(Touch auth_touch, List<Touch> successor_list_auth, int auth_index, List<Token> auth_tokens, Window auth_window) {
        //TODO
        System.out.println("token weight: " + successor_list_auth.get(auth_index).get_probability(auth_tokens, auth_window));

        // token weight is simply the probability in the auth model
        // this is because we are weighting by occurrences and auth_probability represents
        // the fractional amount of time the token occurred
        // return successor_list_auth.get(index_list_auth.get(i)).get_probability(this.get_tokens(), window);
        return successor_list_auth.get(auth_index).get_probability(auth_tokens, auth_window);
    }
}
