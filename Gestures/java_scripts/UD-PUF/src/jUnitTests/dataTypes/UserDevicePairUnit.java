package jUnitTests.dataTypes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dataTypes.Challenge;
import dataTypes.Point;
import dataTypes.Profile;
import dataTypes.Response;
import dataTypes.UserDevicePair;

/**
 * this class will test the UserDevicePair class. the goal is to make sure this
 * class performs the authentication correctly. This is the only non-trivial
 * thing it is responsible for.
 * 
 * @author element
 *
 */

public class UserDevicePairUnit {
    UserDevicePair ud_pair;

    // TODO make tests which incorporate point distance
    @Before
    public void init() {
	Challenge challenge;
	Response response;
	List<Point> response_points;

	// create a userDeficePair
	ud_pair = new UserDevicePair(0);

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

	// the mu sigma for the responses should be
	// mu : 1
	// sigma : sqrt(2/3)
	ud_pair.addChallenge(challenge);
    }

    @Test
    public void test_authenticate_should_authenticate_high() {
	List<Point> response_points;

	// make a response which should authenticate higher than normal pressure
	response_points = new ArrayList<Point>();

	// create the response
	for (int j = 0; j < 32; j++) {
	    response_points.add(new Point((300 / 32) * j + 100, 100, 1.5));
	}

	assertTrue(ud_pair.authenticate(response_points, 0));
    }

    @Test
    public void test_authenticate_should_authenticate_same() {
	List<Point> response_points;

	// make a response which should authenticate higher than normal pressure
	response_points = new ArrayList<Point>();

	// create the response
	for (int j = 0; j < 32; j++) {
	    response_points.add(new Point((300 / 32) * j + 100, 100, 1));
	}

	assertTrue(ud_pair.authenticate(response_points, 0));
    }

    @Test
    public void test_authenticate_should_authenticate_low() {
	List<Point> response_points;

	// make a response which should authenticate higher than normal pressure
	response_points = new ArrayList<Point>();

	// create the response
	for (int j = 0; j < 32; j++) {
	    response_points.add(new Point((300 / 32) * j + 100, 100, .5));
	}

	assertTrue(ud_pair.authenticate(response_points, 0));
    }

    @Test
    public void test_authenticate_should_not_authenticate_high() {
	List<Point> response_points;

	// make a response which should authenticate higher than normal pressure
	response_points = new ArrayList<Point>();

	// create the response
	for (int j = 0; j < 32; j++) {
	    response_points.add(new Point((300 / 32) * j + 100, 100, 2));
	}

	assertFalse(ud_pair.authenticate(response_points, 0));
    }

    @Test
    public void test_authenticate_should_not_authenticate_low() {
	List<Point> response_points;

	// make a response which should authenticate higher than normal pressure
	response_points = new ArrayList<Point>();

	// create the response
	for (int j = 0; j < 32; j++) {
	    response_points.add(new Point((300 / 32) * j + 100, 100, 0));
	}

	assertFalse(ud_pair.authenticate(response_points, 0));
    }

    @Test
    public void test_failed_points_all_fail() {
	// create new response to be used
	List<Point> new_response = new ArrayList<Point>();

	for (int j = 0; j < 32; j++) {
	    new_response.add(new Point((300 / 32) * j + 100, 100, 0));
	}

	// create new challenge to be used
	Challenge challenge = ud_pair.getChallenges().get(0);
	Profile profile = challenge.getProfile();

	// call the private method of UserDevicePair
	String methodName = "failed_points";
	Class[] classList = { List.class, Profile.class, double.class };
	Object[] methodParameters = { new_response, profile, 1 };

	int answer = (int) TestUtil.runPrivateMethod(this.ud_pair, methodName, classList, methodParameters);

	assertTrue(answer == 32);
    }

    @Test
    public void test_failed_points_all_pass() {
	// create new response to be used
	List<Point> new_response = new ArrayList<Point>();

	for (int j = 0; j < 32; j++) {
	    new_response.add(new Point((300 / 32) * j + 100, 100, 1));
	}

	// create new challenge to be used
	Challenge challenge = ud_pair.getChallenges().get(0);
	Profile profile = challenge.getProfile();

	// call the private method of UserDevicePair
	String methodName = "failed_points";
	Class[] classList = { List.class, Profile.class, double.class };
	Object[] methodParameters = { new_response, profile, 1 };

	int answer = (int) TestUtil.runPrivateMethod(this.ud_pair, methodName, classList, methodParameters);

	assertTrue(answer == 0);
    }

    /**
     * TODO figure out why this test fails!!!
     */
    @Test
    public void test_failed_points_ratio() {
	// test combinations which result in failed point ratio of 0, .5, 1
	// combination result in 0
	List<Point> new_response = new ArrayList<Point>();

	for (int j = 0; j < 32; j++) {
	    new_response.add(new Point((300 / 32) * j + 100, 100, 1));
	}

	// create new challenge to be used
	Challenge challenge = ud_pair.getChallenges().get(0);
	Profile profile = challenge.getProfile();

	this.ud_pair.authenticate(new_response, profile);
	assertTrue(this.ud_pair.failedPointRatio(UserDevicePair.RatioType.PRESSURE) == 0);

	// combination result in 1
	new_response = new ArrayList<Point>();

	for (int j = 0; j < 32; j++) {
	    new_response.add(new Point((300 / 32) * j + 100, 100, 0));
	}

	this.ud_pair.authenticate(new_response, profile);
	assertTrue(this.ud_pair.failedPointRatio(UserDevicePair.RatioType.PRESSURE) == 1);

	// combination result in .5
	new_response = new ArrayList<Point>();

	for (int j = 0; j < 32; j++) {
	    new_response.add(new Point((300 / 32) * j + 100, 100, j % 2));
	}

	this.ud_pair.authenticate(new_response, profile);
	// System.out.println(this.ud_pair.failedPointRatio());
	// System.out.println(new_response);
	assertTrue(this.ud_pair.failedPointRatio(UserDevicePair.RatioType.PRESSURE) == .5);
    }

    /**
     * dumps a lot of information which is useful for debugging
     */
    @Test
    public void test_info_dump_authenticate() {
	Challenge challenge = ud_pair.getChallenges().get(0);
	Profile profile = challenge.getProfile();

	// combination result in .5
	ArrayList<Point> new_response = new ArrayList<Point>();

	for (int j = 0; j < 32; j++) {
	    new_response.add(new Point((300.0 / 31) * j + 100, 100, j % 2, 100, j));
	}

	System.out.println(this.ud_pair.information_dump_authenticate(new_response, profile));

	// should not ac
	assertTrue(this.ud_pair.failedPointRatio(UserDevicePair.RatioType.PRESSURE) == .5);
    }
}
