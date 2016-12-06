package dataTypes;

//import javafx.beans.binding.StringBinding;

import metrics.Point_metrics;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user/device pair, containing a unique identifier and a list of
 * all challenges correlating to that user
 */
public class UserDevicePair {
    // group things including:
    // allowed_deviations, authentication_threshold, authentication failed point ratio, point_vector
    private class AuthValues<T> {
        public Point.Metrics metrics_type;

        public T allowed_deviations;
        public T authentication_threshold;
        public T authentication_failed_point_ratio;
        public List<T> point_vector;
    }

    public final static double PRESSURE_DEFAULT_ALLOWED_DEVIATIONS = .89;
    public final static double DISTANCE_DEFAULT_ALLOWED_DEVIATIONS = 1.11;
    public final static double TIME_DEFAULT_ALLOWED_DEVIATIONS = .415;
    public final static double TIME_LENGTH_DEFAULT_ALLOWED_DEVIATIONS = .415;
    public final static double ACCELERATION_DEFAULT_ALLOWED_DEVIATIONS = .415;
    public final static double VELOCITY_LENGTH_DEFAULT_ALLOWED_DEVIATIONS = 2.0;

    public final static double PRESSURE_DEFAULT_AUTHENTICATION_THRESHOLD = 0.4;
    public final static double DISTANCE_DEFAULT_AUTHENTICATION_THRESHOLD = 0.9;
    public final static double TIME_DEFAULT_AUTHENTICATION_THRESHOLD = 0.7;
    public final static double VELOCITY_DEFAULT_AUTHENTICATION_THRESHOLD = 0.7;
    public final static double ACCELERATION_DEFAULT_AUTHENTICATION_THRESHOLD = 0.7;

    //TODO get rid of this... replace usage with Point.Metrics
    public enum RatioType {
        PRESSURE, DISTANCE, TIME, VELOCiTY, ACCELERATION, TIME_LENGTH
    }

    // this set toggles between vector authenticaiton and number of failed points authentication
    public enum AuthenticationType {
        POINT_VECTOR,
        FAILED_POINTS
    }

    // this set of authentications attempts to use the vectors in order to authenticate
    //TODO

    // this is a set of authentication options for testing number of failed points
    public enum AuthenticationPredicate {
        PRESSURE,
        NO_PRESSURE,
        TIME,
        DISTANCE,
        VELOCITY,
        ACCELERATION,
        TIME_LENGTH,
        TIME_OR_DISTANCE,
        PRESSURE_OR_TIME,
        PRESSURE_OR_DISTANCE_AND_TIME,
        PRESSURE_OR_DISTANCE
    }

    // determine what type of predicate to authenticate with
    public final static AuthenticationType AUTHENTICATION_TYPE = AuthenticationType.FAILED_POINTS; //AuthenticationType.POINT_VECTOR;
    public final static AuthenticationPredicate AUTHENTICATION_PREDICATE = AuthenticationPredicate.VELOCITY;

    // List of challenges correlating to this user/device pair
    private List<Challenge> challenges;

    // Unique identifier given to each user/device pair
    private int userDeviceID;

    ArrayList<AuthValues<Double>> auth_values_list;

    //TODO remove these in favor of grouping
    private double time_length_allowed_deviations;

    // Confidence Interval for the most recent authenticating response
    private double new_response_confidence_interval;

    private Response previous_authentication_response;

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

        this.auth_values_list = new ArrayList<>();

