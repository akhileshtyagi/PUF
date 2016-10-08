package puf_test;

import components.*;
import trie.TrieList;
import utility.Utility;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * the purpose of this class is to test
 * the Token Map generated in Chain
 *
 * The goal is to show that tokens are correctly created
 * for each keycode
 *
 * The way I will go about this is by
 * 1. creating a chain from some data
 * 2. see if each touch in the chain
 * 2.1 falls into its correct token range
 * 2.2 that about .05% of the touches fall into no token ranges
 */
public class CompareTest {
    public static final String DATA_FOLDER = "all_data_sets";
    public static final String CHAIN_FILE_NAME_0 = "t_tim_d_tim.csv_4512";
    public static final String CHAIN_FILE_NAME_1 = "t_ian_d_tim.csv_4512";

    public static final int WINDOW_SIZE = 1;
    public static final int TOKEN_NUMBER = 3;
    public static final int TIME_THRESHOLD = 5000;
    public static final int CHAIN_SIZE = 4512;

    public static final int BASE_CHAIN_SIZE = 2256;
    public static final int AUTH_CHAIN_SIZE = 2256;

    public static void main(String[] args){
        // chain_0 = base model
        // chain_1 = auth model

        // create a chain from some data
        Chain chain_0_data = Utility.read_chain(new File(DATA_FOLDER, CHAIN_FILE_NAME_0).getPath(), WINDOW_SIZE, TOKEN_NUMBER, TIME_THRESHOLD, CHAIN_SIZE);
        Chain chain_1_data = Utility.read_chain(new File(DATA_FOLDER, CHAIN_FILE_NAME_1).getPath(), WINDOW_SIZE, TOKEN_NUMBER, TIME_THRESHOLD, CHAIN_SIZE);

        // determine which touches from each chain will be used to compare
        // need to account for comparing different data in the same file
        // this is why the
        // base chain starts at the beginning of the file
        // auth chain starts at the end of the file
        //
        // use the first BASE_CHAIN_SIZE touches for the base chain
        Chain chain_0 = new Chain(WINDOW_SIZE, TOKEN_NUMBER, TIME_THRESHOLD, BASE_CHAIN_SIZE);
        for(int i=0; i<BASE_CHAIN_SIZE; i++){
            chain_0.add_touch(chain_0_data.get_touches().get(i));
        }

        // use the last AUTH_CHAIN_SIZE touches for the auth chain
        Chain chain_1 = new Chain(WINDOW_SIZE, TOKEN_NUMBER, TIME_THRESHOLD, AUTH_CHAIN_SIZE);
        for(int i=0; i<AUTH_CHAIN_SIZE; i++){
            List<Touch> touch_list = chain_1_data.get_touches();

            chain_1.add_touch(touch_list.get(touch_list.size() - i - 1));
        }

        // set the distribution of chain_1 to the distribution of chain_0
        chain_1.set_distribution(chain_0.get_distribution(), chain_0.get_key_distribution());

        // call functions to make sure everything in chains is computed
        chain_0.compute_uncomputed();
        chain_1.compute_uncomputed();

        // print out the auth windows which have a corresponding base window
        // and the probability with which they compare to base window
        Map<Integer, List<Token>> base_token_map = chain_0.get_token_map();
        List<Window> base_window_list = chain_0.get_windows();
        List<Window> auth_window_list = chain_1.get_windows();

        List< Touch > successor_list_base = chain_0.get_successors();
        List<Touch> successor_list_auth = chain_1.get_successors();

        double compare_probability = 0.0;
        boolean match_found;

        System.out.println("auth_window | compare_probability");
        for(Window auth_window : auth_window_list) {
            match_found = false;

            for(Window base_window : base_window_list) {
                // test if the auth window matches any base windows
                if (auth_window.compare_with_token(base_token_map, base_window)) {
                    // determine the compare_probability
                    compare_probability = chain_0.get_window_difference(base_window, (TrieList)base_window_list, (TrieList)auth_window_list, successor_list_base, successor_list_auth);

                    System.out.println(String.format("%s | %f", auth_window.toString(), compare_probability));

                    match_found = true;
                    break;
                }
            }

            // print out the auth windows which do not have a corresponding base window
            // if we made it all the way through without a match,
            // then there is not corresponding base window
            if(!match_found){
                System.out.println(String.format("%s | %s", auth_window.toString(), "no match!"));
            }
        }

        System.out.println(String.format("%s | %f", "overall difference", chain_0.compare_to(chain_1)));
    }
}
