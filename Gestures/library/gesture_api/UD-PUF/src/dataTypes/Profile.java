package dataTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a profile containing Mu and Sigma values, along with a list of
 * normalized challanges
 * <p>
 * This class assumees the responses have already been normalized.
 */
public class Profile implements Serializable {
    /**
     * serial version id
     */
    private static final long serialVersionUID = -8090388590557141249L;

    // List of normalized Responses
    private ArrayList<Response> normalizedResponses;

    // Confidence Interval for this Profile
    private double confidence_interval;

    // Confidence Interval for the most recent authenticating response
    private double new_response_confidence_interval;

    // list of time_lengths corresponding to the responses in the
    // normalizedResponses list
    private ArrayList<Double> time_lengths;

    // list of motion event counts for each response in the challenge
    private ArrayList<Double> motion_event_counts;

    // Mu Sigma values that define the profile
    private MuSigma pressure_muSigmaValues;
    private MuSigma point_distance_muSigmaValues;
    private MuSigma time_muSigmaValues;
    private MuSigma velocity_muSigmaValues;
    private MuSigma acceleration_muSigmaValues;

    private double time_length_mu;
    private double time_length_sigma;

    private double motion_event_count_mu;
    private double motion_event_count_sigma;

    // true if mu sigma has been computed
    private boolean mu_sigma_computed;

    double num_motion_events_contribution;
    double sd_motion_events_contribution;

    // Profile Standard Deviations of pressure, time, and distance
    double sd_pressure_contribution;
    double sd_time_contribution;
    double sd_distance_contribution;

    // Authentication Standard Deviations of pressure, time, and distance
    double auth_sd_pressure_contribution;
    double auth_sd_time_contribution;
    double auth_sd_distance_contribution;

    public Profile(List<Response> normalizedResponses, List<Double> time_lengths, List<Double> motion_event_counts) {
        this.normalizedResponses = new ArrayList<Response>(normalizedResponses);
        this.time_lengths = new ArrayList<Double>(time_lengths);
        this.motion_event_counts = new ArrayList<Double>(motion_event_counts);
        // Calculate mu and sigma values for this profile
        // and assign them to muValues and sigmaValues
        // For now, just create blank ones
        pressure_muSigmaValues = new MuSigma();
        point_distance_muSigmaValues = new MuSigma();
        time_muSigmaValues = new MuSigma();
        this.velocity_muSigmaValues = new MuSigma();
        this.acceleration_muSigmaValues = new MuSigma();

        mu_sigma_computed = false;

    }

    // Constructor without normalized responses, for initially constructing a
    // challenge
    public Profile() {
        normalizedResponses = new ArrayList<Response>();
        this.time_lengths = new ArrayList<Double>();
        this.motion_event_counts = new ArrayList<Double>();
        pressure_muSigmaValues = new MuSigma();
        point_distance_muSigmaValues = new MuSigma();
        time_muSigmaValues = new MuSigma();
        this.velocity_muSigmaValues = new MuSigma();
        this.acceleration_muSigmaValues = new MuSigma();

        time_length_mu = 0;
        time_length_sigma = 0;

        mu_sigma_computed = false;
        confidence_interval = -1;
        new_response_confidence_interval = -1;

        num_motion_events_contribution = 0;
        sd_motion_events_contribution = 0;

        sd_pressure_contribution = 0;
        sd_time_contribution = 0;
        sd_distance_contribution = 0;

        auth_sd_pressure_contribution = 0;
        auth_sd_time_contribution = 0;
        auth_sd_distance_contribution = 0;
    }

    public void addNormalizedResponses(List<Response> normalizedResponses) {
        for (Response response : normalizedResponses) {
            // add the response to this list of normalized responses
            this.normalizedResponses.add(response);

            // also add the motion_event_count of this normalized response to
            // the list
            this.motion_event_counts.add(new Double(response.getMotionEvenCount()));
        }

        this.mu_sigma_computed = false;
    }

