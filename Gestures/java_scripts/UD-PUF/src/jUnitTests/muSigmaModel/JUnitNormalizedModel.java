package jUnitTests.muSigmaModel;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import data.ChallengeResponse;
import data.DataReader;
import muSigmaModel.NormalizedModel;

public class JUnitNormalizedModel {
    private static String inputDirectory = "data_link_tim_2000";

    private File inputFile;
    private DataReader reader;
    private NormalizedModel normalizedResponse;
    List<ChallengeResponse> responses;

    @Before
    public void initObjects() throws SecurityException, InstantiationException, IllegalAccessException {
	inputFile = new File(inputDirectory);
	reader = new DataReader(inputFile);

	// read in the data
	responses = reader.readDataDirecotry();

	// check to see if the data is correct in the challenge response object
	// System.out.println(responses.get(0).getResponseList().get(0));
	// System.out.println(responses.get(0).getChallengeList().get(0));

	// construct the list of normalized models for each response
	normalizedResponse = new NormalizedModel(responses, 1);
    }

    @Test
    public void testFileExists() {
	assertTrue(inputFile.exists());
    }

    @Test
    public void testResponsesReadCorrectly() {
	double x = responses.get(0).getResponseList().get(0).get(0);
	double y = responses.get(0).getResponseList().get(0).get(1);
	double z = responses.get(0).getResponseList().get(0).get(2);

	assertTrue(x >= 0 && x <= 1080);
	assertTrue(y >= 0 && y <= 1920);
	assertTrue(z >= 0 && z <= 1);
    }

    @Test
    public void testChallengeReadCorrectly() {
	double x = responses.get(0).getChallengeList().get(0).get(0);
	double y = responses.get(0).getChallengeList().get(0).get(1);

	assertTrue(x >= 0 && x <= 1080);
	assertTrue(y >= 0 && y <= 1920);
    }

    @Test
    public void testNormalizeChallengeResponseList() {
	assertTrue(false);
    }

    @Test
    public void testComputeHorizontalPointsAlongChallenge() {
	assertTrue(false);
    }

    @Test
    public void testComputeVerticalPointsAlongChallenge() {
	assertTrue(false);
    }

    @Test
    public void testComputeChallengeYDistance() {
	boolean correct = false;
	ChallengeResponse response;

	// create a challengeResponse with known XDistance
	response = new ChallengeResponse("", "");

	// add challenge points the the response
	for (int i = 0; i < 100; i++) {
	    response.addChallengePoint(100.0, i);
	}

	// run the private method
	String methodName = "computeChallengeYDistance";
	Class[] classList = { ChallengeResponse.class };
	Object[] methodParameters = { response };

	double answer = (double) runPrivateMethod(normalizedResponse, methodName, classList, methodParameters);

	// actual value should be around 100
	if (answer > (98.95) && answer < (99.05)) {
	    correct = true;
	}

	assertTrue(correct);
    }

    @Test
    public void testComputeChallengeXDistance() {
	boolean correct = false;
	ChallengeResponse response;

	// create a challengeResponse with known XDistance
	response = new ChallengeResponse("", "");

	// add challenge points the the response
	for (int i = 0; i < 100; i++) {
	    response.addChallengePoint(i, 100.0);
	}

	// run the private method
	String methodName = "computeChallengeXDistance";
	Class[] classList = { ChallengeResponse.class };
	Object[] methodParameters = { response };

	double answer = (double) runPrivateMethod(normalizedResponse, methodName, classList, methodParameters);

	// actual value should be around 100
	if (answer > (98.95) && answer < (99.05)) {
	    correct = true;
	}

	assertTrue(correct);
    }

    @Test
    public void testComputeMuSigma() {
	assertTrue(false);
    }

    @Test
    public void testComputeMu() {
	boolean correct = false;
	ArrayList<Double> numbers = new ArrayList<Double>();

	// create a List of touches with a minimum value
	for (int i = 0; i < 9; i++) {
	    numbers.add(.1 * (i + 1));
	}

	// run the private method
	String methodName = "computeMu";
	Class[] classList = { java.util.List.class };
	Object[] methodParameters = { numbers };

	double answer = (double) runPrivateMethod(normalizedResponse, methodName, classList, methodParameters);

	// actual value should be around .5
	if (answer > (.49) && answer < (.51)) {
	    correct = true;
	}

	assertTrue(correct);
    }

    @Test
    public void testComputeSigma() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	boolean correct = false;
	ArrayList<Double> numbers = new ArrayList<Double>();

	// create a List of touches with a minimum value
	for (int i = 0; i < 9; i++) {
	    numbers.add(.1 * (i + 1));
	}

	// run the private method
	String methodName = "computeSigma";
	Class[] classList = { java.util.List.class };
	Object[] methodParameters = { numbers };

	double answer = (double) runPrivateMethod(normalizedResponse, methodName, classList, methodParameters);

	// actual value should be around .2582
	if (answer > (.258) && answer < (.2585)) {
	    correct = true;
	}

	assertTrue(correct);
    }

    /**
     * returns the method requested in an accessible state.
     * 
     * @param o
     */
    private Object runPrivateMethod(Object o, String methodName, Class[] argClasses, Object[] methodParameters) {
	Method method = null;
	Object object = null;

	try {
	    method = o.getClass().getDeclaredMethod(methodName, argClasses);

	    // set method to accessible
	    method.setAccessible(true);

	    // invoke the method
	    object = method.invoke(o, methodParameters);
	} catch (NoSuchMethodException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (SecurityException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InvocationTargetException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return object;
    }
}