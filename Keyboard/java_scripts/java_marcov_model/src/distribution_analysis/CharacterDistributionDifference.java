package distribution_analysis;

import components.Distribution;
import utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO hand check some result to make sure they are accurate

/**
 * compute the difference between two distributions for the same
 * keycode over all keycodes.
 *
 * WATCH OUT FOR INTEGER DIVISION
 */
public class CharacterDistributionDifference {
    public static final String CHAIN_FILENAME_0 = "data_sets/t_tim_d_tim.csv_4512";
    public static final String CHAIN_FILENAME_1 = "data_sets/t_ian_d_tim.csv_4512";

    /**
     * print a list of { keycode, Pr_F }
     */
    public static void main(String[] args){
        Map<Integer, Double> keycode_difference_map = new HashMap<Integer, Double>();

        // read in the chain from the file
        List<Distribution> distribution_list_0 = Utility.read_chain(CHAIN_FILENAME_0).get_key_distribution();
        List<Distribution> distribution_list_1 = Utility.read_chain(CHAIN_FILENAME_1).get_key_distribution();

        // for each distribution,
        for(Distribution distribution_0 : distribution_list_0) {
            // find the equivilent distribution in the other list
            for(Distribution distribution_1 : distribution_list_1) {
                if (distribution_0.get_keycode() == distribution_1.get_keycode()) {
                    Integer keycode = distribution_0.get_keycode();
                    Double difference = compute_difference_metric(distribution_0, distribution_1);

                    keycode_difference_map.put(keycode, difference);
                }
            }
        }

        // print out the keycode difference map
        // format:
        // keycode, difference
        for(Map.Entry<Integer, Double> entry : keycode_difference_map.entrySet()){
            System.out.println(entry.getKey().toString() + ", " + entry.getValue());
        }
    }

    /**
     * compute the difference metric between the distributions
     */
    private static double compute_difference_metric(Distribution distribution_0, Distribution distribution_1){
        double mu_0 = distribution_0.get_average();
        double std_dev_0 = distribution_0.get_standard_deviation();
        double var_0 = Math.pow(std_dev_0, 2.0);

        double mu_1 = distribution_1.get_average();
        double std_dev_1 = distribution_1.get_standard_deviation();
        double var_1 =Math.pow(std_dev_1, 2.0);

        /* begin formula computation */
        double alpha =
                (
                    (var_1 * mu_0 - var_0 * mu_1) +
                    std_dev_0 * std_dev_1 * Math.sqrt(
                            Math.pow(mu_1 - mu_0, 2.0) +
                            2.0 * Math.log(std_dev_0 / std_dev_1) *
                            (var_0 - var_1)
                    )
                ) / (var_1 - var_0);

        //System.out.println("alpha: " + alpha);

        // one billion is practially infinity, right?
        double PRACTICAL_INFINITY = 100000000.0;
        double result =
                integrate_function(alpha, PRACTICAL_INFINITY, mu_0, std_dev_0) +
                integrate_function(-1*PRACTICAL_INFINITY, alpha, mu_1, std_dev_1);
        /* end formula computation */

        return result;
    }

    /**
     * integrate the hard-coded function
     * over the provided bounds
     *
     * from a to b
     *
     * uses N recrangle to approximate the area under the curve
     */
    public static int N = 100000000;
    public static int THREAD_COUNT = 64;
    private static double integrate_function(double a, double b, double mu_0, double std_dev_0){
        double sum = 0.0;
        double step_size = (b - a) / N;

        ArrayList<ComputeArea> compute_area_list = new ArrayList<>();
        ArrayList<Thread> compute_area_thread_list = new ArrayList<>();

        // use N rectangles
        // parallelize a number of iterations of this loop
        int iterations_per_thread = N / THREAD_COUNT;
        for(int i=0; i<N; i+=iterations_per_thread){
            int beginning_index = i;
            int end_index = i + iterations_per_thread - 1;

            // make sure end_index does not extend beyond the bounds
            end_index = (end_index < N) ? end_index : N-1;

            ComputeArea compute_area = new ComputeArea(beginning_index, end_index, step_size, mu_0, std_dev_0);
            Thread compute_area_thread = new Thread(compute_area);

            compute_area_thread.start();

            compute_area_list.add(compute_area);
            compute_area_thread_list.add(compute_area_thread);
        }

        // wait for all the threads to finish
        for(Thread compute_area_thread : compute_area_thread_list){
            try{ compute_area_thread.join(); }
            catch(Exception e) { e.printStackTrace(); }
        }

        // sum up the results
        for(ComputeArea compute_area : compute_area_list){
            sum += compute_area.get_result();
        }

        return sum;
    }

    /**
     * this class computes the area of a rectangle
     * under function given
     * lower_x, upper_x
     */
    public static class ComputeArea implements Runnable {
        int lower_i;
        int upper_i;

        double step_size;

        double mu_0;
        double std_dev_0;

        double result;

        public  ComputeArea(int beginning, int end, double step_size_0, double mu, double std_dev){
            lower_i = beginning;
            upper_i = end;

            step_size = step_size_0;

            mu_0 = mu;
            std_dev_0 = std_dev;

            result = 0.0;
        }

        @Override
        public void run() {
            result = 0.0;

            // go from lower_i up to and including upper_i
            for(int i=lower_i; i<=upper_i; i++) {
                double lower_x = i * step_size;
                double upper_x = (i + 1) * step_size;

                double lower_y = function(lower_x, mu_0, std_dev_0);
                double upper_y = function(upper_x, mu_0, std_dev_0);

                //System.out.println(String.format("lower_y %f\t upper_y %f", lower_y, upper_y));

                result += (upper_x - lower_x) * (lower_y + upper_y) / 2.0;
            }
        }

        public double get_result(){
            return result;
        }
    }

    /**
     * evaluates the function at x
     * function here is the pdf of a normal distribution
     */
    private static double function(double x, double mu_0, double std_dev_0){
        return (1.0 / (std_dev_0 * Math.sqrt(2.0 * Math.PI))) * Math.exp(
                -1.0*((1.0 / 2.0) * Math.pow((x - mu_0) / std_dev_0, 2.0))
        );
    }
}