    public MuSigma getPressureMuSigmaValues() {
        // if mu sigma have not been computed, compute them
        if (!mu_sigma_computed) {
            compute_mu_sigma();
        }

        return pressure_muSigmaValues;
    }

    public MuSigma getPointDistanceMuSigmaValues() {
        // if mu sigma have not been computed, compute them
        if (!mu_sigma_computed) {
            compute_mu_sigma();
        }

        return point_distance_muSigmaValues;
    }

    public MuSigma getTimeDistanceMuSigmaValues() {
        // if mu sigma have not been computed, compute them
        if (!mu_sigma_computed) {
            compute_mu_sigma();
        }

        return time_muSigmaValues;
    }

    /**
     * return the pre-normalized mu_sigma time length values.
     */
    public double getTimeLengthSigma() {
        // if mu sigma have not been computed, compute them
        if (!mu_sigma_computed) {
            compute_mu_sigma();
        }

        return time_length_sigma;
    }

    public double getTimeLengthMu() {
        // if mu sigma have not been computed, compute them
        if (!mu_sigma_computed) {
            compute_mu_sigma();
        }

        return time_length_mu;
    }

    /**
     * return the pre-normalized mu_sigma motion event count values.
     */
    public double getMotionEventCountSigma() {
        // if mu sigma have not been computed, compute them
        if (!mu_sigma_computed) {
            compute_mu_sigma();
        }

        return motion_event_count_sigma;
    }

    public double getMotionEventCountMu() {
        // if mu sigma have not been computed, compute them
        if (!mu_sigma_computed) {
            compute_mu_sigma();
        }

        return motion_event_count_mu;
    }

    public ArrayList<Response> getNormalizedResponses() {
        return normalizedResponses;
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

        return average;
    }

    /**
     * computes Profile confidence interval (if not yet computed), then returns
     * confidence interval
     */
    public double getConfidence_interval() {
        if (confidence_interval <= 0)
            compute_confidence_interval();
        return confidence_interval;
    }

    public double get_sd_pressure_contribution() {
        if (confidence_interval <= 0)
            compute_confidence_interval();
        return sd_pressure_contribution;
    }

    public double get_sd_time_contribution() {
        if (confidence_interval <= 0)
            compute_confidence_interval();
        return sd_time_contribution;
    }

    public double get_sd_distance_contribution() {
        if (confidence_interval <= 0)
            compute_confidence_interval();
        return sd_distance_contribution;
    }

    public double get_num_motion_event_contribution() {
        if (confidence_interval <= 0)
            compute_confidence_interval();
        return num_motion_events_contribution;
    }

    public double get_sd_motion_event_contribution() {
        if (confidence_interval <= 0)
            compute_confidence_interval();
        return sd_motion_events_contribution;
    }

    /**
     * computes Authenticating confidence interval (if not yet computed), then
     * returns confidence interval
     */
    protected double get_new_response_CI(List<Point> new_response_data) {
        compute_new_response_CI(new_response_data);
        return new_response_confidence_interval;
    }

    protected double get_auth_pressure_contribution(List<Point> new_response_data) {
        compute_new_response_CI(new_response_data);
        return auth_sd_pressure_contribution;
    }

    protected double get_auth_time_contribution(List<Point> new_response_data) {
        compute_new_response_CI(new_response_data);
        return auth_sd_time_contribution;
    }

    protected double get_auth_distance_contribution(List<Point> new_response_data) {
        compute_new_response_CI(new_response_data);
        return auth_sd_distance_contribution;
    }

    // public double get_auth_motion_event_contribution(List<Point>
    // new_response_data) {
    // if(new_response_confidence_interval < 0)
    // compute_new_response_CI(new_response_data);
    // return auth_num_motion_event_contribution;
    // }

