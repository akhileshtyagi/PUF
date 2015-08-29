package muSigmaModel;

import java.util.ArrayList;
import java.util.List;

import data.ChallengeResponse;

/**
 * Takes a ChallengeResponse object in its constructor and creates the mu-sigma
 * model from this.
 * 
 * @author element
 *
 */
public class NormalizedResponse {
    // list of {(mu_x,mu_y,sigma_x,sigma_y), (mu_x,mu_y,sigma_x,sigma_y), ...}
    List<List<Double>> sigmaModel;
    int challengeNumber;

    /**
     * Takes a challenge response object list. This list should contain
     * challengeResponse objects for a single (user, challenge) with many
     * responses. Uses this object to construct the normalized mu-sigma model.
     * 
     * @param challengeResponse
     */
    public NormalizedResponse(List<ChallengeResponse> challengeResponseList, int challengeNumber) {
	this.challengeNumber = challengeNumber;

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
     * compute average of the list
     */
    private double computeMu(List<Double> list) {
	// TODO
	return 0;
    }

    /**
     * compute the standard deviation for the list
     * 
     * @return
     */
    private double computeSigma(List<Double> list) {
	// TODO
	return 0;
    }

    /*
     * get the mu-sigmal model.
     */
    public List<List<Double>> getModel() {
	return this.sigmaModel;
    }
}
