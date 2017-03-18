package simulator.baseinterface;

import basestation.BaseStation;
import basestation.vision.VisionCoordinate;
import basestation.vision.VisionObject;
import basestation.vision.VisionSystem;
import org.jbox2d.common.Vec2;
import simulator.physics.PhysicalObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jbox2d.dynamics.World;

/**
 * Created by Administrator on 11/8/2016.
 */
public class SimulatorVisionSystem extends VisionSystem {
    /**
     * Sets up a VisionSystem with o as its origin
     *
     * @param o A coordinate specifying the origin of the VisionSystem
     */
    private static SimulatorVisionSystem instance;
    private volatile HashSet<VisionObject> set;
    private HashSet<PhysicalObject> po_set;
    private World world;

    public SimulatorVisionSystem() {
        super(new VisionCoordinate(0, 0, 0));
        set = new HashSet<>();
        world = new World(new Vec2(0f, 0f));
        po_set = new HashSet<>();
    }

    public static SimulatorVisionSystem getInstance() {
        if (instance == null) {
            instance = new SimulatorVisionSystem();
        }

        return instance;
    }

    @Override
    public Set<VisionObject> getAllObjects() {
        return set;
    }

    public World getWorld() { return world;}
    /**
     * Handles a new input of data
     *
     * @param pObjs The data sent from simulator
     */
    public void processPhysicalObjects(ArrayList<PhysicalObject> pObjs) {
        HashSet<VisionObject> newSet = new HashSet<>();
        for(PhysicalObject obj: pObjs) {
            //direction is stored as radians in simulator.physics
//            double dirDegrees = obj.getPhysics().getDirection() * 360.0 / (2*Math.PI);

            VisionCoordinate vc = new VisionCoordinate(obj.getX(),obj.getY(), 0.0);
            VisionObject vo = new VisionObject(this,obj.getID(),vc);
            newSet.add(vo);
            po_set.add(obj);
        }


    }

    public void stepSimulation() {
        float timeStep = 1.0f / 30.f;
        int velocityIterations = 6;
        int positionIterations = 4;

        HashSet<VisionObject> newSet = new HashSet<>();
            for(PhysicalObject po: po_set ) {
                System.out.println("step");
                po.getWorld().step(timeStep, velocityIterations, positionIterations);

                VisionCoordinate vc = new VisionCoordinate(po.getX(),po.getY(), 100.0);
                VisionObject vo = new VisionObject(this,po.getID(),vc);

                newSet.add(vo);

                //Vec2 position = po.getBody().getPosition();
                //float angle = po.getBody().getAngle();
                //System.out.printf("%4.2f %4.2f %4.2f\n", position.x, position.y, angle);
            }
        this.set = newSet;


    }
}