package opengl_artifacts;

/**
 * Represents an OpenGLES vertex
 */
public class Vertex {
    public float x;
    public float y;
    public float z;

    /**
     * ensures the x,y,z variables are set
     */
    public Vertex(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * compute the euclidean distance between the verticies
     */
    public float compute_euclidean_distance(Vertex other_vertex){
        float x_dist = this.x - other_vertex.x;
        float y_dist = this.y - other_vertex.y;

        return (float)Math.sqrt(x_dist * x_dist + y_dist * y_dist);
    }

    /**
     * compute the slope of the line between the verticies
     */
    public float compute_slope(Vertex other_vertex){
        float x_dist = this.x - other_vertex.x;
        float y_dist = this.y - other_vertex.y;

        return y_dist / x_dist;
    }

    /**
     * compute the x component of distance given
     * a slope and
     * distance along the line
     */
    public static float compute_x_distance(float slope, float distance){
        if(slope == 0){
            return distance;
        }

        // algebra
        return (float)(distance / (Math.sqrt( (slope * slope) + 1 )));
    }

    /**
     * compute the y component of distance given
     * a slope and
     * distance along the line
     */
    public static float compute_y_distance(float slope, float distance) {
        if(slope == Float.POSITIVE_INFINITY ||
                slope == Float.NEGATIVE_INFINITY){
            return distance;
        }

        // algebra
        return (float)(distance / (Math.sqrt( (1 / (slope * slope)) + 1 )));
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        sb.append("x: ");
        sb.append(x);
        sb.append(" ");

        sb.append("y: ");
        sb.append(y);
        sb.append(" ");

        sb.append("z: ");
        sb.append(z);

        sb.append("}");

        return sb.toString();
    }
}
