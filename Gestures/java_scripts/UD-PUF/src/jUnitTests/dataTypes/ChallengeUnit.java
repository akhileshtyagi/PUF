package jUnitTests.dataTypes;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import data.DataReader;
import dataTypes.Challenge;
import dataTypes.Point;
import dataTypes.UserDevicePair;

public class ChallengeUnit {
    private static String inputDirectory = "data_link_tim_2000";

    private File inputFile;
    private DataReader reader;
    private UserDevicePair user_device_pair;

    @Before
    public void initObjects() throws SecurityException, InstantiationException, IllegalAccessException {
	inputFile = new File(inputDirectory);
	reader = new DataReader(inputFile);

	// read in the data
	// List<Challenge> challenges = reader.readDataDirecotryChallenge();

	// construct some test data
	List<Challenge> challenges = new ArrayList<Challenge>();
	List<Point> challenge_points = new ArrayList<Point>();

	// sample points for testing
	challenge_points.add(new Point(100, 100, 0));
	challenge_points.add(new Point(200, 200, 0));
	challenge_points.add(new Point(300, 300, 0));
	challenge_points.add(new Point(400, 400, 0));

	challenges.add(new Challenge(challenge_points, 0));

	// put the challenges into the user, device pair
	user_device_pair = new UserDevicePair(0, challenges);
    }

    @Test
    public void testFileExists() {
	assertTrue(inputFile.exists());
    }

    /**
     * test computeHorizontalPointsAlongChallenge
     */
    @Test
    public void testHorizontalPointsAlongChallenge() {
	double x_distance;

	// compute the x distance of the first challenge
	String methodName = "computeChallengeXDistance";
	Class[] classList = {};
	Object[] methodParameters = {};

	x_distance = (double) TestUtil.runPrivateMethod(user_device_pair.getChallenges().get(0), methodName, classList,
		methodParameters);

	// run the private method
	methodName = "computeHorizontalPointsAlongChallenge";
	Class[] classList_temp = { double.class };
	Object[] methodParameters_temp = { x_distance };

	classList = classList_temp;
	methodParameters = methodParameters_temp;

	List<Point> answer = (List<Point>) TestUtil.runPrivateMethod(user_device_pair.getChallenges().get(0),
		methodName, classList, methodParameters);

	// check that the horizontal points are computed correctly given
	// that the x distance was computed correctly
	// check all points along challenge
	for (int i = 0; i < answer.size(); i++) {
	    assertTrue(answer.get(i).getX() == 100 + (i * x_distance / (answer.size() - 1)));
	    assertTrue(answer.get(i).getY() == 100);
	}
    }

    /**
     * test computeHorizontalPointsAlongChallenge
     */
    @Test
    public void testVerticalPointsAlongChallenge() {
	double y_distance;

	// compute the x distance of the first challenge
	String methodName = "computeChallengeYDistance";
	Class[] classList = {};
	Object[] methodParameters = {};

	y_distance = (double) TestUtil.runPrivateMethod(user_device_pair.getChallenges().get(0), methodName, classList,
		methodParameters);

	// run the private method
	methodName = "computeVerticalPointsAlongChallenge";
	Class[] classList_temp = { double.class };
	Object[] methodParameters_temp = { y_distance };

	classList = classList_temp;
	methodParameters = methodParameters_temp;

	List<Point> answer = (List<Point>) TestUtil.runPrivateMethod(user_device_pair.getChallenges().get(0),
		methodName, classList, methodParameters);

	// check that the horizontal points are computed correctly given
	// that the x distance was computed correctly
	// check all points along challenge
	for (int i = 0; i < answer.size(); i++) {
	    assertTrue(answer.get(i).getY() == 100 + (i * y_distance / (answer.size() - 1)));
	    assertTrue(answer.get(i).getX() == 100);
	}
    }

    /**
     * tests getChallengeXDistance on the first challenge in the list
     */
    @Test
    public void testComputeChallengeXDistance() {
	double answer = -1;

	String methodName = "computeChallengeXDistance";
	Class[] classList = {};
	Object[] methodParameters = {};

	answer = (double) TestUtil.runPrivateMethod(user_device_pair.getChallenges().get(0), methodName, classList,
		methodParameters);

	// make sure answer is correct
	assertTrue(answer == 300);
    }

    /**
     * tests getChallengeXDistance on the first challenge in the list
     */
    @Test
    public void testComputeChallengeYDistance() {
	double answer = -1;

	String methodName = "computeChallengeYDistance";
	Class[] classList = {};
	Object[] methodParameters = {};

	answer = (double) TestUtil.runPrivateMethod(user_device_pair.getChallenges().get(0), methodName, classList,
		methodParameters);

	// make sure answer is correct
	assertTrue(answer == 300);
    }
}
