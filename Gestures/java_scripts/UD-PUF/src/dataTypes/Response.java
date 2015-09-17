package dataTypes;

import java.util.List;

/**
 * Represents one response created by a user
 */
public class Response {

    // List of points which the user swiped
    private List<Point> responsePattern;


    public Response(List<Point> responsePatter) {
        this.responsePattern = responsePatter;
    }

    public List<Point> getResponse() {
        return responsePattern;
    }

    // Normalizes points in response
    public void Normalize(List<Point> normalizingPoints) {
        // TODO
        // Implement method of normalizing ResponsePattern to points
        // given in to method
    }
}
