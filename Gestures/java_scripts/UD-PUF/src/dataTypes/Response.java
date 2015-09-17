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
     * origional response.
     */
    public void normalize(List<Point> normalizingPoints) {
	// Implement method of normalizing ResponsePattern to points
	// given in to method
	List<Point> normalizedResponsePattern = new ArrayList<Point>();

	// TODO
	

	this.responsePattern = normalizedResponsePattern;
    }
}