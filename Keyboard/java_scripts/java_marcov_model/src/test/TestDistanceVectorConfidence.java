import components.Chain;
import components.Touch;
import computation.DistanceVector;
import computation.TokenDistance;
import computation.WindowDistance;
import computation.Confidence;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * TODO list
 * [ ] Test that distance vector is correct
 *      [ ] x
 * [ ] Test that distance vector can be used to compute confidence correctly
 *      [ ] x
 */

/**
 * the purpose of this test is to determine if the distance vector is
 * working correctly.
 *
 * the secondary purpose of this class is to test
 * if the distance vector can be used to compute confidence correctly
 */
public class TestDistanceVectorConfidence {
    /**
     * defines a test to be run
     */
    interface Test{ double execute(); }

    /**
     * used to evaluate whether or not a test passes
     */
    interface Evaluate{ boolean evaluate(double result); }

    public static void main(String[] args){
        List<Test> test_list = new ArrayList<>();
        List<Evaluate> evaluate_list = new ArrayList<>();
        List<String> test_name_list = new ArrayList<>();

        /* SET UP TEST VARIABLES */

        Random rand = new Random();

        // create the chains
        Chain user_chain = create_chain(rand.nextInt());
        Chain auth_chain = create_chain(rand.nextInt());

        // make sure everything has been computed for the chains by compareing them
        double compare_to_result = user_chain.compare_to(auth_chain);
        //double compare_to_result = auth_chain.compare_to(user_chain);

        // create the distance vectors which will be used in compairason
        DistanceVector distance_vector = new DistanceVector(user_chain, auth_chain);
        DistanceVector distance_vector_same = new DistanceVector(user_chain, user_chain);

        // used for testing
        //DistanceVector distance_vector_same = distance_vector;

        /* END SET UP TEST VARIABLES */

        // create the test_list and predicate_list
        // test format
        // test name
        // how will the test be evaluated (evaluates double for correctness)
        // what is the test (returns double)

        /* BEGIN TEST CASES */

        /* distance vector tests
        * test that the distance vector functions correctly */

        // expression example
        test_name_list.add("expression example");
        evaluate_list.add( (result) -> (result >= 1.0) );
        test_list.add( () -> (1.0) );

        // block example
        test_name_list.add("block example");
        evaluate_list.add( (result) -> {return result >= 1.0;} );
        test_list.add( () -> {return 1.0;} );

        // maximum test
        test_name_list.add("all window distance less than 1.0");
        evaluate_list.add( (result) -> {
            // result is max of window distance
            return result <= 1.0;
        } );
        test_list.add( () -> {
            // return the maximum of the window distances
            double max = 0.0;
            for(WindowDistance window_distance : distance_vector){
                // determine if there should be a new max
                // check weighted windows
                // weighted tokens
                if(window_distance.get_weighted_distance(true) > max){
                    max = window_distance.get_weighted_distance(true);
                }

                // unweighted tokens
                if(window_distance.get_weighted_distance(false) > max){
                    max = window_distance.get_weighted_distance(false);
                }

                // also check unweighted
                // weighted tokens
                if(window_distance.get_unweighted_distance(true) > max){
                    max = window_distance.get_unweighted_distance(true);
                }

                // unweighted tokens
                if(window_distance.get_unweighted_distance(false) > max){
                    max = window_distance.get_unweighted_distance(false);
                }
            }

            return max;
        } );

        // minimum test test
        test_name_list.add("all window distance greater than 0.0");
        evaluate_list.add( (result) -> {
            // result is min of window distance
            return result >= 0.0;
        } );
        test_list.add( () -> {
            // return the maximum of the window distances
            double min = 1.0;
            for(WindowDistance window_distance : distance_vector){
                // determine if there should be a new max
                // check weighted windows
                // weighted tokens
                if(window_distance.get_weighted_distance(true) < min){
                    min = window_distance.get_weighted_distance(true);
                }

                // unweighted tokens
                if(window_distance.get_weighted_distance(false) < min){
                    min = window_distance.get_weighted_distance(false);
                }

                // also check unweighted
                // weighted tokens
                if(window_distance.get_unweighted_distance(true) < min){
                    min = window_distance.get_unweighted_distance(true);
                }

                // unweighted tokens
                if(window_distance.get_unweighted_distance(false) < min){
                    min = window_distance.get_unweighted_distance(false);
                }
            }

            return min;
        } );

        // chain self distance is 0.0
        test_name_list.add("weighted distance between chains is not 0.0");
        evaluate_list.add( (result) -> {
            // result is the difference between a chain and itself
            return result != 0.0;
        } );
        test_list.add( () -> {
            return distance_vector.get_weighted_distance();
        } );

        // chain self distance is 0.0
        test_name_list.add("unweighted distance between chains is not 0.0");
        evaluate_list.add( (result) -> {
            // result is the difference between a chain and itself
            return result != 0.0;
        } );
        test_list.add( () -> {
            return distance_vector.get_unweighted_distance();
        } );

        // chain self distance is 0.0
        test_name_list.add("weighted distance between chain and itself is 0.0");
        evaluate_list.add( (result) -> {
            // result is the difference between a chain and itself
            return result == 0.0;
        } );
        test_list.add( () -> {
            return distance_vector_same.get_weighted_distance();
        } );

        // chain self distance is 0.0
        test_name_list.add("unweighted distance between chain and itself is 0.0");
        evaluate_list.add( (result) -> {
            // result is the difference between a chain and itself
            return result == 0.0;
        } );
        test_list.add( () -> {
            return distance_vector_same.get_unweighted_distance();
        } );

        // is the distance vector correct?
        test_name_list.add("Chain.compare_to() equal to distance vector?");
        evaluate_list.add( (result) -> {
            //TODO obviously, the computed result is incorrect ( one of them is incorrect )
            //TODO identify which one and what is being done differently
            // print out the result
            System.out.println(String.format("result: %s\t compare_to_result: %s", result, compare_to_result));

            // result is the difference between a chain and itself
            return result == compare_to_result;
        } );
        test_list.add( () -> {
            return distance_vector.get_weighted_distance();
        } );

        // sum of window weights equal 1?
        test_name_list.add("window weight sum correct");
        evaluate_list.add( (result) -> {
            double epsilon = .0000000000001;
            return (result > 1.0 - epsilon) && (result < 1.0 + epsilon);
        } );
        test_list.add( () -> {
            double sum = 0.0;

            for(WindowDistance window_distance : distance_vector){
                sum += window_distance.get_weight();
            }

            return sum;
        } );

        // sum of token weights equal 1? for each window
        test_name_list.add("token weight sum correct");
        evaluate_list.add( (result) -> {
            double epsilon = .0000000000001;
            return (result > 1.0 - epsilon) && (result < 1.0 + epsilon);
        } );
        test_list.add( () -> {
            double epsilon = .0000000000001;

            // returning 0.0 to indicate false
            // or 1.0 to indicate true
            //
            // sum up all the window weights
            for(WindowDistance window_distance : distance_vector){
                double sum = 0.0;

                for(TokenDistance token_distance : window_distance) {
                    sum += token_distance.get_weight();
                }

                // check to see if token sum is not within epsilon of 1.0
                if(!((sum > 1.0 - epsilon) && (sum < 1.0 + epsilon))) {
                    System.out.println("token weight sum " + sum);
                    return 0.0;
                }
            }

            return 1.0;
        } );

        //TODO

        /* confidence tests
        * test that Confidence uses distance vector to compute confidence correctly */
        // test whether the dv confidence is appropriate
        test_name_list.add("confidence value > 0?");
        evaluate_list.add( (result) -> {
            //TODO
            //System.out.println("DV confidence result: "  + result);

            return result > 0.0;
        } );
        test_list.add( () -> {
            // get the result of the confidence computation
            return Confidence.compute_confidence(distance_vector);
        } );

        // test the distance vector confidence for same data
        test_name_list.add("confidence value (same data) close to 0?");
        evaluate_list.add( (result) -> {
            //TODO
            //System.out.println("DV confidence result: "  + result);

            double epsilon = .0000000000001;
            double value = 0.0;
            return (result > value - epsilon) && (result < value + epsilon);
        } );
        test_list.add( () -> {
            // get the result of the confidence computation
            return Confidence.compute_confidence(distance_vector_same);
        } );

        //TODO

        /* END TEST CASES */

        // run the tests
        List<Boolean> pass_list = run_tests(test_list, evaluate_list);

        // print out which tests failed (using lambda functions)
        Predicate<Boolean> failed = (pass) -> (!pass);

        System.out.println("Tests Failed:");
        print_results(test_name_list, pass_list, failed);

        // print out which tests passed (using lambda functions)
        Predicate<Boolean> passed = (pass) -> (pass);

        System.out.println("Tests Passed:");
        print_results(test_name_list, pass_list, passed);
    }