    // public double get_auth_motion_event_contribution(List<Point>
    // new_response_data) {
    // if(new_response_confidence_interval < 0)
    // compute_new_response_CI(new_response_data);
    // return auth_ds_motion_event_contribution;
    // }

    /**
     * computes confidence interval by using: 1) # of Motion Events Used in
     * Normalizing 2) Standard deviation of motion events 3) Average standard
     * deviations for point attributes (pressure, time, and distance)
     */
    private void compute_confidence_interval() {

        int valid_Confidence_Interval_contributions = 0;

        if (!mu_sigma_computed) {
            compute_mu_sigma();
        }

        int num_points = normalizedResponses.get(0).getNormalizedResponse().size();

        for (int i = 0; i < num_points; i++) {
            if (!Double.isNaN(pressure_muSigmaValues.getSigmaValues().get(i)))
                sd_pressure_contribution += (1 - (pressure_muSigmaValues.getSigmaValues().get(i)
                        / pressure_muSigmaValues.getMuValues().get(i)));
            if (!Double.isNaN(time_muSigmaValues.getSigmaValues().get(i)))
                sd_time_contribution += (1
                        - (time_muSigmaValues.getSigmaValues().get(i) / time_muSigmaValues.getMuValues().get(i)));
            if (!Double.isNaN(point_distance_muSigmaValues.getSigmaValues().get(i)))
                sd_distance_contribution += (1 - (point_distance_muSigmaValues.getSigmaValues().get(i)
                        / point_distance_muSigmaValues.getMuValues().get(i)));
        }

        if (!Double.isNaN(sd_pressure_contribution) && sd_pressure_contribution > 0) {
            sd_pressure_contribution = sd_distance_contribution / num_points;
            valid_Confidence_Interval_contributions++;
        } else
            sd_pressure_contribution = 0;

        if (!Double.isNaN(sd_time_contribution) && sd_time_contribution > 0) {
            sd_time_contribution = sd_time_contribution / num_points;
            valid_Confidence_Interval_contributions++;
        } else
            sd_time_contribution = 0;

        if (!Double.isNaN(sd_distance_contribution) && sd_distance_contribution > 0) {
            sd_distance_contribution = sd_distance_contribution / num_points;
            valid_Confidence_Interval_contributions++;
        } else
            sd_distance_contribution = 0;

        // TODO
        // calculate compute challenge's challenge's normalized_elements count
        // to a standard
        // to determine if the profile has enough points

        // num_motion_events_contribution = motion_event_count_mu / 200;
        // sd_motion_events_contribution = motion_event_count_sigma;
        // confidence_interval = ( num_motion_events_contribution +
        // sd_motion_events_contribution + sd_pressure_contribution
        // + sd_time_contribution + sd_distance_contribution) / 5;

        // just contributions from pressure, time, and distance for now
        if (valid_Confidence_Interval_contributions > 0)
            confidence_interval = (sd_pressure_contribution + sd_time_contribution + sd_distance_contribution)
                    / valid_Confidence_Interval_contributions;
        else
            confidence_interval = -1;

    }

