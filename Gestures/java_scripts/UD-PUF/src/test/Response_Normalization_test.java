package test;

import java.util.ArrayList;

import dataTypes.Point;
import dataTypes.Response;

// problem: sometimes the normalized responses contain NAN or Infinity
// 1) often times when pressure changes, normalized values becomes NAN or Infinity
// 2) 0.0/0.0 => NaN
// 3) num/0.0 => Infinity
// 4) pressure distance and time all become Infinity or NaN on the same points
// 5) when the left and right points are equal there is division by 0 in normalization

// specific problem: left and right points are becoming the same in the middle
// 1) look at where correction for left point being one 

// possible solutions:
// 1) slope is very large causing infinity

/**
 * Created by element on 1/13/16.
 */
public class Response_Normalization_test {
    public static void main(String[] args) {
        ArrayList<Point> response_points = new ArrayList<Point>();
        ArrayList<Point> normalization_points = new ArrayList<Point>();
        boolean is_challenge_horizontal = true;

        // create the response with enormous slope
//        for (int j = 0; j < 2; j++) {
//            response_points.add(new Point((300 / 32) * j + 100, 100, 1.5));
//        }

        // create normalization points
//        for (int j = 0; j < 2; j++) {
//            normalization_points.add(new Point(1* j + 100, 100, 1.5));
//        }

        // (x,y,pressure,distance,time)
        // create response points
        response_points.add(new Point(0, 1158, 0.825, 0, 0));
        response_points.add(new Point(0, 1165, .825, 0, 0));

        // create normalization points
        normalization_points.add(new Point(841, 1161, 0));
        //normalization_points.add(new Point(1* j + 100, 100, 1.5));

        // normalize response
        Response response = new Response(response_points);
        response.normalize(normalization_points, is_challenge_horizontal);

        // print response
        System.out.println(response.getNormalizedResponse());
    }
}

//abstraction of normalization
/**
 * for point in normalization_points
 *   find points on left and right
 *   compute slope between left and right points
 *     if the points are not one index away from each other
 *       which point we use depends on which of the left and right point is closest to the previous point
 *   multiply slope by distance from left  or right point (and add value of that point to this value)
 */

