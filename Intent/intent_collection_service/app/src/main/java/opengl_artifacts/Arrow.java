package opengl_artifacts;

/**
 * An arrow is a combination of a triangle and a square
 */
public class Arrow implements Drawable{
    /** the portion of the arrow that is the arrow head */
    final static float ARROW_RATIO = 0.25f;

    Rectangle rectangle;
    Triangle triangle;

    /**
     * create arrow based on start and end vertexes
     */
    //TODO Arrow is not being created in the correct location
    public Arrow(float width, Vertex begin, Vertex end){
        // total distance
        float euclidean_distance = begin.compute_euclidean_distance(end);

        // rectangle is 1-ARROW_RATIO the total distance
        float rectangle_distance = euclidean_distance * (1 - ARROW_RATIO);

        // this line should have the same ratio of x to y
        float slope = begin.compute_slope(end);
        float rectangle_x = Vertex.compute_x_distance(slope, rectangle_distance) + begin.x;
        float rectangle_y = Vertex.compute_y_distance(slope, rectangle_distance) + begin.y;

        //TODO test code
        rectangle_x = begin.x + 0.0f;
        rectangle_y = begin.y - 0.5f;

        // define the size of the rectangle
        float rectangle_width = width / 2;

        rectangle = new Rectangle(
                rectangle_width,
                begin,
                new Vertex(rectangle_x, rectangle_y, 0.0f));

        // rectangle_x_y represent the base of the triangle
        // the other two vertexes of the triangle are along
        // a like with perpendicular slope to begin -> end
        float perpendicular_slope = -1 / slope;

        float triangle_x = Vertex.compute_x_distance(perpendicular_slope, width / 2);
        float triangle_y = Vertex.compute_y_distance(perpendicular_slope, width / 2);

        // triangle is ARROW_RATIO the width
        triangle = new Triangle(
                // top
                end,
                // bottom left
                new Vertex(triangle_x + rectangle_x, triangle_y + rectangle_y, 0.0f),
                // bottom right
                new Vertex(rectangle_x - triangle_x, rectangle_y - triangle_y, 0.0f)
        );
    }

    /**
     * consists of a triangle and a rectangle
     *
     * arrow starts pointing directly west
     */
    public Arrow(float height, float width, float center_x, float center_y){
        //TODO this doesn't work quite right
        // triangle is 1/4 the width
        triangle = new Triangle(
                // top
                new Vertex(center_x + width / 2, center_y, 0.0f),
                // bottom left
                new Vertex(center_x + width / 4, center_y - height, 0.0f),
                // bottom right
                new Vertex(center_x + width / 4, center_y + height, 0.0f)
        );

        // rectangle is 3/4 the width
        rectangle = new Rectangle(height * 3 / 4, width * 3 / 4, center_x - width * 2 / 4, center_y );
    }

    @Override
    public void draw(float[] mvpMatrix) {
        rectangle.draw(mvpMatrix);
        //TODO
        //triangle.draw(mvpMatrix);
    }
}
