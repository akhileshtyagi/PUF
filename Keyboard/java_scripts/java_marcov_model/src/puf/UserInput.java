package puf;

import components.Touch;

import java.util.List;

/**
 * Created by element on 9/9/16.
 *
 * represents user input in response to
 * the challenge
 */
public class UserInput {
    protected List<Touch> touch_input_list;

    public UserInput(List<Touch> touch_input_list){
        this.touch_input_list = touch_input_list;
    }

    public List<Touch> get_input_list(){
        return touch_input_list;
    }
}
