package puf_test;

import components.Chain;
import components.Distribution;
import utility.Utility;

import java.io.File;
import java.util.List;

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
public class TokenTest {
    public static final String DATA_FOLDER = "data_sets";
    public static final String CHAIN_FILE_NAME = "t_tim_d_tim.csv_4512";

    public static void main(String[] args){
        // create a chain from some data
        Chain chain = Utility.read_chain(new File(DATA_FOLDER, CHAIN_FILE_NAME).getPath());

        // print out the token map created by the chain
        System.out.println(chain.get_token_map());

        // print out the distributions for each key
        List<Distribution> distribution_list = chain.get_key_distribution();
        for(int i=0; i<distribution_list.size(); i++) {
            System.out.print(distribution_list.get(i).get_keycode() + "\t");
            System.out.println(distribution_list.get(i));
        }
    }
}
