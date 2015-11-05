package dataTypes;

import java.io.Serializable;

/**
 * This class represents one point of x, y, and pressure values
 */
public class Point implements Serializable {
    private static final long serialVersionUID = -6396773155505367546L;

    // Coordinates and pressure value for given point
    private double x;
    private double y;
    private double pressure;
    private double distance;
    private double time;

    public Point(double x, double y, double pressure, double distance, double timestamp) {
	this(x, y, pressure, distance);
	this.time = timestamp;
    }

    public Point(double x, double y, double pressure, double distance) {
	this(x, y, pressure);
	this.distance = distance;
    }

    public Point(double x, double y, double pressure) {
	this.x = x;
	this.y = y;
	this.pressure = pressure;
	this.distance = -1;
	this.time = 0;
    }

    public Point(Point p) {
	this.x = p.x;
	this.y = p.y;
	this.pressure = p.pressure;
	this.distance = p.distance;
	this.time = p.time;
    }

    public double getX() {
	return x;
    }

    public double getY() {
	return y;
    }

    public double getPressure() {
	return pressure;
    }

    public double getDistance() {
	return this.distance;
    }

    public double getTime() {
	return this.time;
    }

    public void setDistance(double distance) {
	this.distance = distance;
    }

    public void setTime(double timestamp) {
	this.time = timestamp;
    }

    /**
     * equality does not check for distance or timestamp. This is done on
     * purpose. It doesn't make sense to check for time because all touchs are
     * separated by time. If we did check for time this would never return true.
     * It doesn't make sense to check for distance because this is a function of
     * the x/y values and the axis to which this is being normalized. Because we
     * already check x,y we know that if the touches were being normalized to
     * the same axis, the distance would be the same. And if they are not being
     * normalized to the same axis, we don't want this to affect equality, so we
     * don't check.
     */
    @Override
    public boolean equals(Object p) {
	Point other = (Point) p;
	boolean same = true;

	same = same && this.x == other.x;
	same = same && this.y == other.y;
	same = same && this.pressure == other.pressure;

	return same;
    }
}
