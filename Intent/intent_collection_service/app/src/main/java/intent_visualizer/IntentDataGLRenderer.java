package intent_visualizer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl_artifacts.Square;
import opengl_artifacts.Triangle;

/**
 * Created by element on 7/16/16.
 */
public class IntentDataGLRenderer implements GLSurfaceView.Renderer {
    private Triangle mTriangle;
    private Square mSquare;

    // create a model matrix for the triangle
    private final float[] mModelMatrix = new float[16];
    // create a temporary matrix for calculation purposes,
    // to avoid the same matrix on the right and left side of multiplyMM later
    // see http://stackoverflow.com/questions/13480043/opengl-es-android-matrix-transformations#comment18443759_13480364
    private float[] mTempMatrix = new float[16];

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    // for rotating shape
    private float[] mRotationMatrix = new float[16];

    /* exposes the rotation angle */
    public volatile float mAngle;

    /**
     * called once to set up the view's OpenGL ES enviornment
     *
     * creates a black surface at startup
     */
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // initialize a triangle
        mTriangle = new Triangle();
        // initialize a square
        mSquare = new Square();
    }

    /**
     * called for each redraw of the view
     */
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        float eye_x = 0f;
        float eye_y = 0f;
        float eye_z = -3f;
        Matrix.setLookAtM(mViewMatrix, 0, eye_x, eye_y, eye_z, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        /* translate the shape */
        Matrix.setIdentityM(mModelMatrix, 0); // initialize to identity matrix

        float translate_x, translate_y, translate_z;
        translate_x = 0.5f;
        translate_y = 0f;
        translate_z = 0f;
        Matrix.translateM(mModelMatrix, 0, translate_x, translate_y, translate_z); // translation to the left

        // Draw shape ( no rotation )
        //mTriangle.draw(mMVPMatrix);

        /* rotate a shape */
        // Create a rotation transformation for the triangle
        //long time = SystemClock.uptimeMillis() % 4000L;
        //float angle = 0.090f * ((int) time);
        //Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);

        /* translation */
        // Combine Rotation and Translation matrices
        mTempMatrix = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, mTempMatrix, 0, mRotationMatrix, 0);

        // Combine the model matrix with the projection and camera view
        mTempMatrix = mMVPMatrix.clone();
        Matrix.multiplyMM(mMVPMatrix, 0, mTempMatrix, 0, mModelMatrix, 0);

        /* camera */
        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        //Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        // Draw triangle
        //mTriangle.draw(scratch);
        mTriangle.draw(mMVPMatrix);
    }

    /**
     * called if the geometry of the view changes
     * (screen rotation)
     */
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        /* define camera projection */
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    /**
     * utility method to load shaders
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }
}
