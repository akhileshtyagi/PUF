package puf;

import components.Chain;
import generator.Generator;

/**
 * Created by element on 9/8/16.
 */
public class Challenge{
    private String challenge_string;
    private UserInput user_input;

    public Challenge(String string){
        this.challenge_string = string;
    }

    public Challenge(String string, UserInput user_input){
        this.challenge_string = string;
        this.user_input = user_input;
    }

    public String get_challenge_string(){
        return challenge_string;
    }

    public UserInput get_user_input(){
        return this.user_input;
    }

    public void set_user_input(UserInput user_input){
        this.user_input = user_input;
    }

    /**
     * compute the userInput in response to this challenge string given a Chain
     *
     * return true if successful
     *          false if unsuccessful
     *
     * this could fail if an element of the userInput does not appear in the chain
     */
    public boolean compute_user_input(Chain chain, Generator generator){
        this.user_input = generator.generate(chain, this.challenge_string);

        return !(this.user_input == null);
    }
}
