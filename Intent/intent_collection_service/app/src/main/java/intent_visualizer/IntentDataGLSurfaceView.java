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
    private final float TOUCH_SCALE_FACTOR = 1.0f / 1000;
    private final float ACCEL_SCALE_FACTOR = 1.0f / 10;

    private float mPreviousX;
    private float mPreviousY;

    private float mSecondPreviousX;
    private float mSecondPreviousY;

    private float mPreviousDownTime;

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
        float time = e.getDownTime();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // this checks the time difference between motionevents to ensure they are part of the same action
                // if they are not part of the same action,
                // reset the previous values to avoid skipping of the image
                if(time != mPreviousDownTime) {
                    mPreviousX = x;
                    mPreviousY = y;
                    mSecondPreviousX = x;
                    mSecondPreviousY = y;
                }

                // change in x from previous MotionEvent
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // acceleration of change
                // acceleration is an increase (or decrease) in dx compared to the previous d_x,y
                float ax = dx - (mPreviousX - mSecondPreviousX);
                float ay = dy - (mPreviousY - mSecondPreviousY);

                // set acceleration allways >= 0
                ax = (ax > 0) ? (ax) : 0;
                ay = (ay > 0) ? (ay) : 0;

                // reverse direction of rotation above the mid-line
//                if (y > getHeight() / 2) {
//                    dx = dx * -1;
//                }

                // reverse direction of rotation to left of the mid-line
//                if (x < getWidth() / 2) {
//                    dy = dy * -1;
//                }

                /* this code uses the variables to change the angle */
                //renderer.setAngle(renderer.getAngle() + ((dx + dy) * TOUCH_SCALE_FACTOR));

                /* here the location is changed */
                renderer.set_center_postion(
                        renderer.get_center_postion_x() + ((dx + (dx * ax * ACCEL_SCALE_FACTOR)) * TOUCH_SCALE_FACTOR),
                        renderer.get_center_postion_y() + ((dy + (dy * ay * ACCEL_SCALE_FACTOR)) * TOUCH_SCALE_FACTOR));

                /* set the render status to dirty */
                requestRender();
        }

        mPreviousDownTime = time;

        // record the second previous for acceleration computation
        mSecondPreviousX = mPreviousX;
        mSecondPreviousY = mPreviousY;

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}
