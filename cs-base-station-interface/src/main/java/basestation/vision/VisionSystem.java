package basestation.vision;

import basestation.bot.robot.Bot;
import util.MathUtilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a source of input for vision. Objects are tracked with VisionObjects which hold a VisionCoordinate.
 * Each VisionObject must be represented by a persistent and uniquely identifiable integer which is VisionId, though
 * VisionObjects are permitted to be changed at will as long as the integer ID is preserved for the same object.
 */
public abstract class VisionSystem {

    // Used in case one vision system has a different scale from another
    private final double scalingFactor;
    private final VisionCoordinate origin;
    private Map<Bot, Integer> botMap;
    private int[][] occupancyMatrix;

    /**
     * Sets up a VisionSystem with o as its origin
     *
     * @param o A coordinate specifying the origin of the VisionSystem
     */
    protected VisionSystem(VisionCoordinate o) {
        this.origin = o;
        this.scalingFactor = 1.0;
        this.botMap = new HashMap<>();
    }

    /**
     * Transforms a coordinate from another system to this system.
     *
     * @param other       The other coordinate
     * @param otherSystem The other system
     * @return a transformed coordinate
     */
    public VisionCoordinate transformCoordinates(VisionCoordinate other, VisionSystem otherSystem) {
        double newX = (otherSystem.origin.x + other.x) / otherSystem.scalingFactor;
        double newY = (otherSystem.origin.y + other.y) / otherSystem.scalingFactor;
        double newTheta = MathUtilities.normalizeAngle(otherSystem.origin.getThetaOrZero() +
                other.getThetaOrZero() - origin.getThetaOrZero());

        return new VisionCoordinate(newX, newY, newTheta);
    }

    /**
     * @return the set of all VisionObjects actively tracked by the VisionSystem
     */
    public abstract Set<VisionObject> getAllObjects();

    /**
     * Returns the vision object represented by target or null if none exists
     *
     * @param target the vision object we are looking for
     * @return the VisionObject represented by target or null if none exists
     */
    public VisionObject getById(int target) {
        Set<VisionObject> all = this.getAllObjects();
        for (VisionObject vo : all) {
            if (vo.vid == target) return vo;
        }

        return null;
    }

    /**
     * Returns the vision id for this system representing b or null if none exists
     *
     * @param b The bot for which you are looking up the id
     * @return the vision id or null if none exists
     */
    public Integer getIdForBot(Bot b) {
        return botMap.get(b);
    }

    public void mapBotToVisionId(Bot b, int id) {
        botMap.put(b, id);
    }

    public Set<VisionObject> getAllObjectsWithRespectTo(VisionSystem other) {
        Set<VisionObject> vset = getAllObjects();
        HashSet<VisionObject> transformed = new HashSet<>();
        for (VisionObject vo : vset) {
            transformed.add(new VisionObject(vo, other.transformCoordinates(vo.coord, this)));
        }

        return transformed;
    }

    public String toString() {
        return "[Vision System|" + this.getClass().getSimpleName() + "]";
    }


    @Override
    public boolean equals(Object other) {
        return other instanceof VisionSystem && this.hashCode() == other.hashCode();
    }
}
