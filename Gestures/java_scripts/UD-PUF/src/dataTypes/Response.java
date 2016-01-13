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
                        ? ((normalizingPoint.getX() - responsePoint.getX()) < (normalizingPoint.getX()
                        - closestLeftPoint.getX())) && ((normalizingPoint.getX() - responsePoint.getX()) >= 0)
                        : ((normalizingPoint.getY() - responsePoint.getY()) < (normalizingPoint.getY()
                        - closestLeftPoint.getY()))
                        && ((normalizingPoint.getY() - responsePoint.getY() >= 0)))) {

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
            if (closestRightPoint.equals(closestLeftPoint)) {
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

            // TODO figure out why point_distance is not being computed
            // correctly
            // System.out.println(point_distance);

            // TODO figure out why time is incorrect
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