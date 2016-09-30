package puf;

import components.Distribution;
import components.Touch;
import components.Window;

import java.util.List;
import java.util.Map;

/**
 * Created by element on 9/9/16.
 *
 * represents user input in response to
 * the challenge
 */
public class UserInput {
    /**
     * a list of lists containing
     * lists of Touches corresponding to
     * touches at that index in the challenge sequence
     */
    protected List<List<Touch>> touch_input_list;
    protected List<Distribution> distribution_list;

    /**
     * a list of lists containing
     * lists of Maps of characters to next state probabilities corresponding to
     * each of the touches in touch_input_list
     */
//    protected List<List<Map<Window, Double>>> next_state_probability_list;

    public UserInput(List<List<Touch>> touch_input_list, List<Distribution> distribution_list){ //, List<List<Map<Window, Double>>> next_state_probability_list){
        this.touch_input_list = touch_input_list;
        this.distribution_list = distribution_list;
//        this.next_state_probability_list = next_state_probability_list;
    }

    public List<List<Touch>> get_input_list(){
        return touch_input_list;
    }

    public List<Distribution> get_distribution_list() { return this.distribution_list; }

//    public List<List<Map<Window, Double>>> get_next_state_probability_list(){
//        return next_state_probability_list;
//    }
}
