package dataTypes;

/**
 * This class represents one point of x, y, and pressure values
 */
public class Point {

    // Coordinates and pressure value for given point
    private double x;
    private double y;
    private double pressure;

    public Point(double x, double y, double pressure) {
	this.x = x;
	this.y = y;
	this.pressure = pressure;
    }

    public Point(Point p) {
	this.x = p.x;
	this.y = p.y;
	this.pressure = p.pressure;
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
}
