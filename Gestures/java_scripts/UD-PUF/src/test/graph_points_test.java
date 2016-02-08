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
        NEGATIVE_SLOPE_LINE_BACK,
        POSITIVE_ARC,
        POSITIVE_SLOPE_LINE,
        POSITIVE_SLOPE_LINE_BACK,
        CIRCLE,
        RIGHT_TO_LEFT,
        TOWARD_ORIGIN_THEN_AWAY
    }

    public static void main(String[] args) {
        graph_points graph_frame = new graph_points();

        // create response point list
        List<Point> response_points = create_response_point_list(ResponseNature.NEGATIVE_STAIRS);

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
                points.add(new Point(100, 100));

                points.add(new Point(100, 300));
                points.add(new Point(300, 300));
                points.add(new Point(300, 400));

                points.add(new Point(300, 500));
                points.add(new Point(500, 500));
                points.add(new Point(500, 600));

                points.add(new Point(500, 700));
                points.add(new Point(700, 700));
                points.add(new Point(700, 800));

                points.add(new Point(700, 900));
                points.add(new Point(900, 900));
                break;
            case NEGATIVE_SLOPE_LINE:
                points.add(new Point(100, 100));
                points.add(new Point(150, 150));
                points.add(new Point(250, 250));
                points.add(new Point(400, 400));
                points.add(new Point(600, 600));
                points.add(new Point(850, 850));
                break;
            case NEGATIVE_SLOPE_LINE_BACK:
                points.add(new Point(850, 850));
                points.add(new Point(600, 600));
                points.add(new Point(400, 400));
                points.add(new Point(250, 250));
                points.add(new Point(150, 150));
                points.add(new Point(100, 100));
                break;
            case POSITIVE_SLOPE_LINE:
                points.add(new Point(100, 800));
                points.add(new Point(150, 750));
                points.add(new Point(250, 650));
                points.add(new Point(400, 500));
                points.add(new Point(600, 300));
                points.add(new Point(850, 50));
                break;
            case POSITIVE_SLOPE_LINE_BACK:
                points.add(new Point(100, 800));
                points.add(new Point(150, 750));
                points.add(new Point(250, 650));
                points.add(new Point(400, 500));
                points.add(new Point(600, 300));
                points.add(new Point(850, 50));
                break;
            case POSITIVE_ARC:
                points.add(new Point(850, 100));
                points.add(new Point(600, 150));
                points.add(new Point(400, 250));
                points.add(new Point(250, 400));
                points.add(new Point(150, 600));
                points.add(new Point(100, 850));
                break;
            case RIGHT_TO_LEFT:
                points.add(new Point(800, 400));
                points.add(new Point(700, 400));
                points.add(new Point(550, 400));
                points.add(new Point(500, 400));
                points.add(new Point(470, 400));
                points.add(new Point(350, 400));
                points.add(new Point(200, 400));
                break;
            case TOWARD_ORIGIN_THEN_AWAY:
                points.add(new Point(400, 500));
                points.add(new Point(300, 350));
                points.add(new Point(200, 200));

                points.add(new Point(100, 100));

                points.add(new Point(250, 150));
                points.add(new Point(400, 200));
                points.add(new Point(450, 250));
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
                    points.add(new Point(circle_x, circle_y));
                }

                // repeat the first point
                points.add(new Point((int)(center_x + radius), (int)(center_y)));

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

        points.add(new Point(50, 50));

        points.add(new Point(50, 250));
        points.add(new Point(250, 250));
        points.add(new Point(200, 250));

        points.add(new Point(50, 450));
        points.add(new Point(450, 500));
        points.add(new Point(500, 500));

        points.add(new Point(100, 700));
        points.add(new Point(700, 700));

        points.add(new Point(100, 900));
        points.add(new Point(900, 900));

        return points;
    }
}
