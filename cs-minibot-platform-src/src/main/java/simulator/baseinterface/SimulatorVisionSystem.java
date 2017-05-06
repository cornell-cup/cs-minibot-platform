package simulator.baseinterface;


import basestation.vision.VisionCoordinate;
import basestation.vision.VisionObject;
import basestation.vision.VisionSystem;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
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
    private Set<PhysicalObject> poSet;
    private World world;
    private long before;
    private int[][] occupancyMatrix;

    public static final float UPDATES_PER_SECOND = 30;

    public SimulatorVisionSystem() {
        super(new VisionCoordinate(0, 0, 0));
        visionObjectSet = ConcurrentHashMap.newKeySet();
        world = new World(new Vec2(0f, 0f));
        poSet = ConcurrentHashMap.newKeySet();
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
        return visionObjectSet;
    }

    public World getWorld() { return world;}

    /**
     * Adds pObj to the simulation for vision tracking
     *
     */
    public void importPhysicalObject(PhysicalObject pObj) {
        poSet.add(pObj);
    }

    /**
     * Updates the simulator vision system to display locs of objects after a
     * simulation step
     */
    public void updateVisionCoordinates() {
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
        updateVisionCoordinates();
    }

    public Set<PhysicalObject> getAllPhysicalObjects() {
        return poSet;
    }

    public int[][] getOccupancyMatrix() {
        return occupancyMatrix;
    }

    public void setOccupancyMatrix(int[][] om) {
        occupancyMatrix = om;
    }

    public void generateOccupancyMatrix(int occupancyMatrixHeight, int occupancyMatrixWidth, float occupancyMatrixBoxSize) {
        int[][] om = new int[occupancyMatrixHeight][occupancyMatrixWidth];

        for(int i = 0; i < occupancyMatrixHeight; i++){
            for(int j = 0; j < occupancyMatrixWidth; j++) {
                Vec2 lowerVertex = new Vec2((float)(i), (float)(j));
                Vec2 upperVertex = new Vec2((float)(i+occupancyMatrixBoxSize), (float)(j+occupancyMatrixBoxSize));
                AABB currentSquare = new AABB(lowerVertex, upperVertex);
                final Vec2 middle = new Vec2(i , j );
                final int icopy = i;
                final int jcopy = j;
                QueryCallback callback = new QueryCallback() {
                    @Override
                    public boolean reportFixture(Fixture fixture) {
                        if(fixture.testPoint(middle)) {
                            om[icopy][jcopy] = 1;
                        }
                        return true;
                    }
                };
                world.queryAABB(callback, currentSquare);
            }
        }
        setOccupancyMatrix(om);
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
