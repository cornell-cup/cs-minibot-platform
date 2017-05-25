package simulator.baseinterface;


import basestation.vision.VisionCoordinate;
import basestation.vision.VisionObject;
import basestation.vision.VisionSystem;
import simulator.physics.PhysicalObject;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class SimulatorVisionSystem extends VisionSystem {
    /**
     * Sets up a VisionSystem with o as its origin
     *
     * @param o A coordinate specifying the origin of the VisionSystem
     */
    private static SimulatorVisionSystem instance;
    private volatile Set<VisionObject> visionObjectSet;


    private SimulatorVisionSystem() {
        super(new VisionCoordinate(0, 0, 0));
        visionObjectSet = ConcurrentHashMap.newKeySet();
    }

    public static SimulatorVisionSystem getInstance() {
        if (instance == null) {
            instance = new SimulatorVisionSystem();
        }
        return instance;
    }

    /**
     * @return the set of all vision objects being tracked by the vision system
     */
    @Override
    public Set<VisionObject> getAllObjects() {
        return visionObjectSet;
    }

    /**
     * called when the simulator is reset (when a new simulation is started
     * based on a new scenario), this resets the set of vision objects
     */
    public void resetWorld() {
        visionObjectSet = ConcurrentHashMap.newKeySet();
    }

    /**
     * Updates the simulator vision system to display locs of objects after a
     * simulation step
     *
     * @param poSet the set of physical objects passed from the simulator,
     *              representing the bots and obstacles in the world
     */
    public void updateVisionCoordinates(Set<PhysicalObject> poSet) {
        Set<VisionObject> newSet = ConcurrentHashMap.newKeySet();
        for (PhysicalObject obj : poSet) {
            VisionCoordinate vc = new VisionCoordinate(obj.getX(), obj.getY(),
                    obj.getAngle());
            VisionObject vo = new VisionObject(this, obj.getID(), vc, obj
                    .getSize());
            newSet.add(vo);
        }
        visionObjectSet = newSet;
    }
}