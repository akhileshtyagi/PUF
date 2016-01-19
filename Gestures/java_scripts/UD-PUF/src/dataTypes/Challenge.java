package dataTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents challenge which contains the pattern of points for the
 * corresponding challenge, responses that a user will create for said
 * challenge, and that user's profile which will be correlated with this
 * challenge
 */
public class Challenge implements Serializable {
    // number of elements in normalized list default value.
    final static int NORMALIZED_ELEMENTS_DEFAULT = 16;

    // threshold for determining if response has enough motion events
    final static int NORMALIZED_THRESHOLD = 20;

    // number of elements in the normalized list
    private int normalized_elements;

    // a list of number of points in each response added to the challenge
    // (used for computing mu/sigma of number of MotionEvents
    private ArrayList<Double> motion_event_counts;

    // Pattern of points that create the challenge
    private List<Point> challengePattern;

    // pattern of points to normalize responses to
    List<Point> normalizingPoints;

    // Unique identifier to distinguish which challenge this is
    private long challengeID;

    // List of NORMALIZED responses, each representing one response by the user
    // for this challenge
    private List<Response> responses;

    private boolean isChallengeHorizontal;

    // Profile associated with this challenge
    private Profile profile;

    // time_length values for the responses
    private ArrayList<Double> time_lengths;

    // changes
    public Challenge(List<Point> challengePattern, long challengeID) {
        this(challengePattern, challengeID, NORMALIZED_ELEMENTS_DEFAULT);
    }

    /**
     * allows for parameterization of the number of normalized elements
     *
     * @param challengePattern
     * @param challengeID
     * @param normalizationPoints
     */
    public Challenge(List<Point> challengePattern, long challengeID, int normalizationPoints) {
        this.challengePattern = challengePattern;
        this.challengeID = challengeID;
        this.normalized_elements = normalizationPoints;

        responses = new ArrayList<Response>();
        this.time_lengths = new ArrayList<Double>();
        profile = null;
        motion_event_counts = new ArrayList<Double>();
    }

    // Adds normalized response to the list or Responses
    public void addResponse(Response response) {
        // add the time length of the response to the list
        this.time_lengths.add(response.getTimeLength());

        // If first response added, use it as baseline for number of
        // normalization points,
        // then calculated normalizing points with
        if (responses.size() <= 0) {
            this.normalized_elements = response.getOrigionalResponse().size();

            // TODO move this back to constructor
            // determine if the challenge is more horizontal or more vertical in
            // orientation
            double x_dist = computeChallengeXDistance();
            double y_dist = computeChallengeYDistance();

            // System.out.println("X:" + x_dist + " Y:" + y_dist);

            isChallengeHorizontal = x_dist > y_dist;

            // compute the list of points used to normalize the responses to
            // this
            // challenge
            // normalizingPoints = computeNormalizingPoints(x_dist, y_dist);

            // euclidean distance
            this.normalizingPoints = computeNormalizingPoints(response);
        }

        // before normalizing response, add length of the response to list of
        // motion_event_counts
        motion_event_counts.add(new Double(response.getMotionEvenCount()));

        // normalize the response before it is added to the challenge
        // System.out.println(isChallengeHorizontal);
        response.normalize(normalizingPoints, isChallengeHorizontal);

        responses.add(response);

        // profile is now invalid and needs to be re-computed
        this.profile = null;
    }

    // Creates a profile associate with this challenge with NORMALIZED responses
    public Profile getProfile() {
        // if the profile hasn't been created, create the profile
        if (profile == null) {
            // all properties of the points are computed when this is created
            profile = new Profile(responses, time_lengths, motion_event_counts);
        }

        return profile;
    }

    public int getNormalizedElementsCount() {
        return normalized_elements;
    }

    public void setChallengeID(long challengeID) {
        this.challengeID = challengeID;
    }

    public long getChallengeID() {
        return challengeID;
    }

    public List<Double> getmotion_event_counts() {
        return motion_event_counts;
    }

    public List<Point> getChallengePattern() {
        return challengePattern;
    }

    public List<Response> getResponsePattern() {
        return responses;
    }

    public boolean isHorizontal() {
        return isChallengeHorizontal;
    }

