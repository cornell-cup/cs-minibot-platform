package basestation.vision;

import java.util.HashSet;
import java.util.Set;

/**
 * A vanilla vision system used to represent BaseStation's single exposed coordinate system.
 * It should not have any objects, but instead serves as a coordinate system for all other
 * vision systems to agree on.
 */
public class CanonicalVisionSystem extends VisionSystem {

    public CanonicalVisionSystem() {
        super(new VisionCoordinate(0, 0, 0));

    }

    @Override
    public Set<VisionObject> getAllObjects() {
        return new HashSet<>();
    }
}
