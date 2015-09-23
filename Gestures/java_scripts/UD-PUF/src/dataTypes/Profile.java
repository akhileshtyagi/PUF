package dataTypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a profile containing Mu and Sigma values, along with a list of
 * normalized challanges
 */
public class Profile {


    // List of normalized Responses
    private List<Response> normalizedResponses;

    // Mu Sigma values that define the profile
    private List<MuSigma> muSigmaValues;

    public Profile(List<Response> normalizedResponses) {
	this.normalizedResponses = normalizedResponses;

	// TODO
	// Calculate mu and sigma values for this profile
	// and assign them to muValues and sigmaValues
	// For now, just create blank ones
    muSigmaValues = new ArrayList<MuSigma>();

    }

    // Constructor without normalized responses, for initially constructing a
    // challenge
    public Profile() {
    normalizedResponses = new ArrayList<Response>();
    muSigmaValues = new ArrayList<MuSigma>();
    }

    public void addNormalizedResponses(List<Response> normalizedResponses) {
	this.normalizedResponses = normalizedResponses;
    }

    public List<MuSigma> getMuSigmaValues() {
	return muSigmaValues;
    }

    public List<Response> getNormalizedResponses() {
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
