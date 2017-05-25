package simulator.simbot;

import basestation.bot.commands.FourWheelMovement;
import basestation.bot.connection.Connection;
import com.google.gson.JsonObject;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import simulator.Simulator;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimBotCommandCenter implements FourWheelMovement {
    public final float topspeed = 0.655f; //top speed of minibot in meters/second
    public final float turningspeed = (float) Math.PI;
    public transient boolean record = false;
    Map<String, String> commandsLog = new ConcurrentHashMap<>();
    private SimBot bot;
    private Simulator simulator;

    public SimBotCommandCenter(SimBot myBot, Body obj_body, Simulator
            mySimulator) {
        bot = myBot;
        simulator = mySimulator;
    }

    /**
     * Does not record data until start is called
     */
    public void toggleLogging() {
        this.record = !this.record;
    }

    public boolean isLogging() {
        return this.record;
    }

    public void setData(String name, String value) {
        this.commandsLog.put(name, value);
    }

    public void setWheelsData(String fl, String v1, String fr, String v2, String bl, String v3, String br, String v4) {
        this.commandsLog.put(fl, v1);
        this.commandsLog.put(fr, v2);
        this.commandsLog.put(bl, v3);
        this.commandsLog.put(br, v4);
    }

    public JsonObject getData(String name) {
        JsonObject data = new JsonObject();
        for (Map.Entry<String, String> entry : this.commandsLog.entrySet()) {
            String n = entry.getKey();
            if (name.equals(n)) {
                String value = entry.getValue();
                data.addProperty(name, value);
                return data;
            }
        }
        data.addProperty(name, "");
        return data;
    }

    public JsonObject getAllData() {
        JsonObject allData = new JsonObject();

        for (Map.Entry<String, String> entry : this.commandsLog.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            allData.addProperty(name, value);
        }

        return allData;
    }

    @Override
    public boolean setWheelPower(double fl, double fr, double bl, double br) {
        Body b = bot.getMyPhysicalObject().getBody();

        //forwards
        if (fl > 0 && fr > 0 && bl > 0 && br > 0) {
            float angle = b.getAngle();
            float newX = (float) (topspeed * fl / 100 * Math.cos(angle));
            float newY = (float) (topspeed * fl / 100 * Math.sin(angle));
            b.setLinearVelocity(new Vec2(newX, newY));
            b.setAngularVelocity(0.0f);
        }

        //backwards
        else if (fl < 0 && fr < 0 && bl < 0 && br < 0) {
            float angle = b.getAngle();
            float newX = (float) (topspeed * fl / 100 * Math.cos(angle));
            float newY = (float) (topspeed * fl / 100 * Math.sin(angle));
            b.setLinearVelocity(new Vec2(newX, newY));
            b.setAngularVelocity(0.0f);
        }

        //no motor power
        else if (fl == 0 && fr == 0 && bl == 0 && br == 0) {
            b.setLinearVelocity(new Vec2(0.0f, 0.0f));
            b.setAngularVelocity(0.0f);
        }

        //turning right
        else if (fl > 0 && fr < 0 && bl > 0 && br < 0) {
            b.setLinearVelocity(new Vec2(0.0f, 0.0f));
            b.setAngularVelocity((float) (-turningspeed * fl / 100));
        }

        //turning left
        else if (fl < 0 && fr > 0 && bl < 0 && br > 0) {
            b.setLinearVelocity(new Vec2(0.0f, 0.0f));
            b.setAngularVelocity((float) (-turningspeed * fl / 100));
        } else {
            float angle = b.getAngle();
            float floatL = (float) fl;
            float floatR = (float) fr;
            float linearMagnitude = topspeed * (floatL + floatR) / (float) Math
                    .sqrt
                            (floatL * floatL + floatR * floatR);
            if (linearMagnitude < 1f) {
                b.setLinearVelocity(new Vec2(0.0f, 0.0f));
                b.setAngularVelocity(0.0f);
            }
            float angularSpeed = turningspeed * ((floatR - floatL) / (float) Math
                    .sqrt(100 * 100 +
                            100 * 100));
            b.setLinearVelocity(new Vec2(linearMagnitude * (float) Math.cos(angle),
                    linearMagnitude * (float) Math.sin(angle)));
            b.setAngularVelocity(angularSpeed);
        }

        return true;
    }

    @Override
    public boolean sendKV(String key, String value) {
        if (key.equals("WHEELS")) {
            String[] wheelCommands = value.split(",");

            double fl = Double.parseDouble(wheelCommands[0]);
            double fr = Double.parseDouble(wheelCommands[1]);
            double bl = Double.parseDouble(wheelCommands[2]);
            double br = Double.parseDouble(wheelCommands[3]);

            Body b = bot.getMyPhysicalObject().getBody();

            //forwards
            if (fl > 0 && fr > 0 && bl > 0 && br > 0) {
                float angle = b.getAngle();
                float newX = (float) (topspeed * fl / 100 * Math.cos(angle));
                float newY = (float) (topspeed * fl / 100 * Math.sin(angle));
                b.setLinearVelocity(new Vec2(newX, newY));
                b.setAngularVelocity(0.0f);
            }

            //backwards
            else if (fl < 0 && fr < 0 && bl < 0 && br < 0) {
                float angle = b.getAngle();
                float newX = (float) (topspeed * fl / 100 * Math.cos(angle));
                float newY = (float) (topspeed * fl / 100 * Math.sin(angle));
                b.setLinearVelocity(new Vec2(newX, newY));
                b.setAngularVelocity(0.0f);
            }

            //no motor power
            else if (fl == 0 && fr == 0 && bl == 0 && br == 0) {
                b.setLinearVelocity(new Vec2(0.0f, 0.0f));
                b.setAngularVelocity(0.0f);
            }

            //turning right
            else if (fl > 0 && fr < 0 && bl > 0 && br < 0) {
                b.setLinearVelocity(new Vec2(0.0f, 0.0f));
                b.setAngularVelocity((float) (-turningspeed * fl / 100));
            }

            //turning left
            else if (fl < 0 && fr > 0 && bl < 0 && br > 0) {
                b.setLinearVelocity(new Vec2(0.0f, 0.0f));
                b.setAngularVelocity((float) (-turningspeed * fl / 100));
            } else {
                System.out.println("Invalid wheel power command!");
            }

            this.setWheelsData("fl", wheelCommands[0], "fr", wheelCommands[1],
                    "bl", wheelCommands[2], "br", wheelCommands[3]);

            return true;

        } else {
            try {
                BufferedReader reader =
                        new BufferedReader(new FileReader("cs-minibot-platform-src/src/main/java/simulator/simbot/ScriptHeader.py"));
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

                ProcessBuilder pb = new ProcessBuilder("python", "script.py");
                Process p = pb.start();

                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

                String ret;
                String line;
                while ((line = in.readLine()) != null) {
                    ret = new String(line);
                    System.out.println(ret);
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        }
        return false;
    }

    @Override
    public Connection getConnection() {
        return null;
    }
}
