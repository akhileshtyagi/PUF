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

    protected void setNormalizingPoints(List<Point> normalizingPoints) {
        this.normalizedResponsePattern = normalizedResponsePattern;
    }
    public void normalize(List<Point> normalizingPoints) {

        double x_dist, y_dist, extrapolatedPressure, nPressure, theta, theta_r, theta_d;
        Point curNormalizedPoint, prevNormalizedPoint, pointLeft, pointRight;
        ArrayList<Point> newNormalizedList = new ArrayList<>();

        // Experimental flag to used to retry finding next point with differing direction
        // badGuess = 0 -> First try
        // badGuess = 1 -> Try opposite direction
        boolean badGuess = false;

        // If no point is past the first normalizing point's radius, extrapolate it
        // TODO instead of making a temp point, extrapolate all response points similarly

        /*if(getRadius(responsePattern.get(0)) > getRadius(normalizingPoints.get(0))) {
            double theta = Math.atan(responsePattern.get(0).getY() / responsePattern.get(0).getX());
            double pRadius = getRadius(normalizingPoints.get(0));
            Point temp_point = new Point(pRadius * Math.cos(theta), pRadius * Math.sin(theta), 0);
        }
        */

        /*
        for(int i = 0; i < responsePattern.size(); i++) {
            theta = Math.atan(responsePattern.get(i).getY() / responsePattern.get(i).getX());
            pRadius = getRadius(normalizingPoints.get(i));
            Point p = responsePattern.get(i);
            responsePattern.set(i, new Point(pRadius * Math.cos(theta), pRadius * Math.sin(theta), p.getPressure()));
        }
        */

        double x_transform, y_transform, direction = 0;

        x_transform = normalizingPoints.get(0).getX() - responsePattern.get(0).getX();
        y_transform = normalizingPoints.get(0).getY() - responsePattern.get(0).getY();

        for(int i = 0; i < responsePattern.size(); i++) {
            Point p = responsePattern.get(i);
            responsePattern.set(i, new Point(p.getX() + x_transform, p.getY() + y_transform, p.getPressure()));
        }

        // For now, just add the first point as the normalizedPoint. Will correct this after correcting main method1
        newNormalizedList.add(responsePattern.get(0));

        // Index for counting responsePattern of trace: [j-1] is the point "before" CurNormalizedPoint, [j] is
        // point "after" CurNormalizedPoint
        int j = 1;
        for(int i = 1; i < normalizingPoints.size(); i++) {
            curNormalizedPoint = normalizingPoints.get(i);
            prevNormalizedPoint = normalizingPoints.get(i - 1);
            int k = 0;

            // Determines if the newer radius has increased or decreased from previous radius
            if(!badGuess) direction = getRadius(curNormalizedPoint) - getRadius(prevNormalizedPoint);
            else direction = - direction;

            // If radius is increasing, loop until find a radius larger than current normalized point
            if(direction >= 0) {
                while( getRadius(responsePattern.get(j - 1 + k)) <= getRadius(curNormalizedPoint)) {
                    if ((j + k) >= responsePattern.size()) {
                        this.normalizedResponsePattern = newNormalizedList;
                        return;
                    }

                    // If past 1/7 of all points are in the wrong direction, retry with different direction
                    if(k > 5) {
                        // If already have tried reversing direction, return
                        if(badGuess) {
                            this.normalizedResponsePattern = newNormalizedList;
                            return;
                        }
                        badGuess = true;
                    }

                    k++;
                }
            }

            // If radius is decreasing, loop until find a radius smaller than current normalized point
            else if(direction < 0) {
                while( getRadius(responsePattern.get(j - 1 + k)) >= getRadius(curNormalizedPoint)) {
                    if ((j + k) >= responsePattern.size()) {
                        this.normalizedResponsePattern = newNormalizedList;
                        return;
                    }

                    // If past 1/7 of all points are in the wrong direction, retry with different direction
                    if(k > 5) {
                        // If already have tried reversing direction, return
                        if(badGuess) {
                            this.normalizedResponsePattern = newNormalizedList;
                            return;
                        }
                        badGuess = true;
                    }

                    k++;
                }
            }

            // Update trace index with new point to examine, immediately to the right of normalizePoints[i]
            j = j + k - 1;
            if(j >= responsePattern.size()) break;

            pointLeft = responsePattern.get(j-1);
            pointRight = responsePattern.get(j);

            // Interpolate
            theta_r = Math.atan( (pointLeft.getY() / pointLeft.getX()) );
            theta = Math.atan((pointRight.getY() - pointLeft.getY()) /
                    (pointRight.getX() - pointLeft.getX()));
            theta_d = - (theta_r - theta);

            // because tan returns a value between -pi/2 and pi/2, cos will never be negative
            // we need to persevere the x direction we are traveling
            double x_difference = (pointRight.getX() - pointLeft.getX());
            double x_sine = 1;
            // check that the difference isn't equal to zero to ensure no divide by 0 error
            if(!(x_difference==0)){
                // this will make x_sine +1 or -1 depending on the sine
                x_sine = x_difference / Math.abs(x_difference);
            }

            x_dist = pointLeft.getX() + ((getRadius(curNormalizedPoint) - getRadius(pointLeft)) * (Math.cos(theta) / Math.cos(theta_d)) * x_sine);
            y_dist = pointLeft.getY() + ((getRadius(curNormalizedPoint)- getRadius(pointLeft)) * (Math.sin(theta) / Math.cos(theta_d))* x_sine);
            extrapolatedPressure = (getRadius(curNormalizedPoint) - getRadius(pointLeft)) / (getRadius(pointRight) - getRadius(pointLeft)) * (pointRight.getPressure() - pointLeft.getPressure());
            nPressure = pointLeft.getPressure() + extrapolatedPressure;
            System.out.println("Pressure for point (" + x_dist + ", " + y_dist + " ): " + nPressure);
            newNormalizedList.add(new Point(x_dist, y_dist, nPressure));
            j++;
        }

        this.normalizedResponsePattern = newNormalizedList;
    }

    private double getRadius(Point p) {
        return Math.sqrt( Math.pow(p.getX(),2) + Math.pow(p.getY(), 2));
    }

    public void oldNormalize(List<Point> normalizingPoints) {
        // For iterating over Response Points
        int j = 0;

        double closestDistance;

        ArrayList<Point> normalizedResponse = new ArrayList<>();

        for(Point normPoint : normalizingPoints) {

            // Set closest distance to the distance from the first normalizing point to the first response point
            closestDistance = computeEuclideanDistance(normPoint, responsePattern.get(j));

            // Continually check if the next response points is closer to normalizing point than the previous
            while(computeEuclideanDistance(normPoint, responsePattern.get(j + 1)) < closestDistance) {
                closestDistance = computeEuclideanDistance(normPoint, responsePattern.get(j++));
                if(responsePattern.size() - 1 < j+ 1) break;
            }

            // Current point is point closest to normalizing point
            normalizedResponse.add(responsePattern.get(j));
            j++;
            if(responsePattern.size() - 1 < j+ 1) break;
        }

        this.normalizedResponsePattern = normalizedResponse;
    }

    /**
     * compute the euclidean distance between two points
     */
    private double computeEuclideanDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow((p1.getX() - p2.getX()), 2) +
                Math.pow((p1.getY() - p2.getY()), 2));
    }

     /*
     * Normalizes points in response. The normalizingPoints are a list of points
     * to normalize the response to. In other words the response will then
     * contain exactly these point having some pressure determined by the
     * original response.
     */
    public void normalize(List<Point> normalizingPoints, boolean isChallengeHorizontal) {
        // System.out.println("before:\t" + this.responsePattern);

        // Implement method of normalizing ResponsePattern to points
        // given in to method
        ArrayList<Point> normalizedResponsePattern = new ArrayList<Point>();
        Point closestLeftPoint = null; // left or below
        Point closestRightPoint = null; // right or above
        Point normalizedPoint = null;
        double pressure = 0;
        double point_distance = 0;
        double time = 0;

        int closestLeftIndex = 0;
        int closestRightIndex = 0;

        // we need to find the correct pressure value for each normalizingPoints
        for (Point normalizingPoint : normalizingPoints) {
            // if the person did the challenge in the correct direction
            closestLeftPoint = responsePattern.get(0);
            closestRightPoint = responsePattern.get(responsePattern.size() - 1);

            closestLeftIndex = 0;
            closestRightIndex = responsePattern.size() - 1;

            // if the person did the challenge in the incorrect direction
            // if (responsePattern.get(0).getX() >
            // normalizingPoints.get(0).getX()) {
            // closestLeftPoint = responsePattern.get(0);
            // closestRightPoint = responsePattern.get(responsePattern.size() -
            // 1);
            // }

            // find the closest left/ below point and the closest right
            // above point
            for (int i = 0; i < responsePattern.size(); i++) {
                Point responsePoint = responsePattern.get(i);

                // if response Point is closer to the left then it becomes
                // closestLeftPoint
                if (((isChallengeHorizontal)
                        ? ((normalizingPoint.getX() - responsePoint.getX()) < (normalizingPoint.getX() - closestLeftPoint.getX())) && ((normalizingPoint.getX() - responsePoint.getX()) >= 0)
                        : ((normalizingPoint.getY() - responsePoint.getY()) < (normalizingPoint.getY() - closestLeftPoint.getY())) && ((normalizingPoint.getY() - responsePoint.getY() >= 0)))) {

                    closestLeftPoint = responsePoint;
                    closestLeftIndex = i;
                }

                // if responsePoint is closer to the right then it becomes
                // closestRightPoint
                if (((isChallengeHorizontal)
                        ? ((normalizingPoint.getX() - responsePoint.getX()) > (normalizingPoint.getX()
                        - closestRightPoint.getX())) && ((normalizingPoint.getX() - responsePoint.getX()) <= 0)
                        : ((normalizingPoint.getY() - responsePoint.getY()) > (normalizingPoint.getY()
                        - closestRightPoint.getY()))
                        && ((normalizingPoint.getY() - responsePoint.getY() <= 0)))) {

                    closestRightPoint = responsePoint;
                    closestRightIndex = i;
                }
            }

            // check to see if there is a descrepency between the closest right
            // point and the closest left point
            if (!((closestRightIndex == (closestLeftIndex + 1)) || (closestRightIndex == closestLeftIndex))) {
                // print out the values
                // System.out.println("---------------");
                // System.out.println(closestLeftIndex);
                // System.out.println(closestRightIndex);
                // System.out.println("---------------");

                // check to see which point is closer to the previous value in
                // the sequence
                if (normalizedResponsePattern.size() > 0) {
                    // find the point closest to the previous response.
                    Point previous_point = normalizedResponsePattern.get(normalizedResponsePattern.size() - 1);

                    if (isChallengeHorizontal) {
                        // closest in X
                        if (Math.abs(previous_point.getX() - responsePattern.get(closestLeftIndex).getX()) < Math
                                .abs(previous_point.getX() - responsePattern.get(closestRightIndex).getX())) {
                            // the left point is closest
                            if (closestLeftIndex < responsePattern.size() - 1) {
                                // the index will not be out of bounds
                                closestRightIndex = closestLeftIndex + 1;
                            } else {
                                // the index will be out of bounds, so set it to
                                // the last point in the array.
                                // we know this is closestLeftIndex :p
                                closestRightIndex = closestLeftIndex;
                            }
                        } else {
                            // the right point is closest
                            if (closestLeftIndex > 0) {
                                // the index will not be out of bounds
                                closestLeftIndex = closestRightIndex - 1;
                            } else {
                                // the index will be out of bounds, so set it to
                                // the last point in the array.
                                // we know this is closestRightIndex :p
                                closestLeftIndex = closestRightIndex;
                            }
                        }
                    } else {
                        // closest in Y
                        if (Math.abs(previous_point.getY() - responsePattern.get(closestLeftIndex).getY()) < Math
                                .abs(previous_point.getY() - responsePattern.get(closestRightIndex).getY())) {
                            // the left point is closest
                            if (closestLeftIndex < responsePattern.size() - 1) {
                                // the index will not be out of bounds
                                closestRightIndex = closestLeftIndex + 1;
                            } else {
                                // the index will be out of bounds, so set it to
                                // the last point in the array.
                                // we know this is closestLeftIndex :p
                                closestRightIndex = closestLeftIndex;
                            }
                        } else {
                            // the right point is closest
                            if (closestLeftIndex > 0) {
                                // the index will not be out of bounds
                                closestLeftIndex = closestRightIndex - 1;
                            } else {
                                // the index will be out of bounds, so set it to
                                // the last point in the array.
                                // we know this is closestRightIndex :p
                                closestLeftIndex = closestRightIndex;
                            }
                        }
                    }
                } else {
                    // there are no previous responses, choose the response
                    // closest to the upper left
                    // TODO find a better method of doing this
                    if (isChallengeHorizontal) {
                        // closest in X
                        if (responsePattern.get(closestLeftIndex).getX() < responsePattern.get(closestRightIndex)
                                .getX()) {
                            // the left point is closest
                            if (closestLeftIndex < responsePattern.size() - 1) {
                                // the index will not be out of bounds
                                closestRightIndex = closestLeftIndex + 1;
                            } else {
                                // the index will be out of bounds, so set it to
                                // the last point in the array.
                                // we know this is closestLeftIndex :p
                                closestRightIndex = closestLeftIndex;
                            }
                        } else {
                            // the right point is closest
                            if (closestLeftIndex > 0) {
                                // the index will not be out of bounds
                                closestLeftIndex = closestRightIndex - 1;
                            } else {
                                // the index will be out of bounds, so set it to
                                // the last point in the array.
                                // we know this is closestRightIndex :p
                                closestLeftIndex = closestRightIndex;
                            }
                        }
                    } else {
                        // closest in Y
                        if (responsePattern.get(closestLeftIndex).getY() < responsePattern.get(closestRightIndex)
                                .getY()) {
                            // the left point is closest
                            if (closestLeftIndex < responsePattern.size() - 1) {
                                // the index will not be out of bounds
                                closestRightIndex = closestLeftIndex + 1;
                            } else {
                                // the index will be out of bounds, so set it to
                                // the last point in the array.
                                // we know this is closestLeftIndex :p
                                closestRightIndex = closestLeftIndex;
                            }
                        } else {
                            // the right point is closest
                            if (closestLeftIndex > 0) {
                                // the index will not be out of bounds
                                closestLeftIndex = closestRightIndex - 1;
                            } else {
                                // the index will be out of bounds, so set it to
                                // the last point in the array.
                                // we know this is closestRightIndex :p
                                closestLeftIndex = closestRightIndex;
                            }
                        }
                    }
                }

                // set the response points based on the new indexes
                closestRightPoint = responsePattern.get(closestRightIndex);
                closestLeftPoint = responsePattern.get(closestLeftIndex);

                // System.out.println(responsePattern);
            }

            // System.out.println("+++++++++++++++");
            // System.out.println(closestLeftIndex);
            // System.out.println(closestRightIndex);
            // System.out.println("+++++++++++++++");

            // System.out.println(closestRightPoint.getX());
            // System.out.println(closestLeftPoint.getX());

            // if the closest left and right points are equal, simply add
            // the
            // pressure value of that point to the list
            if(closestRightIndex == closestLeftIndex){
            //if (closestRightPoint.equals(closestLeftPoint)) {
                pressure = closestRightPoint.getPressure();

                // if the challenge is horizontal => we have points along
                // the x
                // axis => we want Y value
                // if the challenge is not horizontal => we have points
                // along y
                // axis => we want X value
                point_distance = (isChallengeHorizontal) ? (closestRightPoint.getY()) : (closestRightPoint.getX());

                // System.out.println(closestRightPoint);
                // System.out.println(point_distance);
                // System.out.println(isChallengeHorizontal);

                time = closestRightPoint.getTime();
            } else {
                if (isChallengeHorizontal) {
                    // find the slope of the pressure, distance, and time
                    // between the points
                    double pressure_slope = (closestRightPoint.getPressure() - closestLeftPoint.getPressure())
                            / (closestRightPoint.getX() - closestLeftPoint.getX());
                    double distance_slope = (closestRightPoint.getY() - closestLeftPoint.getY())
                            / (closestRightPoint.getX() - closestLeftPoint.getX()); // Y
                    // =
                    // distance
                    // for
                    // horizontal
                    // challenge
                    double time_slope = (closestRightPoint.getTime() - closestLeftPoint.getTime())
                            / (closestRightPoint.getX() - closestLeftPoint.getX());

                    // find the difference between normalized point and the
                    // point on the left
                    double x_differance = normalizingPoint.getX() - closestLeftPoint.getX();

                    // determine the normaized pressue
                    pressure = closestLeftPoint.getPressure() + pressure_slope * x_differance;

                    // determine the point distance
                    // horizontal challenge => we have points along x axis
                    // =>
                    // need y values
                    point_distance = closestLeftPoint.getY() + distance_slope * x_differance;

                    // System.out.println(point_distance);

                    // determine the normalized time
                    time = closestLeftPoint.getTime() + time_slope * x_differance;

                } else {
                    // challenge is not horizontal => projected onto Y axis
                    // find the slope of the pressure, distance, and time
                    // between the points
                    double pressure_slope = (closestRightPoint.getPressure() - closestLeftPoint.getPressure())
                            / (closestRightPoint.getY() - closestLeftPoint.getY());
                    double distance_slope = (closestRightPoint.getX() - closestLeftPoint.getX())
                            / (closestRightPoint.getY() - closestLeftPoint.getY()); // X
                    // =
                    // distance
                    // for
                    // non-horizontal
                    // challenge
                    double time_slope = (closestRightPoint.getTime() - closestLeftPoint.getTime())
                            / (closestRightPoint.getY() - closestLeftPoint.getY());

                    // find the difference between normalized point and the
                    // point on the left
                    double y_differance = normalizingPoint.getY() - closestLeftPoint.getY();

                    // determine normalized pressure
                    pressure = closestLeftPoint.getPressure() + pressure_slope * y_differance;

                    // determine the point distance
                    // vertical challenge => we have points along y axis =>
                    // need
                    // x values
                    point_distance = closestLeftPoint.getX() + distance_slope * y_differance;

                    // System.out.println(point_distance);

                    // determine the normalized time
                    time = closestLeftPoint.getTime() + time_slope * y_differance;
                }
            }

            // if (closestLeftIndex == closestRightIndex) {
            // System.out.println(point_distance);
            // }

            // correctly
            // System.out.println(point_distance);

            // System.out.println(time);

            // create normalized point to add to the list based on found
            // pressure value
            normalizedPoint = new Point(normalizingPoint.getX(), normalizingPoint.getY(), pressure, point_distance,
                    time);

            normalizedResponsePattern.add(normalizedPoint);
        }

        this.normalizedResponsePattern = normalizedResponsePattern;
        // this.normalizedResponsePattern = normalizedResponsePattern;

        // System.out.println("before:\t" + this.responsePattern);
        // System.out.println("after:\t" + this.normalizedResponsePattern);
    }
}