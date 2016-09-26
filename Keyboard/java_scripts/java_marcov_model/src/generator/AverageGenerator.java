package generator;

import components.Chain;
import components.Touch;
import components.Window;
import puf.UserInput;
import utility.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by element on 9/10/16.
 *
 * Take the 'average' [something] (next state probabilities)
 * of all touches of a particular character
 * found in the chain
 */
public class AverageGenerator implements Generator{
    public UserInput generate(Chain chain, String string){
        // these are the things which must be created for a UserInput
        List<List<Touch>> touch_input_list;
//        List<List<Map<Window, Double>>> next_state_probability_list;

        touch_input_list = new ArrayList<>();
//        next_state_probability_list = new ArrayList<>();

        // for each caracter generate:
        // 1. list of touches corresponding to that character
        // 2. a map for that touch
        //      from a successor keycode
        //      to next state probabilitity for that keycode
        for(char character : string.toCharArray()) {
            // get the android code for this character
            int android_code = Utility.char_to_android_code(character);

            // get a list of touches corresponding to this character
            ArrayList<Touch> character_touch_list = new ArrayList<>();

            //System.out.println(String.format("character, android code | %c, %d", character, android_code));

            // for all touches in chain
            for(Touch touch : chain.get_touches()) {
                // if a touch key matches our current character key

                //System.out.println(String.format("touch_key, android code | %d, %d", touch.get_key(), android_code));

                if(touch.get_key() == android_code){
                    character_touch_list.add(touch);
                }
            }

            // want to know for which characters empty lists are occurring
//            if(character_touch_list.size() == 0){
//                System.out.println(String.format("character, android code | %c, %d", character, android_code));
//            }

            touch_input_list.add(character_touch_list);
        }

        //System.out.println(touch_input_list);

        return new UserInput(touch_input_list);//, next_state_probability_list);
    }
}
