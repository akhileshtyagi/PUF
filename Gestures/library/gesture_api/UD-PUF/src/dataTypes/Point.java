package dataTypes;

import java.io.Serializable;

import metrics.Point_metrics;

/**
 * This class represents one point of x, y, and pressure values
 */
public class Point implements Serializable {
    public static double DEFAULT_MEASUREMENT_FREQUENCY = 17.0;

    public enum Metrics {
        PRESSURE,
        DISTANCE,
        TIME,
        VELOCITY,
        ACCELERATION
    }

    private static final long serialVersionUID = -6396773155505367546L;

    // Coordinates and pressure value for given point
    private double x;
    private double y;
    private double pressure;
    private double distance;
    private double time;
    private double velocity;
    private double acceleration;

    public Point(double x, double y, double pressure, double distance, double timestamp) {
        this(x, y, pressure, distance);
        this.time = timestamp;
        this.velocity = 0;
        this.acceleration = 0;
    }

    public Point(double x, double y, double pressure, double distance) {
        this(x, y, pressure);
        this.distance = distance;
        this.velocity = 0;
        this.acceleration = 0;
    }

    public Point(double x, double y, double pressure) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
        this.distance = -1;
        this.time = DEFAULT_MEASUREMENT_FREQUENCY;
        this.velocity = 0;
        this.acceleration = 0;
    }


    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        this.distance = 0;
        this.time = DEFAULT_MEASUREMENT_FREQUENCY;
        this.velocity = 0;
        this.acceleration = 0;
    }

    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
        this.pressure = p.pressure;
        this.distance = p.distance;
        this.time = p.time;
        this.velocity = p.velocity;
        this.acceleration = p.acceleration;
    }

    public double get_metric(Metrics type){
        switch(type){
            case PRESSURE:
                return this.pressure;

            case DISTANCE:
                return this.distance;

            case TIME:
                return this.time;

            case VELOCITY:
                return this.velocity;

            case ACCELERATION:
                return this.acceleration;
        }

        return -1.0;
    }

    public void set_metric(Metrics type, double value){
        switch(type){
            case PRESSURE:
                this.pressure = value;
                break;

            case DISTANCE:
                this.distance = value;
                break;

            case TIME:
                this.time = value;
                break;

            case VELOCITY:
                this.velocity = value;
                break;

            case ACCELERATION:
                this.acceleration = value;
                break;
        }
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

    @Override
    public String toString() {
        String string = "";

        string += "[";
        string += this.x + ", ";
        string += this.y + ", ";
        string += String.format("%.3f", this.pressure) + ", ";
        string += this.distance + ", ";
        string += this.time + "]";

        return string;
    }

    /**
     * format used by .r script
     */
    public String toRString() {
        String string = "";

        //string += "[";
        string += this.x + ", ";
        string += this.y + ", ";
        string += String.format("%f", this.pressure);
        //string += "]";

        return string;
    }
}
