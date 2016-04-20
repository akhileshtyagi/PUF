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
    final static ResponseNature NATURE = ResponseNature.CHECK;

    private enum ResponseNature {
        NEGATIVE_STAIRS,
        NEGATIVE_SLOPE_LINE,
        NEGATIVE_SLOPE_LINE_BACK,
        POSITIVE_ARC,
        POSITIVE_SLOPE_LINE,
        POSITIVE_SLOPE_LINE_BACK,
        CIRCLE,
        RIGHT_TO_LEFT,
        TOWARD_ORIGIN_THEN_AWAY,
        CHECK
    }

    public static void main(String[] args) {
        graph_points graph_frame = new graph_points();

        // create response point list
        List<Point> response_points = create_response_point_list(NATURE);

        // create challenge pattern
        List<Point> challenge_pattern = create_challenge_pattern();

        // create a response and normalize it
        Challenge challenge = new Challenge(challenge_pattern, 0);
        Response response = new Response(response_points);
        graph_frame.addPointList(response_points, "origional_response_points");
        challenge.addResponse(response);

        graph_frame.addPointList(challenge.getNormalizingPoints(), "normalizing_points");
        graph_frame.addPointList(response.getNormalizedResponse(), "normalized_response_points");

        // print out response and normalized response
        System.out.println("origional_response:\t" + response.getOrigionalResponse());
        System.out.println("normalizing_points:\t" + challenge.getNormalizingPoints());
        System.out.println("normalized_response:\t" + response.getNormalizedResponse());
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
                points.add(new Point(100, 100, .1));

                points.add(new Point(100, 300, .2));
                points.add(new Point(300, 300, .3));
                points.add(new Point(300, 400, .4));

                points.add(new Point(300, 500, .5));
                points.add(new Point(500, 500, .6));
                points.add(new Point(500, 600, .7));

                points.add(new Point(500, 700, .8));
                points.add(new Point(700, 700, .9));
                points.add(new Point(700, 800, 1));

                points.add(new Point(700, 900, 1.1));
                points.add(new Point(900, 900, 1.2));
                break;
            case NEGATIVE_SLOPE_LINE:
                points.add(new Point(100, 100, 0));
                points.add(new Point(150, 150, 0));
                points.add(new Point(250, 250, 0));
                points.add(new Point(400, 400, 0));
                points.add(new Point(600, 600, 0));
                points.add(new Point(850, 850, 0));
                break;
            case NEGATIVE_SLOPE_LINE_BACK:
                points.add(new Point(850, 850, 0));
                points.add(new Point(600, 600, 0));
                points.add(new Point(400, 400, 0));
                points.add(new Point(250, 250, 0));
                points.add(new Point(150, 150, 0));
                points.add(new Point(100, 100, 0));
                break;
            case POSITIVE_SLOPE_LINE:
                points.add(new Point(100, 800, 0));
                points.add(new Point(150, 750, 0));
                points.add(new Point(250, 650, 0));
                points.add(new Point(400, 500, 0));
                points.add(new Point(600, 300, 0));
                points.add(new Point(850, 50, 0));
                break;
            case POSITIVE_SLOPE_LINE_BACK:
                points.add(new Point(100, 800, 0));
                points.add(new Point(150, 750, 0));
                points.add(new Point(250, 650, 0));
                points.add(new Point(400, 500, 0));
                points.add(new Point(600, 300, 0));
                points.add(new Point(850, 50, 0));
                break;
            case POSITIVE_ARC:
                points.add(new Point(850, 100, 0));
                points.add(new Point(600, 150, 0));
                points.add(new Point(400, 250, 0));
                points.add(new Point(250, 400, 0));
                points.add(new Point(150, 600, 0));
                points.add(new Point(100, 850, 0));
                break;
            case RIGHT_TO_LEFT:
                points.add(new Point(800, 400, 0));
                points.add(new Point(700, 400, 0));
                points.add(new Point(550, 400, 0));
                points.add(new Point(500, 400, 0));
                points.add(new Point(470, 400, 0));
                points.add(new Point(350, 400, 0));
                points.add(new Point(200, 400, 0));
                break;
            case TOWARD_ORIGIN_THEN_AWAY:
                points.add(new Point(400, 500, 0));
                points.add(new Point(300, 350, 0));
                points.add(new Point(200, 200, 0));

                points.add(new Point(100, 100, 0));

                points.add(new Point(250, 150, 0));
                points.add(new Point(400, 200, 0));
                points.add(new Point(450, 250, 0));
                break;
            case CHECK:
                // on nexus 7 ( josh, tim )
                points.add(new Point(88, 75, 0));
                points.add(new Point(188, 376, 0));
                points.add(new Point(288, 275, 0));
                break;
            case CIRCLE:
                // works with circles of size 6
                // does not work on circle of size 10
                int points_around_circle = 20;

                // circle options
                int radius = 200;
                int center_x = 400;
                int center_y = 400;

                // (x−h)^2+(y−k)^2=r^2
                // for radius = 200, r^2 = 40000
                // centered at 400, 400 => h=400, k=400
                // (x-400)^2 + (y-400)^2 = 200^2

                // circle
                for (int i = 0; i < points_around_circle; i++) {
                    // compute theta
                    double theta = i * 2 * Math.PI / points_around_circle;

                    // compute the circle point
                    int circle_x = (int)(center_x + radius * Math.cos(theta));
                    int circle_y = (int)(center_y + radius * Math.sin(theta));

                    // add the point to the list
                    points.add(new Point(circle_x, circle_y, 0));
                }

                // repeat the first point
                points.add(new Point((int)(center_x + radius), (int)(center_y), 0));

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
