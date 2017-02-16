package minibot;

/**
 * Created by celinechoo on 10/29/16.
 */

import basestation.BaseStation;
import basestation.bot.connection.IceConnection;
import basestation.bot.connection.TCPConnection;
import basestation.bot.robot.Bot;
import basestation.bot.robot.minibot.MiniBot;
import basestation.bot.robot.modbot.ModBot;
import basestation.bot.robot.modbot.ModbotCommandCenter;
import basestation.vision.OverheadVisionSystem;
import basestation.vision.VisionObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.route.RouteOverview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;




import static spark.Spark.*;

/**
 * HTTP connections for modbot GUI
 * <p>
 * Used for modbot to control and use the GUI while connecting to basestation.
 * */
public class BaseHTTPInterface {

    // Temp config settings
    public static final boolean OVERHEAD_VISION = true;
    private static Map<String, Integer> botList;

    public static void main(String[] args) {
        port(4567);
        staticFiles.location("/public");
        JsonParser jp = new JsonParser();
        BaseStation station = new BaseStation();
        botList = new HashMap<>();

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
            String name = addInfo.get("name").getAsString();
            String type = addInfo.get("type").getAsString(); //differentiate between modbot and minibot

            /* new modbot is created to add */
            Bot newBot;
            if(type.equals("modbot")) {
                IceConnection ice = new IceConnection(ip, port);
                newBot = new ModBot(station, ice);
            } else {
                TCPConnection c = new TCPConnection(ip, port);
                newBot = new MiniBot(station, c);
            }
            int ret = station.getBotManager().addBot(newBot); // returns the id used to get the bot info from BotManager
            botList.put(name, ret);
            return ret;
        });

        post("/removeBot", (req,res) -> {
            String body = req.body();
            JsonObject removeInfo = jp.parse(body).getAsJsonObject(); // gets (ip, port) from js

            String ip = removeInfo.get("ip").getAsString();
            int port = removeInfo.get("port").getAsInt();
            String name = removeInfo.get("name").getAsString();

            int index = botList.get(name); // finds the index stored in local hashmap of Bots relating Bot to its index in BotManager's map.
            station.getBotManager().removeBotById(index);

            return true;
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

        post("/sendScript", (req,res) -> {
            String body = req.body();
            JsonObject commandInfo = jp.parse(body).getAsJsonObject(); // gets (botID, fl, fr, bl, br) from js

            /* storing json objects into actual variables */
            int botID = commandInfo.get("botID").getAsInt();
            String script = commandInfo.get("script").getAsString();

            return station.getBotManager().getBotById(botID).getCommandCenter().sendKV("SCRIPT",script);
        });

        /*
            Collects updated JSON objects in the form:
            {   "x": vo.coord.x,
                "y": vo.coord.y,
                "angle": vo.coord.getThetaOrZero(),
                "id": vo.id
            }
         */
        get("/updateloc", (req, res) -> {
            List<VisionObject> vol = station.getVisionManager().getAllLocationData(); // all locations of active bots.
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

    }
}