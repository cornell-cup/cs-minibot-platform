package examples.gobot;

import basestation.vision.VisionCoordinate;

import java.awt.geom.Path2D;
import java.util.ArrayList;

public class Course {
    private Border inner;
    private Border outer;
    private Path2D.Double startArea;
    //private Path2D.Double finishArea;
    private Path2D.Double middleArea; //sketchy anti-cheating mechanism
    private static final double MARGIN = 0.2;
    private static final double TIMEALLOWEDOUT = 1.0;
    private double timeOutside = 0;

    public Course() {
    }


    public void setOuter(ArrayList<VisionCoordinate> outer) {
        this.outer = new Border(outer);
    }

    public Border getOuter() {
        return this.outer;
    }

    public void setInner(ArrayList<VisionCoordinate> inner) {
        this.inner = new Border(inner);
    }

    public Border getInner() {
        return this.inner;
    }

    public void setStartArea(ArrayList<VisionCoordinate> startCoords) {
        this.startArea = createPath(startCoords);
    }

    /*public void setFinishArea(ArrayList<VisionCoordinate> finishCoords) {
        this.finishArea = createPath(finishCoords);
    }*/

    public void setMiddleArea(ArrayList<VisionCoordinate> middleCoords) {
        this.middleArea = createPath(middleCoords);
    }

    public Path2D.Double getStartArea() {
        return this.startArea;
    }

    public Path2D.Double getMiddleArea() {
        return this.middleArea;
    }

    /*public Path2D.Double getFinishArea() {
        return this.finishArea;
    }*/

    private Path2D.Double createPath(ArrayList<VisionCoordinate>coords){
        Path2D.Double path = new Path2D.Double();
        path.moveTo(coords.get(0).x, coords.get(0).y);
        for(int i = 1; i <coords.size(); i++){
            path.lineTo(coords.get(i).x, coords.get(i).y);
        }
        path.closePath();
        return path;
    }

    public boolean isInsideTrack(VisionCoordinate vc) {
        return outer.inPath(vc) && !inner.inPath(vc);
    }

    public boolean isInsideInsideTrack(VisionCoordinate vc) {
        return inner.inPath(vc);
    }

    public boolean isOutsideOutsideTrack(VisionCoordinate vc) {
        return !outer.inPath(vc);
    }
}
