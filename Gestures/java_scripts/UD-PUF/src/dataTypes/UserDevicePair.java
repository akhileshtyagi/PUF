package dataTypes;

//import javafx.beans.binding.StringBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user/device pair, containing a unique identifier and a list of
 * all challenges correlating to that user
 */
public class UserDevicePair {
    public final static double PRESSURE_DEFAULT_ALLOWED_DEVIATIONS = .89;
    public final static double DISTANCE_DEFAULT_ALLOWED_DEVIATIONS = 1.11;
    public final static double TIME_DEFAULT_ALLOWED_DEVIATIONS = .415;
    public final static double TIME_LENGTH_DEFAULT_ALLOWED_DEVIATIONS = 2.0;
    public final static double PRESSURE_DEFAULT_AUTHENTICATION_THRESHOLD = 0.4;
    public final static double DISTANCE_DEFAULT_AUTHENTICATION_THRESHOLD = 0.9;
    public final static double TIME_DEFAULT_AUTHENTICATION_THRESHOLD = 0.7;

    public enum RatioType {
        PRESSURE, DISTANCE, TIME, TIME_LENGTH
    }

    public enum AuthenticationPredicate {
        PRESSURE, NO_PRESSURE, TIME, DISTANCE, TIME_LENGTH, TIME_OR_DISTANCE, PRESSURE_OR_TIME, PRESSURE_OR_DISTANCE_AND_TIME, PRESSURE_OR_DISTANCE;
    }

    // determine what type of predicate to authenticate with
    public final static AuthenticationPredicate AUTHENTICATION_PREDICATE = AuthenticationPredicate.PRESSURE_OR_DISTANCE;

    // List of challenges correlating to this user/device pair
    private List<Challenge> challenges;

    // Unique identifier given to each user/device pair
    private int userDeviceID;
    private double pressure_allowed_deviations;
    private double distance_allowed_deviations;
    private double time_allowed_deviations;
    private double time_length_allowed_deviations;

    private double pressure_authentication_threshold;
    private double distance_authentication_threshold;
    private double time_authentication_threshold;

    // stores the failed points from the previous authentication
    private double pressure_authentication_failed_point_ratio;
    private double distance_authentication_failed_point_ratio;
    private double time_authentication_failed_point_ratio;

    // Confidence Interval for the most recent authenticating response
    private double new_response_confidence_interval;

    // Point vectors for most resent authentication
    private List<Double> pressure_point_vector;
    private List<Double> distance_point_vector;
    private List<Double> time_point_vector;

    public UserDevicePair(int userDeviceID) {
        this(userDeviceID, new ArrayList<Challenge>());
    }

