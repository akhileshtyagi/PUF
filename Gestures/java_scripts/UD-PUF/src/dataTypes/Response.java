package dataTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one response created by a user
 */
public class Response {

    // List of points which the user swiped
    private List<Point> responsePattern;

    public Response(List<Point> responsePattern) {
	this.responsePattern = responsePattern;
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
	List<Point> normalizedResponsePattern = new ArrayList<Point>();
	Point closestLeftPoint = null; // left or below
	Point closestRightPoint = null; // right or above
	Point normalizedPoint = null;
	double pressure = 0;

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

	    System.out.println(closestRightPoint.getX());
	    System.out.println(closestLeftPoint.getX());

	    // if the closest left and right points are equal, simply add the
	    // pressure value of that point to the list
	    if (closestRightPoint.equals(closestLeftPoint)) {
		pressure = closestRightPoint.getPressure();
	    } else {
		// find pressure value for this point by considering the
		// closest points
		double deltaX = closestRightPoint.getX() - closestLeftPoint.getX();
		double deltaY = closestRightPoint.getY() - closestLeftPoint.getY();
		double angle = (isChallengeHorizontal) ? (Math.abs(Math.atan(deltaY / deltaX)))
			: (Math.abs(Math.atan(deltaX / deltaY)));

		if (isChallengeHorizontal) {
		    double deltaLeft = normalizingPoint.getX() - closestLeftPoint.getX();
		    double leftPart = deltaLeft / Math.cos(angle);

		    double totalPart = deltaX / Math.cos(angle);
		    double rightPart = totalPart - leftPart;

		    pressure = (leftPart / totalPart) * closestLeftPoint.getPressure()
			    + (rightPart / totalPart) * closestRightPoint.getPressure();
		} else {
		    // should this be sine?
		    double deltaLeft = normalizingPoint.getY() - closestLeftPoint.getY();
		    double leftPart = deltaLeft / Math.cos(angle);

		    double totalPart = deltaY / Math.cos(angle);
		    double rightPart = totalPart - leftPart;

		    pressure = (leftPart / totalPart) * closestLeftPoint.getPressure()
			    + (rightPart / totalPart) * closestRightPoint.getPressure();
		}
	    }

	    // create normalized point to add to the list based on found
	    // pressure value
	    normalizedPoint = new Point(normalizingPoint.getX(), normalizingPoint.getY(), pressure);

	    normalizedResponsePattern.add(normalizedPoint);
	}

	this.responsePattern = normalizedResponsePattern;
    }
}