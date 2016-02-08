package test;

import java.util.ArrayList;
import java.util.List;

import dataTypes.Challenge;
import dataTypes.Point;
import dataTypes.Response;
import dataTypes.UserDevicePair;
import metrics.Metric;
import metrics.PointMetrics;
import metrics.PressureMetric;
import metrics.TimeMetric;

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

        // Graph the point lists to see normalization is going correctly
        graph_points graph_frame = new graph_points();

        graph_frame.addPointList(response_points, "origional_response_points");
        graph_frame.addPointList(ud_pair.getChallenges().get(0).getNormalizingPoints(), "normalizing_points");

        // want the normalized response points to graph
//        Response response = new Response(response_points);
//        ud_pair.getChallenges().get(0).addResponse(response);
//        graph_frame.addPointList(response.getNormalizedResponse(), "normalized_response_points");
    }

    /**
     * generates some response points
     */
    public static ArrayList<Point> generate_response_points() {
        ArrayList<Point> response_points = new ArrayList<Point>();
        PointMetrics point_metrics = new PointMetrics();

        // create the response
        int num_points = 10;
        for (int j = 0; j < num_points; j++) {
            point_metrics = new PointMetrics();
            point_metrics.add_metric(new PressureMetric(1.5));
            point_metrics.add_metric(new TimeMetric(17.0));

            response_points.add(new Point((300 / num_points) * j + 100, 100, point_metrics));
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
        PointMetrics point_metrics = new PointMetrics();

        // create a userDeficePair
        UserDevicePair ud_pair = new UserDevicePair(0);

        // create a list of challenge points
        List<Point> challenge_points = new ArrayList<Point>();

        // sample points for testing
        challenge_points.add(new Point(100, 100));
        challenge_points.add(new Point(200, 100));
        challenge_points.add(new Point(300, 100));
        challenge_points.add(new Point(400, 100));

        // add the challenge to it which I want to authenticate against
        // create 3 responses to add to this challenge
        challenge = new Challenge(challenge_points, 0);

        for (int i = 0; i < 3; i++) {
            response_points = new ArrayList<Point>();

            // create the response
            for (int j = 0; j < 32; j++) {
                point_metrics = new PointMetrics();
                point_metrics.add_metric(new PressureMetric(1.0 * i));
                point_metrics.add_metric(new TimeMetric(1.0 * j));

                response_points.add(new Point((300 / 31) * j + 100, 100, point_metrics));
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