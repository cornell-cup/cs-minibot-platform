package examples.gobot;

import basestation.vision.VisionCoordinate;

/**
 * Construct an equation for a line given either two points on the line, or a starting point and an angle.
 * Variables to keep track of are the slope and yIntercept of the line and whether or not it is a vertical line.
 * In the case that it is a vertical line, the equation should be an equation of the form x = c, where c is the
 * x-coordinate of any point on the line (value stored in xval). The boundary edges are line segments and not lines,
 * so their bounds are stored using minimum x and y coordinate variables (startx, endx, starty, endy).
 */
public class Equation {

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

    /**
     * Constructs an Equation for the line given its starting and ending point.
     * If the bounding x coordinates are the same, then the line is vertical
     * and should be treated as such. Here the slope and y intercepts do not matter.
     * else, the line is of the form y = mx+b, in which case, the slope and y-intercept
     * are determined from the two points and stored. In both cases, the bounding x and y values are stored.
     *
     * @param start
     * @param end
     */
    public Equation(VisionCoordinate start, VisionCoordinate end) {
        if (start.x == end.x) {
            vertical = true;
            xval = start.x;
            if (start.y < end.y) {
                starty = start.y;
                endy = end.y;
            } else {
                starty = end.y;
                endy = start.y;
            }
        } else {
            slope = (end.y - start.y) / (end.x - start.x);
            yIntercept = start.y - slope * start.x;

            if (start.x < end.x) {
                startx = start.x;
                endx = end.x;
            } else {
                startx = end.x;
                endx = start.x;
            }
            if (start.y < end.y) {
                starty = start.y;
                endy = end.y;
            } else {
                starty = end.y;
                endy = start.y;
            }
        }
    }

    /**
     * Creates an equation given a starting coordinate and angle. If the angle is near 90 degrees,
     * it is considered a vertical line and is treated as such. else, it is of the form y = mx+b. The slope
     * can be directly determined by the angle, and the y-intercept then determined.
     * This constructor is mainly used to build equations for the sweep lines, which have no bounds.
     *
     * @param start
     * @param angle
     */
    public Equation(VisionCoordinate start, double angle) {

        if (Math.abs(angle % (Math.PI / 2)) < 0.001) {
            vertical = true;
            xval = start.x;

        } else {
            slope = Math.tan(angle);
            yIntercept = start.y - slope * start.x;
        }

        startx = Double.MIN_VALUE;
        endx = Double.MAX_VALUE;
    }

    /**
     * @param test intersection point
     * @return true if test point is on the line segment corresponding to a boundary edge
     */
    public boolean inDomain(Point test) {
        //if vertical line check that the intersection is between the
        // starting and ending y coordinates
        if (test != null) {
            if (vertical || startx == endx) {
                return test.ycor >= starty && test.ycor <= endy;
            }
            //horizontal lines
            else if (starty == endy) {
                return test.xcor >= startx && test.xcor <= endx;
            }
            //everything else
            else {
                return test.xcor >= startx && test.xcor <= endx
                        && test.ycor >= starty && test.ycor <= endy;
            }
        } else {
            return false;
        }
    }

    /**
     * @return String representation of an Equation
     */
    @Override
    public String toString() {
        if (!vertical) {
            return ("Y =" + slope + "x + " + yIntercept);
        } else {
            return ("X = " + xval);
        }
    }

}