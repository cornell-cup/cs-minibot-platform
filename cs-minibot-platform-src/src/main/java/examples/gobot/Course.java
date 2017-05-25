package examples.gobot;

import basestation.vision.VisionCoordinate;

import java.awt.geom.Path2D;
import java.util.ArrayList;

public class Course {

    /**
     * inner will represent the inner track of the course, outer will represent the outer track of the course
     */
    private Border inner;
    private Border outer;

    /**
     * startArea is a Path2D.Double instance that the goBot will start behind at the beginning of a race
     * middleArea is a Path2D.Double instance that the goBot must pass
     * before re-crossing the start area for a lap to be counted
     */
    private Path2D.Double startArea;
    private Path2D.Double middleArea;

    /**
     * @return returns the Border of the outer track of the Course
     */
    public Border getOuter() {
        return this.outer;
    }

    /**
     * @param outer Creates the outer border of the track with the ArrayList of VisionCoordinates passed in
     */
    public void setOuter(ArrayList<VisionCoordinate> outer) {
        this.outer = new Border(outer);
    }

    /**
     * @return returns the Border of the outer track of the Course
     */
    public Border getInner() {
        return this.inner;
    }

    /**
     * @param inner Creates the inner border of the track with the ArrayList of VisionCoordinates passed in
     */
    public void setInner(ArrayList<VisionCoordinate> inner) {
        this.inner = new Border(inner);
    }

    /**
     * @return returns the start area of the Course
     */
    public Path2D.Double getStartArea() {
        return this.startArea;
    }

    /**
     * @param startCoords Creates the Path2D.Double instance for the starting area of the Course
     */
    public void setStartArea(ArrayList<VisionCoordinate> startCoords) {
        this.startArea = createPath(startCoords);
    }

    /**
     * @return returns the middle area of the Course
     */
    public Path2D.Double getMiddleArea() {
        return this.middleArea;
    }

    /**
     * @param middleCoords Creates the Path2D.Double instance for the middle area of the Course
     */
    public void setMiddleArea(ArrayList<VisionCoordinate> middleCoords) {
        this.middleArea = createPath(middleCoords);
    }

    /**
     * @param coords list of VisionCoordinates to create a Path2D.Double instance
     * @return returns a Path2D.Double shape created by connecting the ArrayList of VisionCoordinates
     * (1st to 2nd, 2nd to 3rd, ..., last to 1st)
     */
    private Path2D.Double createPath(ArrayList<VisionCoordinate> coords) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(coords.get(0).x, coords.get(0).y);
        for (int i = 1; i < coords.size(); i++) {
            path.lineTo(coords.get(i).x, coords.get(i).y);
        }
        path.closePath();
        return path;
    }

    /**
     * @param vc is the VisionCoordinate to be tested
     * @return returns a boolean whether the coordinate is between the inner and outer tracks
     */
    public boolean isInsideTrack(VisionCoordinate vc) {
        return outer.inPath(vc) && !inner.inPath(vc);
    }

    /**
     * @param vc is the VisionCoordinate to be tested
     * @return returns a boolean whether the coordinate is inside the inner track
     */
    public boolean isInsideInsideTrack(VisionCoordinate vc) {
        return inner.inPath(vc);
    }

    /**
     * @param vc is the VisionCoordinate to be tested
     * @return returns a boolean whether the coordinate is inside the outer track
     */
    public boolean isOutsideOutsideTrack(VisionCoordinate vc) {
        return !outer.inPath(vc);
    }
}
