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
    private ArrayList<Point> responsePattern;

    public Response(List<Point> responsePattern) {
	this.responsePattern = new ArrayList<Point>(responsePattern);
    }

    public List<Point> getResponse() {
	return responsePattern;
    }

    /*
     * Normalizes points in response. The normalizingPoints are a list of points
     * to normalize the response to. In other words the response will then
     * contain exactly these point having some pressure determined by the
     * original response.
     */
    public void normalize(List<Point> normalizingPoints, boolean isChallengeHorizontal) {
	// Implement method of normalizing ResponsePattern to points
	// given in to method
	ArrayList<Point> normalizedResponsePattern = new ArrayList<Point>();
	Point closestLeftPoint = null; // left or below
	Point closestRightPoint = null; // right or above
	Point normalizedPoint = null;
	double pressure = 0;
	double point_distance = 0;
	double time = 0;

	// we need to find the correct pressure value for each normalizingPoints
	for (Point normalizingPoint : normalizingPoints) {
	    // if the person did the challenge in the correct direction
	    closestLeftPoint = responsePattern.get(0);
	    closestRightPoint = responsePattern.get(responsePattern.size() - 1);

	    // if the person did the challenge in the incorrect direction
	    if (responsePattern.get(0).getX() > normalizingPoints.get(0).getX()) {
		closestLeftPoint = responsePattern.get(0);
		closestRightPoint = responsePattern.get(responsePattern.size() - 1);
	    }

	    // find the closest left/ below point and the closest right
	    // above point
	    for (Point responsePoint : responsePattern) {
		// if response Point is closer to the left then it becomes
		// closestLeftPoint
		if (((isChallengeHorizontal)
			? ((normalizingPoint.getX() - responsePoint.getX()) < (normalizingPoint.getX()
				- closestLeftPoint.getX())) && ((normalizingPoint.getX() - responsePoint.getX()) >= 0)
			: ((normalizingPoint.getY() - responsePoint.getY()) < (normalizingPoint.getY()
				- closestLeftPoint.getY()))
				&& ((normalizingPoint.getY() - responsePoint.getY() >= 0)))) {

		    closestLeftPoint = responsePoint;
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
		}
	    }

	    // System.out.println(closestRightPoint.getX());
	    // System.out.println(closestLeftPoint.getX());

	    // if the closest left and right points are equal, simply add the
	    // pressure value of that point to the list
	    if (closestRightPoint.equals(closestLeftPoint)) {
		pressure = closestRightPoint.getPressure();

		// if the challenge is horizontal => we have points along the x
		// axis => we want Y value
		// if the challenge is not horizontal => we have points along y
		// axis => we want X value
		point_distance = (isChallengeHorizontal) ? (closestRightPoint.getY()) : (closestRightPoint.getX());

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
		    // horizontal challenge => we have points along x axis =>
		    // need y values
		    point_distance = closestLeftPoint.getY() + distance_slope * x_differance;

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
		    // vertical challenge => we have points along y axis => need
		    // x values
		    point_distance = closestLeftPoint.getX() + distance_slope * y_differance;

		    // determine the normalized time
		    time = closestLeftPoint.getTime() + time_slope * y_differance;
		}
	    }

	    // create normalized point to add to the list based on found
	    // pressure value
	    normalizedPoint = new Point(normalizingPoint.getX(), normalizingPoint.getY(), pressure, point_distance,
		    time);

	    normalizedResponsePattern.add(normalizedPoint);
	}

	this.responsePattern = normalizedResponsePattern;
    }
}