    /**
     * calculate and return the confidence interval for an attempted
     * authentication
     */
    private void compute_new_response_CI(List<Point> new_response) {

        int valid_Confidence_Interval_contributions = 0;

        if (!mu_sigma_computed) {
            compute_mu_sigma();
        }

        // TODO get normalized new_response to be the same size of muSigmaValues in the first place
        int num_points = (new_response.size() < pressure_muSigmaValues.getMuValues().size()) ? new_response.size() : pressure_muSigmaValues.getMuValues().size();

        // [1 - Sigma_{i=1}^N( |p_i - mu_i| / mu_i)] / N
        for (int i = 0; i < num_points; i++) {
            if (!Double.isNaN(pressure_muSigmaValues.getSigmaValues().get(i))
                    && !Double.isInfinite(new_response.get(i).getPressure())) {
                auth_sd_pressure_contribution += (1
                        - (Math.abs(new_response.get(i).getPressure() - (pressure_muSigmaValues.getMuValues().get(i)))
                        / pressure_muSigmaValues.getMuValues().get(i)));
            }

            if (!Double.isNaN(time_muSigmaValues.getSigmaValues().get(i))
                    && !Double.isInfinite(new_response.get(i).getPressure())) {
                auth_sd_time_contribution += (1
                        - (Math.abs(new_response.get(i).getTime() - (time_muSigmaValues.getMuValues().get(i)))
                        / time_muSigmaValues.getMuValues().get(i)));
            }

            if (!Double.isNaN(point_distance_muSigmaValues.getSigmaValues().get(i))
                    && !Double.isInfinite(new_response.get(i).getPressure())) {
                auth_sd_distance_contribution += (1 - (Math
                        .abs(new_response.get(i).getDistance() - (point_distance_muSigmaValues.getMuValues().get(i)))
                        / point_distance_muSigmaValues.getMuValues().get(i)));
            }
        }

        if (!Double.isNaN(auth_sd_pressure_contribution) && auth_sd_pressure_contribution >= 0) {
            auth_sd_pressure_contribution = auth_sd_pressure_contribution / num_points;
            valid_Confidence_Interval_contributions++;
        } else
            auth_sd_pressure_contribution = 0;

        if (!Double.isNaN(auth_sd_time_contribution) && auth_sd_time_contribution >= 0) {
            auth_sd_time_contribution = auth_sd_time_contribution / num_points;
            valid_Confidence_Interval_contributions++;
        } else
            auth_sd_time_contribution = 0;

        if (!Double.isNaN(auth_sd_distance_contribution) && auth_sd_distance_contribution >= 0) {
            auth_sd_distance_contribution = auth_sd_distance_contribution / num_points;
            valid_Confidence_Interval_contributions++;
        } else
            auth_sd_distance_contribution = 0;

        if (valid_Confidence_Interval_contributions > 0)
            new_response_confidence_interval = (auth_sd_pressure_contribution + auth_sd_time_contribution
                    + auth_sd_distance_contribution) / valid_Confidence_Interval_contributions;
        else
            new_response_confidence_interval = -1;
    }

    /**
     * used to compute mu and sigma values and store
     */
    private void compute_and_store(List<Double> list, Point.Metrics type){
        double mu = computeMu(list);
        double sigma = computeSigma(list, mu);

        store_mu_sigma(mu, sigma, type);
    }

    /**
     * used by MuSigmaComputation to store values
     * <p>
     * this functionality extracted out purely
     * to make this easier to program.
     * <p>
     * stores a single MuSigma Point in the respective list
     */
    private void store_mu_sigma(double mu, double sigma, Point.Metrics type) {
        switch (type) {
            case PRESSURE:
                this.pressure_muSigmaValues.addMuSigma(mu, sigma);
                break;

            case DISTANCE:
                this.point_distance_muSigmaValues.addMuSigma(mu, sigma);
                break;

            case TIME:
                this.time_muSigmaValues.addMuSigma(mu, sigma);
                break;

            case VELOCITY:
                this.velocity_muSigmaValues.addMuSigma(mu, sigma);
                break;

            case ACCELERATION:
                this.acceleration_muSigmaValues.addMuSigma(mu, sigma);
                break;
        }
    }

    /**
     * used to extract a list of points
     * <p>
     * these points are stored in parallell in the normalized responses at index, index
     */
    private List<Double> extract_parallel_points(List<Response> normalized_response_list, int index, Point.Metrics type) {
        // go though each of the responses collecting value
        // of point index in the response
        List<Double> normalized_point_list = new ArrayList<Double>();

        for (Response response : normalized_response_list) {
            if (response.getNormalizedResponse().size() <= index) continue;

            normalized_point_list.add(response.getNormalizedResponse().get(index).get_metric(type));
        }

        return normalized_point_list;
    }

