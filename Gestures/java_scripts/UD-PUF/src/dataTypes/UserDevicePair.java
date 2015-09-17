package dataTypes;

import java.util.List;

/**
 * Represents a user/device pair, containing a unique identifier and
 * a list of all challenges correlating to that user
 */
public class UserDevicePair {

    // List of challenges correlating to this user/device pair
    private List<Challenge> challenges;

    // Unique identifier given to each user/device pair
    private int userDeviceID;


    public UserDevicePair(int userDeviceID, List<Challenge> challenges) {
        this.challenges = challenges;
        this.userDeviceID = userDeviceID;
    }

    // Adds challenge to list of challenges correlating to this user/device pair
    public void addChallenge(Challenge challenge) {
        challenges.add(challenge);
    }
}
