package edu.isu.reu.keyboard_one_shot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

//import components.Chain;
//import components.Touch;

/**
 * the goal of this class is simply to
 * 1. preform an authentication given a list of x,y,p values
 * 1.1 list of x,y,p will be retrieved from the intent extras
 * 2. display the results of this authentication
 */
public class KeyboardAuthenticationResult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication_results);

        // get the List<List<Float>> from the intent
        List<List<Float>> touch_point_list = get_intent_touch_point_list();

        // make the edit text not editable
        EditText result_edit_text = (EditText)findViewById(R.id.result_edit_text);
        result_edit_text.setKeyListener(null);

        // preform the authentication based on this list
        String authentication_result = compute_authentication_result(touch_point_list);

        // display the results of the authentication, set the edit_text value
        result_edit_text.setText(authentication_result, TextView.BufferType.EDITABLE);
    }

    /**
     * get the touch_point_list from the intent
     * decode this list and return
     */
    private List<List<Float>> get_intent_touch_point_list(){
        Intent intent = getIntent();

        // grab the touch point string from the intent
        String touch_point_string = intent.getStringExtra(StringAuthenticationActivity.TOUCH_POINT_STRING);

        // decode the touch point string and return
        return StringAuthenticationActivity.decode_touch_point_list(touch_point_string);
    }

    /**
     * compute the result of authentication
     */
    private String compute_authentication_result(List<List<Float>> touch_point_list){
        String authentication_result_string = "";

        //TODO for testing purposes
        //authentication_result_string += StringAuthenticationActivity.encode_touch_point_list(touch_point_list);
        //authentication_result_string += '\n';
        //authentication_result_string += touch_point_list.toString();

        // preform the authentication using keyboard_markov_model library

        //TODO

        // build the result string after the authentication has finished
        //TODO

        return authentication_result_string;
    }

    //TODO make these methods work
//    private static void chain_example(){
//        int base_chain_size = 6000;
//        int auth_chain_size = 2000;
//
//        // window 3, tokens 7, threshold 500, size 6000
//        Chain base_chain = new Chain(3, 10, 500, base_chain_size);
//        Chain auth_chain = new Chain(3, 10, 500, auth_chain_size);
//
//        // add touches to chain_builder
//        // touches have raw data
//        for(int i=0; i<base_chain_size; i++){
//            base_chain.add_touch(generate_dummy_touch());
//        }
//
//        for(int i=0; i<auth_chain_size; i++){
//            auth_chain.add_touch(generate_dummy_touch());
//        }
//
//        // this function returns an double representing the difference between the chains
//        // the range of this value is 0 to 1
//        double auth_result = base_chain.compare_to(auth_chain);
//        double auth_threshold = .6;
//
//        System.out.println("auth_result: " + auth_result);
//
//        // it may be compared against a threshold like this
//        System.out.println("auth_passed?: " + (auth_result > auth_threshold));
//    }
//
//    private static Touch generate_dummy_touch(){
//        // randomly generate some raw data
//        Random random = new Random(System.currentTimeMillis());
//
//        // keycode which indicates the screen area
//        // from which the touch came
//        // this value can be the keycode, bu
//        int keycode = random.nextInt();
//
//        // pressure value from the touch
//        double pressure = random.nextDouble();
//
//        // timestamp of the touch
//        long timestamp = System.currentTimeMillis();
//
//        return new Touch(keycode, pressure, timestamp);
//    }
}
