package components;

import java.util.ArrayList;
import java.util.List;

/**
 * describes the difference between two chains
 *
 * given a window in chain,
 * this class should be able to return a difference
 * between that window, and
 * the corresponding window in the auth chain
 * it was constructed with
 */
public class DistanceVector{
    ChainDistanceVector chain_distance_vector;

    /**
     * represents a list of windows differences that make up a chain
     */
    private class ChainDistanceVector extends ArrayList<WindowDistance>{
        public ChainDistanceVector(){
            super();
        }

        /**
         * return a window difference given a window
         */
        public double get_window_difference(Window window){
            //TODO
            return 0.0;
        }
    }

    /**
     * describes the distance between each set of tokens which succeed the window
     * and the corresponding set of tokens which succeed the window
     * in the other model
     *
     * in other words, this vector describes the difference between two windows
     */
    private class WindowDistance extends ArrayList<TokenDistance> {
        public WindowDistance(Window window_0, Window window_1){
            super();
        }

        /**
         * returns the weighted difference between the windows
         */
        public double get_weighted_differenece(){
            //TODO
            return 0.0;
        }

        /**
         * returns the unweighted difference between the windows
         */
        public double get_unweighted_difference(){
            //TODO
            return 0.0;
        }
    }

    /**
     * describes the difference between two tokens
     */
    private class TokenDistance{
        private double difference;

        public TokenDistance(Touch touch_0, Touch touch_1){
            this.difference = 0.0;
        }

        /**
         * returns the weighted difference between the tokens
         */
        public double get_weighted_differenece(){
            //TODO
            return 0.0;
        }

        /**
         * returns the unweighted difference between the tokens
         */
        public double get_unweighted_difference(){
            //TODO
            return 0.0;
        }
    }

    /**
     * construct a vector representing the difference between two chains
     */
    public DistanceVector(Chain user_chain, Chain auth_chain){
        this.chain_distance_vector = new ChainDistanceVector();

        // I want to find the window difference for each window
        //TODO
    }
}
