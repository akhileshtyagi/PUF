package dataTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user/device pair, containing a unique identifier and a list of
 * all challenges correlating to that user
 */
public class UserDevicePair {
    final static double DEFAULT_ALLOWED_DEVIATIONS = 1.0;
    final static double DEFAULT_AUTHENTICATION_THRESHOLD = 0.75;

    // List of challenges correlating to this user/device pair
    private List<Challenge> challenges;

    // Unique identifier given to each user/device pair
    private int userDeviceID;
    private double allowed_deviations;
    private double authentication_threshold;

    // stores the failed points from the previous authentication
    // -1 if not set
    private double authentication_failed_point_ratio;

    public UserDevicePair(int userDeviceID) {
	this(userDeviceID, new ArrayList<Challenge>());
    }

    public UserDevicePair(int userDeviceID, List<Challenge> challenges) {
	this(userDeviceID, challenges, DEFAULT_ALLOWED_DEVIATIONS, DEFAULT_AUTHENTICATION_THRESHOLD);
    }

    /**
     * all other constructors call this constructor indirectly. Each of the
     * other constructors provides default parameters in some sense.
     * 
     * @param userDeviceID
     * @param challenges
     */
    public UserDevicePair(int userDeviceID, List<Challenge> challenges, double allowed_deviations,
	    double authentication_threshold) {
	this.userDeviceID = userDeviceID;
	this.challenges = challenges;
	this.allowed_deviations = allowed_deviations;
	this.authentication_threshold = authentication_threshold;
	this.authentication_failed_point_ratio = -1;
    }

    // Adds challenge to list of challenges correlating to this user/device pair
    public void addChallenge(Challenge challenge) {
	challenges.add(challenge);
    }

    // gets the challenges for this user, device
    public List<Challenge> getChallenges() {
	return challenges;
    }

    /**
     * true if the new_response_data has a certain percentage of points which
     * fall within the profile for the challenge indicated by challenge_id
     * 
     * The python script which pertains to this is normalDistrib.py. It
     * describes the compairason of Ryan's data against Ryan's data and Ryan's
     * data against Jakes data.
     * 
     * The testPressListVsDistrib() method in the Util file seems to be
     * performing the authentication
     */
    public boolean authenticate(List<Point> new_response_data, int challenge_id) {
	Challenge challenge = get_challenge_index(challenge_id);
	Profile profile = challenge.getProfile();

	return authenticate(new_response_data, profile);
    }

    /**
     * provides for authentication against a profile. This is as opposed to
     * authentication against a challenge
     * 
     * @param new_response_data
     * @param profile
     * @return
     */
    public boolean authenticate(List<Point> new_response_data, Profile profile) {

	// if there are no responses to authenticate against, return false
	if (profile.getNormalizedResponses().size() == 0) {
	    return false;
	}

	// determine the number of failed points
	int failed_points = failed_points(new_response_data, profile, this.allowed_deviations);

	// determine the size of the list
	int list_size = profile.getNormalizedResponses().get(0).getResponse().size();

	// set the failed point ratio
	this.authentication_failed_point_ratio = failed_points / list_size;

	// if the fraction of points that pass is greater than the
	// authentication threshold, then we pass this person
	return ((list_size - failed_points) / list_size) >= this.authentication_threshold;
    }

    /**
     * return the userDeviceId
     */
    public int getUserDeviceId() {
	return this.userDeviceID;
    }

    /**
     * return the number of failed points from the previous authentication.
     * Return -1 if there is not previous authentication.
     * 
     * @return
     */
    public double failedPointRatio() {
	return this.authentication_failed_point_ratio;
    }

    /**
     * return the challenge with the given challenge_id
     * 
     * @param new_response
     * @param challenge_profile
     * @param allowed_deviations
     * @return
     */
    private Challenge get_challenge_index(int challenge_id) {
	for (Challenge challenge : challenges) {
	    if (challenge.getChallengeID() == challenge_id) {
		return challenge;
	    }
	}

	return null;
    }

    /*
     * calculate the number of points in the new response which fall outside of
     * number_standard_deviations of mean
     */
    private int failed_points(List<Point> new_response, Profile profile, double allowed_deviations) {
	int points = 0;

	// get the mu, sigma values from the profile
	List<Double> pressure_mu_values = profile.getPressureMuSigmaValues().getMuValues();
	List<Double> pressure_sigma_values = profile.getPressureMuSigmaValues().getSigmaValues();

	// normalize the response
	Response response_object = new Response(new_response);
	boolean is_profile_horizontal = is_horizontal(profile.getNormalizedResponses().get(0).getResponse());

	response_object.normalize(profile.getNormalizedResponses().get(0).getResponse(), is_profile_horizontal);

	// compare the response to the challenge_profile
	// For each point determine whether or not it falls with in
	// std_deviations
	// for PRESSURE
	for (int i = 0; i < response_object.getResponse().size(); i++) {
	    // determine if this point fails
	    if ((response_object.getResponse().get(i)
		    .getPressure() < (pressure_mu_values.get(i) - pressure_sigma_values.get(i) * allowed_deviations))
		    || (response_object.getResponse().get(i).getPressure() > (pressure_mu_values.get(i)
			    + pressure_sigma_values.get(i) * allowed_deviations))) {
		// point fails
		points++;
	    }
	}

	return points;
    }

    /**
     * determine if the list of points given is more horizontal or more vertical
     * 
     * @return true if the list is more horizontal
     * @return false if thel ist is more vertical
     */
    private boolean is_horizontal(List<Point> point_list) {
	int x_dist = 0;
	int y_dist = 0;

	// calculate x_dist and y_dist covered by the list
	Point prev_challenge_point = null;

	for (Point challenge_point : point_list) {
	    if (prev_challenge_point == null) {
		prev_challenge_point = challenge_point;
		continue;
	    }

	    // compute the distance between the current point and the previous
	    // point
	    x_dist += Math.abs(challenge_point.getX() - prev_challenge_point.getX());
	    y_dist += Math.abs(challenge_point.getY() - prev_challenge_point.getY());

	    prev_challenge_point = challenge_point;
	}

	return x_dist > y_dist;
    }
}
