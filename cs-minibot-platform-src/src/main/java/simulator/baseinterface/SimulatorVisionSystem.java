package simulator.baseinterface;


import basestation.vision.VisionCoordinate;
import basestation.vision.VisionObject;
import basestation.vision.VisionSystem;
import org.jbox2d.common.Vec2;
import simulator.physics.PhysicalObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jbox2d.dynamics.World;

public class SimulatorVisionSystem extends VisionSystem {
    /**
     * Sets up a VisionSystem with o as its origin
     *
     * @param o A coordinate specifying the origin of the VisionSystem
     */
    private static SimulatorVisionSystem instance;
    private volatile Set<VisionObject> visionObjectSet;

    public SimulatorVisionSystem() {
        super(new VisionCoordinate(0, 0, 0));
        visionObjectSet = ConcurrentHashMap.newKeySet();
    }

    public static SimulatorVisionSystem getInstance() {
        if (instance == null) {
            instance = new SimulatorVisionSystem();
        }
        return instance;
    }

    @Override
    public Set<VisionObject> getAllObjects() {
        return visionObjectSet;
    }

    public void resetWorld() {
        visionObjectSet = ConcurrentHashMap.newKeySet();
    }

    /**
     * Updates the simulator vision system to display locs of objects after a
     * simulation step
     */
    public void updateVisionCoordinates(Set<PhysicalObject> poSet) {
        Set<VisionObject> newSet = ConcurrentHashMap.newKeySet();
        for(PhysicalObject obj: poSet) {
            VisionCoordinate vc = new VisionCoordinate(obj.getX(),obj.getY(),
                    obj.getAngle());
            VisionObject vo = new VisionObject(this,obj.getID(),vc, obj
                    .getSize());
            newSet.add(vo);
        }
        visionObjectSet = newSet;
    }


}
