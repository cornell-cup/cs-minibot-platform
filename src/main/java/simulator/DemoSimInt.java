package simulator;

/**
 * Created by celinechoo on 10/29/16.
 */

import basestation.BaseStation;
import basestation.bot.connection.IceConnection;
import basestation.bot.robot.modbot.ModBot;
import basestation.bot.robot.modbot.ModbotCommandCenter;
import basestation.vision.OverheadVisionSystem;
import basestation.vision.VisionObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import simulator.baseinterface.SimulatorModBotConnection;
import simulator.baseinterface.SimulatorVisionSystem;
import spark.route.RouteOverview;

import java.util.List;

import static spark.Spark.*;

/* attempt at life TODO Make this more informative */
public class DemoSimInt {

    // Temp config settings
    public static final boolean OVERHEAD_VISION = false;

    public static void main(String[] args) {
        port(8083);
        staticFiles.location("/public");
        JsonParser jp = new JsonParser();
        BaseStation station = new BaseStation();
        SimulatorVisionSystem svs = new SimulatorVisionSystem();
        Simulator sim = new Simulator(svs);
        SimModBot bot = new SimModBot();
        sim.addPhysObject(bot);
        station.getVisionManager().addVisionSystem(svs);

        station.getBotManager().addBot(new ModBot(station, new SimulatorModBotConnection(bot)));


        if (OVERHEAD_VISION) {
            OverheadVisionSystem ovs = new OverheadVisionSystem();
            station.getVisionManager().addVisionSystem(ovs);
        }

        RouteOverview.enableRouteOverview("/");

        post("/addBot", (req,res) -> {
            String body = req.body();
            JsonObject addInfo = jp.parse(body).getAsJsonObject(); // gets (ip, port) from js

            /* storing json objects into actual variables */
            String ip = addInfo.get("ip").getAsString();
            int port = addInfo.get("port").getAsInt();

            /* setting up ice connection */
            IceConnection ice = new IceConnection(ip, port);

            /* new modbot is created to add */
            ModBot newBot = new ModBot(new BaseStation(), ice);
            int ret = station.getBotManager().addBot(newBot);

            return ret;
        });

        post("/commandBot", (req,res) -> {
            String body = req.body();
            JsonObject commandInfo = jp.parse(body).getAsJsonObject(); // gets (botID, fl, fr, bl, br) from js

            /* storing json objects into actual variables */
            int botID = commandInfo.get("botID").getAsInt();
            int fl = commandInfo.get("fl").getAsInt();
            int fr = commandInfo.get("fr").getAsInt();
            int bl = commandInfo.get("bl").getAsInt();
            int br = commandInfo.get("br").getAsInt();

            ((ModbotCommandCenter)station.getBotManager().getBotById(botID).getCommandCenter()).setWheelPower(fl,fr,bl,br);

            return true;
        });


        get("/updateloc", (req, res) -> {
            List<VisionObject> vol = station.getVisionManager().getAllLocationData();
            JsonArray respData = new JsonArray();
            for (VisionObject vo : vol) {
                JsonObject jo = new JsonObject();
                jo.addProperty("x", vo.coord.x);
                jo.addProperty("y", vo.coord.y);
                jo.addProperty("angle", vo.coord.getThetaOrZero());
                jo.addProperty("id", vo.vid);
                respData.add(jo);
            }


            return respData;
        });

        sim.run();
    }
}