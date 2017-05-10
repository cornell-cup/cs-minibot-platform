package simulator;


import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;

import basestation.BaseStation;
import com.google.gson.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import simulator.baseinterface.SimulatorVisionSystem;
import simulator.physics.PhysicalObject;
import simulator.simbot.Dijkstras;

import java.util.ArrayList;
import java.util.LinkedList;
import simulator.simbot.SimBot;
import simulator.simbot.SimBotConnection;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class Simulator {

    private int[][] occupancyMatrix;


    public static final float UPDATES_PER_SECOND = 30;

    private Set<PhysicalObject> physicalObjectSet;
    private World world;
    private SimulatorVisionSystem visionSystem;
    private SimRunner simRunner;

    private long lastUpdateTime;


    public Simulator(){
        visionSystem = SimulatorVisionSystem.getInstance();
        world = new World(new Vec2(0f, 0f));
        physicalObjectSet = ConcurrentHashMap.newKeySet();
        lastUpdateTime = System.nanoTime();
        SimRunner sr = new SimRunner();
        sr.start();
    }

    /**
     * @return the vision system that is tied to the simulator
     */
    public SimulatorVisionSystem getVisionSystem(){
        return visionSystem;
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
        // cleanup previous state
        if (world != null) {
            for (PhysicalObject physicalObject : physicalObjectSet) {
                world.destroyBody(physicalObject.getBody());
            }
        }
        if (simRunner != null) {
            simRunner.setShouldStep(false);
            while (simRunner.isGoing()) {
                try {
                    Thread.sleep(0,1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        physicalObjectSet.clear();
        world = new World(new Vec2(0f, 0f));
        physicalObjectSet = ConcurrentHashMap.newKeySet();
        lastUpdateTime = System.nanoTime();
        visionSystem.resetWorld();

        simRunner = new SimRunner();
        simRunner.start();
    }

    /**
     * A single step of the simulation- updates JBox2D world, updates all the
     * physical objects in the world, and updates all the corresponding
     * vision objects in the vision system
     */
    public void stepSimulation() {
        long now = System.nanoTime();
        long delta = now - lastUpdateTime;
        lastUpdateTime = now;
        float timeStep = (float)(delta / 10e8);
        int velocityIterations = 6;
        int positionIterations = 4;

        world.step(timeStep, velocityIterations, positionIterations);

        visionSystem.updateVisionCoordinates(physicalObjectSet);
    }

    public boolean importScenario(Gson gson, JsonParser jsonParser, JsonObject scenario) {
        // clear previous
        resetWorld();
        String scenarioBody = scenario.get("scenario").getAsString();
        JsonArray addInfo = jsonParser.parse(scenarioBody).getAsJsonArray();

        for (JsonElement je : addInfo) {
            String type = je.getAsJsonObject().get("type").getAsString();
            int angle = je.getAsJsonObject().get("angle").getAsInt();
            int[] position = gson.fromJson(je.getAsJsonObject().get("position")
                    .getAsString(), int[].class);
            String name = Integer.toString(angle)
                    + Arrays.toString(position);

            //for scenario obstacles
            PhysicalObject physicalObject;
            if (!type.equals("simulator.simbot")) {
                int size = je.getAsJsonObject().get("size").getAsInt();
                physicalObject = new PhysicalObject(name, 100,
                        world, (float) position[0],
                        (float) position[1], size, angle);
                importPhysicalObject(physicalObject);
            }
            //for bots listed in scenario
            else {
                name = "Simbot" + name;
                SimBotConnection sbc = new SimBotConnection();
                SimBot simbot;
                simbot = new SimBot(sbc, this, name, 50, world, 0.0f,
                        0.0f, (float) position[0], (float)
                        position[1], angle, true);

                physicalObject = simbot.getMyPhysicalObject();
                importPhysicalObject(physicalObject);

                // Color sensor TODO put somewhere nice
//                    ColorIntensitySensor colorSensorL = new ColorIntensitySensor((SimBotSensorCenter) simbot.getSensorCenter(),"right",simbot, 5);
//                    ColorIntensitySensor colorSensorR = new ColorIntensitySensor((SimBotSensorCenter) simbot.getSensorCenter(),"left",simbot, -5);
//                    ColorIntensitySensor colorSensorM = new ColorIntensitySensor((SimBotSensorCenter) simbot.getSensorCenter(),"center",simbot, 0);

                try {
                    BaseStation.getInstance().getBotManager().addBot(simbot);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Runs the simulations at the specified number of updates/second
     */
    private class SimRunner extends Thread {

        private boolean shouldStep;
        private boolean going;
        private final Object lock = new Object();

        void setShouldStep(boolean update) {
            synchronized (lock) {
                this.shouldStep = update;
            }
        }

        boolean isShouldStep() {
            synchronized (lock) {
                return shouldStep;
            }
        }

        void setGoing(boolean update) {
            synchronized (lock) {
                this.going = update;
            }
        }

        boolean isGoing() {
            synchronized (lock) {
                return going;
            }
        }

        @Override
        public void run() {
            setShouldStep(true);
            while (true) {
                if (!isShouldStep()) return;
                setGoing(true);
                stepSimulation();
                try {
                    Thread.sleep((long)(1000f / UPDATES_PER_SECOND));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    setGoing(false);
                }
            }
        }
    }

    /**
     * Adds pObj to the simulation for vision tracking
     *
     */
    public void importPhysicalObject(PhysicalObject pObj) {
        physicalObjectSet.add(pObj);
    }


    public int[][] getOccupancyMatrix() {
        return occupancyMatrix;
    }

    public void setOccupancyMatrix(int[][] om) {
        occupancyMatrix = om;
    }

    public ArrayList<ArrayList<Integer>> getWayPoints(int[][]om) {
        ArrayList<ArrayList<Integer>> points = new ArrayList<ArrayList<Integer>>();
        for(int i = 1; i < om.length-1; i++) {
            for(int j = 1; j < om[0].length-1; j++) {
                if(om[i][j] == 1 &&
                        ( (om[i-1][j] == 1 && om[i][j+1] == 1) || (om[i][j+1] == 1 && om[i+1][j] == 1)))
                {
                    points.get(0).add(i);
                    points.get(1).add(j);
                }
            }
        }
        return points;
    }


    public void generateOccupancyMatrix(int occupancyMatrixHeight, int occupancyMatrixWidth, float occupancyMatrixBoxSize) {
        int[][] om = new int[occupancyMatrixHeight][occupancyMatrixWidth];
        //System.out.println("The occupancy matrix box size is " + occupancyMatrixBoxSize);
        for(int i = 0; i < occupancyMatrixHeight; i++){
            for(int j = 0; j < occupancyMatrixWidth; j++) {
                float lower_x = (float)(i*occupancyMatrixBoxSize);
                float lower_y = (float)(j*occupancyMatrixBoxSize);
                Vec2 lowerVertex = new Vec2(lower_x, lower_y);
                Vec2 upperVertex = new Vec2(lower_x + occupancyMatrixBoxSize, lower_y + occupancyMatrixBoxSize);
                AABB currentSquare = new AABB(lowerVertex, upperVertex);
                final Vec2 testpoint1 = new Vec2(lower_x + occupancyMatrixBoxSize/2 , lower_y + occupancyMatrixBoxSize/2 );
                final Vec2 testpoint2 = new Vec2(lower_x + occupancyMatrixBoxSize/4, lower_y + occupancyMatrixBoxSize/4);
                final Vec2 testpoint3 = new Vec2(lower_x + 3*occupancyMatrixBoxSize/4, lower_y + 3*occupancyMatrixBoxSize/4);
                final Vec2 testpoint4 = new Vec2(lower_x + occupancyMatrixBoxSize/4, lower_y + 3*occupancyMatrixBoxSize/4);
                final Vec2 testpoint5 = new Vec2(lower_x + 3*occupancyMatrixBoxSize/4, lower_y + occupancyMatrixBoxSize/4);


                final int icopy = i;
                final int jcopy = j;
                QueryCallback callback = new QueryCallback() {
                    @Override
                    public boolean reportFixture(Fixture fixture) {
                        if(fixture.testPoint(testpoint1) || fixture.testPoint(testpoint2) || fixture.testPoint(testpoint3) || fixture.testPoint(testpoint4) || fixture.testPoint(testpoint5)) {
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

    public int[][] getDijkstras() {
        Dijkstras.answer(occupancyMatrix);

//        for (int j = 0; j < path.length; j++) {
//            for (int i = 0; i < path[j].length; i++) {
//                System.out.print(path[i][j] + " ");
//            }
//            System.out.println();
//        }

        Dijkstras.Node targetNode = Dijkstras.targetNode;

        LinkedList<Dijkstras.Node> path = Dijkstras.getPath(targetNode);
        int[][] pathMatrix = new int[occupancyMatrix.length+1][occupancyMatrix[0].length+1];


        for(int i = 0; i < path.size(); i++) {
            //System.out.println("X:" + path.get(i).getX() + " Y:" + path.get(i).getY());
            pathMatrix[path.get(i).getX()][path.get(i).getY()] = 1;
        }

//        for (int j = 1; j < pathMatrix.length; j++) {
//            for (int i = 1; i < pathMatrix[j].length; i++) {
//                System.out.print(pathMatrix[i][j] + " ");
//            }
//            System.out.println();
//        }
        return pathMatrix;
    }

}
