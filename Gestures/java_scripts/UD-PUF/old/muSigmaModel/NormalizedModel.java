package muSigmaModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import data.ChallengeResponse;

/**
 * Takes a ChallengeResponse object in its constructor and creates the mu-sigma
 * model from this.
 * 
 * @author element
 *
 */
public class NormalizedModel {
    // number of elements in normalized list.
    final int NORMALIZED_ELEMENTS = 32;

    // list of {(mu_x,mu_y,sigma_x,sigma_y), (mu_x,mu_y,sigma_x,sigma_y), ...}
    // it may only be {(mu,sigma), (mu,sigma), ...} becuase I think we only
    // compute mu,sigma for either x or y, not both
    List<List<Double>> sigmaModel;
    int challengeNumber;

    private boolean mu_computed;
    private double mu;

    /**
     * Takes a challenge response object list. This list should contain
     * challengeResponse objects for a single (user, challenge) with many
     * responses. Uses this object to construct the normalized mu-sigma model.
     * 
     * @param challengeResponse
     */
    public NormalizedModel(List<ChallengeResponse> challengeResponseList, int challengeNumber) {
	this.challengeNumber = challengeNumber;
	this.mu_computed = false;
	mu = 0;

	// TODO this will be normalization strat 4
	// TODO construct the mu,sigma model here
	List<List<Double>> normalizedList = normalizeChallengeResponseList(challengeResponseList);
	this.sigmaModel = computeMuSigma(normalizedList);
    }

    /**
     * creates the normalized challenge response list.
     * 
     * @return {(x,y,pressure), (x,y,pressure), ...}
     */
    private List<List<Double>> normalizeChallengeResponseList(List<ChallengeResponse> challengeResponseList) {
	ArrayList<List<Double>> normalizedList = new ArrayList<List<Double>>();
	List<List<Double>> x_y_list;
	ArrayList<Double> pressure_list = new ArrayList<Double>();

	// TODO normalize the list
	// we either normalize with respect to x or y
	// we need to normalize x,y,pressure values
	for (ChallengeResponse response : challengeResponseList) {
	    // determine if the challenge is more horizontal or more vertical in
	    // oreantation
	    double x_dist = computeChallengeXDistance(response);
	    double y_dist = computeChallengeYDistance(response);

	    if (x_dist > y_dist) {
		// normalize with respect to x
		x_y_list = computeHorizontalPointsAlongChallenge(response, x_dist);

		// TODO I think the next step is to figure out the closcest
		// thing the the x_y_list points in the responses. We do
		// something to align these points to get our normalized list.
	    } else {
		// normalize with respect to y
		x_y_list = computeVerticalPointsAlongChallenge(response, y_dist);

		// TODO I think the next step is to figure out the closcest
		// thing the the x_y_list points in the responses. We do
		// something to align these points to get our normalized list.
	    }
	    // TODO add the element to normalizedList
	}

	return normalizedList;
    }

    /**
     * return a list of NORMALIZED_ELEMENTS number of points horizontally along
     * the challenge. We will normalize with respect to these points.
     */
    private List<List<Double>> computeHorizontalPointsAlongChallenge(ChallengeResponse response, double x_dist) {
	ArrayList<List<Double>> points = new ArrayList<List<Double>>();

	// amount of x separation for each point
	double point_increment = x_dist / NORMALIZED_ELEMENTS;

	for (int i = 0; i < NORMALIZED_ELEMENTS; i++) {
	    List<Double> next_point = new ArrayList<Double>();

	    // do something different for the first iteration
	    if (points.size() == 0) {
		// use the x,y values of the first point
		next_point.add(response.getChallengeList().get(0).get(0)); // x
									   // value
		next_point.add(response.getChallengeList().get(0).get(1)); // y
									   // value
	    } else {
		// add point increment to the y value while staying at the same
		// x value
		next_point.add(points.get(i - 1).get(0) + point_increment); // x
									    // value
		next_point.add(points.get(i - 1).get(1)); // y
							  // value
	    }

	    points.add(next_point);
	}

	return points;

    }

