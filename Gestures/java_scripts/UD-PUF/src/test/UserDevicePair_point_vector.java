package test;

import java.util.ArrayList;
import java.util.List;

import dataTypes.Challenge;
import dataTypes.Point;
import dataTypes.Response;
import dataTypes.UserDevicePair;

/**
 * this class tests that UserDevicePair returns point_vector correctly.
 */
public class UserDevicePair_point_vector {
    public static void main(String[] args) {
        // set up user_device pair with a few challenges
        UserDevicePair ud_pair = create_generated_ud_pair();


        // authenticate a response against the profile
        ArrayList<Point> response_points = generate_response_points();

        // authenticate
        ud_pair.authenticate(response_points, 0);

        // get the point vectors
        List<Double> pressure_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.PRESSURE);
        List<Double> distance_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.DISTANCE);
        List<Double> time_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.TIME);

        // print out point vectors
        System.out.println("Pressure:\t" + pressure_point_vector);
        System.out.println("Distance:\t" + distance_point_vector);
        System.out.println("Time:\t\t" + time_point_vector);
    }

    /**
     * generates some response points
     */
    public static ArrayList<Point> generate_response_points(){
        ArrayList<Point> response_points  = new ArrayList<Point>();

        // create the response
        for (int j = 0; j < 32; j++) {
            response_points.add(new Point((300 / 32) * j + 100, 100, 1.5));
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
        }

        // the mu sigma for the responses should be
        // mu : 1
        // sigma : sqrt(2/3)
        ud_pair.addChallenge(challenge);

        return ud_pair;
    }
}