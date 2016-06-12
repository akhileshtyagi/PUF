package computation;

import components.*;
import trie.TrieList;

import java.util.List;

/**
 * TODO list
 * [x] verify touch confidence is being computed correctly
 * [x] verify window confidence is being computed correctly, given touch touch confidence
 * [x] verify overall weighted confidence is being computed correctly, given window confidence
 *      * hand computed some values from the output of print_model
 * [x] verify unweighted confidence is being computed correctly
 *      * if weighted is correct, then unweighted is correct. Tested by setting window weight = 1. got same value as unweighted
 * [ ] create methods capable of computing confidence interval for a distance vector be
 */

/**
 * Measure how go or bad data may be
 */
public class Confidence {
    public enum Weight { WEIGHTED, UNWEIGHTED }

    final static Weight WEIGHT = Weight.WEIGHTED;

    /**
     * compute confidence 0.0 to 1.0 for the distance vector
     */
    public static double compute_confidence(DistanceVector distance_vector){
        //TODO UIMPLEMENTED
        return 0.0;
    }

    /**
     * compute confidence 0.0 to 1.0 for the chain
     */
    public static double compute_confidence(Chain chain){
        return compute_confidence(
            chain.get_key_distribution(),
            chain.get_tokens(),
            (TrieList)chain.get_windows(),
            chain.get_successors()
        );
    }

    /**
     * computes the confidence interval for this chain.
     * this is defined as a number between 0.0 and 1.0
     * 1.0 is maximally confident
     * 0.0 is no confidence
     */
    public static double compute_confidence(
            List<Distribution> key_distribution_list,
            List<Token> token_list,
            TrieList window_list,
            List<Touch> successor_list
    ){
        double confidence = 0.0;

        if(WEIGHT == Weight.WEIGHTED) {
            // weighted computation
            confidence = compute_weighted_confidence(key_distribution_list, token_list, window_list, successor_list);
        }else if(WEIGHT == Weight.UNWEIGHTED){
            // unweighted computation
            confidence = compute_unweighted_confidence(key_distribution_list);
        }

        //TODO remove print computed confidence
        System.out.println("confidence: " + confidence);

        return confidence;
    }

    /**
     * compute weighted confidence interval
     *
     * this mirrors the difference computation
     * the goal is that the windows and tokens which
     * will actually get used in a difference computation
     * will be the ones that contribute to the confidence interval for the data
     *
     * confidence interval will be
     * 1. average confidence for each window
     * 2. weighted by the occurrence of a window
     *
     * confidence of a window will be
     * 1. average of successor touch confidences
     * 2. weighted by the occurrence of a successor touch
     *
     * confidence of a touch will be
     * 1. (sigma / mu) for the key corresponding to that touch
     */
    private static double compute_weighted_confidence(
            List<Distribution> key_distribution_list,
            List<Token> token_list,
            TrieList window_list,
            List<Touch> successor_list
    ){
        double confidence = 0.0;

        // get the unique windows
        List<Integer> unique_window_list = Chain.compute_unique_windows(token_list, window_list);

        // for each unique successor
        for(Integer i : unique_window_list){
            // weight of a window is ( [occurrences of window] / [total windows] )
            //double weight = 1.0 / unique_window_list.size();
            double weight = ((double)window_list.occurrence_count(window_list.get(i))) / ((double)window_list.size());
            double window_confidence = compute_window_confidence(key_distribution_list, token_list, window_list, successor_list, window_list.get(i));

            //System.out.println("window_confidence: " + window_confidence + "\twindow_weight: " + weight);

            confidence += weight * window_confidence;
        }

        return confidence;
    }

    /**
     * compute confidence of a window
     *
     * confidence of a window will be
     * 1. average of successor touch distributions
     * 2. weighted by the occurrence of a successor touch
     */
    private static double compute_window_confidence(
            List<Distribution> key_distribution_list,
            List<Token> token_list,
            TrieList window_list,
            List<Touch> successor_list,
            Window window
    ){
        double confidence = 0.0;

        // get the unique successor touches of a window
        List<Integer> unique_successor_list = Chain.compute_unique_successors(token_list, successor_list, window_list.get_index_list(window));

        // for each unique successor
        for(Integer i : unique_successor_list){
            // weight should simply be the probability of the touch occurring after the given window
            //double weight = 1 / unique_successor_list.size();
            double weight = successor_list.get(i).get_probability(token_list, window);
            double touch_confidence = compute_touch_confidence(key_distribution_list, successor_list.get(i));

            //System.out.println("touch_confidence: " + touch_confidence + "touch_weight: " + weight);

            confidence += weight * touch_confidence;
        }

        return confidence;
    }

    /**
     * compute touch confidence
     *
     * confidence of a touch will be
     * 1. (sigma / mu) for the key corresponding to that touch
     */
    private static double compute_touch_confidence(List<Distribution> key_distribution_list, Touch touch){
        // get the relevent key distribution
        for(Distribution key_distribution : key_distribution_list){
            if(key_distribution.get_keycode() == touch.get_key()){
                return key_distribution.get_standard_deviation() / key_distribution.get_average();
            }
        }

        // if distribution is not found, return 0.0
        System.out.println("Confidence.compute_touch_confidence: this case shouldn't occur.");
        return 0.0;
    }

    /**
     * compute unweighted confidence interval
     *
     * key_distribution_list holds a pressure distribution for each token
     * take the average \sigma by \mu for each for each distribution
     */
    private static double compute_unweighted_confidence(List<Distribution> key_distribution_list){
        double confidence = 0.0;

        // \Sigma_i^n ( (\sigma / \mu) / list_size )
        for(Distribution distribution : key_distribution_list){
            confidence += (distribution.get_standard_deviation() / distribution.get_average()) / key_distribution_list.size();
        }

        return confidence;
    }
}
