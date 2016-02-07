package test;

import dataTypes.Challenge;
import dataTypes.Point;
import dataTypes.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * try passing in responses without a duplicate value and
 * responses with a duplicate value.
 * <p>
 * See if any trigger the System.out.println statement that
 * indicates points were removed.
 */
public class remove_duplicates_test {
    public static void main(String[] args) {
        Challenge challenge = TestUtil.generate_challenge();
        List<Point> response_point_list_no_d = new ArrayList<Point>();
        List<Point> response_point_list_one_d = new ArrayList<Point>();
        List<Point> response_point_list_many_d = new ArrayList<Point>();

        // no duplicates
        Response response_no_duplicates;

        // one duplicate at the end of the response
        Response response_one_duplicate;

        // many duplicates including the first two and the last two
        Response response_many_duplicates;

        // initialize the test responses
        int num_response_points = 10;
        for (int i = 0; i < num_response_points; i++) {
            response_point_list_no_d.add(new Point(i, i, i, 0, i));
            response_point_list_one_d.add(new Point(i, i, i, 0, i));
            response_point_list_many_d.add(new Point(i, i, i, 0, i));
        }

        // create no duplicate response
        response_no_duplicates = new Response(response_point_list_no_d);

        // create one duplicate response
        response_point_list_one_d.add(new Point(num_response_points - 1, num_response_points - 1, 0, 0, 0));
        response_one_duplicate = new Response(response_point_list_one_d);

        // create many duplicate response
        response_point_list_many_d.add(new Point(num_response_points - 1, num_response_points - 1, 0, 0, 0));
        response_point_list_many_d.add(0, new Point(0, 0, 0, 0, 0));
        response_point_list_many_d.add((int)Math.floor(num_response_points / 2.0)+1, new Point(Math.floor(num_response_points / 2.0), Math.floor(num_response_points / 2.0), 0, 0, 0));
        response_many_duplicates = new Response(response_point_list_many_d);

        // points should not be removed
        challenge.addResponse(response_no_duplicates);

        // points should be removed
        challenge.addResponse(response_one_duplicate);
        challenge.addResponse(response_many_duplicates);
    }
}
/**
 * it doesn't look like the correct points are being compared.
 * check prev and current point to be sure they are correct.
 */
