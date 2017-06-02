package machine_learning;

import components.Chain;
import components.Token;
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
 * read in all the data in data_sets directory and output
 * a chain constructed from each of the data files with the
 * same name in the output directory
 *
 * data is output to OUTPUT_FOLDER_ROOT/[parameter_set]/[file_name].csv
 */
public class ChainDataGenerator {
    public static String OUTPUT_FOLDER_ROOT = "src/machine_learning/chain_data";
    public static String DATA_FOLDER_NAME = "data_sets/";

    /* the parameter set for which the ROC curve is to be generated
    *  (window, token, treshold, user_model_size, auth_model_size)
    *  large values will cause every touch in the file to be used */
    public static int MODEL_SIZE = 800;
    //public static int MODEL_SIZE = 10000;

    /* setting WINDOW_SIZE = MODEL_SIZE computes the full markov chain */
    // 1 or 2? ( 1 was here previously )
    public static int WINDOW_SIZE = 2;
    //public static int WINDOW_SIZE = MODEL_SIZE;

    public static ParameterSet PARAMETER_SET = new ParameterSet(WINDOW_SIZE, 2, 1000, MODEL_SIZE, MODEL_SIZE);

    /* only handle challenges within the challenge set */
    public static int[] CHALLENGE_SET = {};

    public static void main(String[] args) {
        // generate compare value data with the best ParameterSet
        ChainDataGenerator.generate(PARAMETER_SET, OUTPUT_FOLDER_ROOT);
    }

