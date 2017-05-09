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

    protected double starty;
    protected double endy;


    public Equation(VisionCoordinate start, VisionCoordinate end){
        if (start.x == end.x){
            vertical = true;
            xval = start.x;
            if (start.y < end.y) {
                starty = start.y;
                endy = end.y;
            } else {
                starty = end.y;
                endy = start.y;
            }
        }
        else{
            slope = (end.y - start.y)/(end.x - start.x);
            yIntercept = start.y - slope*start.x;
            if (start.x < end.x) {
                startx = start.x;
                endx = end.x;
            } else {
                startx = end.x;
                endx = start.x;
            }
            if (start.y < end.y){
                starty = start.y;
                endy = end.y;
            }
            else{
                starty = end.y;
                endy = start.y;
            }
        }
    }

    public Equation(VisionCoordinate start, double angle){
        if ( Math.abs(angle % Math.PI/2) < 0.001) {
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
        //if vertical line check that the intersection is between the
        // starting and ending y coordinates
        if (vertical || startx == endx) {
            return test.ycor >= starty && test.ycor <= endy;
        }
        //horizontal lines
        else if (starty == endy){
            return test.xcor >= startx && test.xcor <= endx;
        }
        //everything else
        else{
            return test.xcor >= startx && test.xcor <= endx
                    && test.ycor >= starty && test.ycor <= endy;
        }
    }

    @Override
    public String toString(){
        if (!vertical) {
            return ("Y ="+ slope + "x + " + yIntercept);
        }
        else{
            return ("X = " + xval);
        }
    }

}