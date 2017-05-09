package simulator;

import basestation.BaseStation;
import basestation.bot.robot.Bot;
import com.google.gson.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import simulator.baseinterface.SimulatorVisionSystem;
import simulator.physics.PhysicalObject;
import simulator.simbot.SimBot;
import simulator.simbot.SimBotConnection;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;


public class Simulator {

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

}
