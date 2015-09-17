package dataTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents challenge which contains the pattern of points for the corresponding challenge,
 * responses that a user will create for said challenge, and that user's profile which will be
 * correlated with this challenge
 */
public class Challenge {

    // Pattern of points that create the challenge
    private List<Point> challengePattern;

    // Unique identifier to distinguish which challenge this is
    private int challengeID;

    // List of NORMALIZED responses, each representing one response by the user for this challenge
    private List<Response> responses;

    // Profile associated with this challenge
    private Profile profile;


    public Challenge(List<Point> challengePattern, int challengeID) {
        this.challengePattern = challengePattern;
        this.challengeID = challengeID;
        responses = new ArrayList<Response>();
        profile = new Profile();
    }

    // Adds normalized response to the list or Responses
    public void addResponse(Response response) {
        responses.add(response);
    }

    // Creates a profile associate with this challenge with NORMALIZED responses
    public Profile createPofile() {
        profile = new Profile(responses);
        return profile;
    }

    public Profile getProfile() {
        return profile;
    }

}