        // for each point metric, create a new AuthValues construct.
        //TODO set based on allowed deviations
        for(Point.Metrics metric : Point.Metrics.values()) {
            this.auth_values_list.add(new AuthValues<Double>());
            this.auth_values_list.get(this.auth_values_list.size() - 1).metrics_type = metric;

            if(metric == Point.Metrics.PRESSURE) {
                this.auth_values_list.get(this.auth_values_list.size() - 1).allowed_deviations = pressure_allowed_deviations;

                //TODO this verifies that the value is being set, but what happens to it?
                //System.out.println("pressure_allowed_deviations_set: " + pressure_allowed_deviations);
            }else
            if(metric == Point.Metrics.DISTANCE) {
                this.auth_values_list.get(this.auth_values_list.size() - 1).allowed_deviations = distance_allowed_deviations;
            }else
            if(metric == Point.Metrics.TIME) {
                this.auth_values_list.get(this.auth_values_list.size() - 1).allowed_deviations = time_allowed_deviations;
            }else
            if(metric == Point.Metrics.VELOCITY) {
                this.auth_values_list.get(this.auth_values_list.size() - 1).allowed_deviations = VELOCITY_LENGTH_DEFAULT_ALLOWED_DEVIATIONS;
            }else
            if(metric == Point.Metrics.ACCELERATION) {
                this.auth_values_list.get(this.auth_values_list.size() - 1).allowed_deviations = ACCELERATION_DEFAULT_ALLOWED_DEVIATIONS;
            }

            //TODO right now a singular authentication threshold is being used.
            //TODO this could be changed ot be a separate authentication threshold for each (switch above could be used)
            this.auth_values_list.get(this.auth_values_list.size() - 1).authentication_threshold = authentication_threshold;
            this.auth_values_list.get(this.auth_values_list.size() - 1).authentication_failed_point_ratio = 0.0;
            this.auth_values_list.get(this.auth_values_list.size() - 1).point_vector = new ArrayList<>();
        }

        this.time_length_allowed_deviations = TIME_LENGTH_DEFAULT_ALLOWED_DEVIATIONS;
        this.new_response_confidence_interval = -1;

        this.previous_authentication_response = null;
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
        //TODO why is this happening? I have no clue
        //System.out.println("pressure_deviations_allowed: " + this.auth_values_list.get(0).allowed_deviations + ", ");
        //System.out.println("pressure_deviations_allowed: " + this.auth_values_list.get(auth_value_of(Point.Metrics.PRESSURE)).allowed_deviations + ", ");

        Profile profile = challenge.getProfile();

        // set a value which represents all points failing
        for(int i=0; i<auth_values_list.size(); i++){
            this.auth_values_list.get(i).authentication_failed_point_ratio = 1.0;
        }

        // normalize the response
        Response response_object = new Response(new_response_data);
        response_object.normalize(challenge.getNormalizingPoints());

        // make the response from this authentication available
        this.previous_authentication_response = response_object;

        //System.out.println("normalized_response:\t" + response_object.getNormalizedResponse());

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

