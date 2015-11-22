package analysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import com.google.gson.Gson;

import dataTypes.Point;
import dataTypes.Response;

/**
 * The goal of this class is to analyze the effectiveness of the authentication
 * system.
 * 
 * useful metrics: false postive rate false negative rate accuracy
 * 
 * @author element
 *
 */
public class Effectiveness {
    public static final String PROFILE_DIRECTORY = "response_profiles/";
    public static final String PROFILE_A_FILENAME = PROFILE_DIRECTORY + "response_profile_tim";
    public static final String PROFILE_B_FILENAME = PROFILE_DIRECTORY + "response_profile_tim";

    public static void main(String[] args) {
	ArrayList<Test> test_set = new ArrayList<Test>();

	// set up the challenge points
	ArrayList<Point> challenge_points = new ArrayList<Point>();

	challenge_points.add(new Point(0, 0, 0, 0, 0));

	// read in the files with response objects
	ArrayList<Response> profile_a_responses = get_response_list(PROFILE_A_FILENAME);
	ArrayList<Response> profile_b_responses = get_response_list(PROFILE_B_FILENAME);

	// set up a number of tests to be conducted
	test_set = generate_tests(profile_a_responses, profile_b_responses);

	// analyze the results of the tests
	analyze_test_results(test_set);
    }

    private static void analyze_test_results(ArrayList<Test> test_set) {
	// analyze the results of the tests
	// 1) false positive
	// 2) false negative
	// 3) accuracy

	// analyze each of the three metrics for this test set
	double false_positive = compute_false_positive(test_set);
	double false_negative = compute_false_negative(test_set);
	double accuracy = compute_accuracy(test_set);

	// print out the results
	StringBuilder output = new StringBuilder();

	output.append("false positive: " + false_positive + "\n");
	output.append("false negative: " + false_negative + "\n");
	output.append("accuracy: " + accuracy + "\n");

	System.out.println(output);
    }

    /**
     * compute the false positive ratio
     * 
     * [should_not_authenticate_but_did] / [total_tests]
     * 
     * @param test_set
     * @return
     */
    private static double compute_false_positive(ArrayList<Test> test_set) {
	return 0;
    }

    /**
     * compute the false negative ratio
     * 
     * [should authenticate but did not] / [total_tests]
     * 
     * @param test_set
     * @return
     */
    private static double compute_false_negative(ArrayList<Test> test_set) {
	return 0;
    }

    /**
     * given a set of tests, compute how often the test set makes the correct
     * decision. Meaning if the user is the authentic user who generated the
     * profile, we authenticate. and if the user is a user who did not generate
     * the profile then we dont' authenticate.
     * 
     * @param test_set
     * @return
     */
    private static double compute_accuracy(ArrayList<Test> test_set) {
	return 0;
    }

    /**
     * generates a set of tests. This will take the first half of each set of
     * responses as the profile. It will then set up the tests so each respone
     * in the remaining half will be compared against both profiles.
     * 
     * @return
     */
    private static ArrayList<Test> generate_tests(ArrayList<Response> profile_a_responses,
	    ArrayList<Response> profile_b_responses) {
	ArrayList<Test> test_set = new ArrayList<Test>();

	// TODO
	// Test test = new Test(,,challenge_points);

	// test_set.add(test,test,true);

	return test_set;
    }

    /**
     * gets the list of responses from a file encoded in json format
     * 
     * @param filename
     * @return
     */
    private static ArrayList<Response> get_response_list(String filename) {
	// TODO
	Gson gson = new Gson();
	BufferedReader br = null;
	Response[] responses = {};

	try {
	    br = new BufferedReader(new FileReader(filename));

	    responses = gson.fromJson(br, Response[].class);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}

	for (int i = 0; i < responses.length; i++) {
	    System.out.println(responses[i].getResponse());
	}

	ArrayList<Response> response_list = new ArrayList<Response>();

	for (int i = 0; i < responses.length; i++) {
	    response_list.add(responses[i]);
	}

	return response_list;
    }

}
