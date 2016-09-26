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
        int code;

        // keycodes for a-z are the same as ascii
        // lower case and upper case versions of letters map to the same code
        //
        // handle conditions where keys do not alight with ascii
        // these values come from  the values used in the ANDROID KEYBOARD's KEYMAP
        /*
         * [.]
         * [enter]
         * [del]
         * [space]
         *
         * [shift]
         * [symbol]
         */
        // translate the special cases
        if(character == 'a'+26) {
            // enter
            code = 10;
        }else if(character == 'a'+27) {
            // . ,
            code = 46;
        }else if(character == 'a'+28) {
            // del
            code = -5;
        }else if(character == 'a'+29) {
            // space
            code = 32;
        }else if(character == 'a'+30) {
            // shift
            code = -1;
        }else if(character == 'a'+31){
            // symbol
            code = -2;
        }else{
            // convert to lower case and return
            code = (int) Character.toLowerCase(character);
        }

        return code;
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
//    private static void create_keyboard(){
        // if the keyboard has not yet been created, create the keyboard
        //if(keyboard == null){

        //}

        // otherwise simply return
//        return;
//    }

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
