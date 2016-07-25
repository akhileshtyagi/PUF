package opengl_artifacts;

/**
 * Created by element on 7/19/16.
 */
public class Rectangle implements Drawable{
    final String TAG = "rectangle";

    private Triangle north_west_triangle;
    private Triangle south_east_triangle;

    /**
     * make a rectangle based on start and end vertexes
     * and width
     */
    public Rectangle(float width, Vertex begin, Vertex end){
        // slope between verticies
        float verticies_slope = begin.compute_slope(end);

//        Log.d(TAG, "rectangle slope: " + verticies_slope);

        float corner_point_slope = -1 / verticies_slope;

//        Log.d(TAG, "corner point slope: " + corner_point_slope);

        // to define the rectangle,
        // from each vertex, the corner points lie
        // along a line with an opposite reciprical slope to the
        // slope between the verticies
        Vertex north_west = new Vertex(
                Vertex.compute_x_distance(corner_point_slope, width / 2) + begin.x,
                Vertex.compute_y_distance(corner_point_slope, width / 2) + begin.y,
                0.0f);
        Vertex south_west = new Vertex(
                -1 * Vertex.compute_x_distance(corner_point_slope, width / 2) + begin.x,
                -1 * Vertex.compute_y_distance(corner_point_slope, width / 2) + begin.y,
                0.0f);
        Vertex north_east = new Vertex(
                Vertex.compute_x_distance(corner_point_slope, width / 2) + end.x,
                Vertex.compute_y_distance(corner_point_slope, width / 2) + end.y,
                0.0f);
        Vertex south_east = new Vertex(
                -1 * Vertex.compute_x_distance(corner_point_slope, width / 2) + end.x,
                -1 * Vertex.compute_y_distance(corner_point_slope, width / 2) + end.y,
                0.0f);

//        Log.d(TAG, "north_west: " + north_west.toString());
//        Log.d(TAG, "south_west: " + south_west.toString());
//        Log.d(TAG, "north_east: " + north_east.toString());
//        Log.d(TAG, "south_east: " + south_east.toString());

        // vertex definition order is: top, bottom left, bottom right
        north_west_triangle = new Triangle(
                north_west,
                south_west,
                north_east);
        south_east_triangle = new Triangle(
                south_east,
                north_east,
                south_west);
    }

    /**
     * a Rectangle is built from two triangles
     */
    public Rectangle(float height, float width, float center_x, float center_y){
        // vertex definition order is: top, bottom left, bottom right
        north_west_triangle = new Triangle(
                new Vertex(center_x - width, center_y - height, 0.0f),
                new Vertex(center_x - width, center_y + height, 0.0f),
                new Vertex(center_x + width, center_y - height, 0.0f)
        );

        south_east_triangle = new Triangle(
                new Vertex(center_x + width, center_y + height, 0.0f),
                new Vertex(center_x + width, center_y - height, 0.0f),
                new Vertex(center_x - width, center_y + height, 0.0f)
        );
    }

    @Override
    public void draw(float[] mvpMatrix) {
        north_west_triangle.draw(mvpMatrix);
        south_east_triangle.draw(mvpMatrix);
    }
}
