package examples.gobot;

import basestation.vision.VisionCoordinate;

//Find equation of line between two points
public class Equation{

    //in terms of y
    protected double slope;
    protected double yIntercept;

    //special case: vertical lines
    protected boolean vertical;
    protected double xval;
    protected double startx;
    protected double endx;

    public Equation(VisionCoordinate start, VisionCoordinate end){
        if (start.x == end.x){
            vertical = true;
            xval = start.x;
            if (start.y < end.y) {
                startx = start.y;
                endx = end.y;
            } else {
                startx = end.y;
                endx = start.y;
            }
        }
        else{
            slope = (end.y - start.y)/(end.x - start.x);
            yIntercept = start.y - slope*start.x;
        }

        if (start.x < end.x) {
            startx = start.x;
            endx = end.x;
        } else {
            startx = end.x;
            endx = start.x;
        }
    }

    public Equation(VisionCoordinate start, double angle){
        if ( (angle % Math.PI/2) < 0.001) {
            vertical = true;
            xval = start.x;
        }
        else{
            slope = Math.tan(angle);
            yIntercept = start.y - slope*start.x;
        }

        startx = Double.MIN_VALUE;
        endx = Double.MAX_VALUE;
    }

    public boolean inDomain(Point test) {
        if (vertical) {
            return test.ycor >= startx && test.ycor <= endx;
        }
        return test.xcor >= startx && test.xcor <= endx;
    }

}