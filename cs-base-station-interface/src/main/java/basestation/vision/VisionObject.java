package basestation.vision;

/**
 * Represents an object being tracked by a Vision System. Must have a persistent id in order to be
 * maintained between receipt of data. VisionObjects are functional and thus should be
 * retrieved again if a newer version is desired.
 */
public class VisionObject {
    /**
     * The vision system this object is associated with
     */
    public final VisionSystem vs;

    /**
     * The id of this object (vision id)
     */
    public final int vid;

    /**
     * The coordinate of this object.
     */
    public final VisionCoordinate coord;

    /**
     * Creates a visionobject
     * @param vs The VisionSystem this object belongs to
     * @param vid The id assigned to this object by its VisionSystem
     * @param myCoord The coordinate of this object in relation to its VisionSystem
     */
    public VisionObject(VisionSystem vs, int vid, VisionCoordinate myCoord) {
        this.vs = vs;
        this.vid = vid;
        this.coord = myCoord;
    }

    public VisionObject(VisionObject old, VisionCoordinate newCoord) {
        this.vs = old.vs;
        this.vid = old.vid;
        this.coord = newCoord;
    }

    @Override
    public String toString() {
        return "[Vision Object|" + vs + "|" + coord + "|" + vid + "]";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof VisionObject && o.toString().equals(toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
