package utility;

import android.view.KeyEvent;

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
        //TODO make sure this works
        return KeyEvent.keyCodeFromString("" + character);
    }

    /**
     * converts an Android primaryCode into the equivilent character
     *
     * KeyEvent.keycodetostring
     */
    public static char android_code_to_char(int android_code){
        //TODO make sure this works
        return KeyEvent.keyCodeToString(android_code).charAt(0);
    }
}
