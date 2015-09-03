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
     * @return
     */
    private List<List<Double>> normalizeChallengeResponseList(List<ChallengeResponse> challengeResponseList) {
	ArrayList<List<Double>> normalizedList = new ArrayList<List<Double>>();
	ArrayList<Double> normalizedElement = new ArrayList<Double>();

	// TODO normalize the list

	return normalizedList;
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