    /**
     * print test name if it passed
     *
     * this is a way of filtering results printed
     */
    private static void print_results(List<String> test_name_list, List<Boolean> pass_list, Predicate<Boolean> predicate){
        // print out all tests for which the predicate is true
        for(int i=0; i<test_name_list.size(); i++){
            // test if the predicate is true
            if(predicate.test(pass_list.get(i))){
                System.out.println("\t[" + test_name_list.get(i) + "]");
            }
        }
    }

    /**
     * Executes a list of tests
     *  given test list
     *  given predicate list to determine if tests pass
     *
     * returns a boolean list of pass fails
     */
    private static List<Boolean> run_tests(List<Test> test_list, List<Evaluate> evaluate_list){
        List<Boolean> pass_list = new ArrayList<>();

        // go though all the tests and indicate whether or not they passed
        for(int i=0; i<test_list.size(); i++){
            // run the test
            double result = test_list.get(i).execute();

            // determine if the test passes
            boolean pass = evaluate_list.get(i).evaluate(result);

            // add the result (pass/fail) to the pass list
            pass_list.add(pass);
        }

        return pass_list;
    }

    /**
     * this method generates a chain given a random seed
     *
     * the seed is used to generate the touch pressure values which are added to the chain
     *
     * the purpose of this is to create predictably varied chains for testing
     */
    private static Chain create_chain(int seed){
        Random rand = new Random(seed);

        // create a chain with given parameters
        int window = 3;
        int token = 3;
        int threshold = 1000;
        int model_size = 10000;

        Chain chain = new Chain(window, token, threshold, model_size);

        // use the seed to generate a list of touches, add them to chain
        for(int i=0; i<model_size; i++){
            // use up to 3 keycodes
            int keycode = rand.nextInt() % 3;
            // use a random double between 0 and 1 for the pressure value
            double pressure = rand.nextDouble();
            long timestamp = i;

            chain.add_touch(new Touch(keycode, pressure, timestamp));
        }

        return chain;
    }
}
