package computation;

import components.Chain;
import components.Touch;
import components.Window;
import trie.TrieList;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO list
 * [ ] finish implementing ChainDistanceVector, WindowDistance, TokenDistance
 * [ ] implement methods which will be needed by Confidence in order to compute Distance confidence
 */

/**
 * describes the difference between two chains
 * <p>
 * given a window in chain,
 * this class should be able to return a difference
 * between that window, and
 * the corresponding window in the auth chain
 * it was constructed with
 */
public class DistanceVector extends ArrayList<WindowDistance> {
    /**
     * create an element in this array for every unique window
     */
    public DistanceVector(Chain user_chain, Chain auth_chain) {
        super();

        // get the unique windows
        List<Integer> unique_window_list = Chain.compute_unique_windows(token_list, window_list);

        // for each unique successor
        for (Integer i : unique_window_list) {
            // add a window difference to this
            this.add(new WindowDistance(window_list.get(i), ));
        }
    }
}
