package examples.gobot;

import basestation.vision.VisionCoordinate;

import java.awt.geom.Path2D;
import java.util.ArrayList;

public class Border {
    private Path2D.Double boundary;

    public Border(ArrayList<VisionCoordinate> coords){
        this.boundary = createPath(coords);
    }

    private Path2D.Double createPath(ArrayList<VisionCoordinate>coords){
        Path2D.Double path = new Path2D.Double();
        path.moveTo(coords.get(0).x, coords.get(0).y);
        for(int i = 1; i <coords.size(); i++){
            path.lineTo(coords.get(i).x, coords.get(i).y);
        }
        path.closePath();
        return path;
    }

    public Boolean inPath(VisionCoordinate vc) {
        if (this.boundary.contains(vc.x, vc.y)) {
            return true;
        } else {
            return false;
        }
    }
}
