package test;

import java.util.ArrayList;
import java.util.List;

import dataTypes.Challenge;
import dataTypes.Point;
import dataTypes.Response;

/**
 * Created by element on 1/15/16.
 * <p>
 * tests the graph points class
 */
public class graph_points_test {
    private enum ResponseNature {
        NEGATIVE_STAIRS,
        NEGATIVE_SLOPE_LINE,
        POSITIVE_ARC,
        POSITIVE_SLOPE_LINE,
        CIRCLE
    }

    public static void main(String[] args) {
        graph_points graph_frame = new graph_points();

        // create response point list
        List<Point> response_points = create_response_point_list(ResponseNature.NEGATIVE_SLOPE_LINE);

        // create challenge pattern
        List<Point> challenge_pattern = create_challenge_pattern();

        // create a response and normalize it
        Challenge challenge = new Challenge(challenge_pattern, 0);
        Response response = new Response(response_points);
        challenge.addResponse(response);

        graph_frame.addPointList(response_points, "origional_response_points");
        graph_frame.addPointList(response.getNormalizedResponse(), "normalized_response_points");
    }

    /**
     * create response points.
     * <p>
     * the normalizing points in the challenge will be computed from the first response
     */
    private static List<Point> create_response_point_list(ResponseNature nature) {
        List<Point> points = new ArrayList<Point>();

        switch (nature) {
            case NEGATIVE_STAIRS:
                points.add(new Point(100, 100, 0));

                points.add(new Point(100, 300, 0));
                points.add(new Point(300, 300, 0));
                points.add(new Point(300, 400, 0));

                points.add(new Point(300, 500, 0));
                points.add(new Point(500, 500, 0));
                points.add(new Point(500, 600, 0));

                points.add(new Point(500, 700, 0));
                points.add(new Point(700, 700, 0));
                points.add(new Point(700, 800, 0));

                points.add(new Point(700, 900, 0));
                points.add(new Point(900, 900, 0));
                break;
            case NEGATIVE_SLOPE_LINE:
                points.add(new Point(100, 100, 0));
                points.add(new Point(150, 150, 0));
                points.add(new Point(250, 250, 0));
                points.add(new Point(400, 400, 0));
                points.add(new Point(600, 600, 0));
                points.add(new Point(850, 850, 0));
                break;
            case POSITIVE_SLOPE_LINE:

                break;
            case POSITIVE_ARC:
                points.add(new Point(850, 100, 0));
                points.add(new Point(600, 150, 0));
                points.add(new Point(400, 250, 0));
                points.add(new Point(250, 400, 0));
                points.add(new Point(150, 600, 0));
                points.add(new Point(100, 850, 0));
                break;
            case CIRCLE:
                int points_around_circle = 100;

                // circle options
                int radius = 200;
                int center_x = 400;
                int center_y = 400;

                // (x−h)^2+(y−k)^2=r^2
                // for radius = 200, r^2 = 40000
                // centered at 400, 400 => h=400, k=400
                // (x-400)^2 + (y-400)^2 = 200^2

                // draw upper half
                for(int i=0; i < points_around_circle/2; i++){
                    // compute the circle point
                    int circle_x_max = center_x + radius;
                    int circle_x = circle_x_max - i * ((radius * 2) / (points_around_circle / 2));
                    int circle_y = (int)(Math.sqrt(radius^2 - (circle_x - center_x)^2) - center_y);

                    // add the point to the list
                    points.add(new Point(circle_x, circle_y, 0));
                }

                // TODO draw lower half

                break;
        }

        return points;
    }

    /**
     * create challenge pattern.
     * <p>
     * For current purposes this doesn't matter in the least.
     */
    private static List<Point> create_challenge_pattern() {
        List<Point> points = new ArrayList<Point>();

        points.add(new Point(50, 50, 0));

        points.add(new Point(50, 250, 0));
        points.add(new Point(250, 250, 0));
        points.add(new Point(200, 250, 0));

        points.add(new Point(50, 450, 0));
        points.add(new Point(450, 500, 0));
        points.add(new Point(500, 500, 0));

        points.add(new Point(100, 700, 0));
        points.add(new Point(700, 700, 0));

        points.add(new Point(100, 900, 0));
        points.add(new Point(900, 900, 0));

        return points;
    }
}
