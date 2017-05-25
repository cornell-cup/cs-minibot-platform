package examples.gobot;

public class Point {
    public double xcor;
    public double ycor;

    public Point(double x, double y) {
        xcor = x;
        ycor = y;
    }

    @Override
    public String toString() {
        return xcor + ", " + ycor;
    }
}