package jUnitTests.dataTypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import dataTypes.*;
import org.junit.Before;
import org.junit.Test;

import data.DataReader;

/**
 * Unit tests for the Profile
 */
public class ProfileUnit {
    private static String inputDirectory = "tim_2000_1";

    private File inputFile;
    private DataReader reader;

    private ArrayList<Response> defaultResponses;
    private ArrayList<ArrayList<Point>> defaultPointSets;
    private Profile defaultProfile;

    private ArrayList<Response> linearResponses;
    private ArrayList<ArrayList<Point>> linearPointSets;
    private Profile linearProfile;

    @Before
    public void init() {

        // initialize default sets
        defaultPointSets = new ArrayList<>();
        defaultResponses = new ArrayList<>();
        inputFile = new File(inputDirectory);
        reader = new DataReader(inputFile);

        // initialize linear sets
        linearPointSets = new ArrayList<>();
        linearResponses = new ArrayList<>();
        inputFile = new File(inputDirectory);
        reader = new DataReader(inputFile);


        // Creates a list of identical defaultResponses, each at an
        // ascending x and y coordinate and with .5 pressure

        // outer loop to loop through each response
        for(int i = 0; i < 20; i++) {
            defaultPointSets.add(new ArrayList<Point>());
            linearPointSets.add(new ArrayList<Point>());
            // inner loop to assign point values to each response
            for(int j = 0; j < 32; j++) {
                defaultPointSets.get(i).add(new Point(j, j, .5));
                linearPointSets.get(i).add(new Point(j, j, (double)j / 32));
            }
            defaultResponses.add(new Response(defaultPointSets.get(i)));
            linearResponses.add(new Response(linearPointSets.get(i)));
        }

        defaultProfile = new Profile(defaultResponses);
        linearProfile = new Profile(linearResponses);

    }

    // Tests the default profile to assure the correct mu and sigma values
    // are being created
    @Test
    public void testMuSigmaValues() {
        for(int i = 0; i < 32; i++) {
            assertTrue(.5 == defaultProfile.getMuSigmaValues().getMuValues().get(i));
            assertTrue(0 == defaultProfile.getMuSigmaValues().getSigmaValues().get(i));
            assertTrue((double)i/32== linearProfile.getMuSigmaValues().getMuValues().get(i));
            assertTrue(0 == linearProfile.getMuSigmaValues().getSigmaValues().get(i));
        }
    }


=======
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dataTypes.Challenge;
import dataTypes.MuSigma;
import dataTypes.Point;
import dataTypes.Profile;
import dataTypes.Response;

/**
 * This test case tests the Profile class for correctness. The tests in this
 * class assume that Challenge class works
 * 
 * @author element
 *
 */
public class ProfileUnit {
    private Profile profile;

    @Before
    public void init() {
	// create a new challenge and extract the profile
	Challenge challenge;
	Response response;
	List<Point> response_points;

	// create a list of challenge points
	List<Point> challenge_points = new ArrayList<Point>();

	// sample points for testing
	challenge_points.add(new Point(100, 100, 0));
	challenge_points.add(new Point(200, 100, 0));
	challenge_points.add(new Point(300, 100, 0));
	challenge_points.add(new Point(400, 100, 0));

	// add the challenge to it which I want to authenticate against
	// create 3 responses to add to this challenge
	challenge = new Challenge(challenge_points, 0);

	for (int i = 0; i < 3; i++) {
	    response_points = new ArrayList<Point>();

	    // create the response
	    for (int j = 0; j < 32; j++) {
		response_points.add(new Point((300 / 32) * j + 100, 100, i));
	    }

	    response = new Response(response_points);
	    challenge.addResponse(response);
	}

	// the mu sigma for all points in the responses should be
	// mu : 1
	// sigma : sqrt(2/3)
	this.profile = challenge.getProfile();
    }

    /**
     * test teh getMuSigmaValues method for correctness.
     */
    @Test
    public void test_get_mu_sigma_values() {
	MuSigma mu_sigma = this.profile.getMuSigmaValues();

	List<Double> mu_list = mu_sigma.getMuValues();
	List<Double> sigma_list = mu_sigma.getSigmaValues();

	// assert that mu an sigma are what they should be for all points
	for (int i = 0; i < mu_list.size(); i++) {
	    // for each point check that it is correct
	    assertTrue(mu_list.get(i) == 1);
	    assertTrue(sigma_list.get(i) == Math.sqrt(2.0 / 3.0));
	}
    }

    /**
     * test that MuSigma computes a mu, sigma value for each point in the
     * normalized response
     */
    @Test
    public void test_number_points_correct() {
	MuSigma mu_sigma = this.profile.getMuSigmaValues();

	List<Double> mu_list = mu_sigma.getMuValues();
	List<Double> sigma_list = mu_sigma.getSigmaValues();

	// System.out.println(mu_list.size());
	// System.out.println(sigma_list.size());

	assertTrue(mu_list.size() == 32);
	assertTrue(sigma_list.size() == 32);
    }
}
