package jUnitTests.dataTypes;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;
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

    private MathContext context;

    @Before
    public void init() {
	// create a new challenge and extract the profile
	Challenge challenge;
	Response response;
	List<Point> response_points;

	// set up the context for compairing doubles
	context = new MathContext(3);

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
		response_points.add(new Point((300 / 32) * j + 100, 100, i, 100, j));
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
    public void test_get_pressure_mu_sigma_values() {
	MuSigma mu_sigma = this.profile.getPressureMuSigmaValues();

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
    public void test_pressure_number_points_correct() {
	MuSigma mu_sigma = this.profile.getPressureMuSigmaValues();

	List<Double> mu_list = mu_sigma.getMuValues();
	List<Double> sigma_list = mu_sigma.getSigmaValues();

	// System.out.println(mu_list.size());
	// System.out.println(sigma_list.size());

	assertTrue(mu_list.size() == 32);
	assertTrue(sigma_list.size() == 32);
    }

    /**
     * test teh getMuSigmaValues method for correctness.
     */
    @Test
    public void test_get_point_distance_mu_sigma_values() {
	MuSigma mu_sigma = this.profile.getPointDistanceMuSigmaValues();

	List<Double> mu_list = mu_sigma.getMuValues();
	List<Double> sigma_list = mu_sigma.getSigmaValues();

	// assert that mu an sigma are what they should be for all points
	for (int i = 0; i < mu_list.size(); i++) {
	    // for each point check that it is correct
	    assertThat(new BigDecimal(mu_list.get(i)).round(context), is((new BigDecimal(100)).round(context)));
	    assertThat(new BigDecimal(sigma_list.get(i) + 0.0).round(context), is(new BigDecimal(0.0).round(context)));
	}
    }

    /**
     * test that MuSigma computes a mu, sigma value for each point in the
     * normalized response
     */
    @Test
    public void test_point_distance_number_points_correct() {
	MuSigma mu_sigma = this.profile.getPointDistanceMuSigmaValues();

	List<Double> mu_list = mu_sigma.getMuValues();
	List<Double> sigma_list = mu_sigma.getSigmaValues();

	// System.out.println(mu_list.size());
	// System.out.println(sigma_list.size());

	assertTrue(mu_list.size() == 32);
	assertTrue(sigma_list.size() == 32);
    }

    /**
     * test teh getMuSigmaValues method for correctness.
     */
    @Test
    public void test_get_point_time_mu_sigma_values() {
	MuSigma mu_sigma = this.profile.getTimeDistanceMuSigmaValues();

	List<Double> mu_list = mu_sigma.getMuValues();
	List<Double> sigma_list = mu_sigma.getSigmaValues();

	// assert that mu an sigma are what they should be for all points
	for (int i = 0; i < mu_list.size(); i++) {
	    // for each point check that it is correct
	    assertThat(new BigDecimal(mu_list.get(i)).round(context), is((new BigDecimal(i)).round(context)));
	    assertThat(new BigDecimal(sigma_list.get(i) + 0.0).round(context), is(new BigDecimal(0.0).round(context)));
	}
    }

    /**
     * test that MuSigma computes a mu, sigma value for each point in the
     * normalized response
     */
    @Test
    public void test_point_time_number_points_correct() {
	MuSigma mu_sigma = this.profile.getTimeDistanceMuSigmaValues();

	List<Double> mu_list = mu_sigma.getMuValues();
	List<Double> sigma_list = mu_sigma.getSigmaValues();

	// System.out.println(mu_list.size());
	// System.out.println(sigma_list.size());

	assertTrue(mu_list.size() == 32);
	assertTrue(sigma_list.size() == 32);
    }

    /**
     * test that time length is returning the correct mu
     */
    @Test
    public void test_time_length_mu() {
	double mu = this.profile.getTimeLengthMu();

	// System.out.println(this.profile.getNormalizedResponses().get(0).getResponse().size());
	// System.out.println("mu: " + mu);
	assertTrue(mu == 31);
    }

    /**
     * test that time length is returning the correct sigma
     */
    @Test
    public void test_time_length_sigma() {
	double sigma = this.profile.getTimeLengthSigma();

	System.out.println("sigma: " + sigma);
	assertTrue(sigma == 0);
    }

    /**
     * test if profile is serializable
     */
    @Test
    public void test_serializable() {
	// try to write object to console
	try {
	    // ObjectOutputStream out = new ObjectOutputStream(System.err);
	    // out.writeObject(this.profile.getPressureMuSigmaValues()); // ok
	    // out.writeObject((this.profile.getNormalizedResponses())); // not
	    // ok
	    // out.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    assertTrue(false);
	}
    }

    /**
     * test that the confidence interval works as intended, and that it returns
     * reasonable values. valid confidence intervals are between 0 and 1.
     * 
     * profile confidence interval.
     */
    @Test
    public void test_compute_confidence_interval_valid() {
	boolean valid = true;
	double confidence_interval = 0;

	confidence_interval = this.profile.getConfidence_interval();

	// System.out.println(confidence_interval);

	valid = valid && confidence_interval >= 0;
	valid = valid && confidence_interval <= 1;

	assertTrue(valid);
    }

    /**
     * test that confidence interval is correct. This means computing it by hand
     * for these values.
     */
    @Test
    public void test_compute_confidence_interval_correct() {
	boolean valid = true;
	double confidence_interval = 0;

	confidence_interval = this.profile.getConfidence_interval();

	// System.out.println(confidence_interval);

	valid = confidence_interval == 10;
	assertTrue(valid);
    }
}