    public UserDevicePair(int userDeviceID, List<Challenge> challenges) {
        this(userDeviceID, challenges, PRESSURE_DEFAULT_ALLOWED_DEVIATIONS, DISTANCE_DEFAULT_ALLOWED_DEVIATIONS,
                TIME_DEFAULT_ALLOWED_DEVIATIONS, PRESSURE_DEFAULT_AUTHENTICATION_THRESHOLD);
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

        this.pressure_authentication_threshold = authentication_threshold;
        this.distance_authentication_threshold = authentication_threshold;
        this.time_authentication_threshold = authentication_threshold;

        this.pressure_authentication_failed_point_ratio = 1;
        this.distance_authentication_failed_point_ratio = 1;
        this.time_authentication_failed_point_ratio = 1;

        this.time_length_allowed_deviations = TIME_LENGTH_DEFAULT_ALLOWED_DEVIATIONS;
        this.new_response_confidence_interval = -1;

        this.pressure_point_vector = new ArrayList<Double>();
        this.distance_point_vector = new ArrayList<Double>();
        this.time_point_vector = new ArrayList<Double>();
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
     * returns true if the pre-normalization amount of time from the beginning
     * to end of the response is within the given number of std deviations.
     */
    public boolean isWithinTimeLength(List<Point> new_response_data, long challenge_id) {
        Challenge challenge = get_challenge_index(challenge_id);
        Profile profile = challenge.getProfile();

        return isWithinTimeLength(new_response_data, profile);
    }

    /**
     * returns true if the pre-normalization amount of time from the beginning
     * to end of the response is within the given number of std deviations.
     */
    public boolean isWithinTimeLength(List<Point> new_response_data, Profile profile) {
        Response response = new Response(new_response_data);

        if ((response.getTimeLength() < (profile.getTimeLengthMu()
                + profile.getTimeLengthSigma() * this.time_length_allowed_deviations))
                && (response.getTimeLength() > (profile.getTimeLengthMu()
                - profile.getTimeLengthSigma() * this.time_length_allowed_deviations))) {
            return true;
        }

        return false;
    }

    /**
     * true if the new_response_data has a certain percentage of points which
     * fall within the profile for the challenge indicated by challenge_id
     * <p>
     * The python script which pertains to this is normalDistrib.py. It
     * describes the compairason of Ryan's data against Ryan's data and Ryan's
     * data against Jakes data.
     * <p>
     * The testPressListVsDistrib() method in the Util file seems to be
     * performing the authentication
     */
    public boolean authenticate(List<Point> new_response_data, long challenge_id) {
        Challenge challenge = get_challenge_index(challenge_id);

        return authenticate(new_response_data, challenge);
    }

    /**
     * provides for authentication against a profile. This is as opposed to
     * authentication against a challenge
     *
     * @param new_response_data
     * @return
     */
    public boolean authenticate(List<Point> new_response_data, Challenge challenge) {
        Profile profile = challenge.getProfile();

        // set a value which represents all points failing
        this.pressure_authentication_failed_point_ratio = 1.0;
        this.distance_authentication_failed_point_ratio = 1.0;
        this.time_authentication_failed_point_ratio = 1.0;

        // normalize the response
        Response response_object = new Response(new_response_data);
        response_object.normalize(challenge.getNormalizingPoints());

        // System.out.println("normalized_response:\t" + response_object.getNormalizedResponse());

        // compute the point vectors
        compute_point_vector(response_object.getNormalizedResponse(), profile);

        // compute confidence interval with normalized points
        new_response_confidence_interval = profile.get_new_response_CI(response_object.getNormalizedResponse());

        // if there are no responses to authenticate against, return false
        if (profile.getNormalizedResponses().size() == 0) {
            System.out.println("no normalized responses");
            return false;
        }

        // if number of points in new response data is not within 3 sigma of
        // mean of
        // MotionEvent objects in profile's challenge, reject it immediately
        if (new_response_data.size() < (profile.getMotionEventCountMu() - (3 * profile.getMotionEventCountSigma()))
                || (new_response_data
                .size() > (profile.getMotionEventCountMu() + (3 * profile.getMotionEventCountSigma())))) {
            // System.out.println(
            // "Mu / Sigma : " + profile.getMotionEventCountMu() + " / " +
            // profile.getMotionEventCountSigma());
            return false;
        }
        // determine the number of failed points
        int failed_pressure_points = failed_pressure_points(new_response_data, profile,
                this.pressure_allowed_deviations);
        int failed_distance_points = failed_distance_points(new_response_data, profile,
                this.distance_allowed_deviations);
        int failed_time_points = failed_time_points(new_response_data, profile, this.time_allowed_deviations);

        // determine the size of the list
        int list_size = profile.getNormalizedResponses().get(0).getNormalizedResponse().size();

        // set the failed point ratio for pressure time and distance
        this.pressure_authentication_failed_point_ratio = ((double) failed_pressure_points) / list_size;
        this.distance_authentication_failed_point_ratio = ((double) failed_distance_points) / list_size;
        this.time_authentication_failed_point_ratio = ((double) failed_time_points) / list_size;

        double response_time_length = new_response_data.get(new_response_data.size() - 1).getTime()
                - new_response_data.get(0).getTime();
        boolean time_length_within_sigma = (Math.abs(profile.getTimeLengthMu()
                - response_time_length) <= (profile.getTimeLengthSigma() * this.time_length_allowed_deviations));

        // if the fraction of points that pass is greater than the
        // authentication threshold, then we pass this person
        return authenticatePreticate(this.pressure_authentication_failed_point_ratio,
                this.distance_authentication_failed_point_ratio, this.time_authentication_failed_point_ratio,
                time_length_within_sigma);
    }

    /**
     * preticate used to combine the failed point ratios. Returns true if the
     * device passes. preticate: (pressure) or (distance and time)
     */
    private boolean authenticatePreticate(double pressure_failed_point_ratio, double distance_failed_point_ratio,
                                          double time_failed_point_ratio, boolean time_length_within_sigma) {
        boolean pass = false;

        boolean pressure_pass = (1 - pressure_failed_point_ratio) > this.pressure_authentication_threshold;
        boolean distance_pass = (1 - distance_failed_point_ratio) > this.distance_authentication_threshold;
        boolean time_pass = (1 - time_failed_point_ratio) > this.time_authentication_threshold;
        boolean time_length_pass = time_length_within_sigma;

        switch (AUTHENTICATION_PREDICATE) {
            case TIME_OR_DISTANCE:
                pass = time_pass || distance_pass;
                break;
            case TIME_LENGTH:
                pass = time_length_pass;
                break;
            case DISTANCE:
                pass = distance_pass;
                break;
            case PRESSURE:
                pass = pressure_pass;
                break;
            case NO_PRESSURE:
                pass = distance_pass && time_pass;
                break;
            case TIME:
                pass = time_pass;
                break;
            case PRESSURE_OR_TIME:
                pass = pressure_pass || time_pass;
                break;
            case PRESSURE_OR_DISTANCE_AND_TIME:
                pass = pressure_pass || (distance_pass && time_pass);
                break;
            case PRESSURE_OR_DISTANCE:
                pass = pressure_pass || distance_pass;
                break;
            default:
                pass = pressure_pass || (distance_pass && time_pass);
                break;
        }

        return pass;
    }

    /**
     * returns the authenticating response's confidence interval
     */
    public double getNew_response_confidence_interval() {
        return new_response_confidence_interval;
    }

    public double getNew_response_pressure_CI(List<Point> new_response_data, Profile profile) {
        // normalize the response
        Response response_object = new Response(new_response_data);
        response_object.normalize(profile.getNormalizedResponses().get(0).getNormalizedResponse());

        // compute confidence interval with normalized points
        return profile.get_auth_pressure_contribution(response_object.getNormalizedResponse());
    }

    public double getNew_response_time_CI(List<Point> new_response_data, Profile profile) {
        // normalize the response
        Response response_object = new Response(new_response_data);
        response_object.normalize(profile.getNormalizedResponses().get(0).getNormalizedResponse());

        // compute confidence interval with normalized points
        return profile.get_auth_time_contribution(response_object.getNormalizedResponse());
    }

    public double getNew_response_distance_CI(List<Point> new_response_data, Profile profile) {
        // normalize the response
        Response response_object = new Response(new_response_data);
        response_object.normalize(profile.getNormalizedResponses().get(0).getNormalizedResponse());

        // compute confidence interval with normalized points
        return profile.get_auth_distance_contribution(response_object.getNormalizedResponse());
    }

    /**
     * return the an array of size N.
     * N = number of points in response
     * each element in the array is abs(profile[i] - response[i])
     */
    public List<Double> getNew_response_point_vector(RatioType type) {
        // return a failed point ratio dependtant on the ratio type
        switch (type) {
            case PRESSURE:
                return pressure_point_vector;

            case DISTANCE:
                return distance_point_vector;

            case TIME:
                return time_point_vector;
        }

        return new ArrayList<Double>();
    }

    /**
     * Prints a dump of all UserDevicePair info:
     *  UD-Pair: Authentication Vectors
     *  Profile: Mu Sigmas (pressure, distance, etc.)
     *  Challenge: Normalizing Points, Responses
     *
     *  @param challenge challenge associated with this UserDivePair
     *  @return String represntation of the info "dump"
     */
    public String dumpUserDevicePairData(Challenge challenge) {
        StringBuilder sb = new StringBuilder();
        Profile profile = challenge.getProfile();


        // Print all vectors for profile
        // Pressure Vector
        sb.append("Profile Vectors\n\n");
        sb.append("Pressure: \n");
        for(int i = 0; i < pressure_point_vector.size(); i++) {
            sb.append("PressureVector[").append(i).append("]: ").append(pressure_point_vector.get(i)).append("\n");
        }
        // Distance Vector
        sb.append("\nDistance: \n");
        for(int i = 0; i < distance_point_vector.size(); i++) {
            sb.append("DistanceVector[").append(i).append("]: ").append(distance_point_vector.get(i)).append("\n");
        }
        // Time Vector
        sb.append("\nTime: \n");
        for(int i = 0; i < time_point_vector.size(); i++) {
            sb.append("TimeVector[").append(i).append("]: ").append(time_point_vector.get(i)).append("\n");
        }

        // Print all MuSigmas from profile
        sb.append("\n\nMu Sigma Values: \n\n");
        //Pressure MuSigma
        sb.append("\nPressure: \n");
        for(int i = 0; i < profile.getPressureMuSigmaValues().getMuValues().size(); i++) {
            sb.append("PressureMu[").append(i).append("]: ").append(profile.getPressureMuSigmaValues().getMuValues().get(i))
                    .append(", PressureSigma[").append(i).append("]: ")
                    .append(profile.getPressureMuSigmaValues().getSigmaValues().get(i)).append("\n");
        }
        //Distance MuSigma
        sb.append("\nDistance: \n");
        for(int i = 0; i < profile.getPointDistanceMuSigmaValues().getMuValues().size(); i++) {
            sb.append("DistanceMu[").append(i).append("]: ").append(profile.getPointDistanceMuSigmaValues().getMuValues().get(i))
                    .append(", DistanceSigma[").append(i).append("]: ")
                    .append(profile.getPointDistanceMuSigmaValues().getSigmaValues().get(i)).append("\n");
        }
        //Time MuSigma
        sb.append("\nTime: \n");
        for(int i = 0; i < profile.getTimeDistanceMuSigmaValues().getMuValues().size(); i++) {
            sb.append("TimeMu[").append(i).append("]: ").append(profile.getTimeDistanceMuSigmaValues().getMuValues().get(i))
                    .append(", TimeSigma[").append(i).append("]: ")
                    .append(profile.getTimeDistanceMuSigmaValues().getSigmaValues().get(i)).append("\n");
        }

        // Print Normalizing Points from challenge
        sb.append("\n\nNormalizing Points: \n\n");
        for(int i = 0; i < challenge.getNormalizingPoints().size(); i++) {
            sb.append("NormalizingPoint[").append(i).append("]: ")
                    .append(challenge.getNormalizingPoints().get(i).toString()).append("\n");
        }

        // Print All Responses that make up profile
        sb.append("\n\n\nResponses:");

        // Iterate over all responses
        for(int i = 0; i < challenge.getResponsePattern().size(); i++) {
            sb.append("Response ").append(i).append(":\n");
            Response tempResponse = challenge.getResponsePattern().get(i);
            for(int j = 0; j < tempResponse.getNormalizedResponse().size(); j++) {
                sb.append("Point[").append(j).append("]: ")
                        .append(tempResponse.getNormalizedResponse().get(j).toString()).append("\n");
            }
            sb.append("\n\n");
        }

        String dump = sb.toString();
        System.out.println(dump);
        return dump;
    }

    /**
     * compute the point vectors based on the new_response_data and the profile
     */
    private void compute_point_vector(List<Point> new_response_data, Profile profile) {
        pressure_point_vector = new ArrayList<Double>();
        distance_point_vector = new ArrayList<Double>();
        time_point_vector = new ArrayList<Double>();

        // error check
        if (new_response_data.size() != profile.getNormalizedResponses().get(0).getNormalizedResponse().size()) {
            System.out.println("shouldn't be here: " + new_response_data.size() + " | " + profile.getNormalizedResponses().get(0).getNormalizedResponse().size());
            return;
        }

        // TEST: print out response and profile data
//        ArrayList<Double> response_pressure = new ArrayList<Double>();
//        ArrayList<Double> response_distance = new ArrayList<Double>();
//        ArrayList<Double> response_time = new ArrayList<Double>();
//
//        for (int i = 0; i < new_response_data.size(); i++) {
//            response_pressure.add(new_response_data.get(i).getPressure());
//            response_distance.add(new_response_data.get(i).getDistance());
//            response_time.add(new_response_data.get(i).getTime());
//        }
//
//        System.out.println("Response Pressure:\t" + response_pressure);
//        System.out.println("Response Distance:\t" + response_distance);
//        System.out.println("Response Time:\t" + response_time);
//        System.out.println();
//
//        System.out.println("Profile Pressure:\t" + profile.getPressureMuSigmaValues().getMuValues());
//        System.out.println("Profile Distance:\t" + profile.getPointDistanceMuSigmaValues().getMuValues());
//        System.out.println("Profile Time:\t" + profile.getTimeDistanceMuSigmaValues().getMuValues());
//        System.out.println();

        // for each point in new_response, take abs(profile[i] - response[i])
        for (int i = 0; i < new_response_data.size(); i++) {
            pressure_point_vector.add(Math.abs(new_response_data.get(i).getPressure() - profile.getPressureMuSigmaValues().getMuValues().get(i)));
            distance_point_vector.add(Math.abs(new_response_data.get(i).getDistance() - profile.getPointDistanceMuSigmaValues().getMuValues().get(i)));
            time_point_vector.add(Math.abs(new_response_data.get(i).getTime() - profile.getTimeDistanceMuSigmaValues().getMuValues().get(i)));
        }

        return;
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
     * <p>
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

            case TIME_LENGTH:
                this.time_length_allowed_deviations = standard_deviations;
                break;
        }
    }

