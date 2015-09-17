package dataTypes;

/**
 * This class represents one point of x, y, and pressure values
 */
public class Point {

    // Coordinates and pressure value for given point
    private int x;
    private int y;
    private double pressure;


    public Point(int x, int y, double pressure) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getPressure() {
        return pressure;
    }
}
