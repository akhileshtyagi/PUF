package puf.iastate.edu.puf_enrollment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import dataTypes.Challenge;
import dataTypes.Point;
import dataTypes.Response;

/**
 * TODO: document your custom view class.
 */
public class NewNormalizeTestView extends View {
    private Paint mPufPaint, mFirstPointPaint, mLastPointPaint, mNormPaint, mRespPathPaint, mNormRespPaint;
    private Path mPath, mRespPath;
    private TextPaint mTextPaint;
    private float mX, mY, mPressure;
    private Point firstPoint, lastPoint;
    private Challenge mChallenge;
    private Response mResponse, mNewResponse;
    private ArrayList<Point> mPoints;
    private ArrayList<Point> mChallengePoints;
    private boolean drawNormalizedPoints, drawNormalizedResponsePoints, drawnFirstTrace;

    public NewNormalizeTestView(Context context) {
        super(context);
        init(null, 0);
    }

    public NewNormalizeTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        mPufPaint = new Paint(0);
        mPufPaint.setStyle(Paint.Style.STROKE);
        mPufPaint.setStrokeJoin(Paint.Join.ROUND);
        mPufPaint.setStrokeWidth(4f);
        mPufPaint.setAntiAlias(true);
        mPufPaint.setColor(Color.BLACK);

        mRespPathPaint = new Paint(0);
        mRespPathPaint.setStyle(Paint.Style.STROKE);
        mRespPathPaint.setStrokeJoin(Paint.Join.ROUND);
        mRespPathPaint.setStrokeWidth(4f);
        mRespPathPaint.setAntiAlias(true);
        mRespPathPaint.setColor(Color.GRAY);
        
        mFirstPointPaint = new Paint(0);
        mFirstPointPaint.setStyle(Paint.Style.FILL);
        mFirstPointPaint.setStrokeJoin(Paint.Join.ROUND);
        mFirstPointPaint.setStrokeWidth(4f);
        mFirstPointPaint.setAntiAlias(true);
        mFirstPointPaint.setColor(Color.GREEN);

        mLastPointPaint = new Paint(0);
        mLastPointPaint.setStyle(Paint.Style.FILL);
        mLastPointPaint.setStrokeJoin(Paint.Join.ROUND);
        mLastPointPaint.setStrokeWidth(4f);
        mLastPointPaint.setAntiAlias(true);
        mLastPointPaint.setColor(Color.RED);

        mNormPaint = new Paint(0);
        mNormPaint.setStyle(Paint.Style.FILL);
        mNormPaint.setStrokeJoin(Paint.Join.ROUND);
        mNormPaint.setStrokeWidth(4f);
        mNormPaint.setAntiAlias(true);
        mNormPaint.setColor(Color.BLACK);
        
        mNormRespPaint = new Paint(0);
        mNormRespPaint.setStyle(Paint.Style.FILL);
        mNormRespPaint.setStrokeJoin(Paint.Join.ROUND);
        mNormRespPaint.setStrokeWidth(4f);
        mNormRespPaint.setAntiAlias(true);
        mNormRespPaint.setColor(Color.MAGENTA);
        
        mPath = new Path();
        mRespPath = new Path();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        firstPoint = new Point(0,0,0);
        lastPoint = new Point(0,0,0);

        mPoints = new ArrayList<>();

        mChallengePoints = new ArrayList<>();
        mChallengePoints.add(new Point(50, 50, 0));
        mChallengePoints.add(new Point(50, 250, 0));
        mChallengePoints.add(new Point(250, 250, 0));
        mChallengePoints.add(new Point(200, 250, 0));
        mChallengePoints.add(new Point(50, 450, 0));
        mChallengePoints.add(new Point(450, 500, 0));
        mChallengePoints.add(new Point(500, 500, 0));
        mChallengePoints.add(new Point(100, 700, 0));
        mChallengePoints.add(new Point(700, 700, 0));
        mChallengePoints.add(new Point(100, 900, 0));
        mChallengePoints.add(new Point(900, 900, 0));

        drawNormalizedPoints = false;
        drawNormalizedResponsePoints = false;
        drawnFirstTrace = false;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPufPaint);
        canvas.drawPath(mRespPath, mRespPathPaint);
        canvas.drawCircle((float) firstPoint.getX(), (float) firstPoint.getY(), 20, mFirstPointPaint);
        canvas.drawCircle((float) lastPoint.getX(), (float) lastPoint.getY(), 20, mLastPointPaint);


        if(drawNormalizedPoints) {
            /*
            for(int i = 0; i < mResponse.getNormalizedResponse().size(); i++) {
                Point tempPoint = mResponse.getNormalizedResponse().get(i);
                canvas.drawCircle((float) tempPoint.getX(), (float) tempPoint.getY(), 10, mNormPaint);
            }*/

            for(int i = 0; i < mResponse.getNormalizedResponse().size(); i++) {
                Point tempPoint = mResponse.getNormalizedResponse().get(i);
                canvas.drawCircle((float) tempPoint.getX(), (float) tempPoint.getY(), 10, mNormPaint);
            }
        }

        if(drawNormalizedResponsePoints) {
            for(int i = 0; i < mChallenge.getResponsePattern().get(1).getNormalizedResponse().size(); i++) {
                Point normTempPoint = mChallenge.getResponsePattern().get(1).getNormalizedResponse().get(i);
                canvas.drawCircle((float) normTempPoint.getX(), (float) normTempPoint.getY(), 10, mNormRespPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y, event.getPressure());
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y, event.getPressure());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y, float pressure) {
        if(!drawnFirstTrace) {
            clearCanvas();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;

            firstPoint = new Point(x, y, pressure);

            mPoints.clear();

            mPoints.add(new Point(x, y, pressure));
            drawNormalizedPoints = false;
            drawNormalizedResponsePoints = false;
        }

        else {
            mRespPath.moveTo(x, y);
            mX = x;
            mY = y;

            firstPoint = new Point(x, y, pressure);
            mPoints.clear();
            mPoints.add(new Point(x, y, pressure));
        }
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y, float pressure) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= 5|| dy >= 5) {
            if(!drawnFirstTrace) mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            else mRespPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            mPressure = pressure;
        }
        mPoints.add(new Point(x, y, pressure));
    }

    private void upTouch() {
        if(!drawnFirstTrace) {
            mPath.lineTo(mX, mY);
            lastPoint = new Point(mX, mY, mPressure);

            mChallenge = new Challenge(mChallengePoints, 0);
            mResponse = new Response(mPoints);
            mChallenge.addResponse(mResponse);
            drawNormalizedPoints = true;
            drawnFirstTrace = true;
        }
        else {
            mRespPath.lineTo(mX, mY);
            lastPoint = new Point(mX, mY, mPressure);

            mNewResponse = new Response(mPoints);
            mChallenge.addResponse(mNewResponse);
            drawNormalizedResponsePoints = true;
            drawnFirstTrace = false;
        }
    }

    public void clearCanvas() {
        mPath.reset();
        mRespPath.reset();
        invalidate();
    }

}
