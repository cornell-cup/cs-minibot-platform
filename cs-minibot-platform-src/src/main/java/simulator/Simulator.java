package simulator;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import simulator.baseinterface.SimulatorVisionSystem;
import simulator.physics.PhysicalObject;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 4/29/2017.
 */
public class Simulator {
    private Set<PhysicalObject> poSet;
    private World world;
    private SimulatorVisionSystem visionsystem;
    private long before;
    public static final float UPDATES_PER_SECOND = 30;

    public Simulator(){
        visionsystem = SimulatorVisionSystem.getInstance();
        world = new World(new Vec2(0f, 0f));
        poSet = ConcurrentHashMap.newKeySet();
        before = System.nanoTime();
        SimRunner sr = new SimRunner();
        sr.start();
    }

    /**
     * @return the vision system that is tied to the simulator
     */
    public SimulatorVisionSystem getVisionSystem(){
        return visionsystem;
    }

    /**
     *
     * @return the JBox2D world that the simulation is running in
     */
    public World getWorld() { return world;}

    /**
     * This is called when a new scenario is added (starting a new simulation
     * based on the scenario)
     *
     * Resets the simulation by creating a new world, resetting all physical
     * objects and vision objcts, and starting a new simulation
     */
    public void resetWorld() {
        world = new World(new Vec2(0f, 0f));
        poSet = ConcurrentHashMap.newKeySet();
        before = System.nanoTime();
        visionsystem.resetWorld();
        SimRunner sr = new SimRunner();
        sr.start();
    }

    /**
     * A single step of the simulation- updates JBox2D world, updates all the
     * physical objects in the world, and updates all the corresponding
     * vision objects in the vision system
     */
    public void stepSimulation() {
        long now = System.nanoTime();
        long delta = now - before;
        before = now;
        float timeStep = (float)(delta / 10e8);
        int velocityIterations = 6;
        int positionIterations = 4;

        for(PhysicalObject po: poSet) {
            po.getWorld().step(timeStep, velocityIterations, positionIterations);
        }
        visionsystem.updateVisionCoordinates(poSet);
    }

    /**
     * Runs the simulations at the specified number of updates/second
     */
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

    /**
     * Adds pObj to the simulation for vision tracking
     *
     */
    public void importPhysicalObject(PhysicalObject pObj) {
        poSet.add(pObj);
    }

}