    /**
     * compute list of normalizing points using euclidean distance.
     * this is based on the first response points.
     */
    private List<Point> computeNormalizingPoints(Response response) {
        List<Point> norm_points = new ArrayList<Point>();
        List<Point> response_points = response.getOrigionalResponse();

        // compute the distance between each normalizing point ( N-1 segments to split d into)
        double distance = computeResponseLength(response_points) / (response_points.size() - 1);

        // first point in the list is the first point in the response
        norm_points.add(response_points.get(0));

        // choose all the points in the middle (N-2) of them
        // for each norma
        // normalization point to findc
        // i is normalization points
        // j is response points
        //double prev_remaining_distance = 0;
        //double right_neighbor_distance = computeEuclideanDistance(norm_points.get(0), response_points.get(1));
        int j = 1;
        for (int i = 0; i < response_points.size() - 2; i++) {
            // k keeps track of the number of indexs the next point is away from the current point
            int k = 0;

            // add prev_remaining_distance to distance to avoid having the distance from
            // left neighbor to the previous normalization point be double counted
            double remaining_distance = distance; //- right_neighbor_distance; //+ prev_remaining_distance;

            // determine the closest left neighbor (j + k - 1) and
            // the distance of the normalization point from this neighbor

//            while (computeEuclideanDistance(response_points.get(j - 1), response_points.get(j + k)) < distance) {
//                remaining_distance -= computeEuclideanDistance(response_points.get(j + k - 1), response_points.get(j + k));
//                k++;
//            }

            // TODO figure out a way to subtract the additional distance resulting from adding an additional point
            while (computeEuclideanDistance(norm_points.get(i), response_points.get(j + k)) < distance) {
                // if it is the first point, take the differance from the previous normalization point
                if(k == 0) {
                    remaining_distance -= computeEuclideanDistance(norm_points.get(i), response_points.get(j));
                } else {
                    remaining_distance -= computeEuclideanDistance(norm_points.get(j + k - 1), response_points.get(j + k));
                }

                k++;
            }

            // keep track of remaining distance
            // prev_remaining_distance = remaining_distance;

            // now we know the point closes to the left of the normalization point in the response.
            j += k;

            // TODO we are not finding the left neighbor correctly,
            // TODO we are double counting the distance between the left neighbor and the normalized point
            //System.out.print("left point:" + ( j-1 ) + "\trem_dist:");
            //System.out.println(remaining_distance);

            // now normalization point is between j and j-1
            double theta = Math.atan((response_points.get(j).getY() - response_points.get(j - 1).getY()) /
                    (response_points.get(j).getX() - response_points.get(j - 1).getX()));

            // compute the appropriate x,y coordinates for the point
            int norm_point_x = (int) (response_points.get(j - 1).getX() + remaining_distance * Math.cos(theta));
            int norm_point_y = (int) (response_points.get(j - 1).getY() + remaining_distance * Math.sin(theta));

            // at the point to the normalization points list
            norm_points.add(new Point(norm_point_x, norm_point_y, 0));

            // keep track of remaining distance to right neighbor
            //right_neighbor_distance = computeEuclideanDistance(norm_points.get(i + 1), response_points.get(j));
        }

        // last point in the list is the last point in the response
        norm_points.add(response_points.get(response_points.size() - 1));

        //System.out.println(response_points);
        //System.out.println(norm_points);

        return norm_points;
    }

    /**
     * compute the euclidean length of a point list
     */
    private double computeResponseLength(List<Point> points) {
        double length = 0.0;

        for (int i = 1; i < points.size(); i++) {
            length += computeEuclideanDistance(points.get(i), points.get(i - 1));
        }

        return length;
    }

    /**
     * compute the euclidean distance between two points
     */
    private double computeEuclideanDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow((p1.getX() - p2.getX()), 2) +
                Math.pow((p1.getY() - p2.getY()), 2));
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
        double point_increment = x_dist / (this.normalized_elements - 1);
        Point next_point = null;
        Point min_challenge_point = min_challenge_point_x();

        for (int i = 0; i < this.normalized_elements; i++) {
            // do something different for the first iteration
            if (points.size() == 0) {
                // use the x,y values of the first point
                next_point = new Point(min_challenge_point);
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
        double point_increment = y_dist / (this.normalized_elements - 1);
        Point next_point = null;
        Point min_challenge_point = min_challenge_point_y();

        for (int i = 0; i < this.normalized_elements; i++) {
            // do something different for the first iteration
            if (points.size() == 0) {
                // use the x,y values of the minimum point
                next_point = new Point(min_challenge_point);
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
     * @return
     */
    private double computeChallengeYDistance() {
        if (challengePattern == null) {
            return 0.0;
        }

        Point min = this.challengePattern.get(0);
        Point max = this.challengePattern.get(0);

        for (Point challenge_point : this.challengePattern) {
            if (challenge_point.getY() < min.getY()) {
                min = challenge_point;
            }

            if (challenge_point.getY() > max.getY()) {
                max = challenge_point;
            }
        }

        return max.getY() - min.getY();
    }

    /**
     * computes the x distance covered by this challenge
     *
     * @return
     */
    private double computeChallengeXDistance() {
        if (challengePattern == null) {
            return 0.0;
        }

        Point min = this.challengePattern.get(0);
        Point max = this.challengePattern.get(0);

        for (Point challenge_point : this.challengePattern) {
            if (challenge_point.getX() < min.getX()) {
                min = challenge_point;
            }

            if (challenge_point.getX() > max.getX()) {
                max = challenge_point;
            }
        }

        return max.getX() - min.getX();
    }

    /**
     * get the max challenge point
     */
    private Point max_challenge_point_x() {
        if (challengePattern == null) {
            return null;
        }

        Point min = this.challengePattern.get(0);
        Point max = this.challengePattern.get(0);

        for (Point challenge_point : this.challengePattern) {
            if (challenge_point.getX() > max.getX()) {
                max = challenge_point;
            }
        }

        return max;
    }

    /**
     * get the min challenge point
     */
    private Point min_challenge_point_x() {
        if (challengePattern == null) {
            return null;
        }

        Point min = this.challengePattern.get(0);

        for (Point challenge_point : this.challengePattern) {
            if (challenge_point.getX() < min.getX()) {
                min = challenge_point;
            }
        }

        return min;
    }

    /**
     * get the max challenge point
     */
    private Point max_challenge_point_y() {
        if (challengePattern == null) {
            return null;
        }

        Point max = this.challengePattern.get(0);

        for (Point challenge_point : this.challengePattern) {
            if (challenge_point.getY() > max.getY()) {
                max = challenge_point;
            }
        }

        return max;
    }

    /**
     * get the min challenge point
     */
    private Point min_challenge_point_y() {
        if (challengePattern == null) {
            return null;
        }

        Point min = this.challengePattern.get(0);

        for (Point challenge_point : this.challengePattern) {
            if (challenge_point.getY() < min.getY()) {
                min = challenge_point;
            }
        }

        return min;
    }
}