    /**
     * sets the authentication threshold.
     * <p>
     * IF the threshold provide is greater than 1 or less than 0. THAN threshold
     * will be set to the closest of these values.
     */
    public void setAuthenticationThreshold(RatioType type, double threshold) {
        // if the threshold is greater than 1 or less than 0, change it to the
        // closest of these values.
        double new_threshold = (threshold > 1) ? (1.0) : (threshold);
        new_threshold = (threshold < 0) ? (0.0) : (threshold);

        // set dependtant on type
        switch (type) {
            case PRESSURE:
                this.pressure_authentication_threshold = new_threshold;
                break;

            case DISTANCE:
                this.distance_authentication_threshold = new_threshold;
                break;

            case TIME:
                this.time_authentication_threshold = new_threshold;
                break;

            case TIME_LENGTH:
                // do nothing
                break;
        }
    }

    /**
     * return the challenge with the given challenge_id
     *
     * @return
     */
    private Challenge get_challenge_index(long challenge_id) {
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

        response_object.normalize(profile.getNormalizedResponses().get(0).getNormalizedResponse());

        // create a list of point values for pressure
        for (Point response_point : response_object.getNormalizedResponse()) {
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

        response_object.normalize(profile.getNormalizedResponses().get(0).getNormalizedResponse());

        // create a list of point values for distance
        for (Point response_point : response_object.getNormalizedResponse()) {
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

        response_object.normalize(profile.getNormalizedResponses().get(0).getNormalizedResponse());

        // create a list of point values for time
        for (Point response_point : response_object.getNormalizedResponse()) {
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

        int numPoints = (point_values.size() < mu_values.size()) ? point_values.size() : mu_values.size();

        // compare the response to the challenge_profile
        // For each point determine whether or not it falls with in
        // std_deviations
        // for PRESSURE
        for (int i = 0; i < numPoints; i++) {
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
     * <p>
     * true if a and b are within a percent differance of one another
     */
    private boolean within_episilon(double a, double b) {
        return (a - b) < Math.ulp(a);
    }

    /**
     * TEST METHODS from here to the end. These will be REMOVED eventually.
     */
    /**
     * This method returns a string with a lot of information

    public String information_dump_authenticate(List<Point> new_response_data, Profile profile) {
        String information = "";

        // gather information about the authentication in general
        information += "pressure_allowed_deviations: " + this.pressure_allowed_deviations + "\n";
        information += "distance_allowed_deviations: " + this.distance_allowed_deviations + "\n";
        information += "time_allowed_deviations: " + this.time_allowed_deviations + "\n";
        information += "pressure_authentication_threshold: " + this.pressure_authentication_threshold + "\n";

        // gather information about this specific authentication
        // how does authentication behave as a whole
        information += "authenticated: " + new Boolean(this.authenticate(new_response_data, profile)).toString() + "\n";

        // how do specific aspects of authentication behave
        int pressure_failed_points = this.failed_pressure_points(new_response_data, profile,
                this.pressure_allowed_deviations);
        int distance_failed_points = this.failed_distance_points(new_response_data, profile,
                this.distance_allowed_deviations);
        int time_failed_points = this.failed_time_points(new_response_data, profile, this.time_allowed_deviations);
        int list_size = profile.getNormalizedResponses().get(0).getNormalizedResponse().size();

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
        response_object.normalize(profile.getNormalizedResponses().get(0).getNormalizedResponse());

        information += "normalized_response_points: " + response_object.getNormalizedResponse() + "\n";

        return information;
    }
     */
}
