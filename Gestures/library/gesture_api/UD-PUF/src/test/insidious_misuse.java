package test;

import dataTypes.Challenge;
import dataTypes.Point;
import dataTypes.Response;
import dataTypes.UserDevicePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by element on 2/9/16.
 * <p>
 * The goal of this class is the misuse the library
 * in ways that are reasonable mistakes.
 * <p>
 * Hopefully we can break it a couple times and fix rare issues.
 */
public class insidious_misuse {
    public static void main(String[] args) {
        // set up user_device pair with a few challenges
        UserDevicePair ud_pair = create_generated_ud_pair();

        // authenticate a response against the profile
        ArrayList<Point> response_points = generate_response_points();

        // try to grab point vectors before authentication
        // get the point vectors
        List<Double> pressure_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.PRESSURE);
        List<Double> distance_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.DISTANCE);
        List<Double> time_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.TIME);

        // print out point vectors
        System.out.println("Before Authentication");
        System.out.println("Pressure:\t" + pressure_point_vector);
        System.out.println("Distance:\t" + distance_point_vector);
        System.out.println("Time:\t\t" + time_point_vector);
        System.out.println("\n");

        // authenticate
        ud_pair.authenticate(response_points, 0);

        // get the point vectors
        pressure_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.PRESSURE);
        distance_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.DISTANCE);
        time_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.TIME);

        // print out point vectors
        System.out.println("After authentication");
        System.out.println("Pressure:\t" + pressure_point_vector);
        System.out.println("Distance:\t" + distance_point_vector);
        System.out.println("Time:\t\t" + time_point_vector);
        System.out.println("\n");

        // TODO now that we've done an authentication that works, mess with the trace until it breaks
        int test = 2;

        // 1) try adding a response of differant sizes
        if(test == 1) {
            Challenge user_challenge = ud_pair.getChallenges().get(0);

            // create the other challenge
            List<Point> challenge_pattern = new ArrayList<Point>();
            Challenge other_challenge_1 = new Challenge(challenge_pattern, 1);

            // create a list of response points
            Response response_points_1 = new Response(generate_response_points());

            // mess with response points
            //response_points_1.getOrigionalResponse().add(new Point(555, 939, 0.0));
            //response_points_1.getOrigionalResponse().remove(0);

            //other_challenge_1.addResponse(response_points_1);
            user_challenge.addResponse(response_points_1);
        }

        // 2) try using the response points to normalize a new response
        if(test == 2){
            Response response_2 = new Response(generate_response_points());
            response_2.normalize(response_2.getOrigionalResponse());

            // TODO

            response_points = new ArrayList<Point>(response_2.getNormalizedResponse());
        }

        // authenticate
        ud_pair.authenticate(response_points, 0);

        pressure_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.PRESSURE);
        distance_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.DISTANCE);
        time_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.TIME);

        // print out point vectors
        System.out.println("after having been messed with");
        System.out.println("Pressure:\t" + pressure_point_vector);
        System.out.println("Distance:\t" + distance_point_vector);
        System.out.println("Time:\t\t" + time_point_vector);
        System.out.println("\n");

         /* GRAPHING OF POINTS */
        // Graph the point lists to see normalization is going correctly
        //graph_points graph_frame = new graph_points();

        //graph_frame.addPointList(response_points, "origional_response_points");
        //graph_frame.addPointList(ud_pair.getChallenges().get(0).getNormalizingPoints(), "normalizing_points");

        // want the normalized response points to graph
        //Response response = new Response(response_points);
        //ud_pair.getChallenges().get(0).addResponse(response);
        //graph_frame.addPointList(response.getNormalizedResponse(), "normalized_response_points");
    }

    /**
     * generates some response points
     */
    public static ArrayList<Point> generate_response_points() {
        ArrayList<Point> response_points = new ArrayList<Point>();

        // create the response
        int num_points = 10;
        for (int j = 0; j < num_points; j++) {
            response_points.add(new Point((300 / num_points) * j + 100, 100, 1.5, j, j));
        }

        return response_points;
    }

    /**
     * returns a UserDevicePair preloaded with some challanges
     */
    public static UserDevicePair create_generated_ud_pair() {
        Challenge challenge;
        Response response;
        List<Point> response_points;

        // create a userDeficePair
        UserDevicePair ud_pair = new UserDevicePair(0);

        // create a list of challenge points
        List<Point> challenge_points = new ArrayList<Point>();

        // sample points for testing
        challenge_points.add(new Point(100, 100, 0));
        challenge_points.add(new Point(200, 100, 0));
        challenge_points.add(new Point(300, 100, 0));
        challenge_points.add(new Point(400, 100, 0));

        // add the challenge to it which I want to authenticate against
        // create 3 responses to add to this challenge
        challenge = new Challenge(challenge_points, 0);

        for (int i = 0; i < 3; i++) {
            response_points = new ArrayList<Point>();

            // create the response
            for (int j = 0; j < 32; j++) {
                response_points.add(new Point((300 / 31) * j + 100, 100, i, 100, j));
            }

            response = new Response(response_points);
            challenge.addResponse(response);
            //System.out.println("response_n1:\t" + response.getNormalizedResponse());

            // tried normaliing a second time to see what would be the effect
            //challenge.addResponse(response);
            //System.out.println("response_n2:\t" + response.getNormalizedResponse());
        }

        // the mu sigma for the responses should be
        // mu : 1
        // sigma : sqrt(2/3)
        ud_pair.addChallenge(challenge);

        return ud_pair;
    }
}
