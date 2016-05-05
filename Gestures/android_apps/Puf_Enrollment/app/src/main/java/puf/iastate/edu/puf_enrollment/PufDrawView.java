package puf.iastate.edu.puf_enrollment;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import dataTypes.Challenge;
import dataTypes.Response;


public class PufDrawView extends View
{
    private Paint redPaint;
    private Paint greenPaint;
    private Paint bluePaint;
    private TextView mUpdateView;
    private Point[] challenge;
    private ArrayList<dataTypes.Point> response;
    private RegisterGesturesActivity mRGA;
    private long lastMotionEventTime;
    private Paint mPufPaint, mFirstPointPaint, mLastPointPaint, mNormPaint, mRespPathPaint, mNormRespPaint;

    private Path mPath, mRespPath;
    private dataTypes.Point firstPoint, lastPoint;
    private boolean drawNormalizedPoints, drawNormalizedResponsePoints, drawnFirstTrace;
    private Challenge mChallenge;
    private Response mResponse;
    private float mX, mY;
    private ArrayList<dataTypes.Point> mPoints;
    private ArrayList<dataTypes.Point> mChallengePoints;

    public PufDrawView(Context context, AttributeSet attr)
    {
        super(context, attr);
        init();
        mRGA = (RegisterGesturesActivity) context;
    }

    public void setUpdateView(TextView tv)
    {
        this.mUpdateView = tv;
    }

