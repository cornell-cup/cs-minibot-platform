package examples.gobot;

import basestation.BaseStation;
import basestation.vision.VisionCoordinate;
import basestation.vision.VisionObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AIUtil {

    private final int numLines;
    private final double min;
    private final double max;
    Course course;

    public AIUtil(int numLines, double min, double max) {
        this.numLines = numLines;
        this.min = min;
        this.max = max;
    }

    /**
     * @param e1 Equation one
     * @param e2 Equation two
     * @return Point representing the intersection between e1 and e2
     */
    public Point intersection(Equation e1, Equation e2) {
        try {
            /*
            Two vertical lines should not intersection each other
            unless they are the same line
             */

            if (e1.vertical && e2.vertical) {
                return null;
            }
            //If either are vertical, just plug in
            else if (e1.vertical || e2.vertical) {

                if (e1.vertical) {
                    return new Point(e1.xval, e1.xval * e2.slope +
                            e2.yIntercept);
                } else {
                    return new Point(e2.xval, e2.xval * e1.slope +
                            e1.yIntercept);
                }

            } else {
                double x = (e2.yIntercept - e1.yIntercept) / (e1.slope -
                        e2.slope);
                double y = e1.slope * x + e1.yIntercept;
                return new Point(x, y);
            }
        }

        //no intersection
        catch (Exception noIntersection) {
            noIntersection.printStackTrace();
        }
        return null;
    }

    /**
     * @param cors arraylist of VisionCoordinates of the track
     * @return list of all equations found between each pair of
     * consecutive boundary coordinates
     */
    public List<Equation> findEquations(ArrayList<VisionCoordinate> cors) {
        int size = cors.size();
        List<Equation> eqs = new ArrayList<Equation>();
        for (int i = 0; i < size; i++) {
            eqs.add(new Equation(cors.get(i), cors.get((i + 1) % size)));
        }
        return eqs;
    }


    /**
     * @param allEqs Arraylist of equations
     * @param target Equation to find intersections with
     * @return arraylist of all intersection points between target and the equations
     * in e
     */
    public ArrayList<Point> findIntersection(ArrayList<Equation> allEqs, Equation target) {
        int size = allEqs.size();
        ArrayList<Point> pts = new ArrayList<Point>();
        for (int i = 0; i < size; i++) {
            Point interPoint = intersection(allEqs.get(i), target);
            if (allEqs.get(i) != null && allEqs.get(i).inDomain(interPoint)) {
                pts.add(interPoint);
            }
        }
        return pts;
    }

    /**
     * @param vc VisionCoordinate representing one location
     * @param p  Point two representing another location
     * @return distance between the two locations
     */
    public double distanceBetween(VisionCoordinate vc, Point p) {
        double dx = vc.x - p.xcor;
        double dy = vc.y - p.ycor;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * @param vc       VisionCoordinate representing the bot's current location
     * @param numLines number of sweep lines
     * @return Arraylist of all equations of the sweep lines
     */
    public ArrayList<Equation> sweep(VisionCoordinate vc, int numLines) {
        ArrayList<Equation> sweepLines = new ArrayList<>();
        for (int i = 0; i < numLines; i++) {
            sweepLines.add(new Equation(vc, vc.getThetaOrZero() + i *
                    intervalAngle() - (max - min) / 2));
        }
        return sweepLines;
    }

    /**
     * @return the angle between each pair of consecutive sweep lines
     */
    public double intervalAngle() {
        return (max - min) / (numLines - 1);
    }

    /**
     * @return arraylist consisting of all the angles at which a sweep line should be
     * determined
     */
    public ArrayList<Double> allAngles() {
        ArrayList<Double> angles = new ArrayList<>();
        for (int i = 0; i < numLines; i++) {
            angles.add(min + i * intervalAngle());
        }
        return angles;
    }

    /**
     * @param origin VisionCoordinate representing one point
     * @param target Point representing the destination point
     * @return angle that the vector from origin to target forms with the positive x axis
     */
    public double pointAngle(VisionCoordinate origin, Point target) {
        boolean isVertical = origin.x - target.xcor == 0;
        if (!isVertical) {
            double deltaY = target.ycor - origin.y;
            double deltaX = target.xcor - origin.x;
            return Math.atan2(deltaY, deltaX);
        } else {
            if (target.ycor > origin.y) {
                return Math.PI;
            } else {
                return 3 * Math.PI;
            }
        }
    }

    /**
     * @param pAngle          angle sweep line makes with the positive x-axis
     * @param vc              VisionCoordinate representing current position
     * @param sweepTotalAngle total angle to sweep over
     * @param sweepAngle      angle between sweep lines
     * @return if the point is in front of the bot
     */
    public boolean isValid(double pAngle, VisionCoordinate vc, double
            sweepTotalAngle, double sweepAngle) {
        return Math.abs(pAngle - (vc.getThetaOrZero() - (sweepTotalAngle / 2) +
                sweepAngle)) < 0.1;
    }

    /**
     * @param eq equation of boundary line
     * @param vc VisionCoordinate representing current position
     * @param x  x coordinate of the intersection point
     * @param y  y coordinate of the intersection point
     * @return true if the intersection point is "in front" of the bot.
     * This uses a different way to calculate whether a point is considered in front
     * by looking at the coordinate in terms of the direction the bot is facing.
     */
    public boolean isValid2(Equation eq, VisionCoordinate vc, double x,
                            double y) {
        if (!isValid(pointAngle(vc, new Point(x, y)), vc, max - min, intervalAngle())) {
            return false;
        }
        if (vc.getThetaOrZero() == 0) {
            return x >= vc.x;
        } else if (vc.getThetaOrZero() == Math.PI) {
            return x <= vc.x;
        } else if (vc.getThetaOrZero() < Math.PI) {
            double ycor = x * eq.slope + eq.yIntercept;
            return y >= ycor;
        } else {
            double ycor = x * eq.slope + eq.yIntercept;
            return y <= ycor;
        }
    }

    /**
     * @param vc VisionCoordinate representing current location
     * @param p  intersection point
     * @return true if the intersection point corresponds with the sweep line at 0 and 180 degrees.
     * When sweeping across from 0 to 180 degrees, the sweep lines have the same equations, so two
     * two intersection points must be determined from the same line. This method is used to determine
     * if a point corresponds to the 0 angle sweep line intersection point
     */
    public boolean isZeroAngle(VisionCoordinate vc, Point p) {
        if (vc.getThetaOrZero() == 0) {
            return p.ycor <= vc.y;
        } else if (vc.getThetaOrZero() == Math.PI) {
            return p.ycor >= vc.y;
        } else if (vc.getThetaOrZero() < Math.PI) {
            return p.xcor >= vc.x;
        } else {
            return p.xcor <= vc.x;
        }
    }

    /**
     * @param allLines      arraylist of equations for the boundary lines
     * @param sweepLines    arraylist of equations for the sweep lines
     * @param botCoordinate VisionCoordinate representing the bot's current location
     * @param inner         arraylist of equations for inner track
     * @param outer         arraylist of equations for outer track
     * @return arraylist of doubles representing the minimum distances calculated for each sweep line
     */
    public ArrayList<Double> calculateRayDistances(ArrayList<Equation> allLines,
                                                   ArrayList<Equation> sweepLines,
                                                   VisionCoordinate botCoordinate,
                                                   List<Equation> inner,
                                                   List<Equation> outer) {
        ArrayList<Double> distances = new ArrayList<>();

        /*if the angle is 180 degrees, the first and last sweep lines have the same equation, so
        they should only be counted once
        */
        if ((this.max - this.min) == Math.PI) {
            for (int i = 1; i < sweepLines.size() - 1; i++) {
                double minDist = Double.MAX_VALUE / 100f;
                for (Point intersectionPoint : findIntersection(allLines, sweepLines.get(i))) {
                    if (isValid2(sweepLines.get(0), botCoordinate,
                            intersectionPoint.xcor, intersectionPoint.ycor)) {
                        double dist = distanceBetween(botCoordinate, intersectionPoint);
                        if (dist < minDist) {
                            minDist = dist;
                        }
                    }
                }
                distances.add(minDist);
            }

            /*for the other equations, the equations are unique, so intersection points can
            be calculated for each equation. When sweeping across 180 degrees, since the 0 degree and
            180 degree sweep lines have the same equation, only one equation is used. Two intersection points
            are needed, however, so the two minimum intersection points are calculated for these sweeplines.
             */
            double least = Double.MAX_VALUE;
            double nextLeast = Double.MAX_VALUE;
            Point leastP = null;
            Point nextLeastP = null;
            /**
             * Find the least and next least intersection point distances for this sweepline
             */
            for (Point intersectionP : findIntersection(allLines, sweepLines.get(0))) {
                double dist = distanceBetween(botCoordinate, intersectionP);
                if (dist < least && dist < nextLeast) {
                    nextLeast = least;
                    nextLeastP = leastP;
                    least = dist;
                    leastP = intersectionP;
                } else if (dist < nextLeast) {
                    nextLeast = dist;
                    nextLeastP = intersectionP;
                }
            }

            /**
             * The method isZeroAngle determines if a point corresponds to the point
             * at the zero sweep line, if not then it must correspond to the point
             * at the 180 sweepline. Essentially, the 0 angle intersection point should always
             * be along the outer boundary track
             */
            if (isZeroAngle(botCoordinate, leastP)) {
                distances.add(0, least);
                distances.add(nextLeast);
            } else {
                distances.add(least);
                distances.add(0, nextLeast);
            }
        } else {
            for (int i = 0; i < sweepLines.size() - 1; i++) {
                double minDist = Double.MAX_VALUE / 100f;
                for (Point intersectionPoint : findIntersection(allLines, sweepLines.get(i))) {
                    if (isValid2(sweepLines.get(0), botCoordinate,
                            intersectionPoint.xcor, intersectionPoint.ycor)) {
                        double dist = distanceBetween(botCoordinate, intersectionPoint);
                        if (dist < minDist) {
                            minDist = dist;
                        }
                    }
                }
                distances.add(minDist);
            }
        }
        return distances;
    }

    /**
     * @return the angle the bot should move in next
     */
    public double calculateDriveAngle() {
        // TODO: Configure coordinate based on desired bot
        List<VisionObject> v1 = BaseStation.getInstance().getVisionManager()
                .getAllLocationData();
        if (v1.size() != 0) {
            VisionCoordinate pos = v1.get(0).coord;
            ArrayList<Equation> sweepEquations = sweep(pos, numLines);
            ArrayList<VisionCoordinate> inner = course.getInner().returnCoords();
            ArrayList<VisionCoordinate> outer = course.getOuter()
                    .returnCoords();
            List<Equation> innerEqs = findEquations(inner);
            List<Equation> outerEqs = findEquations(outer);
            ArrayList<Equation> totalEqs = new ArrayList<>();
            totalEqs.addAll(innerEqs);
            totalEqs.addAll(outerEqs);
            ArrayList<Double> important = calculateRayDistances(totalEqs, sweepEquations,
                    pos, innerEqs, outerEqs);
            double sumDistances = 0;
            for (double d : important) {
                sumDistances += d;
            }
            double theta = max - min;
            double out = 0;
            for (int i = 0; i < numLines; i++) {
                out += theta / (numLines - 1) * i * (important.get(i)) / (sumDistances);
            }
            return out + (Math.PI - theta) / 2;

        }
        return 0;
    }
}
