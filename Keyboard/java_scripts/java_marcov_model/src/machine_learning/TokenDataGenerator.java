package machine_learning;

import components.Chain;
import components.Touch;
import components.Window;
import data_analysis.ParameterSet;
import runtime.ChainBuilder;
import trie.TrieList;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class TokenDataGenerator {
    public static String OUTPUT_FOLDER_ROOT = "src/machine_learning/token_data/";
    public static String DATA_FOLDER_NAME = "data_sets/";

    /* the parameter set for which the ROC curve is to be generated
    *  (window, token, treshold, user_model_size, auth_model_size)
    *  large values will cause every touch in the file to be used */
    //public static int MODEL_SIZE = 4512; // this is the minimum number of lines in the data sets
    public static int MODEL_SIZE = 29620; // this is the maximum number of lines in the data sets
    //public static int MODEL_SIZE = 10000;

    /* setting WINDOW_SIZE = MODEL_SIZE computes the full markov chain */
    // 1 or 2? ( 1 was here previously )
    public static int WINDOW_SIZE = 2;
    //public static int WINDOW_SIZE = MODEL_SIZE;

    //TODO choose parameter set
    //public static ParameterSet PARAMETER_SET = new ParameterSet(WINDOW_SIZE, 2, 1000, MODEL_SIZE, MODEL_SIZE);
    public static ParameterSet PARAMETER_SET = new ParameterSet(1, 2, 1000, MODEL_SIZE, MODEL_SIZE);
    //public static ParameterSet PARAMETER_SET = new ParameterSet(1, 2, 1000, 1600, 3200);

    /* only handle challenges within the challenge set */
    public static int[] CHALLENGE_SET = {};

    // map keycodes to index in the successor vector
    public static HashMap<Integer,Integer> keycode_index_map = new HashMap<>();

    public static void main(String[] args) {
        // generate compare value data with the best ParameterSet
        TokenDataGenerator.generate(PARAMETER_SET, OUTPUT_FOLDER_ROOT);
    }

    /**
     * create one output file for each input file
     * the output file contains the pressures converted to tokens
     * [time,keycode,pressure] becomes [time,keycode,token]
     */
    public static void generate(ParameterSet parameter_set, String output_folder_root) {
        Chain chain = null;

        // for every file in data folder
        // format is "time, keycode, pressure"
        File data_folder = new File(DATA_FOLDER_NAME);

        for (File data_file : data_folder.listFiles()) {
            chain = construct_chain(data_file, parameter_set);
            chain.compute_uncomputed();
            File output_file = new File(output_folder_root, data_file.getName());
            output_chain(chain, output_file);
        }

        System.out.println();
        System.out.println("data from: " + DATA_FOLDER_NAME);
        System.out.println("output to: " + output_folder_root);
    }

    /**
     * construct a chain from a data file
     * <p>
     * return null if file not fould
     */
    private static Chain construct_chain(File data_file, ParameterSet parameter_set) {
        Chain chain = null;

        try {
            // get a list of Touches contained within the file
            List<Touch> touch_list = ChainBuilder.parse_csv(data_file);

            chain = new Chain(parameter_set.window_size,
                    parameter_set.token_size, parameter_set.threshold,
                    parameter_set.user_model_size);

            int model_size = Math.min(parameter_set.user_model_size, touch_list.size());
            for (int j = 0; j < model_size; j++) {
                chain.add_touch(touch_list.get(j));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chain;
    }

    /**
     * output the given chain to the given file name
     * [time,keycode,token]
     */
    private static void output_chain(Chain chain, File output_file) {
        try {
            // create the file if it does not exist
            output_file.createNewFile();

            // no header in this file
            PrintWriter file = new PrintWriter(output_file, "UTF-8");

            for(Touch touch : chain.get_touches()){
                int token=touch.get_token_index(chain.get_token_map());
                //if(token_index!=-1)

                file.println(touch.get_timestamp() + "," + touch.get_key() + "," + token);
            }

            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}