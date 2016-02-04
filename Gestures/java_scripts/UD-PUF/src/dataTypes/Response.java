package dataTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents one response created by a user
 */
public class Response implements Serializable {
    private static final long serialVersionUID = -292775056595225846L;

    // List of points which the user swiped
    // always maintains the origional response pattern
    private ArrayList<Point> responsePattern;

    // normalized list of points. This will be overridden every time normalize
    // is called
    private ArrayList<Point> normalizedResponsePattern;

    // Count of motion events this response originally had
    private int motionEvenCount;

    public Response(List<Point> responsePattern) {
        this.responsePattern = new ArrayList<Point>(responsePattern);
        motionEvenCount = responsePattern.size();

        this.normalizedResponsePattern = this.responsePattern;
        // ArrayList<Point>(responsePattern);
    }

    /**
     * return the normalized response pattern
     */
    public List<Point> getOrigionalResponse() {
        return this.responsePattern;
        // return this.normalizedResponsePattern;
    }

    /**
     * return the normalized response pattern
     */
    public List<Point> getNormalizedResponse() {
        return this.normalizedResponsePattern;
    }

    public int getMotionEvenCount() {
        return motionEvenCount;
    }

    /**
     * returns the time of the last point minus the time of the first point.
     * This method preforms the function literally. the time mig variable will
     * be changed during normalization. After the point is normalized this will
     * most likely return a different value compared to before the response
     * being normalized.
     */
    public double getTimeLength() {
        return responsePattern.get(responsePattern.size() - 1).getTime() - responsePattern.get(0).getTime();
    }

