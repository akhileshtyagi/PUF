package analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * The goal of this class is to find the model parameters which maximize the
 * effectiveness.
 * 
 * for now we will define maximizing effectiveness as maximized accuracy, but I
 * suppose I could use a composite of false positive, false negative, and
 * accuracy ratios
 * 
 * @author element
 *
 */
public class BestModelParameters {

    /**
     * WARNING: this method takes 10-15 minutes to complete
     * 
     * compute the effectiveness for each combination of model parameters.
     * maximize accuracy.
     * 
     * @param args
     */
    public static void main(String[] args) {
	ArrayList<Combination> combinations = new ArrayList<Combination>();

	ArrayList<Double> pressure_deviations_list = new ArrayList<Double>();
	ArrayList<Double> distance_deviations_list = new ArrayList<Double>();
	ArrayList<Double> time_deviations_list = new ArrayList<Double>();
	ArrayList<Double> time_length_deviations_list = new ArrayList<Double>();
	ArrayList<Double> pressure_authentication_threshold_list = new ArrayList<Double>();
	ArrayList<Double> distance_authentication_threshold_list = new ArrayList<Double>();
	ArrayList<Double> time_authentication_threshold_list = new ArrayList<Double>();

	// generate a set of parameters to try for each deviation
	// for pressure
	int p_steps = 1;
	for (int i = 0; i < p_steps; i++) {
	    double deviations = (i * (3.0 / p_steps)) + 0.0;
	    pressure_deviations_list.add(deviations);
	}

	// for time
	int t_steps = 1;
	for (int i = 0; i < t_steps; i++) {
	    double deviations = (i * 2.0 / t_steps) + 0.0; // + .275;
	    time_deviations_list.add(deviations);
	}

	// for authentication threshold
	int pa_steps = 1;
	for (int i = 0; i < pa_steps; i++) {
	    pressure_authentication_threshold_list.add(((i * (1.0 / pa_steps)) + 0.0));
	}

	// for authentication threshold
	int da_steps = 10;
	for (int i = 0; i < da_steps; i++) {
	    distance_authentication_threshold_list.add(((i * (1.0 / da_steps)) + 0.0));
	}

	// for authentication threshold
	int ta_steps = 1;
	for (int i = 0; i < ta_steps; i++) {
	    time_authentication_threshold_list.add(((i * (1.0 / ta_steps)) + 0.0));
	}

	// for distance
	int d_steps = 100;
	for (int i = 0; i < d_steps; i++) {
	    double deviations = (i * 3.0 / d_steps) + 0.0;
	    distance_deviations_list.add(deviations);
	}

	// for time length
	int tl_steps = 1;
	for (int i = 0; i < tl_steps; i++) {
	    double deviations = (i * 3.0 / tl_steps) + 0.0;
	    time_length_deviations_list.add(deviations);
	}

	// generate all combinations of these parameters
	for (Double pressure_deviation : pressure_deviations_list) {
	    for (Double distance_deviation : distance_deviations_list) {
		for (Double time_deviation : time_deviations_list) {
		    for (Double time_length_deviation : time_length_deviations_list) {
			for (Double pressure_authentication_threshold : pressure_authentication_threshold_list) {
			    for (Double distance_authentication_threshold : distance_authentication_threshold_list) {
				for (Double time_authentication_threshold : time_authentication_threshold_list) {

				    long time = System.currentTimeMillis();
				    combinations.add(new Combination(new Double(pressure_deviation),
					    new Double(distance_deviation), new Double(time_deviation),
					    new Double(time_length_deviation),
					    new Double(pressure_authentication_threshold),
					    new Double(distance_authentication_threshold),
					    new Double(time_authentication_threshold)));

				    System.out.print("time_taken / accuracy: " + (System.currentTimeMillis() - time)
					    + " / " + combinations.get(combinations.size() - 1).accuracy + "\n");
				}
			    }
			}
		    }
		}
	    }
	}

	// System.out.print(combinations);

	// find the combination with the best accuracy
	Combination best = best_accuracy(combinations);

	System.out.println(best);

    }

    /**
     * return the combination which has the best accuracy
     */
    private static Combination best_accuracy(List<Combination> combinations) {
	// if combinations is empty or null, we can't do anything
	if (combinations == null || combinations.size() == 0) {
	    return null;
	}

	// set best equal to the first combination
	Combination best = combinations.get(0);

	for (Combination c : combinations) {
	    if (c.accuracy > best.accuracy) {
		best = c;
	    }
	}

	return best;
    }
}