    private void init()
    {
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

        redPaint = new Paint();
        redPaint.setAntiAlias(true);
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setColor(Color.RED);
        redPaint.setStrokeWidth(15);

        bluePaint = new Paint();
        bluePaint.setAntiAlias(true);
        bluePaint.setStyle(Paint.Style.STROKE);
        bluePaint.setColor(Color.BLUE);
        bluePaint.setStrokeWidth(10);

        greenPaint = new Paint();
        greenPaint.setAntiAlias(true);
        greenPaint.setStyle(Paint.Style.STROKE);
        greenPaint.setColor(Color.GREEN);
        greenPaint.setStrokeWidth(5);

        mNormRespPaint = new Paint(0);
        mNormRespPaint.setStyle(Paint.Style.FILL);
        mNormRespPaint.setStrokeJoin(Paint.Join.ROUND);
        mNormRespPaint.setStrokeWidth(4f);
        mNormRespPaint.setAntiAlias(true);
        mNormRespPaint.setColor(Color.MAGENTA);

        response = new ArrayList<>();
        lastMotionEventTime = 0;

        firstPoint = new dataTypes.Point(0,0,0);
        lastPoint = new dataTypes.Point(0,0,0);

        mPoints = new ArrayList<>();
        mPath = new Path();
        mRespPath = new Path();

        // Just here so profile has a challenge
        mChallengePoints = new ArrayList<>();
        mChallengePoints.add(new dataTypes.Point(50, 50, 0));
        mChallengePoints.add(new dataTypes.Point(50, 250, 0));
        mChallengePoints.add(new dataTypes.Point(250, 250, 0));
        mChallengePoints.add(new dataTypes.Point(200, 250, 0));
        mChallengePoints.add(new dataTypes.Point(50, 450, 0));
        mChallengePoints.add(new dataTypes.Point(450, 500, 0));
        mChallengePoints.add(new dataTypes.Point(500, 500, 0));
        mChallengePoints.add(new dataTypes.Point(100, 700, 0));
        mChallengePoints.add(new dataTypes.Point(700, 700, 0));
        mChallengePoints.add(new dataTypes.Point(100, 900, 0));
        mChallengePoints.add(new dataTypes.Point(900, 900, 0));

        drawNormalizedPoints = false;
        drawNormalizedResponsePoints = false;
        drawnFirstTrace = false;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPufPaint);
        canvas.drawPath(mRespPath, mRespPathPaint);
        canvas.drawCircle((float) firstPoint.getX(), (float) firstPoint.getY(), 20, mFirstPointPaint);
        canvas.drawCircle((float) lastPoint.getX(), (float) lastPoint.getY(), 20, mLastPointPaint);
        if(challenge.length >= 4)
        {
            Path challengeStart = new Path();
            challengeStart.moveTo(challenge[0].x, challenge[0].y);
            challengeStart.lineTo(challenge[1].x, challenge[1].y);

            Path challengeMiddle = new Path();
            challengeMiddle.moveTo(challenge[1].x, challenge[1].y);
            challengeMiddle.lineTo(challenge[2].x, challenge[2].y);

            Path challengePath = new Path();
            for(int i = 2; i < challenge.length; i++)
            {
                if(i == 2)
                {
                    challengePath.moveTo(challenge[i].x, challenge[i].y);
                }
                else
                {
                    challengePath.lineTo(challenge[i].x, challenge[i].y);
                }
            }
            canvas.drawPath(challengePath, redPaint);
            canvas.drawPath(challengeMiddle, bluePaint);
            canvas.drawPath(challengeStart, greenPaint);

            if(drawNormalizedPoints) {
                for(int i = 0; i < mResponse.getNormalizedResponse().size(); i++) {
                    dataTypes.Point tempPoint = mResponse.getNormalizedResponse().get(i);
                    canvas.drawCircle((float) tempPoint.getX(), (float) tempPoint.getY(), 10, mNormPaint);
                }
            }

        }
        else if(challenge.length == 2)
        {
            Path challengeLine = new Path();
            challengeLine.moveTo(challenge[0].x, challenge[0].y);
            challengeLine.lineTo(challenge[1].x, challenge[1].y);
            canvas.drawPath(challengeLine, redPaint);
            canvas.drawPoint(challenge[0].x, challenge[0].y, greenPaint);
        }
    }

    public void giveChallenge(Point[] challenge)
    {
        this.challenge = challenge;
        this.invalidate();
    }

    @Override
    public boolean onTouchEvent( MotionEvent me )
    {
        float x = me.getX();
        float y = me.getY();

        switch( me.getAction() )
        {
            case MotionEvent.ACTION_DOWN:
                mUpdateView.setText( "Pressure = " + me.getPressure());
                response.clear();
                response.add(new dataTypes.Point(me.getX(), me.getY(), me.getPressure(), -1, (double) me.getEventTime() - me.getDownTime()));
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mUpdateView.setText("No touch detected.");
                mRGA.onResponseAttempt(response);
                upTouch();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                if(this.lastMotionEventTime == 0) {
                    this.lastMotionEventTime = me.getEventTime();
                }

                //mUpdateView.setText( "(x,y) = (" + Math.round(me.getX()) + ", " + Math.round(me.getY()) + ") - Pressure = " + me.getPressure() + " Time = " + (me.getEventTime()-this.lastMotionEventTime));
                mUpdateView.setText( "Pressure = " + me.getPressure());
                response.add(new dataTypes.Point(me.getX(), me.getY(), me.getPressure(), -1, (double) me.getEventTime()-this.lastMotionEventTime));
                this.lastMotionEventTime = me.getEventTime();
                moveTouch(x, y);
                invalidate();
                break;
            default:
                break;
        }

        return true;
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        clearCanvas();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;

        firstPoint = new dataTypes.Point(x, y, 0);

        mPoints.clear();

        mPoints.add(new dataTypes.Point(x, y, 0));
        drawNormalizedPoints = false;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= 5|| dy >= 5) {
            if(!drawnFirstTrace) mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            else mRespPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
        mPoints.add(new dataTypes.Point(x, y, 0));
    }


    private void upTouch() {

        mPath.lineTo(mX, mY);
        lastPoint = new dataTypes.Point(mX, mY, 0);

        mChallenge = new Challenge(mChallengePoints, 0);
        mResponse = new Response(mPoints);
        mChallenge.addResponse(mResponse);
        drawNormalizedPoints = true;

    }

    public void clearCanvas() {
        mPath.reset();
        mRespPath.reset();
        invalidate();
    }

    public interface ResponseListener{
        void onResponseAttempt(ArrayList<dataTypes.Point> response);
    }
}