    /**
     * parameterize the CompareValueGenerator
     * to work on the given:
     * set of model parameters
     * output_file_name
     * <p>
     * this data is used to evaluate the influence of
     * each model parameter
     */
    // output chain as multiple files
    public static boolean MULTI_FILE_CHAIN = true;
    // if true, causes chains to be output with
    //      the same [ngram, key, p(key), weight] pairs
    //      occurring in the same order
    public static boolean MULTI_SAME_OUTPUT = false;
    public static void generate(ParameterSet parameter_set, String output_folder_root) {
        File output_folder = new File(output_folder_root, parameter_set.toFileString());

        // create the folder if it does not exist
        if (!output_folder.exists()) {
            try {
                output_folder.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //TODO perhaps I could do each output folder on a separate thread to speed this up

        // for every file in data folder
        // format is "time, keycode, pressure"
        File data_folder = new File(DATA_FOLDER_NAME);

        Chain chain = null;
        List<Chain> chain_list = null;
        List<Chain> all_chain_list = new ArrayList<>();
        List<File> output_file_list = new ArrayList<>();
        for (File data_file : data_folder.listFiles()) {
            // if multi_file_chain is enabled,
            // output many files for subsets of the data
            if (MULTI_FILE_CHAIN) {
                chain_list = construct_chain_list(data_file, parameter_set);

                int i = 0;
                for (Chain c : chain_list) {
                    c.compute_uncomputed();

                    // output the chain to output_file
                    File output_file = new File(output_folder, data_file.getName() + "_" + i++);
                    if (!MULTI_SAME_OUTPUT) {
                        output_chain(c, output_file);
                    } else {
                        output_file_list.add(output_file);
                        all_chain_list.add(c);
                    }

                    // clear the chain to free up memory
                    //TODO do if necessary
                    //c = null;
                }

                if (MULTI_SAME_OUTPUT) {
                    output_chain_list(all_chain_list, output_file_list);
                }
            } else {
                chain = construct_chain(data_file, parameter_set);

                // ensure all values have been computed
                chain.compute_uncomputed();

                // output the chain to output_file
                File output_file = new File(output_folder, data_file.getName());
                output_chain(chain, output_file);
            }

            // progress dash
            System.out.print("-");
        }

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
     * construct as many complete chains as possible with the given data, parameter set
     */
    private static List<Chain> construct_chain_list(File data_file, ParameterSet parameter_set) {
        List<Chain> chain_list = new ArrayList<>();
        List<Touch> touch_list = new ArrayList<>();
        Chain chain;

        // get a list of Touches contained within the file
        try {
            touch_list = ChainBuilder.parse_csv(data_file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //TODO ensure this works
        // i indicates the starting touch
        // do not do partial chains
        for (int i = 0; i+parameter_set.user_model_size < touch_list.size();
             i += parameter_set.user_model_size) {
            // should always be user model size,
            // loop only begins if there is a enough touches for user model size
            int model_size = Math.min(parameter_set.user_model_size, touch_list.size());

            // do not include partial chains
            if (model_size < parameter_set.user_model_size) break;

            chain = new Chain(parameter_set.window_size,
                    parameter_set.token_size, parameter_set.threshold,
                    parameter_set.user_model_size);

            for (int j = 0; j < model_size; j++) {
                chain.add_touch(touch_list.get(j));
            }

            chain_list.add(chain);
        }

        return chain_list;
    }

    /**
     * output the given chain to the given file name
     */
    //TODO Split up the ngram into multiple features, one for each [ngram, key, probability, weight]
    //TODO every model will contain the same output windows
    //TODO if a window doesn't exist in a particular model,
    //TODO then output 0's for every key, probability, weight
    public static boolean SPLIT_NGRAM = true;

    private static void output_chain(Chain chain, File output_file) {
        try {
            // create the file if it does not exist
            output_file.createNewFile();

            // keep track of all windows/tokens and map them to integers
            // the same window/token will always map to the same integer
            // different windows/tokens will map to different integers
            Map<String, Integer> wt_map = new HashMap<>();

            String header = "";
            if (SPLIT_NGRAM) {
                for (int i = 0; i < chain.get_window(); i++) {
                    header += "\"ngram_" + i + "\",";
                }
            } else {
                header += "ngram,";
            }

            header += "\"key\",\"probability_key\",\"ngram_weight\"";

            PrintWriter file = new PrintWriter(output_file, "UTF-8");

            file.println(header);

            // get the unique windows in chain
            List<Integer> unique_window_index_list = Chain.compute_unique_windows(
                    chain.get_token_map(), chain.get_tokens(), chain.get_windows());

            // for every unique ngram (window)
            for (int i = 0; i < unique_window_index_list.size(); i++) {
                Window window = chain.get_windows().get(unique_window_index_list.get(i));

                // compute a list of indexes for the unique successors to window
                List<Integer> unique_successor_index_list = Chain.compute_unique_successors(
                        chain.get_token_map(), chain.get_successors(),
                        ((TrieList) chain.get_windows()).get_index_list(window));

                // for every unique successor
                for (int j = 0; j < unique_successor_index_list.size(); j++) {
                    Touch successor = chain.get_successors().get(unique_successor_index_list.get(j));

                    // output information
                    String key = String.valueOf(successor.toRString(chain.get_token_map()));
                    String probability_key = String.valueOf(
                            successor.get_probability(chain.get_token_map(), window));
                    String ngram_weight = String.valueOf(
                            ((double) ((TrieList) chain.get_windows()).occurrence_count(window)) /
                                    ((double) chain.get_windows().size()));

                    // if there is no mapping for a given ngram or key, create it
                    if (wt_map.get(key) == null) wt_map.put(key, wt_map.size());

                    // convert the ngram and key to integers
                    // this must be done because R classifiers need integers
                    key = String.valueOf(wt_map.get(key));

                    String ngram;
                    if (SPLIT_NGRAM) {
                        // one feature for each key in the ngram
                        ngram = "";
                        String s;
                        boolean first = true;
                        for (Touch t : window.get_touch_list()) {
                            s = String.valueOf(t.toRString(chain.get_token_map()));
                            if (wt_map.get(s) == null) wt_map.put(s, wt_map.size());

                            if (!first) ngram += ", ";
                            else first = false;

                            ngram += String.valueOf(wt_map.get(s));
                        }
                    } else {
                        ngram = window.toRString(chain.get_token_map());

                        if (wt_map.get(ngram) == null) wt_map.put(ngram, wt_map.size());
                        ngram = String.valueOf(wt_map.get(ngram));
                    }

                    file.println(ngram + ", " + key + ", " + probability_key + ", " + ngram_weight);
                }
            }

            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * insert entries into the window_list, successor_list of each chain to
     * make them all the same,
     * then call output chain for each each chain
     */
    //TODO doing it in exactly this way could cause problems
    //TODO might mess up the prefix tree
    //
    //TODO an alternative could just be to establish a list of all windows to output,
    //TODO then query each chain for the values that should be out put for that chain
    //TODO otherwise the output is -1 0 0 ( or something like this )
    //
    //TODO problem..... windows are not comparable by token across chains
    private static void output_chain_list(List<Chain> all_chain_list, List<File> output_file_list) {
        List<Window> window_list;
        List<Touch> successor_list;
        List<Window> all_window_list = new ArrayList<>();
        List<Touch> all_successor_list = new ArrayList<>();

        // create a list of all windows.
        for(int i=0; i<all_chain_list.size();i++) {
            window_list = all_chain_list.get(i).get_windows();
            successor_list = all_chain_list.get(i).get_successors();

            // for all windows, successor in the chain
            for (int j = 0; j < window_list.size(); j++) {
                // if the window, successor does not occur in my lists, put it there
                //TODO is this the correct way of discriminating windows and successors
                if (!all_window_list.contains(window_list.get(j)) || !all_successor_list.contains(successor_list.get(j))) {
                    all_window_list.add(window_list.get(j));
                    all_successor_list.add(successor_list.get(j));
                }
            }
        }

        // I want that each chain contains the same [window, key, p(key), weight] in the same order
        for(int i=0; i<all_chain_list.size();i++){
            Chain c = all_chain_list.get(i);
            File f = output_file_list.get(i);

            output_window_list(c, all_window_list, all_successor_list, f);
        }
    }

    /**
     * output the given window list with the values provided in the given chain
     */
    //TODO modify this method to do what it says it does.
    private static void output_window_list(Chain chain, List<Window> window_list, List<Touch> successor_list, File output_file){
        try {
            // create the file if it does not exist
            output_file.createNewFile();

            // keep track of all windows/tokens and map them to integers
            // the same window/token will always map to the same integer
            // different windows/tokens will map to different integers
            Map<String, Integer> wt_map = new HashMap<>();

            String header = "";
            if (SPLIT_NGRAM) {
                for (int i = 0; i < chain.get_window(); i++) {
                    header += "\"ngram_" + i + "\",";
                }
            } else {
                header += "ngram,";
            }

            header += "\"key\",\"probability_key\",\"ngram_weight\"";

            PrintWriter file = new PrintWriter(output_file, "UTF-8");

            file.println(header);

            // I want to output each window in the given window list
            for(int k=0; k<window_list.size(); ++k){
                Window w = window_list.get(k);
                Touch s = successor_list.get(k);

                // find the corresponding window in chain
                Window chain_w = null;
                Touch chain_t = null;

                // get the unique windows in chain
                List<Integer> unique_window_index_list = Chain.compute_unique_windows(
                        chain.get_token_map(), chain.get_tokens(), chain.get_windows());

                // for every unique ngram (window)
                outer_loop: for (int i = 0; i < unique_window_index_list.size(); i++) {
                    Window window = chain.get_windows().get(unique_window_index_list.get(i));

                    // compute a list of indexes for the unique successors to window
                    List<Integer> unique_successor_index_list = Chain.compute_unique_successors(
                            chain.get_token_map(), chain.get_successors(),
                            ((TrieList) chain.get_windows()).get_index_list(window));

                    // for every unique successor
                    for (int j = 0; j < unique_successor_index_list.size(); j++) {
                        Touch successor = chain.get_successors().get(unique_successor_index_list.get(j));

                        // determine if window and successor are equal
                        if(w.compare_with_token(chain.get_token_map(), window) &&
                                successor.compare_with_token(chain.get_token_map().get(successor.get_key()), s)){
                            // they are equal
                            chain_w = window;
                            chain_t = successor;
                            break outer_loop;
                        }
                    }
                }

                // if the window equals one of the windows from chain
                if(chain_w != null && chain_t != null) {
                    // output the values for the window contained within the chain
                    file.println(output_ngram(chain, chain_w, chain_t, wt_map, true));
                }
                // the window does not equal one of the windows from chain
                else {
                    //TODO this causes problems because the chain does not have a List<token> for this s.key
                    //TODO perhaps a better strategy would be to simply output something here
                    // output some preset values
                    // the function should automatically take care of this because
                    // I have already determined that the valeus don't exist in chain
                    file.println(output_ngram(chain, w, s, wt_map, false));
                    //TODO make sure the function actually takes care of this
                }
            }

            /*
            // get the unique windows in chain
            List<Integer> unique_window_index_list = Chain.compute_unique_windows(
                    chain.get_token_map(), chain.get_tokens(), chain.get_windows());

            // for every unique ngram (window)
            for (int i = 0; i < unique_window_index_list.size(); i++) {
                Window window = chain.get_windows().get(unique_window_index_list.get(i));

                // compute a list of indexes for the unique successors to window
                List<Integer> unique_successor_index_list = Chain.compute_unique_successors(
                        chain.get_token_map(), chain.get_successors(),
                        ((TrieList) chain.get_windows()).get_index_list(window));

                // for ever            // get the unique windows in chain
            List<Integer> unique_window_index_list = Chain.compute_unique_windows(
                    chain.get_token_map(), chain.get_tokens(), chain.get_windows());

            // for every unique ngram (window)
            for (int i = 0; i < unique_window_index_list.size(); i++) {
                Window window = chain.get_windows().get(unique_window_index_list.get(i));

                // compute a list of indexes for the unique successors to window
                List<Integer> unique_successor_index_list = Chain.compute_unique_successors(
                        chain.get_token_map(), chain.get_successors(),
                        ((TrieList) chain.get_windows()).get_index_list(window));

                // for every unique successor
                for (int j = 0; j < unique_successor_index_list.size(); j++) {
                    Touch successor = chain.get_successors().get(unique_successor_index_list.get(j));
                    file.println(output_ngram(chain, window, successor, wt_map));
                }
            }y unique successor
                for (int j = 0; j < unique_successor_index_list.size(); j++) {
                    Touch successor = chain.get_successors().get(unique_successor_index_list.get(j));
                    file.println(output_ngram(chain, window, successor, wt_map));
                }
            }
            */

            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO does wt_map actually get updated?
    static String output_ngram(Chain chain, Window window, Touch successor, Map<String, Integer> wt_map, boolean exist) throws Exception{
        String key, probability_key, ngram_weight;

        if(exist) {
            // output information
            key = String.valueOf(successor.toRString(chain.get_token_map()));
            probability_key = String.valueOf(
                    successor.get_probability(chain.get_token_map(), window));
            ngram_weight = String.valueOf(
                    ((double) ((TrieList) chain.get_windows()).occurrence_count(window)) /
                            ((double) chain.get_windows().size()));
        }else{
            key = "" + successor.get_key() + "-1";
            probability_key = "0";
            ngram_weight = "0";
        }

            // if there is no mapping for a given ngram or key, create it
            if (wt_map.get(key) == null) wt_map.put(key, wt_map.size());

            // convert the ngram and key to integers
            // this must be done because R classifiers need integers
            key = String.valueOf(wt_map.get(key));

            String ngram;
            if (SPLIT_NGRAM) {
                // one feature for each key in the ngram
                ngram = "";
                String s;
                boolean first = true;
                for (Touch t : window.get_touch_list()) {
                    s = String.valueOf(t.toRString(chain.get_token_map()));
                    if (wt_map.get(s) == null) wt_map.put(s, wt_map.size());

                    if (!first) ngram += ", ";
                    else first = false;

                    ngram += String.valueOf(wt_map.get(s));
                }
            } else {
                ngram = window.toRString(chain.get_token_map());

                if (wt_map.get(ngram) == null) wt_map.put(ngram, wt_map.size());
                ngram = String.valueOf(wt_map.get(ngram));
            }

            return ngram + ", " + key + ", " + probability_key + ", " + ngram_weight;
    }
}