package com.example.gesturesapp;

public class Point
{
    public float x;
    public float y;
    public float pressure;
    public float size;
    public long time;

    public Point(float _x, float _y, float _pressure, float _size, long _time)
    {
        this.x = _x;
        this.y = _y;
        this.pressure = _pressure;
        this.size = _size;
        this.time = _time;
    }
}
