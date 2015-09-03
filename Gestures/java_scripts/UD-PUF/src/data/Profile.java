package data;

import java.util.List;

import muSigmaModel.NormalizedModel;

/**
 * Contains all data necessary for authentication of the user. This class could
 * potentially act as a wrapper around the DataReader, ChallengeResponse, and
 * NormalizedResponse classes. This might make it easier/ more intuitive to
 * construct a profile.
 * 
 * @author element
 *
 */
public class Profile {
    List<ChallengeResponse> challengeResponseList;
    NormalizedModel normalizedResponse;

    /**
     * All data necessary for authentication should be stored here
     * 
     * @param challengeResponseList
     * @param normalizedResponse
     */
    public Profile(List<ChallengeResponse> challengeResponseList, NormalizedModel normalizedResponse) {
	this.challengeResponseList = challengeResponseList;
	this.normalizedResponse = normalizedResponse;
    }
}
