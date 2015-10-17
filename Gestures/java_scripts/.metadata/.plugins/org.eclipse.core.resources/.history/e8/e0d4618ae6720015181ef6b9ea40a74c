package jUnitTests.dataTypes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dataTypes.Challenge;
import dataTypes.Point;
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
		response_points.add(new Point((300 / 32) * j + 100, 100, i));
	    }

	    response = new Response(response_points);
	    challenge.addResponse(response);
	}

	// the mu sigma for the responses should be
	// mu : 1
	// sigma : 2/3
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
}
