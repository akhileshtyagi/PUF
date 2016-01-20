package puf.iastate.edu.puf_enrollment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import dataTypes.*;
import dataTypes.Point;

/**
 * TODO: document your custom view class.
 */
public class NormalizeTestView extends View {
    private Paint mPufPaint, mFirstPointPaint, mLastPointPaint, mNormPaint;
    private Path mPath;
    private TextPaint mTextPaint;
    private float mX, mY;
    private dataTypes.Point firstPoint, lastPoint;
    private Challenge mChallenge;
    private Response mResponse;
    private ArrayList<Point> mPoints;
    private ArrayList<Point> mChallengePoints;
    private boolean drawNormalizedPoints;

    public NormalizeTestView(Context context) {
        super(context);
        init(null, 0);
    }

    public NormalizeTestView(Context context, AttributeSet attrs) {
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
        
        mPath = new Path();

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
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPufPaint);
        canvas.drawCircle((float) firstPoint.getX(), (float) firstPoint.getY(), 20, mFirstPointPaint);
        canvas.drawCircle((float) lastPoint.getX(), (float) lastPoint.getY(), 20, mLastPointPaint);


        if(drawNormalizedPoints) {
            for(int i = 0; i < mResponse.getNormalizedResponse().size(); i++) {
                Point tempPoint = mResponse.getNormalizedResponse().get(i);
                canvas.drawCircle((float) tempPoint.getX(), (float) tempPoint.getY(), 10, mNormPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
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
    private void startTouch(float x, float y) {
        clearCanvas();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;

        firstPoint = new Point(x, y, 0);

        mPoints.clear();

        mPoints.add(new Point(x, y, 0));
        drawNormalizedPoints = false;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= 5|| dy >= 5) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
        mPoints.add(new Point(x, y, 0));
    }

    private void upTouch() {
        mPath.lineTo(mX, mY);
        lastPoint = new Point(mX, mY, 0);

        mChallenge = new Challenge(mChallengePoints, 0);
        mResponse = new Response(mPoints);
        mChallenge.addResponse(mResponse);
        drawNormalizedPoints = true;
    }

    public void clearCanvas() {
        mPath.reset();
        invalidate();
    }

}