    /**
     * Find mu and sigma values for all points in the normalized list. This
     * method will set the value of this.muSigmaValues to the appropriate value
     */
    private void compute_mu_sigma() {
        // make sure there are normalized responses to compute mu,sigma for
        if (this.normalizedResponses.get(0) == null) {
            return;
        }

        // reset all mu sigma values
        this.pressure_muSigmaValues = new MuSigma();
        this.point_distance_muSigmaValues = new MuSigma();
        this.time_muSigmaValues = new MuSigma();
        this.velocity_muSigmaValues = new MuSigma();
        this.acceleration_muSigmaValues = new MuSigma();

        // extract, compute, store for each Point.Metrics
        // this uses the functions we just made
        Point.Metrics[] metrics = Point.Metrics.values();

        for (int i = 0; i < metrics.length; i++) {
            // want to add mu sigma for each point in each metric
            for (int j = 0; j < this.normalizedResponses.get(0).getNormalizedResponse().size(); j++) {
                List<Double> list = extract_parallel_points(this.normalizedResponses, j, metrics[i]);
                compute_and_store(list, metrics[i]);
            }
        }

        // call methods to load the correct mu, sigma objects into instance
        // variables
        compute_time_length_mu_sigma();
        compute_motion_event_count_mu_sigma();

        this.mu_sigma_computed = true;
    }

    /**
     * get the mu sigma values based on metrics enum type
     */
    public MuSigma getMuSigmaValues(Point.Metrics type) {
        // compute mu_sizma's as necessary
        if(!this.mu_sigma_computed) compute_mu_sigma();

        switch (type) {
            case PRESSURE:
                return this.pressure_muSigmaValues;

            case DISTANCE:
                return this.point_distance_muSigmaValues;

            case TIME:
                return this.time_muSigmaValues;

            case VELOCITY:
                return this.velocity_muSigmaValues;

            case ACCELERATION:
                return this.acceleration_muSigmaValues;
        }

        return null;
    }

    /**
     * computes the time_length mu sigma values and stores them in the
     * appropriate instance variables.
     */
    private void compute_time_length_mu_sigma() {
        this.time_length_mu = computeMu(this.time_lengths);
        this.time_length_sigma = computeSigma(time_lengths, this.time_length_mu);
    }

    private void compute_motion_event_count_mu_sigma() {
        // compute the mu, sigma values for motion_event_count
        this.motion_event_count_mu = computeMu(this.motion_event_counts);
        this.motion_event_count_sigma = computeSigma(this.motion_event_counts, this.motion_event_count_mu);
    }

    /**
     * Create and add Mu and Sigma values to the MuSigma
     */
    // private void addMuSigma(MuSigma ms, List<Response> rs) {
    // for (int i = 0; i < rs.size(); i++) {
    // ArrayList<Double> lpressure = new ArrayList<Double>();
    // for (int j = 0; j < rs.get(i).getResponse().size(); j++) {
    // lpressure.add(rs.get(i).getResponse().get(j).getPressure());
    // }
    // double mu = computeMu(lpressure);
    // double sigma = computeSigma(lpressure, mu);
    // ms.addMuSigma(mu, sigma);
    // }
    // }

    /**
     * compute the standard deviation for the list of points
     *
     * @return
     */
    private double computeSigma(List<Double> list, double mu) {
        double std = 0;

        // 1. Work out the Mean (the simple average of the numbers)
        // 2. Then for each number: subtract the Mean and square the result
        // 3. Then work out the mean of those squared differences.
        // 4. Take the square root of that and we are done!
        Iterator<Double> iterator = list.iterator();
        int count = 0;
        double total_subtract_mean_squared = 0;

        while (iterator.hasNext()) {
            Double t = iterator.next();

            total_subtract_mean_squared += Math.pow(t - mu, 2);
            count++;
        }

        // std is the square root of the average of these numbers
        std = Math.sqrt(total_subtract_mean_squared / count);

        return std;
    }

}