        /* determine what type of authentication will happen */
        switch(AUTHENTICATION_TYPE) {
            case POINT_VECTOR:
                return point_vector_authentication(profile);

            case FAILED_POINTS:
                // determine the size of the list
                int list_size = profile.getNormalizedResponses().get(0).getNormalizedResponse().size();

                // for each point metric
                for (int i = 0; i < auth_values_list.size(); i++) {
                    // compute number of failed points
                    int failed_points = failed_points(new_response_data, profile,
                            auth_values_list.get(i).allowed_deviations, auth_values_list.get(i).metrics_type);

                    // compute failed points ratio
                    auth_values_list.get(i).authentication_failed_point_ratio = ((double) failed_points) / list_size;
                }

                double response_time_length = new_response_data.get(new_response_data.size() - 1).getTime()
                        - new_response_data.get(0).getTime();
                boolean time_length_within_sigma = (Math.abs(profile.getTimeLengthMu()
                        - response_time_length) <= (profile.getTimeLengthSigma() * this.time_length_allowed_deviations));

                // if the fraction of points that pass is greater than the
                // authentication threshold, then we pass this person
                return authenticatePredicate(this.auth_values_list,
                        time_length_within_sigma);

            default:
                return false;
        }
    }

    /**
     * Determine whether the user should be authenticated or not.
     * this authentication method is based on point vectors.
     *
     * determine the average value of each vector.
     * This represents the average difference.
     */
    private boolean point_vector_authentication(Profile profile){
        // for all metrics
        for(int i=0; i<this.auth_values_list.size(); i++){
            Point.Metrics metrics = this.auth_values_list.get(i).metrics_type;

            //TODO only do this for certain metrics we have set (eg. PRESSURE => only pressure)
            switch(AUTHENTICATION_PREDICATE) {
                case PRESSURE: if (metrics != Point.Metrics.PRESSURE) { continue; }
                    break;
                case NO_PRESSURE: if (metrics == Point.Metrics.PRESSURE) { continue; }
                    break;
                case TIME: if (metrics != Point.Metrics.TIME) { continue; }
                    break;
                case DISTANCE: if (metrics != Point.Metrics.DISTANCE) { continue; }
                            break;
                case VELOCITY: if (metrics != Point.Metrics.VELOCITY) { continue; }
                    break;
                case ACCELERATION: if (metrics != Point.Metrics.ACCELERATION) { continue; }
                    break;
            }

            // determine the average difference and average sigma for the points
            double average_difference = 0.0;
            double average_sigma = 0.0;
            for (int j = 0; j < this.auth_values_list.get(i).point_vector.size(); j++) {
                // compute the average difference
                // difference is defined to be positive, so I don't need abs value
                average_difference += this.auth_values_list.get(i).point_vector.get(j) /
                        this.auth_values_list.get(i).point_vector.size();

                // compute the average sigma value for each point
                average_sigma += profile.getMuSigmaValues(metrics).getSigmaValues().get(j) /
                        profile.getMuSigmaValues(metrics).getSigmaValues().size();
            }

            // continue to pass authentication if this value is less than the maximum allowed difference
            // the maximum allowed difference is a function of the allowed standard deviations
            double allowed_difference = this.auth_values_list.get(i).allowed_deviations *
                    average_sigma;

            //TODO print out the average_difference and allowed_difference
            //TODO this will indicate what the issue may be
//            System.out.print("deviations_allowed: " + this.auth_values_list.get(i).allowed_deviations + ", ");
//            System.out.print("average_sigma: " + average_sigma + ", ");
//            System.out.print("average_difference: " + average_difference + ", ");
//            System.out.println("allowed_difference: " + allowed_difference);

            //try{ Thread.sleep(5000); } catch(Exception e) { e.printStackTrace(); }

            // if the average difference is greater than the allowed difference
            if(average_difference > allowed_difference){
                // the authenticaiton fails
                return false;
            }
        }

        return true;
    }

    /**
     * preticate used to combine the failed point ratios. Returns true if the
     * device passes. preticate: (pressure) or (distance and time)
     *
     * in this, we only measure whether a point falls outside of a number
     * of standard deviations of the
     */
    //TODO extend this to include velocity and acceleration
    private boolean authenticatePredicate(ArrayList<AuthValues<Double>> auth_list, boolean time_length_within_sigma) {
        boolean pass = false;

        ArrayList<Boolean> pass_list = new ArrayList<>();
        ArrayList<Point.Metrics> metric_list = new ArrayList<>();

        // determine whether or not each metric passed
        for(int i=0; i<auth_values_list.size(); i++){
            // keep track of pass / not pass and metrics type
            // if the number of points which passed is above a threshold, then true
            pass_list.add((1 - auth_values_list.get(i).authentication_failed_point_ratio) >
                    auth_values_list.get(i).authentication_threshold);

            metric_list.add(auth_values_list.get(i).metrics_type);
        }

        boolean time_length_pass = time_length_within_sigma;

        switch (AUTHENTICATION_PREDICATE) {
            case TIME_OR_DISTANCE:
                pass = false;

                for(int i=0; i<metric_list.size(); i++){
                    if(metric_list.get(i) == Point.Metrics.DISTANCE ||
                            metric_list.get(i) == Point.Metrics.TIME){
                        pass = pass || pass_list.get(i);
                    }
                }

                break;
            case TIME_LENGTH:
                pass = time_length_pass;

                break;
            case DISTANCE:
                for(int i=0; i<metric_list.size(); i++){
                    if(metric_list.get(i) == Point.Metrics.DISTANCE){
                        pass = pass_list.get(i);
                        break;
                    }
                }

                break;
            case PRESSURE:
                for(int i=0; i<metric_list.size(); i++){
                    if(metric_list.get(i) == Point.Metrics.PRESSURE){
                        pass = pass_list.get(i);
                        break;
                    }
                }

                break;
            case VELOCITY:
                for(int i=0; i<metric_list.size(); i++){
                    if(metric_list.get(i) == Point.Metrics.VELOCITY){
                        pass = pass_list.get(i);
                        break;
                    }
                }

                break;
            case ACCELERATION:
                for(int i=0; i<metric_list.size(); i++){
                    if(metric_list.get(i) == Point.Metrics.ACCELERATION){
                        pass = pass_list.get(i);
                        break;
                    }
                }

                break;
            case NO_PRESSURE:
                pass = true;
                //TODO reconsider this
                for(int i=0; i<metric_list.size(); i++){
                    if(metric_list.get(i) == Point.Metrics.DISTANCE ||
                            metric_list.get(i) == Point.Metrics.TIME){
                        pass = pass && pass_list.get(i);
                    }
                }

                break;
            case TIME:
                for(int i=0; i<metric_list.size(); i++){
                    if(metric_list.get(i) == Point.Metrics.TIME){
                        pass = pass_list.get(i);
                    }
                }

                break;
            case PRESSURE_OR_TIME:
                pass = false;

                for(int i=0; i<metric_list.size(); i++){
                    if(metric_list.get(i) == Point.Metrics.PRESSURE ||
                            metric_list.get(i) == Point.Metrics.TIME){
                        pass = pass || pass_list.get(i);
                    }
                }

                break;
            case PRESSURE_OR_DISTANCE_AND_TIME:
                pass = true;

                // distance and time
                for(int i=0; i<metric_list.size(); i++){
                    if(metric_list.get(i) == Point.Metrics.DISTANCE ||
                            metric_list.get(i) == Point.Metrics.TIME){
                        pass = pass && pass_list.get(i);
                    }
                }

                // or pressure
                for(int i=0; i<metric_list.size(); i++){
                    if(metric_list.get(i) == Point.Metrics.PRESSURE){
                        pass = pass || pass_list.get(i);
                    }
                }

                break;
            case PRESSURE_OR_DISTANCE:
                pass = false;

                for(int i=0; i<metric_list.size(); i++){
                    if(metric_list.get(i) == Point.Metrics.DISTANCE ||
                            metric_list.get(i) == Point.Metrics.PRESSURE){
                        pass = pass || pass_list.get(i);
                    }
                }

                break;
            default:
                // default to pressure pass
                for(int i=0; i<metric_list.size(); i++){
                    if(metric_list.get(i) == Point.Metrics.PRESSURE){
                        pass = pass_list.get(i);
                    }
                }
                break;
        }

        return pass;
    }

    /**
     * returns the response from the previous authentication.
     *
     * returns null if there has been no previous authentication.
     */
    public Response getPreviousAuthenticationResponse(){
        return this.previous_authentication_response;
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
     * return AuthValues index given Point_metrics
     * return -1 if no match
     */
    private int auth_value_of(Point.Metrics metrics){
        for(int i=0; i<auth_values_list.size(); i++){
            if(auth_values_list.get(i).metrics_type == metrics){
                return i;
            }
        }

        // not found
        return -1;
    }

    /**
     * map RatioType to Point.Metrics
     */
    private Point.Metrics point_metrics_of(RatioType type){
        //TODO this is stupid
        switch (type) {
            case PRESSURE:
            return Point.Metrics.PRESSURE;

            case DISTANCE:
                return Point.Metrics.DISTANCE;

            case TIME:
                return Point.Metrics.TIME;

            case VELOCiTY:
                return Point.Metrics.VELOCITY;

            case ACCELERATION:
                return Point.Metrics.ACCELERATION;
            //TODO fix this, time length should not be computing pressure
            case TIME_LENGTH:
                return Point.Metrics.ACCELERATION;
        }

        //TODO fix this, not technically correct
        System.out.println("shouldn't be here !!!!! UserDevicePair 450");
        return Point.Metrics.PRESSURE;
    }

    /**
     * return the an array of size N.
     * N = number of points in response
     * each element in the array is abs(profile[i] - response[i])
     */
    public List<Double> getNew_response_point_vector(RatioType type) {
        // return a point vector
        return this.auth_values_list.get(auth_value_of(point_metrics_of(type))).point_vector;
    }

    /**
     * Prints a dump of all UserDevicePair info:
     * UD-Pair: Authentication Vectors
     * Profile: Mu Sigmas (pressure, distance, etc.)
     * Challenge: Normalizing Points, Responses
     *
     * @param challenge challenge associated with this UserDivePair
     * @return String represntation of the info "dump"
     */
    public String dumpUserDevicePairData(Challenge challenge) {
        StringBuilder sb = new StringBuilder();
        Profile profile = challenge.getProfile();


        // Print all vectors for profile
        sb.append("Profile Vectors\n");
        for(int i=0; i<this.auth_values_list.size(); i++){
            sb.append(this.auth_values_list.get(i).metrics_type + ": \n");

            for (int j = 0; j < this.auth_values_list.get(i).point_vector.size(); j++) {
                sb.append("PressureVector[").append(j).append("]: ").append(this.auth_values_list.get(i).point_vector.get(j)).append("\n");
            }
        }

        //TODO modularize this

        // Print all MuSigmas from profile
        sb.append("\n\nMu Sigma Values: \n\n");
        //Pressure MuSigma
        sb.append("\nPressure: \n");
        for (int i = 0; i < profile.getPressureMuSigmaValues().getMuValues().size(); i++) {
            sb.append("PressureMu[").append(i).append("]: ").append(profile.getPressureMuSigmaValues().getMuValues().get(i))
                    .append(", PressureSigma[").append(i).append("]: ")
                    .append(profile.getPressureMuSigmaValues().getSigmaValues().get(i)).append("\n");
        }
        //Distance MuSigma
        sb.append("\nDistance: \n");
        for (int i = 0; i < profile.getPointDistanceMuSigmaValues().getMuValues().size(); i++) {
            sb.append("DistanceMu[").append(i).append("]: ").append(profile.getPointDistanceMuSigmaValues().getMuValues().get(i))
                    .append(", DistanceSigma[").append(i).append("]: ")
                    .append(profile.getPointDistanceMuSigmaValues().getSigmaValues().get(i)).append("\n");
        }
        //Time MuSigma
        sb.append("\nTime: \n");
        for (int i = 0; i < profile.getTimeDistanceMuSigmaValues().getMuValues().size(); i++) {
            sb.append("TimeMu[").append(i).append("]: ").append(profile.getTimeDistanceMuSigmaValues().getMuValues().get(i))
                    .append(", TimeSigma[").append(i).append("]: ")
                    .append(profile.getTimeDistanceMuSigmaValues().getSigmaValues().get(i)).append("\n");
        }

        // Print Normalizing Points from challenge
        sb.append("\n\nNormalizing Points: \n\n");
        for (int i = 0; i < challenge.getNormalizingPoints().size(); i++) {
            sb.append("NormalizingPoint[").append(i).append("]: ")
                    .append(challenge.getNormalizingPoints().get(i).toString()).append("\n");
        }

        // Print All Responses that make up profile
        sb.append("\n\n\nResponses:");

        // Iterate over all responses
        for (int i = 0; i < challenge.getResponsePattern().size(); i++) {
            sb.append("Response ").append(i).append(":\n");
            Response tempResponse = challenge.getResponsePattern().get(i);
            for (int j = 0; j < tempResponse.getNormalizedResponse().size(); j++) {
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
        for(int i=0; i<this.auth_values_list.size(); i++){
            this.auth_values_list.get(i).point_vector = new ArrayList<>();
        }

        // error check
        if (new_response_data.size() != profile.getNormalizedResponses().get(0).getNormalizedResponse().size()) {
            System.out.println("shouldn't be here: " + new_response_data.size() + " | " + profile.getNormalizedResponses().get(0).getNormalizedResponse().size());
            return;
        }

        //TODO i think the same computation was being done twice,
        //TODO so the first one is commented out
        // for each point in new_response, take abs(profile[i] - response[i])
        // this computes the difference between the profile and the new response
        // the difference is stored in the point vector for that metric
//        for (int i = 0; i < new_response_data.size(); i++) {
//            // for each metric
//            for(int j=0; j<this.auth_values_list.size(); j++){
//                Double distance = Math.abs(new_response_data.get(i).get_metric(this.auth_values_list.get(j).metrics_type) -
//                                    profile.getMuSigmaValues(this.auth_values_list.get(j).metrics_type).getMuValues().
//                                            get(i));
//                this.auth_values_list.get(j).point_vector.add(distance);
//            }
//        }

        // we want to compute vectors for all the metrics
        for(int i=0; i<this.auth_values_list.size(); i++){
            Point.Metrics metrics = this.auth_values_list.get(i).metrics_type;

            for (int j = 0; j < new_response_data.size(); j++) {
                this.auth_values_list.get(i).point_vector.add(
                        vector_computation(new_response_data.get(j).get_metric(metrics),
                                profile.getMuSigmaValues(metrics).getMuValues().get(j)));
            }

            //TODO
            //System.out.println("vector_" + i + ": " + auth_values_list.get(i).point_vector);
        }
    }

    /**
     * this computation seems trivial, but
     * it is done this way so that it may be easily changed in the future.
     */
    private double vector_computation(double new_response_metric, double profile_metric_mu){
        return Math.abs(new_response_metric - profile_metric_mu);
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
    public double failedPointRatio(Point.Metrics type) {
        double failed_ratio = -1;

        // for(all pointmetrics){ if(type matches metric) return failed_ratio
        for(int i=0; i<this.auth_values_list.size(); i++){
            if(this.auth_values_list.get(i).metrics_type == type){
                failed_ratio = this.auth_values_list.get(i).authentication_failed_point_ratio;
            }
        }

        return failed_ratio;
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
        return this.auth_values_list.get(auth_value_of(point_metrics_of(type))).authentication_failed_point_ratio;
    }

    /**
     * allow the number of std deviations from the mean allowed in the
     * authentication to be set.
     */
    //TODO handle time length
    public void setStandardDeviations(RatioType type, double standard_deviations) {
        //TODO i don't htink this method is handling things correctly
//        if(type == RatioType.PRESSURE) {
//            System.out.println("PRESSURE_set_deviations: " + standard_deviations);
//            System.out.println("point_metrics_type == pressure?: " + (point_metrics_of(type)==Point.Metrics.PRESSURE));
//            System.out.println("auth_values_list index: " + auth_value_of(point_metrics_of(type)));
//        }

        this.auth_values_list.get(auth_value_of(point_metrics_of(type))).allowed_deviations = standard_deviations;
    }

    /**
     * sets the authentication threshold.
     * <p>
     * IF the threshold provide is greater than 1 or less than 0. THAN threshold
     * will be set to the closest of these values.
     */
    //TODO handle time length
    public void setAuthenticationThreshold(RatioType type, double threshold) {
        // if the threshold is greater than 1 or less than 0, change it to the
        // closest of these values.
        double new_threshold = (threshold > 1) ? (1.0) : (threshold);
        new_threshold = (threshold < 0) ? (0.0) : (threshold);

        this.auth_values_list.get(auth_value_of(point_metrics_of(type))).authentication_threshold = new_threshold;
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
    private int failed_points(List<Point> new_response, Profile profile, double allowed_deviations, Point.Metrics metrics_type) {
        int points = 0;

        // get the mu, sigma values from the profile,
        // use metrics_type to determine which should be gotten

        List<Double> mu_values = profile.getMuSigmaValues(metrics_type).getMuValues();
        List<Double> sigma_values = profile.getMuSigmaValues(metrics_type).getSigmaValues();

        //TODO handle time length case

        // normalize the response
        Response response_object = new Response(new_response);

        response_object.normalize(profile.getNormalizedResponses().get(0).getNormalizedResponse());

        // create a list of point values for distance
        List<Double> point_values = new ArrayList<Double>();
        for (Point response_point : response_object.getNormalizedResponse()) {
            point_values.add(response_point.getDistance());
        }

        points = failed_points(mu_values, sigma_values, point_values, allowed_deviations);

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
     * test function to help locate the source of allowed deviations being set to 0
     * before authentication occurs
     */
    public double get_allowed_deviations(Point.Metrics metrics){
        return this.auth_values_list.get(auth_value_of(metrics)).allowed_deviations;
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
     * returns the compare value between this profile and the response
     */
    public double compare(Response response){
        double aggregate_compare_value = 0.0;

        ArrayList<Double> compare_value_list = new ArrayList<>();
        ArrayList<Point.Metrics> metric_list = new ArrayList<>();

        // compute point vector ( computes this.auth_values_list )
        // use the first (and probabally only) challenge in this UD_PAIR
        Challenge challenge = getChallenges().get(0);

        //TODO set parameters of the authentication
        this.setStandardDeviations(RatioType.PRESSURE, 2.0);
        this.setStandardDeviations(RatioType.DISTANCE, 2.0);
        this.setStandardDeviations(RatioType.TIME, 2.0);
        this.setStandardDeviations(RatioType.VELOCiTY, 2.0);
        this.setStandardDeviations(RatioType.ACCELERATION, 2.0);


        // trigger an authentication
        this.authenticate(response.getOrigionalResponse(), challenge);

        // normalize the response
        //Response response_object = new Response(response.getOrigionalResponse());
        //response_object.normalize(challenge.getNormalizingPoints());

        // compute the point vector based on the normalized response
        //compute_point_vector(response_object.getNormalizedResponse(), challenge.getProfile());

        // create parallel arrays which give the compare value for each point metric
        for(int i=0; i<auth_values_list.size(); i++) {
            // keep track of pass / not pass and metrics type
            // if the number of points which passed is above a threshold, then true
            compare_value_list.add((1.0 - auth_values_list.get(i).authentication_failed_point_ratio));
            metric_list.add(auth_values_list.get(i).metrics_type);
        }

        //System.out.println(compare_value_list);
        //System.out.println(this.auth_values_list.get(0).authentication_failed_point_ratio);

        // aggregate the compare values of the point metrics somehow
        double sum = 0.0;
        double weight_sum = 0.0;
        for(int i=0; i<metric_list.size(); i++){
            // assign a weight based on the metric
            double weight = 0.0;
            switch(metric_list.get(i)){
                case PRESSURE: weight = 1.0; break;
                case DISTANCE: weight = 0.0; break;
                case TIME: weight = 0.0; break;
                case VELOCITY: weight = 0.0; break;
                case ACCELERATION: weight = 0.0; break;
            }

            sum += weight * compare_value_list.get(i);
            weight_sum += weight;
        }

        aggregate_compare_value = sum / weight_sum;

        System.out.println(aggregate_compare_value); // TODO

        //TODO is a weighted average the  best way to do this?
        return aggregate_compare_value;
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
/*
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
    */
}
