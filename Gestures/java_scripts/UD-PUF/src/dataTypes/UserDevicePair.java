package dataTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user/device pair, containing a unique identifier and a list of
 * all challenges correlating to that user
 */
public class UserDevicePair {
    final double ALLOWED_DEVIATIONS = 1.0;
    final double AUTHENTICATION_THRESHOLD = 0.75;

    // List of challenges correlating to this user/device pair
    private List<Challenge> challenges;

    // Unique identifier given to each user/device pair
    private int userDeviceID;

    public UserDevicePair(int userDeviceID) {
	this(userDeviceID, new ArrayList<Challenge>());
    }

    public UserDevicePair(int userDeviceID, List<Challenge> challenges) {
	this.challenges = challenges;
	this.userDeviceID = userDeviceID;
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

	// if there are no responses to authenticate against, return false
	if (challenge.getProfile().getNormalizedResponses().size() == 0) {
	    return false;
	}

	// determine the number of failed points
	int failed_points = failed_points(new_response_data, challenge, ALLOWED_DEVIATIONS);

	// determine the size of the list
	int list_size = challenge.getProfile().getNormalizedResponses().get(0).getResponse().size();

	// if the fraction of points that pass is greater than the
	// authentication threshold, then we pass this person
	return ((list_size - failed_points) / list_size) >= AUTHENTICATION_THRESHOLD;
    }

    /**
     * return the userDeviceId
     */
    public int getUserDeviceId() {
	return this.userDeviceID;
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
    private int failed_points(List<Point> new_response, Challenge challenge, double allowed_deviations) {
	int points = 0;

	// get the mu, sigma values from the profile
	List<Double> mu_values = challenge.getProfile().getMuSigmaValues().getMuValues();
	List<Double> sigma_values = challenge.getProfile().getMuSigmaValues().getSigmaValues();

	// normalize the response
	Response response_object = new Response(new_response);
	response_object.normalize(challenge.getProfile().getNormalizedResponses().get(0).getResponse(),
		challenge.isHorizontal());

	// compare the response to the challenge_profile
	// For each point determine whether or not it falls with in
	// std_deviations
	for (int i = 0; i < response_object.getResponse().size(); i++) {
	    // determine if this point fails
	    if ((response_object.getResponse().get(i)
		    .getPressure() < (mu_values.get(i) - sigma_values.get(i) * allowed_deviations))
		    || (response_object.getResponse().get(i)
			    .getPressure() > (mu_values.get(i) + sigma_values.get(i) * allowed_deviations))) {
		// point fails
		points++;
	    }
	}

	return points;
    }
}
