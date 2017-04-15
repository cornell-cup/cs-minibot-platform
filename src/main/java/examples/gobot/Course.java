package examples.gobot;

import basestation.vision.VisionCoordinate;

import java.util.ArrayList;

public class Course {
    private Border inner;
    private Border outer;
    private static final double MARGIN = 0.2;
    private static final double TIMEALLOWEDOUT = 1.0;
    private double timeOutside = 0;

    public Course() {
    }


    public void setOuter(ArrayList<VisionCoordinate> outer) {
        this.outer = new Border(outer);
    }

    public void setInner(ArrayList<VisionCoordinate> inner) {
        this.inner = new Border(inner);
    }

    public boolean isInsideTrack(VisionCoordinate vc) {
        return outer.inPath(vc) && !inner.inPath(vc);
    }
}
