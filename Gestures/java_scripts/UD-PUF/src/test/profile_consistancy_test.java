package test;

import dataTypes.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by element on 2/14/16.
 * <p>
 * the purpose of this class is to make sure the
 * profile mu_sigma values are still being computed in the same way.
 * <p>
 * This also tests the new way of adding metrics to point class.
 */
public class profile_consistancy_test {
    public static void main(String[] args) {
        // create a 2 normalized responses
        Challenge challenge = TestUtil.generate_challenge();

        // give these responses to ProfileOLD and Profile instances
        List<Point> response_0 = TestUtil.generate_response_points(2.0);
        List<Point> response_1 = TestUtil.generate_response_points(1.0);

        // build new profile
        challenge.addResponse(new Response(response_0));
        challenge.addResponse(new Response(response_1));

        // build old profile
        Profile profile_old = new Profile();

        List<Response> responses = new ArrayList<Response>();
        responses.add(new Response(response_0));
        responses.add(new Response(response_1));

        responses.get(0).normalize(challenge.getNormalizingPoints());
        responses.get(1).normalize(challenge.getNormalizingPoints());

        profile_old.addNormalizedResponses(responses);

        // test to see that the mu_sigma's are the same values
        Profile profile_new = challenge.getProfile();

        System.out.println(profile_old.getPressureMuSigmaValues().getMuValues().equals(profile_new.getPressureMuSigmaValues().getMuValues()));

        // print them to be sure the values are valid
        System.out.println(profile_old.getPressureMuSigmaValues().getMuValues());
    }
}
