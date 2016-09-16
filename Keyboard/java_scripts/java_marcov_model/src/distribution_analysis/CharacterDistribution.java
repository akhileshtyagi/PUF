package distribution_analysis;

import components.Distribution;
import components.Touch;
import utility.Utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by element on 9/16/16.
 */
public class CharacterDistribution {
    public static final String CHAIN_FILENAME = "data_sets/t_tim_d_tim.csv";

    /**
     * print the distribution for each character of a Chain
     */
    public static void main(String[] args){
        // read in the chain from the file
        List<Distribution> distribution_list = Utility.read_chain(CHAIN_FILENAME).get_key_distribution();

        for(Distribution distribution : distribution_list) {
            int key_code = distribution.get_keycode();
            double mu = distribution.get_average();
            double std_dev = distribution.get_standard_deviation();

            //System.out.println("keycode: " + key_code + "\t" + "mu: " + mu + "\t" + "std_dev: " + std_dev);
            System.out.println(key_code + "\t" + mu + "\t" + std_dev);
        }
    }
}
