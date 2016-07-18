package intent_visualizer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * reference:
 * https://developer.android.com/training/graphics/opengl/index.html
 */
public class IntentDataGLSurfaceView extends GLSurfaceView {
    private final IntentDataGLRenderer renderer;

    /* responding to user touches */
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    public IntentDataGLSurfaceView(Context context){
        super(context);

        setEGLContextClientVersion(2);

        this.renderer = new IntentDataGLRenderer();

        setRenderer(this.renderer);

        // commenting this out changes render mode to continous
        // render the view only when there are changes
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1;
                }

                renderer.setAngle(renderer.getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}
