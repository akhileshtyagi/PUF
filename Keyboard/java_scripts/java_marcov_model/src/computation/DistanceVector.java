package computation;

import components.Chain;
import components.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO list
 * [x] finish implementing WindowDistance
 * [x] finish implementing  TokenDistance
 * [ ] implement methods which will be needed by Confidence in order to compute Distance confidence
 * [ ] go though WindowDistance, TokenDistance and give them only the arguments they actually need in the constructor
 * [ ] make sure WindowDistance has correct weighted and unweighted distance computations
 * [ ] Test all functionality of distance vector
 *      [ ] WindowDistance weighted correct
 *      [ ] WindowDistance unweighted correct
 *      [ ] TokenDistance weighted correct
 *      [ ] TokenDistance unweighted correct
 * [ ] test Chain.get_distance_confidence for correctness
 * [ ] determine if distance confidence is more confident for higher accuracy authentications
 */

/**
 * describes the difference between two chains
 * <p>
 * given a window in chain,
 * this class should be able to return a difference
 * between that window, and
 * the corresponding window in the auth chain
 * it was constructed with
 *
 * NOTE:
 * This class and the class, and the classes utilized by it are not efficient
 */
public class DistanceVector extends ArrayList<WindowDistance> {
    private double distance_windows_weighted;
    private double distance_windows_unweighted;

    /**
     * create an element in this array for every unique window
     *
     * THIS ASSUMES THAT ALL VALUES FROM THE CHAINS HAVE BEEN COMPUTED
     */
    public DistanceVector(Chain user_chain, Chain auth_chain) {
        super();

        // get the unique windows
        List<Window> auth_window_list = auth_chain.get_windows();
        List<Integer> unique_window_list = Chain.compute_unique_windows(auth_chain.get_tokens(), auth_window_list);

        // for each unique successor
        for (Integer i : unique_window_list) {
            // add a window difference to this
            this.add(new WindowDistance(auth_window_list.get(i), user_chain, auth_chain));
        }

        // determine the overall distance between the chains
        // compute aggregate values for distance and weight
        // compute unweighted distance
        this.distance_windows_weighted = distance_tokens_weighted();
        this.distance_windows_unweighted = distance_tokens_unweighted();
    }

    /**
     * returns the weighted difference between the windows
     */
    public double get_weighted_distance() {
        return this.distance_windows_weighted;
    }

    /**
     * returns the unweighted difference between the windows
     */
    public double get_unweighted_distance() {
        return this.distance_windows_unweighted;
    }

    /**
     * compute the difference between window_0 and window_1 given
     * window_0 from base model
     * window_1 from auth model
     *
     * this function acts based on items stored in "this"
     */
    private double distance_tokens_weighted() {
        double distance = 0.0;

        // for token which succeeds this window
        for(WindowDistance window_distance : this){
            // true => also weighting tokens
            distance += window_distance.get_weighted_distance(true);

            //TODO test
            //TODO DV window distances are a lot smaller than chain window differences... why?
            System.out.println("DV window distance: " + window_distance.get_weighted_distance(true));
        }

        //TODO consider which it should be
        // divide by the size of the list to get an average
        //return distance / this.size();

        return distance;
    }

    /**
     * compute the difference between window_0 and window_1 given
     * window_0 from base model
     * window_1 from auth model
     *
     * this function acts based on items stored in "this"
     *
     * get the unweighted version of tokens
     */
    private double distance_tokens_unweighted() {
        double distance = 0.0;

        // for token which succeeds this window
        for(WindowDistance window_distance : this){
            // true => also weighting tokens
            distance += window_distance.get_unweighted_distance(true);
        }

        //TODO
        System.out.println("DV difference: " + distance);

        // divide by the size of the list to get an average
        return distance / this.size();
    }
}
