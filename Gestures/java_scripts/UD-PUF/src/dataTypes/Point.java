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

    public Point(double x, double y, double pressure, double distance){
	this(x,y,pressure);
	this.distance = distance;
    }
    
    public Point(double x, double y, double pressure) {
	this.x = x;
	this.y = y;
	this.pressure = pressure;
	this.distance = -1;
    }

    public Point(Point p) {
	this.x = p.x;
	this.y = p.y;
	this.pressure = p.pressure;
	this.distance = -1;
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

    public void setDistance(double distance) {
	this.distance = distance;
    }

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
