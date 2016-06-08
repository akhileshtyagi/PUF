package edu.isu.reu.keyboard_one_shot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

import components.Chain;
import components.Touch;

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
        // create a chain
        int window = 0;
        int token = 0;
        int threshold = 0;
        int size = 0;
        Chain chain = new Chain(window, token, threshold, size);

        // add touches to chain
        //TODO timestamp is not accuracte
        for(List<Float> touch_point : touch_point_list){
            //TODO
            int keycode = 0;
            double pressure = touch_point.get(1);
            long timestamp = 0;
            chain.add_touch(new Touch(keycode, pressure, timestamp));
        }

        // quantize the chain
        return quantize(chain).toString();
    }

    /**
     * this mehtod will try to quantize the chain
     *
     * this will eventually be moved into the chain class,
     * but for testing purposes it is implemented here
     *
     * Quantization is preformed in the following way
     */
    private BitSet quantize(Chain chain){
        BitSet bit_set = new BitSet();

        //TODO

        return bit_set;
    }
}
