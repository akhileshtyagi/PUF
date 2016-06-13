package computation;

import components.Touch;
import components.Chain;
import components.Token;
import components.Window;
import trie.TrieList;

import java.util.ArrayList;
import java.util.List;

/**
 * describes the distance between each set of tokens which succeed the window
 * and the corresponding set of tokens which succeed the window
 * in the other model
 * <p>
 * in other words, this vector describes the difference between two windows
 */
public class WindowDistance extends ArrayList<TokenDistance> {
    private Window user_window;
    private Window auth_window;

    private double distance_tokens_weighted;
    private double distance_tokens_unweighted;
    private double weight;

    /**
     * the windowe weight is based on the window from the auth model
     */
    public WindowDistance(Window auth_window, Chain user_chain, Chain auth_chain) {
        super();

        // auth window
        this.auth_window = auth_window;

        // populate this list with token distances as appropriate
        populate_this(auth_chain.get_tokens(), (TrieList)auth_chain.get_windows(), user_chain, auth_chain);

        // compute aggregate values for distance and weight
        // compute unweighted distance
        this.distance_tokens_weighted = distance_tokens_weighted();
        this.distance_tokens_unweighted = distance_tokens_unweighted();

        // compute weight
        this.weight = weight(auth_window, (TrieList)auth_chain.get_windows());
    }

    /**
     * populates this object with TokenDistance objects
     */
    private void populate_this(List<Token> token_list, TrieList window_list, Chain user_chain, Chain auth_chain) {
        // create list of TokenDistance
        List<Touch> successor_list = null;

        // get the unique successor touches of a window
        List<Integer> unique_successor_list = Chain.compute_unique_successors(token_list, successor_list, window_list.get_index_list(this.auth_window));

        // for each unique successor
        for (Integer i : unique_successor_list) {
            // add the TokenDistance object to this
            this.add(new TokenDistance(successor_list.get(i), auth_chain.get_successors(), i, auth_chain.get_tokens(), this.auth_window));
        }
    }

    /**
     * returns the weighted difference between the windows
     */
    public double get_weighted_distance(boolean tokens_are_weighted) {
        if(tokens_are_weighted){ return this.distance_tokens_weighted * this.weight; }
        else{ return this.distance_tokens_unweighted * this.weight; }
    }

    /**
     * returns the unweighted difference between the windows
     */
    public double get_unweighted_distance(boolean tokens_are_weighted) {
        if(tokens_are_weighted){ return this.distance_tokens_weighted; }
        else{ return this.distance_tokens_unweighted; }
    }

    /**
     * compute the difference between window_0 and window_1 given
     * window_0 from base model
     * window_1 from auth model
     *
     * this function acts based on items stored in "this"
     */
    //TODO might be incorrect
    private double distance_tokens_weighted() {
        double distance = 0.0;

        // for token which succeeds this window
        for(TokenDistance token_distance : this){
            distance += token_distance.get_weighted_distance();
        }

        // divide by the size of the list to get an average
        return distance / this.size();
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
    //TODO might be incorrect
    private double distance_tokens_unweighted() {
        double distance = 0.0;

        // for token which succeeds this window
        for(TokenDistance token_distance : this){
            distance += token_distance.get_unweighted_distance();
        }

        // divide by the size of the list to get an average
        return distance / this.size();
    }

    /**
     * returns the weight of the window given
     * window
     * window list
     */
    private double weight(Window window, TrieList window_list) {
        double weight = ((double)window_list.occurrence_count(window)) / ((double)window_list.size());

        return weight;
    }
}