package com.example.element.swipe_box;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class PufDrawView extends View
{
    private Paint redPaint;
    private Paint greenPaint;
    private Paint bluePaint;
    private TextView tv;
    private Point[] challenge;
    private ArrayList<Point> response;

    private long last_motion_event_time;

    public PufDrawView(Context context, AttributeSet attr)
    {
        super(context, attr);
        init();
    }

    public void setTV(TextView tv)
    {
        this.tv = tv;
    }

    private void init()
    {
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

        response = new ArrayList<Point>();
        last_motion_event_time = 0;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
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
            canvas.drawPath(challengeMiddle, redPaint);
            canvas.drawPath(challengeStart, redPaint);
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

    public ArrayList<Point> get_response(){
        return this.response;
    }

    @Override
    public boolean onTouchEvent( MotionEvent me )
    {
        switch( me.getAction() )
        {
            case MotionEvent.ACTION_DOWN:
                tv.setText( "(x,y) = (" + Math.round(me.getX()) + ", " + Math.round(me.getY()) + ") - Pressure = " + me.getPressure() );
                response.clear();

                // only add the point if the motion event is inside the bounds of the box
                // add the first response reguardless
                //if(within_challenge(me)) {
                    response.add(new Point(me.getX(), me.getY(), me.getPressure(), me.getEventTime()-me.getDownTime()));
                //}

                break;
            case MotionEvent.ACTION_UP:
                tv.setText( "No touch detected." );
                //RECORD ACTION FINISH

                // get a reference to the activit and finish it
                Activity activity = (Activity)getContext();
                activity.finish();

                break;
            case MotionEvent.ACTION_MOVE:
                tv.setText( "(x,y) = (" + Math.round(me.getX()) + ", " + Math.round(me.getY()) + ") Pressure = " + me.getPressure() );

                // only add the point if the motion event is inside the bounds of the box
                if(within_challenge(me)) {
                    // me.getEventTime()-me.getDownTime() is the time sense the beginning
                    // time sense beginning // response.add(new Point(me.getX(), me.getY(), me.getPressure(), me.getEventTime()-me.getDownTime()));

                    //Log.d("history_size: ", new Integer(me.getHistorySize()).toString());
                    //Log.d("interpoint_time: ", new Long(me.getEventTime()-me.getHistoricalEventTime(me.getHistorySize() - 1)).toString());
                    //Log.d("last_motion_event_time_difference", new Long(me.getEventTime() - this.last_motion_event_time).toString());

                    // if this is the first time though..... consider this point to have 0 time since the previous
                    if(this.last_motion_event_time == 0){
                        this.last_motion_event_time = me.getEventTime();
                    }

                    response.add(new Point(me.getX(), me.getY(), me.getPressure(), me.getEventTime()-this.last_motion_event_time));
                    this.last_motion_event_time = me.getEventTime();
                }

                break;
            default:
                break;
        }

        return true;
    }

    /**
     * determines whether the motion event falls within the bounds of the box
     * @return true if the motion event is contained within the box
     */
    private boolean within_challenge(MotionEvent me){
        boolean within = true;

        within = within && (me.getX() > challenge[0].x); // left boundry
        within = within && (me.getX() < challenge[2].x); // right boundry

        within = within && (me.getY() < challenge[2].y); // lower boundry
        within = within && (me.getY() > challenge[0].y); // upper boundry

        return within;
    }
}