    /**
     * return a list of NORMALIZED_ELEMENTS number of points vertically along
     * the challenge. We will normalize with respect to these points.
     */
    private List<List<Double>> computeVerticalPointsAlongChallenge(ChallengeResponse response, double y_dist) {
	ArrayList<List<Double>> points = new ArrayList<List<Double>>();

	// amount of y separation for each point
	double point_increment = y_dist / NORMALIZED_ELEMENTS;

	for (int i = 0; i < NORMALIZED_ELEMENTS; i++) {
	    List<Double> next_point = new ArrayList<Double>();

	    // do something different for the first iteration
	    if (points.size() == 0) {
		// use the x,y values of the first point
		next_point.add(response.getChallengeList().get(0).get(0)); // x
									   // value
		next_point.add(response.getChallengeList().get(0).get(1)); // y
									   // value
	    } else {
		// add point increment to the y value while staying at the same
		// x value
		next_point.add(points.get(i - 1).get(0)); // x value
		next_point.add(points.get(i - 1).get(1) + point_increment); // y
									    // value
	    }

	    points.add(next_point);
	}

	return points;
    }

    /**
     * returns true if the given points cover more horizontal distance compared
     * to vertical distance. "given points" are the challenge list.
     */
    private boolean isHorizontalLine(ChallengeResponse response) {
	return computeChallengeXDistance(response) > computeChallengeYDistance(response);
    }

    /**
     * computes the y distance covered by a challenge
     * 
     * @param response
     * @return
     */
    private double computeChallengeYDistance(ChallengeResponse response) {
	double y_dist = 0;

	// calculate x_dist and y_dist covered by the list
	List<Double> prev_challenge_point = null;

	for (List<Double> challenge_point : response.getChallengeList()) {
	    if (prev_challenge_point == null) {
		prev_challenge_point = challenge_point;
		continue;
	    }

	    // compute the distance between the current point and the previous
	    // point
	    y_dist += Math.abs(challenge_point.get(1) - prev_challenge_point.get(1));

	    prev_challenge_point = challenge_point;
	}

	return y_dist;
    }

    /**
     * computes the x distance covered by a challenge
     * 
     * @param response
     * @return
     */
    private double computeChallengeXDistance(ChallengeResponse response) {
	double x_dist = 0;

	// calculate x_dist and y_dist covered by the list
	List<Double> prev_challenge_point = null;

	for (List<Double> challenge_point : response.getChallengeList()) {
	    if (prev_challenge_point == null) {
		prev_challenge_point = challenge_point;
		continue;
	    }

	    // compute the distance between the current point and the previous
	    // point
	    x_dist += Math.abs(challenge_point.get(0) - prev_challenge_point.get(0));

	    prev_challenge_point = challenge_point;
	}

	return x_dist;
    }

    /**
     * computes mu-sigma model for a list which has already been normalized.
     * 
     * @param normalizedList
     * @return
     */
    private List<List<Double>> computeMuSigma(List<List<Double>> normalizedList) {
	// TODO
	return null;
    }

    /**
     * compute average of the list of points
     */
    private double computeMu(List<Double> list) {
	Iterator<Double> iterator = list.iterator();
	double average = 0;
	double total = 0;

	while (iterator.hasNext()) {
	    Double t = iterator.next();

	    total += t;
	}

	average = total / list.size();

	this.mu_computed = true;
	return average;
    }

    /**
     * compute the standard deviation for the list of points
     * 
     * @return
     */
    private double computeSigma(List<Double> list) {
	double std = 0;

	// if the average has not yet been computed, compute it
	if (!mu_computed) {
	    this.mu = computeMu(list);
	}

	// 1. Work out the Mean (the simple average of the numbers)
	// 2. Then for each number: subtract the Mean and square the result
	// 3. Then work out the mean of those squared differences.
	// 4. Take the square root of that and we are done!
	Iterator<Double> iterator = list.iterator();
	int count = 0;
	double total_subtract_mean_squared = 0;

	while (iterator.hasNext()) {
	    Double t = iterator.next();

	    total_subtract_mean_squared += Math.pow(t - this.mu, 2);
	    count++;
	}

	// std is the square root of the average of these numbers
	std = Math.sqrt(total_subtract_mean_squared / count);

	return std;
    }

    /*
     * get the mu-sigmal model.
     */
    public List<List<Double>> getModel() {
	return this.sigmaModel;
    }
}
