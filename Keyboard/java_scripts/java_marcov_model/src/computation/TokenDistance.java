package computation;

import components.Touch;

/**
 * describes the difference between two tokens
 */
public class TokenDistance {
    private Touch user_touch;
    private Touch auth_touch;

    private double distance;
    private double weight;

    public TokenDistance(Touch user_touch, Touch auth_touch) {
        super();

        this.user_touch = user_touch;
        this.auth_touch = auth_touch;

        this.distance = distance(user_touch, auth_touch);
        this.weight = weight();
    }

    /**
     * returns the weighted difference between the tokens
     */
    public double get_weighted_distance() {
        //TODO
        return 0.0;
    }

    /**
     * returns the unweighted difference between the tokens
     */
    public double get_unweighted_distance() {
        //TODO
        return 0.0;
    }

    /**
     * returns the distance between two touches
     */
    private double distance(Touch user_touch, Touch auth_touch) {
        // compute base probability
        double base_probability = successor_list.get(i).get_probability(token_list, window);
        //TODO

        // compute auth probability
        double auth_probability = successor_list.get(i).get_probability(token_list, window);
        //TODO

        // compute absolute difference
        return Math.abs(base_probability - auth_probability);
    }

    /**
     * returns the weight of this difference
     */
    private double weight(Touch user_touch, Touch auth_touch) {
        // token weight is simply the probability in the auth model
        // this is because we are weighting by occurrences and auth_probability represents
        // the fractional amount of time the token occurred
        return successor_list_auth.get(index_list_auth.get(i)).get_probability(this.get_tokens(), window);
    }
}
