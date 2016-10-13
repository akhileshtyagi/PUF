package data_analysis;

/**
 * This class represents a set of parameters to be
 * given to model_compare
 */
public class ParameterSet {
    public int window_size;
    public int token_size;
    public int threshold;
    public int user_model_size;
    public int auth_model_size;

    public ParameterSet(int window_size, int token_size, int threshold, int user_model_size, int auth_model_size){
        this.window_size = window_size;
        this.token_size = token_size;
        this.threshold = threshold;
        this.user_model_size = user_model_size;
        this.auth_model_size = auth_model_size;
    }
}
