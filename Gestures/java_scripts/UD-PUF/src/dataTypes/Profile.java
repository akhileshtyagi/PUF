package dataTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a profile containing Mu and Sigma values, along with a list of
 * normalized challanges
 * 
 * This class assumees the responses have already been normalized.
 */
public class Profile implements Serializable {
    /**
     * serial version id
     */
    private static final long serialVersionUID = -8090388590557141249L;

    // List of normalized Responses
    private ArrayList<Response> normalizedResponses;

    // list of time_lengths corresponding to the responses in the
    // normalizedResponses list
    private ArrayList<Double> time_lengths;

    // list of motion event counts for each response in the challenge
    private ArrayList<Double> motion_event_counts;

    // Mu Sigma values that define the profile
    private MuSigma pressure_muSigmaValues;
    private MuSigma point_distance_muSigmaValues;
    private MuSigma time_muSigmaValues;

    private double time_length_mu;
    private double time_length_sigma;

    private double motion_event_count_mu;
    private double motion_event_count_sigma;

    // true if mu sigma has been computed
    private boolean mu_sigma_computed;

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

	time_length_mu = 0;
	time_length_sigma = 0;

	mu_sigma_computed = false;
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
     * Find mu and sigma values for all points in the normalized list. This
     * method will set the value of this.muSigmaValues to the appropriate value
     */
    private void compute_mu_sigma() {
	// make sure there are normalized responses to compute mu,sigma for
	if (this.normalizedResponses.get(0) == null) {
	    return;
	}

	// call methods to load the correct mu, sigma objects into instance
	// variables
	compute_pressure_mu_sigma();
	compute_point_distance_mu_sigma();
	compute_time_distance_mu_sigma();
	compute_time_length_mu_sigma();
	compute_motion_event_count_mu_sigma();
	this.mu_sigma_computed = true;
    }

    private void compute_pressure_mu_sigma() {
	// compute mu sigma for pressure
	List<Double> normalized_point_pressure_list = null;
	this.pressure_muSigmaValues = new MuSigma();

	// for each point in the distribution, compute mu an sigma
	for (int i = 0; i < this.normalizedResponses.get(0).getResponse().size(); i++) {
	    // go though each of the responses collecting value
	    // of point i in the response
	    normalized_point_pressure_list = new ArrayList<Double>();
	    for (Response response : this.normalizedResponses) {
		normalized_point_pressure_list.add(response.getResponse().get(i).getPressure());
	    }

	    // compute the average (mu)
	    // compute std deviation
	    double mu = this.computeMu(normalized_point_pressure_list);
	    double sigma = this.computeSigma(normalized_point_pressure_list, mu);

	    this.pressure_muSigmaValues.addMuSigma(mu, sigma);
	}
    }

    private void compute_point_distance_mu_sigma() {
	// compute mu sigma for point distance
	List<Double> normalized_point_distance_list = null;
	this.point_distance_muSigmaValues = new MuSigma();

	// for each point in the distribution, compute mu an sigma
	for (int i = 0; i < this.normalizedResponses.get(0).getResponse().size(); i++) {
	    // go though each of the responses collecting value
	    // of point i in the response
	    normalized_point_distance_list = new ArrayList<Double>();
	    for (Response response : this.normalizedResponses) {
		// distance values in the list correspond to point distance
		normalized_point_distance_list.add(response.getResponse().get(i).getDistance());
	    }

	    // compute the average (mu)
	    // compute std deviation
	    double mu = this.computeMu(normalized_point_distance_list);
	    double sigma = this.computeSigma(normalized_point_distance_list, mu);

	    this.point_distance_muSigmaValues.addMuSigma(mu, sigma);
	}
    }

    private void compute_time_distance_mu_sigma() {
	// compute mu sigma for point distance
	List<Double> normalized_time_list = null;
	this.time_muSigmaValues = new MuSigma();

	// for each point in the distribution, compute mu an sigma
	for (int i = 0; i < this.normalizedResponses.get(0).getResponse().size(); i++) {
	    // go though each of the responses collecting value
	    // of point i in the response
	    normalized_time_list = new ArrayList<Double>();
	    for (Response response : this.normalizedResponses) {
		// grab the time values from ith point in the list of responses
		normalized_time_list.add(response.getResponse().get(i).getTime());
	    }

	    // compute the average (mu)
	    // compute std deviation
	    double mu = this.computeMu(normalized_time_list);
	    double sigma = this.computeSigma(normalized_time_list, mu);
	    // System.out.println("mu: " + mu + " | time_list: " +
	    // normalized_time_list + "\n");

	    this.time_muSigmaValues.addMuSigma(mu, sigma);
	}
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
