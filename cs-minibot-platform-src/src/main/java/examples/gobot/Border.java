package examples.gobot;

import basestation.vision.VisionCoordinate;

import java.awt.geom.Path2D;
import java.util.ArrayList;

/**
 * The Border class will represent the tracks (inner and outer) for the Course the goBot races on
 */
public class Border {
    private Path2D.Double boundary;
    private ArrayList<VisionCoordinate> borderCoords;

    /**
     * @param coords represents the list of VisionCoordinates that will be used to create a Border
     */
    public Border(ArrayList<VisionCoordinate> coords) {
        this.boundary = createPath(coords);
        borderCoords = coords;
    }

    /**
     * @param coords is the ArrayList of VisionCoordinates passed in
     * @return returns a Path2D.Double that consists of a shape created by connecting the points in order
     * from the first element to the second, second to the third, ... and the last back to the first.
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
     * @param vc is the VisionCoordinate that is being tested
     * @return returns whether the VisionCoordinate is inside the Path2D.Double object (shape)
     */
    public Boolean inPath(VisionCoordinate vc) {
        return this.boundary.contains(vc.x, vc.y);
    }

    /**
     * @return the list of VisionCoordinates that creates this Border instance
     */
    public ArrayList<VisionCoordinate> returnCoords() {
        return this.borderCoords;
    }
}
