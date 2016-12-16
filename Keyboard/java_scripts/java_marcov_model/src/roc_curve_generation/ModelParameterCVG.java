package roc_curve_generation;

import data_analysis.ParameterSet;

import java.util.ArrayList;
import java.util.List;

/**
 * generate compare_data files for all the model parameters
 * uses CompareValueGenerator to do most of the work.
 */
public class ModelParameterCVG {
    public static String OUTPUT_FOLDER_NAME = "src/roc_curve_generation/parameter_compare/";

    //TODO adjust the sizes of things to generate more data
    //TODO on how model parameters affect the outcome
    private final static int[] window_sizes = {1};
    private final static int[] token_sizes = {2};
    private final static int[] thresholds = {5000};
    //TODO need to change CompareValueGenerator to actually use model_sizes
    private static int[] user_model_sizes = {800};
    private static int[] auth_model_sizes = {800};

    public static void main(String[] args){
        List<ParameterSet> parameter_set_list = generate_parameter_set(
                window_sizes, token_sizes, thresholds, user_model_sizes, auth_model_sizes);

        // run CompareValueGenerator on each parameter set
        for(ParameterSet parameter_set : parameter_set_list) {
            // r scripts will determine which parameters
            // used based on the file name:
            // "window,token,threshold,user_model_size,auth_model_size"
            String file_name = String.format("%d,%d,%d,%d,%d",
                    parameter_set.window_size,
                    parameter_set.token_size,
                    parameter_set.threshold,
                    parameter_set.user_model_size,
                    parameter_set.auth_model_size);

            CompareValueGenerator.generate(parameter_set,
                    String.format("%s%s%s", OUTPUT_FOLDER_NAME, file_name, ".csv"));
        }
    }

    /**
     * generate a parameter_set_list given
     * a set of ranges for each model parameter
     */
    private static List<ParameterSet> generate_parameter_set(int[] window_list,
                                                             int[] token_list,
                                                             int[] threshold_list,
                                                             int[] user_model_size_list,
                                                             int[] auth_model_size_list){
        ArrayList<ParameterSet> parameter_set_list = new ArrayList<>();

        for(int window : window_list){
            for(int token : token_list){
                for(int threshold : threshold_list){
                    for(int user_model_size : user_model_size_list){
                        for(int auth_model_size : auth_model_size_list){
                            parameter_set_list.add(new ParameterSet(
                                    window, token, threshold, user_model_size, auth_model_size));
                        }
                    }
                }
            }
        }

        return parameter_set_list;
    }
}
