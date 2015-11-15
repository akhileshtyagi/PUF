package dataTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user/device pair, containing a unique identifier and a list of
 * all challenges correlating to that user
 */
public class UserDevicePair {
    public final static double PRESSURE_DEFAULT_ALLOWED_DEVIATIONS = 3.0;
    public final static double DISTANCE_DEFAULT_ALLOWED_DEVIATIONS = 2.0;
    public final static double TIME_DEFAULT_ALLOWED_DEVIATIONS = 2.0;
    public final static double DEFAULT_AUTHENTICATION_THRESHOLD = 0.75;

    public enum RatioType {
	PRESSURE, DISTANCE, TIME
    }

    // List of challenges correlating to this user/device pair
    private List<Challenge> challenges;

    // Unique identifier given to each user/device pair
    private int userDeviceID;
    private double pressure_allowed_deviations;
    private double distance_allowed_deviations;
    private double time_allowed_deviations;
    private double authentication_threshold;

    // stores the failed points from the previous authentication
    // -1 if not set
    private double pressure_authentication_failed_point_ratio;
    private double distance_authentication_failed_point_ratio;
    private double time_authentication_failed_point_ratio;

    public UserDevicePair(int userDeviceID) {
	this(userDeviceID, new ArrayList<Challenge>());
    }

    public UserDevicePair(int userDeviceID, List<Challenge> challenges) {
	this(userDeviceID, challenges, PRESSURE_DEFAULT_ALLOWED_DEVIATIONS, DISTANCE_DEFAULT_ALLOWED_DEVIATIONS,
		TIME_DEFAULT_ALLOWED_DEVIATIONS, DEFAULT_AUTHENTICATION_THRESHOLD);
    }

    public UserDevicePair(int userDeviceID, double pressure_allowed_deviations, double distance_allowed_deviations,
	    double time_allowed_deviations, double authentication_threshold) {
	this(userDeviceID, new ArrayList<Challenge>(), pressure_allowed_deviations, distance_allowed_deviations,
		time_allowed_deviations, authentication_threshold);
    }

    /**
     * all other constructors call this constructor indirectly. Each of the
     * other constructors provides default parameters in some sense.
     * 
     * @param userDeviceID
     * @param challenges
     */
    public UserDevicePair(int userDeviceID, List<Challenge> challenges, double pressure_allowed_deviations,
	    double distance_allowed_deviations, double time_allowed_deviations, double authentication_threshold) {
	this.userDeviceID = userDeviceID;
	this.challenges = challenges;
	this.pressure_allowed_deviations = pressure_allowed_deviations;
	this.distance_allowed_deviations = distance_allowed_deviations;
	this.time_allowed_deviations = time_allowed_deviations;
	this.authentication_threshold = authentication_threshold;
	this.pressure_authentication_failed_point_ratio = -1;
	this.distance_authentication_failed_point_ratio = -1;
	this.time_authentication_failed_point_ratio = -1;
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
	int failed_pressure_points = failed_pressure_points(new_response_data, profile,
		this.pressure_allowed_deviations);
	int failed_distance_points = failed_distance_points(new_response_data, profile,
		this.distance_allowed_deviations);
	int failed_time_points = failed_time_points(new_response_data, profile, this.time_allowed_deviations);

	// determine the size of the list
	int list_size = profile.getNormalizedResponses().get(0).getResponse().size();

	// set the failed point ratio for pressure time and distance
	this.pressure_authentication_failed_point_ratio = ((double) failed_pressure_points) / list_size;
	this.distance_authentication_failed_point_ratio = ((double) failed_distance_points) / list_size;
	this.time_authentication_failed_point_ratio = ((double) failed_time_points) / list_size;

	// if the fraction of points that pass is greater than the
	// authentication threshold, then we pass this person
	return authenticatePreticate(this.pressure_authentication_failed_point_ratio,
		this.distance_authentication_failed_point_ratio, this.time_authentication_failed_point_ratio);
    }

