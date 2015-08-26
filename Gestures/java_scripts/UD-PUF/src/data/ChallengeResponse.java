package data;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a single gesture interaction between the user and the
 * touchscreen. Data will be read and populated in the DataReader class.
 * 
 * @author element
 *
 */
public class ChallengeResponse {
    private String device;
    private String tester;

    // challenge is an x,y list of points
    private List<List<Double>> challenge;
    // response is a lit of x,y,pressure
    private List<List<Double>> response;

    public ChallengeResponse(String device, String tester) {
	this.device = device;
	this.tester = tester;

	challenge = new ArrayList<List<Double>>();
	response = new ArrayList<List<Double>>();
    }

    /**
     * adds a challenge point to the list. This is designed to be used
     * incrementally.
     */
    public void addChallengePoint(double x, double y) {
	ArrayList<Double> addList = new ArrayList<Double>();

	addList.add(x);
	addList.add(y);

	this.challenge.add(addList);
    }

    /**
     * adds a resposne point to the list. This is designed to be used
     * incrementally.
     */
    public void addResponsePoint(double x, double y, double pressure) {
	ArrayList<Double> addList = new ArrayList<Double>();

	addList.add(x);
	addList.add(y);
	addList.add(pressure);

	this.response.add(addList);
    }

    /**
     * remove all challenge, response points
     */
    public void clearAllPoints() {
	challenge = new ArrayList<List<Double>>();
	response = new ArrayList<List<Double>>();
    }
}