    public void normalize(List<Point> normalizingPoints) {
        ArrayList<Point> newNormalizedList = new ArrayList<>();
        double xTransform, yTransform, theta, newX, newY, newPressure, newDistance;
        double traceDistance;

<<<<<<< HEAD
        double x_transform, y_transform;
        double radius_a, radius_b, radius_c;
        // Distance between each of the normalizing points
        double deltaD;
>>>>>>> ED_Polar_Normalization_Testing

        // Number of new Normalizing Points the current trace covers
        int NL;

        int N = normalizingPoints.size();
        int M = responsePattern.size();

        // Used to keep a running total of how far along the next point has gone
        double remainingDistance;

<<<<<<< HEAD
        // Index for counting responsePattern of trace: [j-1] is the point "before" CurNormalizedPoint, [j] is
        // point "after" CurNormalizedPoint
        int j = 1;
        for (int i = 1; i < normalizingPoints.size(); i++) {
            // assume no direction change for the last point
            if(i < normalizingPoints.size() -1) {
                // increment j only when we change direction. This is to avoid the same pair being chosen when we go around a curve.
                // in other words.
                // IF we are about to change direction
                // THAN we need to increment j
                // we know we are about to change direction when:
                // the relative radius of this point and the next point is the same
                // as the point two down the line and the next point
                // so if we have three points a, b, c than
                // if radius(a) < radius(b) && radius(c) < radius(b) than direction change
                // if radius(a) > radius(b) && radius(c) > radius(b) than direction change
                radius_a = getRadius(responsePattern.get(j - 1));
                radius_b = getRadius(responsePattern.get(j));
                radius_c = getRadius(responsePattern.get(j + 1));

                // determine if there will be a direction change and increment j accordingly
                if( ( (radius_a < radius_b) && (radius_c < radius_b) ) ||
                        ( (radius_a > radius_b) && (radius_c > radius_b) ) ){
                    j++;
                }
            }

            j = locate_closest_after_point(responsePattern, j, normalizingPoints.get(i));
        // Used when needing to interpolate more points if the trace isn't as long as list of normalizingPoints
        double d;
>>>>>>> ED_Polar_Normalization_Testing

        // Previous and current trace points when looping through response
        Point prevPoint, curPoint;

        // i counts indices of normalizingPoints
        // j counts indices of responsePattern
        int i = 0;
        int j = 0;

        xTransform = normalizingPoints.get(0).getX() - responsePattern.get(0).getX();
        yTransform = normalizingPoints.get(0).getY() - responsePattern.get(0).getY();

<<<<<<< HEAD
            /**
             * at this point we need pointLeft and pointRight to contain the points on either side of our normalization point.
             */
            System.out.println("left_point:" + (j - 1) + "\tright_point:" + j);

            // TODO check that interpolation is done correctly.
            // TODO figure out why pressure is always 0.
        // preform the transformation, add first point to new Normalized List
        transform_response(responsePattern, xTransform, yTransform);
        newNormalizedList.add(responsePattern.get(0));

        // Catch if normalizingTrace is only 1 point (hopefully never happens)
        if(normalizingPoints.size() == 1) {
            this.normalizedResponsePattern = newNormalizedList;
            return;
        }
>>>>>>> ED_Polar_Normalization_Testing

        deltaD = computeEuclideanDistance(normalizingPoints.get(0), normalizingPoints.get(1));

<<<<<<< HEAD
            next_normalized_point = compute_normalized_point_value(normalizingPoints.get(i), pointLeft, pointRight);

            // check that the left point is indeed the close point
//            if (getRadius(pointLeft) <= getRadius(pointRight)) {
//                next_normalized_point = compute_normalized_point_value(normalizingPoints.get(i), pointLeft, pointRight);
//            } else {
//                next_normalized_point = compute_normalized_point_value(normalizingPoints.get(i), pointRight, pointLeft);
//            }

            //System.out.println("Pressure for point (" + x_dist + ", " + y_dist + " ): " + nPressure);
            newNormalizedList.add(next_normalized_point);
        } // end outer for loop
        traceDistance = computeTraceDistance();
        NL = (int) Math.floor((traceDistance / deltaD) + 1);

        // added to handle the case where the response deltaD is longer than normalizingPoints
        NL = (NL <= normalizingPoints.size()) ? NL : normalizingPoints.size();

        remainingDistance = deltaD;
        j = 1;
>>>>>>> ED_Polar_Normalization_Testing

        // this loop will run NL-1 times
        for(i = 1; i < NL; i++) {
            prevPoint = responsePattern.get(j - 1);
            curPoint = responsePattern.get(j);

<<<<<<< HEAD
    /**
     * locate the closest point before the normalization point (the "left" point)
     * <p>
     * response_points, are is the list in which we want to find the index of the closest point before the
     * normalizing point
     * <p>
     * start_index, index to begin looking for the normalized point before
     * <p>
     * normalizing_point, we want to find the closest point index before this
     * <p>
     * returns -1 when a closest before point cannot be found
     */
    private int locate_closest_after_point(List<Point> response_points, int start_index, Point normalizing_point) {
        int k = -1;
        int j = start_index;
        boolean condition_a, condition_b;

        // find the first pair of points the normalizing point falls within
        boolean within_bounds = false;
        while(!within_bounds){
            k++;

            // check if we have gone beyond the bounds of the array without finding a point
            if ((j + k) > (responsePattern.size() - 1)) {
                return -1;
            }

            within_bounds = within_bounding_box(normalizing_point, responsePattern.get(j - 1 + k), responsePattern.get(j + k));
        }
            while(computeEuclideanDistance(prevPoint, curPoint) < remainingDistance) {
                if(j >= responsePattern.size()) {
                    this.normalizedResponsePattern = newNormalizedList;
                    return;
                }
                remainingDistance -= computeEuclideanDistance(prevPoint, curPoint);
                j++;
                prevPoint = responsePattern.get(j - 1);
                curPoint = responsePattern.get(j);
            }

            double x_difference = (curPoint.getX() - prevPoint.getX());
            double x_sine = 1;
            // check that the difference isn't equal to zero to ensure no divide by 0 error
            if (!(x_difference == 0)) {
                // this will make x_sine +1 or -1 depending on the sine
                x_sine = x_difference / Math.abs(x_difference);
            }

            // Now the point we are looking for is between responsePattern[j - 1] and responsePattern[j]
            theta = Math.atan((curPoint.getY() - prevPoint.getY()) / (curPoint.getX() - prevPoint.getX()));
            newX = prevPoint.getX() + (remainingDistance * Math.cos(theta) * x_sine);
            newY = prevPoint.getY() + (remainingDistance * Math.sin(theta) * x_sine);

            //Interpolate pressure and other attributes
            /* pressure */
            newPressure = prevPoint.getPressure() + ((remainingDistance/computeEuclideanDistance(prevPoint, curPoint)) * (curPoint.getPressure() - prevPoint.getPressure()));
            /* distance */
            newDistance = computeEuclideanDistance(new Point(newX, newY, 0), curPoint);
>>>>>>> ED_Polar_Normalization_Testing

            newNormalizedList.add(new Point(newX, newY, newPressure, newDistance));

<<<<<<< HEAD
    /**
     * takes in three points.
     * returns true if the first point is within a box created by the second two
     *
     * in other words, the second two points represent opposite corners of a box.
     */
    private boolean within_bounding_box(Point point, Point corner_point_a, Point corner_point_b){
        int width, height;
        int top_left_x, top_left_y;

        // need to determine top left corner, width, height of box
        /* width */
        width = Math.abs((int)(corner_point_a.getX() - corner_point_b.getX()));

        /* height */
        height = Math.abs((int)(corner_point_a.getY() - corner_point_b.getY()));

        /* top left */
        // whichever point is closer to the top, we want its y value
        // if point a is smaller in y than b
        if( (corner_point_a.getY() - corner_point_b.getY()) < 0) {
            // we know point a is closest to top
            top_left_y = (int)corner_point_a.getY();
        } else {
            // otherwise it must be the other point
            top_left_y = (int)corner_point_b.getY();
        }

        // whichever point is closer to the left, we want its x value
        // if point a is smaller in x than b
        if( (corner_point_a.getX() - corner_point_b.getX()) < 0){
            // point a is smaller and x, we want it's value
            top_left_x = (int)corner_point_a.getX();
        } else {
            top_left_x = (int)corner_point_b.getX();
        }

        // TODO test that top left corner, width, height are correct.

        /* is within box */
        // based on the top left corner, width, height of the box, determine if point falls within
        // we know the point is within the box if:
        // its x value falls within width of top left point x and
        // its y value falls within height of top left point y
        boolean within_x, within_y;

        within_x = (point.getX() >= top_left_x) && (point.getX() <= (top_left_x + width) );
        within_y = (point.getY() >= top_left_y) && (point.getY() <= (top_left_y + height) );

        // TODO check that within_x and within_y are correct

        return within_x && within_y;
    }

    /**
     * transform a response. Add x,y to every point.
     */
    private void transform_response(List<Point> response_points, double x_transform, double y_transform) {
        // add x,y to every point
        for (int i = 0; i < response_points.size(); i++) {
            Point p = response_points.get(i);
            response_points.set(i, new Point(p.getX() + x_transform, p.getY() + y_transform, p.getPressure()));
            remainingDistance = deltaD + computeEuclideanDistance(prevPoint, newNormalizedList.get(i));
>>>>>>> ED_Polar_Normalization_Testing
        }

<<<<<<< HEAD
    /**
     * use neighbors of a normalizing point to figure out what its values should be.
     *
     * Takes left and right points based on where they are in the response sequence.
     * pointLeft is assumed to come before pointRight in the sequence.
     */
    private Point compute_normalized_point_value(Point curNormalizedPoint, Point pointLeft, Point pointRight) {
        double x_dist, y_dist, extrapolatedPressure, nPressure, theta, theta_r, theta_d;

        // Interpolate
        theta_r = Math.atan((pointLeft.getY() / pointLeft.getX()));
        theta = Math.atan((pointRight.getY() - pointLeft.getY()) /
                (pointRight.getX() - pointLeft.getX()));

        theta_d = theta - theta_r;

        // because tan returns a value between -pi/2 and pi/2, cos will never be negative
        // we need to persevere the x direction we are traveling
        double x_difference = (pointRight.getX() - pointLeft.getX());
        // this breaks things because if we have any points left to do and we don't do them then our normalized response lists will be differant sizes.
        // This means there will be an arrayIndexOoutOfBounds error when we get to the part of the code where we find mu. if >= 1
        if(responsePattern.size() <= 1) {
            this.normalizedResponsePattern = newNormalizedList;
            return;
        }

        // Now take care of remaining (NL - N) points which we need to interpolate
        prevPoint = responsePattern.get(M - 2);
        curPoint = responsePattern.get(M - 1);
        double x_difference = (curPoint.getX() - prevPoint.getX());
>>>>>>> ED_Polar_Normalization_Testing
        double x_sine = 1;
        // check that the difference isn't equal to zero to ensure no divide by 0 error
        if (!(x_difference == 0)) {
            // this will make x_sine +1 or -1 depending on the sine
            x_sine = x_difference / Math.abs(x_difference);
        }

<<<<<<< HEAD
        x_dist = pointLeft.getX() + ((getRadius(curNormalizedPoint) - getRadius(pointLeft)) * (Math.cos(theta) / Math.cos(theta_d)) * x_sine);
        y_dist = pointLeft.getY() + ((getRadius(curNormalizedPoint) - getRadius(pointLeft)) * (Math.sin(theta) / Math.cos(theta_d)) * x_sine);

        // TODO this is being computed incorrectly. Fix it.
        extrapolatedPressure = (getRadius(curNormalizedPoint) - getRadius(pointLeft)) / (getRadius(pointRight) - getRadius(pointLeft)) * (pointRight.getPressure() - pointLeft.getPressure());
        nPressure = pointLeft.getPressure() + extrapolatedPressure;

        return new Point(x_dist, y_dist, nPressure);
    }

    public void normalize_real_old(List<Point> normalizingPoints) {
        theta = Math.atan((curPoint.getY() - prevPoint.getY()) / (curPoint.getX() - prevPoint.getX()));
        d = (NL * deltaD) - traceDistance;
>>>>>>> ED_Polar_Normalization_Testing

        for(i = NL; i < N; i++) {
            newX = prevPoint.getX() + (d * Math.cos(theta) * x_sine);
            newY = prevPoint.getY() + (d * Math.sin(theta) * x_sine);

            // Compute pressure and other attributes
            /* pressure */
            newPressure = curPoint.getPressure() + (((curPoint.getPressure() - prevPoint.getPressure()) / (computeEuclideanDistance(curPoint, prevPoint))) * d);
            /* distance */
            newDistance = computeEuclideanDistance(new Point(newX, newY, 0), curPoint);

            newNormalizedList.add(new Point(newX, newY, newPressure, newDistance));

            d += deltaD;
        }

        this.normalizedResponsePattern = newNormalizedList;
    }

    /**
     * Computes total euclidean distance of response pattern
     * @return distance of response pattern
     */
    double computeTraceDistance() {
        double distance = 0;
        Point firstPoint, secondPoint;
        for(int i = 0; i < responsePattern.size() - 1; i++) {
            firstPoint = responsePattern.get(i);
            secondPoint = responsePattern.get(i + 1);
            distance += computeEuclideanDistance(firstPoint, secondPoint);
        }
        return distance;
    }

    /**
     * transform a response. Add x,y to every point.
     */
    private void transform_response(List<Point> response_points, double x_transform, double y_transform) {
        // add x,y to every point
        for (int i = 0; i < response_points.size(); i++) {
            Point p = response_points.get(i);
            response_points.set(i, new Point(p.getX() + x_transform, p.getY() + y_transform, p.getPressure()));
        }
    }

    private double getRadius(Point p) {
        return Math.sqrt(Math.pow(p.getX(), 2) + Math.pow(p.getY(), 2));
    }

    /**
     * compute the euclidean distance between two points
     */
    private double computeEuclideanDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow((p1.getX() - p2.getX()), 2) +
                Math.pow((p1.getY() - p2.getY()), 2));
    }

}
