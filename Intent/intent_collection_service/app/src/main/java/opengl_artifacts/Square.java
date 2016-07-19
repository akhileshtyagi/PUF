package opengl_artifacts;

/**
 * defines two triangles which together create a square
 */
public class Square {
    Triangle north_west_triangle;
    Triangle south_east_triangle;

    /**
     * defines two triangles
     *
     * radius is the radius of a circomescribed circle
     */
    public Square(float radius, float center_x, float center_y){
        // vertex definition order is: top, bottom left, bottom right
        north_west_triangle = new Triangle(
                new Vertex(center_x - radius, center_y - radius, 0.0f),
                new Vertex(center_x - radius, center_y + radius, 0.0f),
                new Vertex(center_x + radius, center_y - radius, 0.0f)
        );

        south_east_triangle = new Triangle(
                new Vertex(center_x + radius, center_y + radius, 0.0f),
                new Vertex(center_x + radius, center_y - radius, 0.0f),
                new Vertex(center_x - radius, center_y + radius, 0.0f)
        );
    }

    /**
     * draw the square by drawing the component triangles
     */
    public void draw(float[] mvpMatrix) {
        north_west_triangle.draw(mvpMatrix);
        south_east_triangle.draw(mvpMatrix);
    }
}
//    private FloatBuffer vertexBuffer;
//    private ShortBuffer drawListBuffer;
//
//    // number of coordinates per vertex in this array
//    static final int COORDS_PER_VERTEX = 3;
//    static float squareCoords[] = {
//            -0.5f,  0.5f, 0.0f,   // top left
//            -0.5f, -0.5f, 0.0f,   // bottom left
//            0.5f, -0.5f, 0.0f,   // bottom right
//            0.5f,  0.5f, 0.0f }; // top right
//
//    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

//    public Square() {
//        // initialize vertex byte buffer for shape coordinates
//        ByteBuffer bb = ByteBuffer.allocateDirect(
//                // (# of coordinate values * 4 bytes per float)
//                squareCoords.length * 4);
//        bb.order(ByteOrder.nativeOrder());
//        vertexBuffer = bb.asFloatBuffer();
//        vertexBuffer.put(squareCoords);
//        vertexBuffer.position(0);
//
//        // initialize byte buffer for the draw list
//        ByteBuffer dlb = ByteBuffer.allocateDirect(
//                // (# of coordinate values * 2 bytes per short)
//                drawOrder.length * 2);
//        dlb.order(ByteOrder.nativeOrder());
//        drawListBuffer = dlb.asShortBuffer();
//        drawListBuffer.put(drawOrder);
//        drawListBuffer.position(0);
//    }