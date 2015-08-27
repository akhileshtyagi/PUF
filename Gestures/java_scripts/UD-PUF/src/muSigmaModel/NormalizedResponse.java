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
    // list of {(mu_x,mu_y,sigma), (mu_x,mu_y,sigma), ...}
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
	this.sigmaModel = normalizeChallengeResponseList(challengeResponseList);
    }

    /**
     * creates the normalized challenge response list.
     * 
     * @return
     */
    private List<List<Double>> normalizeChallengeResponseList(List<ChallengeResponse> challengeResponseList) {
	ArrayList<List<Double>> normalizedList = new ArrayList<List<Double>>();
	ArrayList<Double> normalizedElement = new ArrayList<Double>();

	// TODO compute necessary information about the ChallengeResponseLIst

	// TODO normalize the list
	
	return normalizedList;
    }

    /*
     * get the mu-sigmal model. {(mu_x,mu_y,sigma), (mu_x,mu_y,sigma), ...}
     */
    public List<List<Double>> getModel() {
	return this.sigmaModel;
    }
}
