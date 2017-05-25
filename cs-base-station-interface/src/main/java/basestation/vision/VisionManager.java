package basestation.vision;

import basestation.bot.robot.Bot;

import java.util.*;

/**
 * Collects input from multiple vision systems and exposes a unified vision API across the systems.
 * Vision is relatively under-tested and may be issue-prone/ in need of redesign.
 */
public class VisionManager { //TODO incomplete

    private VisionSystem canonicalVisionSystem; // Represents the BaseStation's understanding of locations
    private Map<Integer, VisionSystem> visionSystemMap;
    private int visionCounter;

    public VisionManager() {
        visionCounter = 0;
        canonicalVisionSystem = new CanonicalVisionSystem();
        visionSystemMap = new HashMap<>();
        visionSystemMap.put(visionCounter++, canonicalVisionSystem);
    }

    /**
     * Adds vs to be tracked by the VisionManager. Assumes vs is already calibrated to the
     * canonicalVisionSystem
     *
     * @param vs The visionSystem to begin tracking
     * @return the counter id of vs
     */
    public int addVisionSystem(VisionSystem vs) {
        int ct = visionCounter;
        this.visionSystemMap.put(visionCounter++, vs);
        return ct;
    }

    /**
     * @return All entries of VisionSystems with their associated ids
     */
    public Set<Map.Entry<Integer, VisionSystem>> getAllVisionMappings() {
        return visionSystemMap.entrySet();
    }

    /**
     * Returns a list of vision objects with vision ids. Coordinates are made canonical.
     *
     * @return A list of all VisionObjects tracked across all VisionSystems, relative to the Canonical system
     */
    public List<VisionObject> getAllLocationData() {
        ArrayList<VisionObject> tracked = new ArrayList<>();
        for (VisionSystem vs : visionSystemMap.values()) {
            tracked.addAll(vs.getAllObjectsWithRespectTo(canonicalVisionSystem));
        }

        return tracked;
    }

    /**
     * Gets the location of a bot relative to the BaseStation's interpretation or null if
     * no such coordinate exists
     *
     * @param bot The bot being looked up
     * @return A vision coordinate or null
     */
    public VisionCoordinate getBotCoordinate(Bot bot) {
        for (VisionSystem vs : visionSystemMap.values()) {
            Integer vid;
            if ((vid = vs.getIdForBot(bot)) != null) {
                VisionObject voo = vs.getById(vid);
                if (voo != null) return voo.coord;
            }
        }

        return null;
    }

    /**
     * @param visionSystemId the int associated with the desired VisionSystem
     * @return the vision system associated with visionSystemId or null if none exists
     */
    public VisionSystem getVisionSystemById(int visionSystemId) {
        return visionSystemMap.get(visionSystemId);
    }
}
