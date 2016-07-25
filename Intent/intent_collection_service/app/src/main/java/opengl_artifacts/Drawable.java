package opengl_artifacts;

/**
 * Requires that the class have a draw method
 */
public interface Drawable {
    boolean location_set = false;

    float width = 0;
    float height = 0;

    float x = 0;
    float y = 0;
    float z = 0;

    public void draw(float[] mvpMatrix);
}
