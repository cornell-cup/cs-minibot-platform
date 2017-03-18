package simulator.baseinterface;


import basestation.vision.VisionCoordinate;
import basestation.vision.VisionObject;
import basestation.vision.VisionSystem;
import org.jbox2d.common.Vec2;
import simulator.physics.PhysicalObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jbox2d.dynamics.World;

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
    private long before;

    public static final float UPDATES_PER_SECOND = 30;

    public SimulatorVisionSystem() {
        super(new VisionCoordinate(0, 0, 0));
        set = new HashSet<>();
        world = new World(new Vec2(0f, 0f));
        po_set = new HashSet<>();
        before = System.nanoTime();
        SimRunner sr = new SimRunner();
        sr.start();
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
            VisionCoordinate vc = new VisionCoordinate(obj.getX(),obj.getY(), 0.0);
            VisionObject vo = new VisionObject(this,obj.getID(),vc);
            newSet.add(vo);
            po_set.add(obj);
        }
    }

    public void stepSimulation() {
        long now = System.nanoTime();
        long delta = now - before;
        before = now;
        float timeStep = (float)(delta / 10e8);
        int velocityIterations = 6;
        int positionIterations = 4;

        HashSet<VisionObject> newSet = new HashSet<>();
            for(PhysicalObject po: po_set ) {
                po.getWorld().step(timeStep, velocityIterations, positionIterations);

                VisionCoordinate vc = new VisionCoordinate(po.getX(),po.getY(), 100.0);
                VisionObject vo = new VisionObject(this,po.getID(),vc);

                newSet.add(vo);
            }
        this.set = newSet;
    }

    private class SimRunner extends Thread {

        @Override
        public void run() {
            while (true) {
                stepSimulation();
                try {
                    Thread.sleep((long)(1000f / UPDATES_PER_SECOND));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
