package simulator;

import basestation.BaseStation;
import basestation.bot.robot.modbot.ModBot;
import basestation.bot.robot.modbot.ModbotCommandCenter;
import basestation.vision.VisionObject;
import simulator.baseinterface.SimulatorModBotConnection;
import simulator.baseinterface.SimulatorVisionSystem;
import simulator.physics.PhysicalObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Initializes the simulator
 */
public class Main {
    public static void main(String[] args) {
        SimulatorVisionSystem svs = new SimulatorVisionSystem();
        Simulator sim = new Simulator(svs);
        BaseStation bs = new BaseStation();
        SimModBot bot = new SimModBot();

        sim.addPhysObject(bot);

        bs.getBotManager().addBot(new ModBot(bs, new SimulatorModBotConnection(bot)));

        ModbotCommandCenter mcc = (ModbotCommandCenter) bs.getBotManager().getBotById(0).getCommandCenter();

        System.out.println(svs.getAllObjects());


        sim.start();
        //mcc.forward(50);
        //mcc.backward(50);
        //mcc.clockwise(100);
        mcc.counterClockwise(100);

        //Lauren: Test backward, CW, and CCW

        while(true){
            /*Set<VisionObject> testVisionObjects = svs.getAllObjects();
            ArrayList<PhysicalObject> simObjects = sim.getSimObjects();
            Iterator<VisionObject> visObj = testVisionObjects.iterator();
            for (int x=0; x<simObjects.size();x++){
                PhysicalObject physObj = simObjects.get(x);
                VisionObject vo = visObj.next();
                System.out.println("Coordinate check for object "+x);
                System.out.println("PhysObj coordinates: x="+physObj.getX()+" y="+physObj.getY());
                System.out.println("VisObj coordinates: x="+vo.coord.x+" y="+vo.coord.y);
                System.out.println("");
            }*/

        }
    }
}