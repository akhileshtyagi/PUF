package com.example.element.swipe_box;

/**
 * Created by element on 10/17/15.
 */
public class Point implements java.io.Serializable
{
    public float x;
    public float y;
    public float pressure;
    public double time;

    public Point(float _x, float _y, float _pressure, float _time)
    {
        this.x = _x;
        this.y = _y;
        this.pressure = _pressure;
        this.time = _time;
    }
}