    /**
     * preticate used to combine the failed point ratios. Returns true if the
     * device passes. preticate: (pressure) or (distance and time)
     */
    private boolean authenticatePreticate(double pressure_failed_point_ratio, double distance_failed_point_ratio,
	    double time_failed_point_ratio) {
	boolean pressure_pass = (1 - pressure_failed_point_ratio) > this.authentication_threshold;
	boolean distance_pass = (1 - distance_failed_point_ratio) > this.authentication_threshold;
	boolean time_pass = (1 - time_failed_point_ratio) > this.authentication_threshold;

	return pressure_pass || (distance_pass && time_pass);
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
     * failed ratio is [failed points / total points] in the authentication
     * 
     * @return
     */
    public double failedPointRatio(RatioType type) {
	double failed_ratio = -1;

	// return a failed point ratio dependtant on the ratio type
	switch (type) {
	case PRESSURE:
	    failed_ratio = this.pressure_authentication_failed_point_ratio;
	    break;

	case DISTANCE:
	    failed_ratio = this.distance_authentication_failed_point_ratio;
	    break;

	case TIME:
	    failed_ratio = this.time_authentication_failed_point_ratio;
	    break;
	}

	return failed_ratio;
    }

    /**
     * allow the number of std deviations from the mean allowed in the
     * authentication to be set.
     */
    public void setStandardDeviations(RatioType type, double standard_deviations) {
	// set dependtant on type
	switch (type) {
	case PRESSURE:
	    this.pressure_allowed_deviations = standard_deviations;
	    break;

	case DISTANCE:
	    this.distance_allowed_deviations = standard_deviations;
	    break;

	case TIME:
	    this.time_allowed_deviations = standard_deviations;
	    break;
	}
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

	// get the number of failed pressure points
	points += failed_pressure_points(new_response, profile, allowed_deviations);

	return points;
    }

    /**
     * Computes the number of pressure points which have failed authentication
     */
    private int failed_pressure_points(List<Point> new_response, Profile profile, double allowed_deviations) {
	int points = 0;

	// get the mu, sigma values from the profile
	List<Double> mu_values = profile.getPressureMuSigmaValues().getMuValues();
	List<Double> sigma_values = profile.getPressureMuSigmaValues().getSigmaValues();
	List<Double> point_values = new ArrayList<Double>();

	// normalize the response
	Response response_object = new Response(new_response);
	boolean is_profile_horizontal = is_horizontal(profile.getNormalizedResponses().get(0).getResponse());

	response_object.normalize(profile.getNormalizedResponses().get(0).getResponse(), is_profile_horizontal);

	// create a list of point values for pressure
	for (Point response_point : response_object.getResponse()) {
	    point_values.add(response_point.getPressure());
	}

	points = failed_points(mu_values, sigma_values, point_values, allowed_deviations);

	return points;
    }

    /**
     * Computes the number of distance points which have failed authentication
     */
    private int failed_distance_points(List<Point> new_response, Profile profile, double allowed_deviations) {
	int points = 0;

	// get the mu, sigma values from the profile
	List<Double> mu_values = profile.getPointDistanceMuSigmaValues().getMuValues();
	List<Double> sigma_values = profile.getPointDistanceMuSigmaValues().getSigmaValues();
	List<Double> point_values = new ArrayList<Double>();

	// normalize the response
	Response response_object = new Response(new_response);
	boolean is_profile_horizontal = is_horizontal(profile.getNormalizedResponses().get(0).getResponse());

	response_object.normalize(profile.getNormalizedResponses().get(0).getResponse(), is_profile_horizontal);

	// create a list of point values for distance
	for (Point response_point : response_object.getResponse()) {
	    point_values.add(response_point.getDistance());
	}

	points = failed_points(mu_values, sigma_values, point_values, allowed_deviations);

	return points;
    }

    /**
     * Computes the number of time points which have failed authentication
     */
    private int failed_time_points(List<Point> new_response, Profile profile, double allowed_deviations) {
	int points = 0;

	// get the mu, sigma values from the profile
	List<Double> mu_values = profile.getTimeDistanceMuSigmaValues().getMuValues();
	List<Double> sigma_values = profile.getTimeDistanceMuSigmaValues().getSigmaValues();
	List<Double> point_values = new ArrayList<Double>();

	// normalize the response
	Response response_object = new Response(new_response);
	boolean is_profile_horizontal = is_horizontal(profile.getNormalizedResponses().get(0).getResponse());

	response_object.normalize(profile.getNormalizedResponses().get(0).getResponse(), is_profile_horizontal);

	// create a list of point values for time
	for (Point response_point : response_object.getResponse()) {
	    point_values.add(response_point.getTime());
	}

	points = failed_points(mu_values, sigma_values, point_values, allowed_deviations);

	return points;
    }

    /**
     * return the number of failed points in the given mu, sigma band Takes in
     * mu_values, sigma_values, normalized list of points from response
     */
    private int failed_points(List<Double> mu_values, List<Double> sigma_values, List<Double> point_values,
	    double allowed_deviations) {
	int points = 0;

	// compare the response to the challenge_profile
	// For each point determine whether or not it falls with in
	// std_deviations
	// for PRESSURE
	for (int i = 0; i < point_values.size(); i++) {
	    // determine if this point fails
	    if ((point_values.get(i) < (mu_values.get(i) - sigma_values.get(i) * allowed_deviations))
		    || (point_values.get(i) > (mu_values.get(i) + sigma_values.get(i) * allowed_deviations))) {
		// System.out.println("point value \ mu_value: " +
		// normalized_response_points.get(i). + "\n");
		// point fails
		points++;
	    }
	}

	return points;
    }

    /**
     * takes two doubles to see if they are roughly equivilent
     * 
     * true if a and b are within a percent differance of one another
     */
    private boolean within_episilon(double a, double b) {
	return (a - b) < Math.ulp(a);
    }

    /**
     * determine if the list of points given is more horizontal or more vertical
     * 
     * @return true if the list is more horizontal
     * @return false if the list is more vertical
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

    /**
     * TEST METHODS from here to the end. These will be REMOVED eventually.
     */
    /**
     * This method returns a string with a lot of information
     */
    public String information_dump_authenticate(List<Point> new_response_data, Profile profile) {
	String information = "";

	// gather information about the authentication in general
	information += "pressure_allowed_deviations: " + this.pressure_allowed_deviations + "\n";
	information += "distance_allowed_deviations: " + this.distance_allowed_deviations + "\n";
	information += "time_allowed_deviations: " + this.time_allowed_deviations + "\n";
	information += "authentication_threshold: " + this.authentication_threshold + "\n";

	// gather information about this specific authentication
	// how does authentication behave as a whole
	information += "authenticated: " + this.authenticate(new_response_data, profile) + "\n";

	// how do specific aspects of authentication behave
	int pressure_failed_points = this.failed_pressure_points(new_response_data, profile,
		this.pressure_allowed_deviations);
	int distance_failed_points = this.failed_distance_points(new_response_data, profile,
		this.distance_allowed_deviations);
	int time_failed_points = this.failed_time_points(new_response_data, profile, this.time_allowed_deviations);
	int list_size = profile.getNormalizedResponses().get(0).getResponse().size();

	information += "pressure_failed_points: " + pressure_failed_points + "\n";
	information += "distance_failed_points: " + distance_failed_points + "\n";
	information += "time_failed_points: " + time_failed_points + "\n";

	// derived metrics
	information += "pressure_failed_points_ratio: " + ((double) pressure_failed_points) / list_size + "\n";
	information += "distance_failed_points_ratio: " + ((double) distance_failed_points) / list_size + "\n";
	information += "time_failed_points_ratio: " + ((double) time_failed_points) / list_size + "\n";

	// put a vertical space before the next segment which prints out lists
	information += "\n";

	// print lists used in authetnication
	MuSigma pressure_mu_sigma = profile.getPressureMuSigmaValues();
	MuSigma distance_mu_sigma = profile.getPointDistanceMuSigmaValues();
	MuSigma time_mu_sigma = profile.getTimeDistanceMuSigmaValues();

	information += "Profile pressure_mu_values: " + pressure_mu_sigma.getMuValues() + "\n";
	information += "Profile pressure_sigma_values: " + pressure_mu_sigma.getSigmaValues() + "\n";

	information += "Profile distance_mu_values: " + distance_mu_sigma.getMuValues() + "\n";
	information += "Profile distance_sigma_values: " + distance_mu_sigma.getSigmaValues() + "\n";

	information += "Profile time_mu_values: " + time_mu_sigma.getMuValues() + "\n";
	information += "Profile time_sigma_values: " + time_mu_sigma.getSigmaValues() + "\n";

	// print the pre/post normalized response data
	information += "respones_points: " + new_response_data + "\n";

	// normalize the response
	Response response_object = new Response(new_response_data);
	boolean is_profile_horizontal = is_horizontal(profile.getNormalizedResponses().get(0).getResponse());
	response_object.normalize(profile.getNormalizedResponses().get(0).getResponse(), is_profile_horizontal);

	information += "normalized_response_points: " + response_object.getResponse() + "\n";

	return information;
    }
}
