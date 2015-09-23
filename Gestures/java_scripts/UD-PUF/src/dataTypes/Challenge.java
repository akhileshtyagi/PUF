package dataTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents challenge which contains the pattern of points for the
 * corresponding challenge, responses that a user will create for said
 * challenge, and that user's profile which will be correlated with this
 * challenge
 */
public class Challenge {
    // number of elements in normalized list.
    final int NORMALIZED_ELEMENTS = 32;

    // Pattern of points that create the challenge
    private List<Point> challengePattern;

    // pattern of points to normalize responses to
    List<Point> normalizingPoints;

    // Unique identifier to distinguish which challenge this is
    private int challengeID;

    // List of NORMALIZED responses, each representing one response by the user
    // for this challenge
    private List<Response> responses;

    private boolean isChallengeHorizontal;

    // Profile associated with this challenge
    private Profile profile;

    public Challenge(List<Point> challengePattern, int challengeID) {
	this.challengePattern = challengePattern;
	this.challengeID = challengeID;
	responses = new ArrayList<Response>();
	profile = new Profile();

	// determine if the challenge is more horizontal or more vertical in
	// oreantation
	double x_dist = computeChallengeXDistance();
	double y_dist = computeChallengeYDistance();
	isChallengeHorizontal = x_dist > y_dist;

	// compute the list of points used to normalize the responses to this
	// challenge
	normalizingPoints = computeNormalizingPoints(x_dist, y_dist);
    }

    // Adds normalized response to the list or Responses
    public void addResponse(Response response) {
	// normlaize the response before it is added to the challenge
	response.normalize(normalizingPoints, isChallengeHorizontal);

	responses.add(response);
    }

    // Creates a profile associate with this challenge with NORMALIZED responses
    public Profile createPofile() {
	profile = new Profile(responses);
	return profile;
    }

    public Profile getProfile() {
	return profile;
    }

    /**
     * computes a list of points to be used in normalization of responses
     */
    private List<Point> computeNormalizingPoints(double x_dist, double y_dist) {
	List<Point> x_y_list;

	if (isChallengeHorizontal) {
	    // normalize with respect to x
	    x_y_list = computeHorizontalPointsAlongChallenge(x_dist);
	} else {
	    // normalize with respect to y
	    x_y_list = computeVerticalPointsAlongChallenge(y_dist);
	}

	return x_y_list;
    }

    /**
     * return a list of NORMALIZED_ELEMENTS number of points horizontally along
     * the challenge. We will normalize with respect to these points.
     */
    private List<Point> computeHorizontalPointsAlongChallenge(double x_dist) {
	ArrayList<Point> points = new ArrayList<Point>();

	// amount of x separation for each point
	double point_increment = x_dist / NORMALIZED_ELEMENTS;
	Point next_point = null;

	for (int i = 0; i < NORMALIZED_ELEMENTS; i++) {
	    // do something different for the first iteration
	    if (points.size() == 0) {
		// use the x,y values of the first point
		next_point = new Point(this.challengePattern.get(0));
	    } else {
		// add point increment to the x value while staying at the same
		// y value
		next_point = new Point(points.get(i - 1).getX() + point_increment, points.get(i - 1).getY(),
			points.get(i - 1).getPressure());
	    }

	    points.add(next_point);
	}

	return points;

    }

    /**
     * return a list of NORMALIZED_ELEMENTS number of points vertically along
     * the challenge. We will normalize with respect to these points.
     */
    private List<Point> computeVerticalPointsAlongChallenge(double y_dist) {
	ArrayList<Point> points = new ArrayList<Point>();

	// amount of y separation for each point
	double point_increment = y_dist / NORMALIZED_ELEMENTS;
	Point next_point = null;

	for (int i = 0; i < NORMALIZED_ELEMENTS; i++) {
	    // do something different for the first iteration
	    if (points.size() == 0) {
		// use the x,y values of the first point
		next_point = new Point(this.challengePattern.get(0));
	    } else {
		// add point increment to the y value while staying at the same
		// x value
		next_point = new Point(points.get(i - 1).getX(), points.get(i - 1).getY() + point_increment,
			points.get(i - 1).getPressure());
	    }

	    points.add(next_point);
	}

	return points;
    }

    /**
     * computes the y distance covered by this challenge
     * 
     * @param response
     * @return
     */
    private double computeChallengeYDistance() {
	double y_dist = 0;

	// calculate x_dist and y_dist covered by the list
	Point prev_challenge_point = null;

	for (Point challenge_point : this.challengePattern) {
	    if (prev_challenge_point == null) {
		prev_challenge_point = challenge_point;
		continue;
	    }

	    // compute the distance between the current point and the previous
	    // point
	    y_dist += Math.abs(challenge_point.getY() - prev_challenge_point.getY());

	    prev_challenge_point = challenge_point;
	}

	return y_dist;
    }

    /**
     * computes the x distance covered by this challenge
     * 
     * @param response
     * @return
     */
    private double computeChallengeXDistance() {
	double x_dist = 0;

	// calculate x_dist and y_dist covered by the list
	Point prev_challenge_point = null;

	for (Point challenge_point : this.challengePattern) {
	    if (prev_challenge_point == null) {
		prev_challenge_point = challenge_point;
		continue;
	    }

	    // compute the distance between the current point and the previous
	    // point
	    x_dist += Math.abs(challenge_point.getX() - prev_challenge_point.getX());

	    prev_challenge_point = challenge_point;
	}

	return x_dist;
    }
}