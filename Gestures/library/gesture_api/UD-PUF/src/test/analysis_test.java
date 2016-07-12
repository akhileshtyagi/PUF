package test;

import analysis.Test;
import dataTypes.Point;
import dataTypes.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by element on 4/13/16.
 *
 * the goal of this test is to tes the classes:
 * Effectiveness
 * Test
 * Combination
 * BestModelParameters
 */
public class analysis_test {
    public static void main(String args[]) {
        // generate a response set
        ArrayList<Response> response_set =  new ArrayList<>();
        for(int i=0; i<20; i++){
            response_set.add(new Response(UserDevicePair_point_vector.generate_response_points()));
        }

        // Generate some response points
        ArrayList<Point> response_points = UserDevicePair_point_vector.generate_response_points();

        // create a Test, this will run it
        double p_dev = 3;
        double dist_dev = 1;
        double time_dev = 1;
        double velocity_dev = 1;
        double acceleration_dev = 1;
        double time_length_dev = 1;

        double pressure_thresh = .3;
        double distance_thresh = 1;
        double time_thresh = 1;
        double acceleration_thresh = 1;
        double velocity_thresh = 1;
        Test test = new Test(
                new Response(response_points),
                response_set,
                true,
                response_points,
                p_dev,
                dist_dev,
                time_dev,
                velocity_dev,
                acceleration_dev,
                time_length_dev,
                pressure_thresh,
                distance_thresh,
                time_thresh,
                velocity_thresh,
                acceleration_thresh);

        // see if the result of test is as expected
        System.out.println(test.authentication_result);
    }
}