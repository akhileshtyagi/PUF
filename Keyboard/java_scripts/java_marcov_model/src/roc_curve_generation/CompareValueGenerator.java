package roc_curve_generation;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Boolean.TRUE;

/**
 * Generate a file with all the compare values
 * format:
 * "compare data", "profile data", "compare value"
 * "data_user", "data_device", "profile_user", "profile_device", "compare_value"
 *
 *
 */
public class CompareValueGenerator {
    public static String OUTPUT_FILE_NAME = "src/roc_curve_generation/compare_data.csv";
    public static String DATA_FOLDER_NAME = "src/roc_curve_generation/data/";

    /* only handle challenges within the challenge set */
    public static int[] CHALLENGE_SET = {};

    public static void main(String[] args){
        ArrayList<Boolean> positive_list = new ArrayList<>();
        ArrayList<Double> compare_value_list = new ArrayList<>();

        // generate dummy results
        //dummy_results_for_testing_r_script(positive_list, compare_value_list);

        // generate real results
        compare_data(positive_list, compare_value_list);

        // output results to file
        output_results(positive_list, compare_value_list);

        System.out.println("data from: " + DATA_FOLDER_NAME);
        System.out.println("output to: " + OUTPUT_FILE_NAME);
    }

    /**
     * use the data in data folder to create compare results
     * compare each data set
     *  - build a list of profiles with all but one Response trace
     *  - keep a list of this "left out" Response trace
     *  - compare every response trace against every profile
     *  - response traces compared against data generated
     *      by the same user on the same device should authenticate
     *      => positive = true
     *  - response traces compared against data generated
     *      by a different user or different device should not authenticate
     *      => positive = false
     */
    public static void compare_data(
            ArrayList<Boolean> positive_list, ArrayList<Double> compare_value_list){
        try{
            File data_folder = new File(DATA_FOLDER_NAME);

            // for each device folder
            for(File device_folder : data_folder.listFiles()){
                String device_name = device_folder.getName();

                // for each user of the device
                for(File user_folder : device_folder.listFiles()){
                    String user_name = user_folder.getName();

                    // Each file is a response to a challenge
                    // given a user_folder, create a set of
                    // challenges corresponding to that user, device
                    //
                    // ONLY FOR THINGS IN CHALLENGE_SET
                    //TODO
                }
            }

            System.out.println(data_folder.listFiles()[0].getName());

        }catch(Exception e){ e.printStackTrace(); }
    }

    /**
     * output the results to file
     */
    public static void output_results(
            ArrayList<Boolean> positive_list, ArrayList<Double> compare_value_list){
        // positive is 1 if the compare value came from a user which should authenticate
        String header = "\"positive\", \"compare_value\"";

        try{
            PrintWriter file = new PrintWriter(OUTPUT_FILE_NAME, "UTF-8");

            file.println(header);

            for(int i=0; i<positive_list.size(); i++){
                String positive_string = positive_list.get(i) == TRUE ? "1" : "0";
                String compare_value_string = String.valueOf(compare_value_list.get(i));

                file.println(positive_string + ", " + compare_value_string);
            }

            file.close();
        }catch(Exception e){ e.printStackTrace(); }
    }

    /**
     * generate some dummy results
     * @param positive_list
     * @param compare_value_list
     */
    public static void dummy_results_for_testing_r_script(
            ArrayList<Boolean> positive_list, ArrayList<Double> compare_value_list){
        int test_data_size = 10;
        Random random = new Random();

        for(int i=0; i<test_data_size; i++){
            positive_list.add(random.nextInt()%2 == 0);
            compare_value_list.add(random.nextDouble());
        }
    }
}
