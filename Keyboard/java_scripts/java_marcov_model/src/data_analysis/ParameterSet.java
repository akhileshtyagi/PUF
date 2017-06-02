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

    public ParameterSet(int window_size, int token_size, int threshold, int user_model_size, int auth_model_size) {
        this.window_size = window_size;
        this.token_size = token_size;
        this.threshold = threshold;
        this.user_model_size = user_model_size;
        this.auth_model_size = auth_model_size;
    }

    @Override
    public String toString() {
        StringBuilder string_builder = new StringBuilder();

        string_builder.append("{ ");

        string_builder.append("window_size | ");
        string_builder.append(this.window_size);

        string_builder.append("\t");

        string_builder.append("token_size | ");
        string_builder.append(this.token_size);

        string_builder.append("\t");

        string_builder.append("threshold | ");
        string_builder.append(this.threshold);

        string_builder.append("\t");

        string_builder.append("user_model_size | ");
        string_builder.append(this.user_model_size);

        string_builder.append("\t");

        string_builder.append("auth_model_size | ");
        string_builder.append(this.auth_model_size);

        string_builder.append("\t");

        string_builder.append("}");

        return string_builder.toString();
    }

    /**
     * return a string which will become part of a file name
     */
    public String toFileString(){
        StringBuilder string_builder = new StringBuilder();

        string_builder.append(this.window_size);
        string_builder.append("_");
        string_builder.append(this.token_size);
        string_builder.append("_");
        string_builder.append(this.threshold);
        string_builder.append("_");
        string_builder.append(this.user_model_size);
        string_builder.append("_");
        string_builder.append(this.auth_model_size);

        return string_builder.toString();
    }
}