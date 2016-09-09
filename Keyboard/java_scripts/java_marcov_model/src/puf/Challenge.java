package puf;

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
}
