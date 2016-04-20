package test;

import components.Chain;
import components.Touch;
import runtime.ChainBuilder;

import java.util.Random;

/**
 * Created by element on 4/20/16.
 *
 * This is my attempt to construct a minimal working example
 * of this code.
 *
 * There are essentially two ways this code may be used.
 * 1) though the use of ChainBuilder class
 * 2) through the use of Chain class
 *
 * The relationship between the above is:
 * ChainBuilder uses the Chain class.
 * ChainBuilder acts like a wrapper around the Chain class
 * providing additional functionality.
 */
public class minimal_working_example {
    private enum Example{
        CHAIN_BUILDER,
        CHAIN
    }

    public static void main(String args[]){
        //Example x = Example.CHAIN_BUILDER;
        Example x = Example.CHAIN;

        if(x == Example.CHAIN_BUILDER){
            chain_builder_example();
        }else if(x == Example.CHAIN) {
            chain_example();
        }
    }

    /* and example use case for ChainBuilder */
    private static void chain_builder_example(){
        // number of previous elements used
        // as a symbol to predict the next element
        int window = 3;

        // number of ranges each screen area is split.
        int token = 7;

        // time threshold,
        // if consecutive touches fall outside of this number of milliseconds,
        // then these touches will not be considered to be within the same window
        int threshold = 500;

        // number of touches which will be used
        int user_model_size = 6000;
        int auth_model_size = 2000;

        // create an instance of chain builder
        ChainBuilder chain_builder = new ChainBuilder(window, token, threshold, user_model_size, auth_model_size);

        // add touches to chain_builder
        // touches have raw data
        for(int i=0; i<user_model_size + auth_model_size; i++){
            chain_builder.handle_touch(generate_dummy_touch());
        }

        /* during authentication,
        the newest [auth_model_size] touches are added to auth model
        the older [user_model_size] touches previous to auth_model_size
        are added to the user model.

        The user model is compared against the auth model in authentication.
         */
        chain_builder.authenticate();

        // the authenticate method will return immediatly
        // authentication will happen on a different thread.
        // with for the authentication to finish
        while(chain_builder.get_authenticate_state() == ChainBuilder.State.IN_PROGRESS){
            // wait until authentication might be finished
            try{ Thread.sleep(100); }catch(Exception e){ e.printStackTrace(); }
        }

        // The success or failure of the authentication can
        // be retrieved in the same way we checked the progress of the authentication
        if(chain_builder.get_authenticate_state() == ChainBuilder.State.SUCCESS){
            // authentication successful
            System.out.println("authentication successful");
        }else{
            // authentication failed
            System.out.println("authentication failed");
        }
    }

    /* an example compairason of two models using
    The chain class.

    The general Idea is to build two chains and
    compare them with one another.
     */
    private static void chain_example(){
        int base_chain_size = 6000;
        int auth_chain_size = 2000;

        // window 3, tokens 7, threshold 500, size 6000
        Chain base_chain = new Chain(3, 10, 500, base_chain_size);
        Chain auth_chain = new Chain(3, 10, 500, auth_chain_size);

        // add touches to chain_builder
        // touches have raw data
        for(int i=0; i<base_chain_size; i++){
            base_chain.add_touch(generate_dummy_touch());
        }

        for(int i=0; i<auth_chain_size; i++){
            auth_chain.add_touch(generate_dummy_touch());
        }

        // this function returns an double representing the difference between the chains
        // the range of this value is 0 to 1
        double auth_result = base_chain.compare_to(auth_chain);
        double auth_threshold = .6;

        System.out.println("auth_result: " + auth_result);

        // it may be compared against a threshold like this
        System.out.println("auth_passed?: " + (auth_result > auth_threshold));
    }

    /* randomly generate some dummy touches. */
    private static Touch generate_dummy_touch(){
        // randomly generate some raw data
        Random random = new Random(System.currentTimeMillis());

        // keycode which indicates the screen area
        // from which the touch came
        // this value can be the keycode, bu
        int keycode = random.nextInt();

        // pressure value from the touch
        double pressure = random.nextDouble();

        // timestamp of the touch
        long timestamp = System.currentTimeMillis();

        return new Touch(keycode, pressure, timestamp);
    }
}
