package computation;

import components.Chain;
import components.Token;
import components.Touch;
import components.Window;

import java.util.List;

/**
 * describes the difference between two tokens
 */
public class TokenDistance {
    private Touch user_touch;
    private Touch auth_touch;

    private double distance;
    private double weight;

    public TokenDistance(Touch auth_touch, List<Touch> successor_list_auth, int auth_index, List<Token> auth_tokens, Window auth_window) {
        super();

        this.auth_touch = auth_touch;

        // determine the corresponding user touch
        this.user_touch = null;

        this.distance = distance(auth_touch, successor_list_auth, auth_index, auth_tokens, auth_window);
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
        return this.distance;
    }

    /**
     * returns the distance between two touches
     */
    private double distance(Touch auth_touch, List<Touch> successor_list_auth, int auth_index, List<Token> auth_tokens, Window auth_window) {
        // compute base probability
        double base_probability = successor_list_auth.get(auth_index).get_probability(auth_tokens, auth_window);

        // compute auth probability
        //TODO
        double auth_probability = successor_list_user.get(user_index).get_probability(auth_tokens, auth_window);

        // compute absolute difference
        return Math.abs(base_probability - auth_probability);
    }

    /**
     * returns the weight of this difference
     */
    private double weight(Touch auth_touch, List<Touch> successor_list_auth, int auth_index, List<Token> auth_tokens, Window auth_window) {
        // token weight is simply the probability in the auth model
        // this is because we are weighting by occurrences and auth_probability represents
        // the fractional amount of time the token occurred
        // return successor_list_auth.get(index_list_auth.get(i)).get_probability(this.get_tokens(), window);
        return successor_list_auth.get(auth_index).get_probability(auth_tokens, auth_window);
    }
}
