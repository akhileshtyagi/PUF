package utility;

//import android.inputmethodservice.Keyboard;
//import android.view.KeyEvent;

import components.Chain;
import components.Touch;
import runtime.ChainBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by element on 9/9/16.
 */
public class Utility {
    /**
     * take the average of the given array
     */
    public static double average(Double[] array){
        Double sum = 0.0;
        for(Double d : array){
            sum += d;
        }

        return sum / array.length;
    }

    /**
     * take the average of a list
     */
    public static double average(List<Double> list){
        Double[] array = new Double[list.size()];

        for(int i=0; i<list.size(); i++) {
            array[i] = list.get(i);
        }

        return average(array);
    }

    /**
     * converts a char into the equivilent
     * Android primaryCode as seen in a keylistener
     *
     * In other words this allows characters to be matched
     * against the Keyboard .csv data
     *
     * KeyEvent.keycodefrom string
     */
    public static int char_to_android_code(char character) {
        //return KeyEvent.keyCodeFromString("" + character);
        // create the keyboard if it has not already been created
//        create_keyboard();

        // get the keys from the keyboard
//        List<Keyboard.Key> key_list = keyboard.getKeys();

        // figure out which key matches the character
//        for(Keyboard.Key key : key_list){
//            if(key.character value){
//                return key.codes[0];
//            }
//        }

        // return the keyCode value of this key

        //TODO it seems like the keycodes are indeed ASCII characters
        //TODO atleast for (A-z)
        return (int)character;
    }

    /**
     * converts an Android primaryCode into the equivilent character
     *
     * KeyEvent.keycodetostring
     */
//    public static char android_code_to_char(int android_code){
//        //TODO make sure this works
//        //return KeyEvent.keyCodeToString(android_code).charAt(0);
//    }

    /**
     * recreate the android keyboard used to collect data
     * //TODO I need to figure out what the key values in the keyboard file correspond to
     */
    //public static Keyboard keyboard = null;
    private static void create_keyboard(){
        // if the keyboard has not yet been created, create the keyboard
        //if(keyboard == null){

        //}

        // otherwise simply return
        return;
    }

    public static void print(String tag, Object value){
        System.out.println(tag + "\t:\t" + value );
    }

    public static Chain read_chain(String file_name){
        //////
        // Chain parameters
        //////
        //TODO adjust chain parameters
        int window_size = 3;
        int token_number = 5;
        int time_threshold = 1000;
        int chain_size = 4000;
        Chain chain = new Chain(window_size, token_number, time_threshold, chain_size);

        List<Touch> touch_list;

        // if now file name is provided
        if(file_name == null){
            touch_list = generate_test_chain(chain_size);
        }else{
            // use file_name to read a chain from the disk
            touch_list = ChainBuilder.parse_csv(new File(file_name));
        }

        // add all the touches to the chain
        for (int i = 0; i < chain_size; i++) {
            chain.add_touch(touch_list.get(i));
        }

        // return the chain
        return chain;
    }

    private static List<Touch> generate_test_chain(int chain_size){
        List<Touch> touch_list = new ArrayList<>();

        // add touches to the chain
        for (int i = 0; i < chain_size; i++) {
            touch_list.add(new Touch(Utility.char_to_android_code((char)('a' + (i % 26))), (i % 11) * .1, 100));
        }

        return touch_list;
    }
}
