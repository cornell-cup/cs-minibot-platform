package simulator;//import simulator.baseinterface.SimulatorVisionSystem;

import basestation.BaseStation;
import simulator.baseinterface.SimulatorVisionSystem;
import simulator.physics.PhysicalObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * The simulator will be able to run bot algorithms and have them be able to display all the objects on a GUI.
 */
public class Simulator extends Thread {
//    public final static int STEPS_PER_SECOND = 100;
//    private ArrayList<PhysicalObject> pObjects = new ArrayList<>();
//    private static HashMap<Integer,String> objectIDs = new HashMap<>();
//    //not too sure what to do with the basestation
//    private BaseStation bs = new BaseStation();
//    private SimulatorVisionSystem simvs;
    //instantiate basestation
    //connector to bot


//    //A list of all of the current objects in the simulator
//    private static ArrayList<PhysicalObject> current_objects = new ArrayList<>();
//
//    public Simulator(SimulatorVisionSystem svs) {
//        this.simvs = svs;
//    }
//
//    /**
//     * Returns an ArrayList of all of the objects currently in the simulator
//     * @return an ArrayList of PhysicalObjects
//     */
//    public ArrayList<PhysicalObject> getSimObjects(){return pObjects;}
//
//
//    public void addPhysObject(PhysicalObject po) {
//        pObjects.add(po);
//    }
//
//
//    /**
//     * Adds all the objects and then moves
//     */
//    @Override
//    public void run() {
//        while (true) {
//            //moving
//            for (int i = 0; i < pObjects.size(); i++) {
//                pObjects.get(i).move();
//            }
//
//            simvs.processPhysicalObjects(pObjects);
//
//            try {
//                Thread.sleep(1000/STEPS_PER_SECOND);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
