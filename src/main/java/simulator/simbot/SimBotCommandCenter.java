package simulator.simbot;
import java.net.*;
import java.io.*;
import java.nio.*;

import basestation.bot.commands.FourWheelMovement;
import simulator.baseinterface.SimulatorVisionSystem;
import org.jbox2d.dynamics.Body;
import org.jbox2d.common.Vec2;

public class SimBotCommandCenter implements FourWheelMovement {
    public final float topspeed = 0.655f; //top speed of minibot in meters/second
    public SimBotCommandCenter(SimBot myBot) {
    }

    @Override
    public boolean setWheelPower(double fl, double fr, double bl, double br) {
        Body b = SimulatorVisionSystem.getInstance().getWorld().getBodyList();

        if(fl > 0 && fr > 0) {
            System.out.println("forward");
            b.setLinearVelocity(new Vec2((float)(0.655*fl/100), 0.0f));
        }
        if(fl <= 0 && fr <= 0) {
            System.out.println("backward");
            b.setLinearVelocity(new Vec2((float)(0.655*fl/100), 0.0f));
        }

        return true;
    }

    @Override
    public boolean sendKV(String key, String value) {
        try{
            BufferedReader reader =
                    new BufferedReader(new FileReader("src/main/java/simulator/simbot/ScriptHeader.py"));
            String header = "";
            String sCurrentLine;
            while ((sCurrentLine = reader.readLine()) != null) {
                header = header + sCurrentLine + "\n";
            }

            String prg = value;
            BufferedWriter out = new BufferedWriter(new FileWriter("script.py"));
            out.write(header);
            out.write(prg);
            out.close();

            ProcessBuilder pb = new ProcessBuilder("python","script.py");
            Process p = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            // int ret = new Integer(in.readLine()).intValue();
            // System.out.println("value is : "+ret);
            int ret;
            String line;
            while ((line = in.readLine()) != null) {
                ret = new Integer(line).intValue();
                System.out.println("value is : "+ ret);
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return false;
    }
}
