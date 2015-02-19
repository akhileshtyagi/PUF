package com.puf.android.softkeyboard;

import android.view.MotionEvent;

/**
 * Created by iantrich on 9/17/2014.
 */

public class Touch
{
    private long time;
    private float x;
    private float y;
    private float pressure;

    public Touch(MotionEvent me)
    {
        this.time = me.getEventTime();
        this.x = me.getX();
        this.y = me.getY();
        this.pressure = me.getPressure();

    }

    public String toString() {
        String str = "Time: " + Long.toString(time) + " X: " + Float.toString(x) +
        " Y: " + Float.toString(y) + " P: " + Float.toString(pressure);

        return str;
    }

    public String[] toArray() {
        String[] str = { Long.toString(time), Float.toString(x), Float.toString(y), Float.toString(pressure) };

        return str;
    }
}