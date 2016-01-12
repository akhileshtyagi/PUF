package test;

import java.util.ArrayList;
import java.util.List;

import dataTypes.Point;
import dataTypes.Response;
import dataTypes.UserDevicePair;

/**
 * this class tests that UserDevicePair returns point_vector correctly.
 */
public class UserDevicePair_point_vector {
    public static void main(String[] args) {
        // set up user_device pair with a few challenges
        UserDevicePair ud_pair = TestUtil.create_generated_ud_pair();

        // authenticate a response against the profile
        ArrayList<Point> response_points = new ArrayList<Point>();

        // create the response
        for (int j = 0; j < 32; j++) {
            response_points.add(new Point((300 / 32) * j + 100, 100, 1.5));
        }

        // authenticate
        ud_pair.authenticate(response_points, 0);

        // get the point vectors
        List<Double> pressure_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.PRESSURE);
        List<Double> distance_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.DISTANCE);
        List<Double> time_point_vector = ud_pair.getNew_response_point_vector(UserDevicePair.RatioType.TIME);

        // TODO test the point vectors for correctness
//        for (int i = 0; i < pressure_point_vector.size(); i++) {
//            // check a single point in each vector
//            if (pressure_point_vector.get(i) == 0) {
//                System.out.println("Pressure[" + i + "] incorrect");
//            }
//
//            if (distance_point_vector.get(i) == 0) {
//                System.out.println("Distance[" + i + "] incorrect");
//            }
//
//            if (time_point_vector.get(i) == 0) {
//                System.out.println("Time[" + i + "] incorrect");
//            }
//        }

        // print out normalized respoonse and profile information


        // print out point vectors
        System.out.println("Pressure:\t" + pressure_point_vector);
        System.out.println("Distance:\t" + distance_point_vector);
        System.out.println("Time:\t\t" + time_point_vector);
    }
}
