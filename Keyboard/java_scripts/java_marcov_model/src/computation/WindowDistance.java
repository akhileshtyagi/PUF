package computation;

import components.Chain;
import components.Window;
import trie.TrieList;

import java.util.ArrayList;
import java.util.List;

/**
 * describes the distance between each set of tokens which succeed the window
 * and the corresponding set of tokens which succeed the window
 * in the other model
 *
 * in other words, this vector describes the difference between two windows
 */
public class WindowDistance extends ArrayList<TokenDistance> {
    private Window user_window;
    private Window auth_window;

    private double distance;
    private double weight;

    /**
     * the windowe weight is based on the window from the auth model
     */
    public WindowDistance(Window user_window, Window auth_window, TrieList auth_window_list){
        super();

        this.user_window = user_window;
        this.auth_window = auth_window;

        // get the unique successor touches of a window
        List<Integer> unique_successor_list = Chain.compute_unique_successors(token_list, successor_list, window_list.get_index_list(window));

        // for each unique successor
        for(Integer i : unique_successor_list){
            // weight should simply be the probability of the touch occurring after the given window
            //double weight = 1 / unique_successor_list.size();
            double weight = successor_list.get(i).get_probability(token_list, window);
            double touch_confidence = compute_touch_confidence(key_distribution_list, successor_list.get(i));

            //System.out.println("touch_confidence: " + touch_confidence + "touch_weight: " + weight);

            double confidence += weight * touch_confidence;

            // add the confidence to this list

        }

        // compute aggregate values for distance and weight
        // compute unweighted distance
        this.distance = distance(window_0, window_1);

        // compute weight
        this.weight = weight(window_1);
    }

    /**
     * returns the weighted difference between the windows
     */
    public double get_weighted_distance(){
        return this.distance * this.weight;
    }

    /**
     * returns the unweighted difference between the windows
     */
    public double get_unweighted_distance(){
        return this.distance;
    }

    /**
     * compute the difference between window_0 and window_1 given
     * window_0 from base model
     * window_1 from auth model
     */
    private double distance(Window user_window, Window auth_window){
        //TODO
        return 0.0;
    }

    /**
     * returns the weight of the window given
     * window
     * window list
     */
    private double weight(Window window, TrieList window_list){
        //TODO
        return 0.0;
    }
}