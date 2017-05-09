package minibot;

import basestation.BaseStation;
import basestation.bot.commands.CommandCenter;
import basestation.bot.commands.FourWheelMovement;
import basestation.bot.connection.IceConnection;
import basestation.bot.connection.TCPConnection;
import basestation.bot.robot.Bot;
import basestation.bot.robot.minibot.MiniBot;
import basestation.bot.robot.modbot.ModBot;
import basestation.vision.OverheadVisionSystem;
import basestation.vision.VisionCoordinate;
import basestation.vision.VisionObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import examples.gobot.Course;
import examples.gobot.GoBot;
import examples.patrol.Patrol;

import org.jbox2d.dynamics.World;
import simulator.Simulator;
import simulator.physics.PhysicalObject;
import simulator.simbot.*;
import spark.route.RouteOverview;

import java.util.*;
import java.util.List;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.io.*;


import simulator.baseinterface.SimulatorVisionSystem;


import xboxhandler.XboxControllerDriver;

import static spark.Spark.*;

/**
 * HTTP connections for modbot GUI
 * <p>
 * Used for modbot to control and use the GUI while connecting to basestation.
 * */
public class BaseHTTPInterface {

    // Temp config settings
    public static final boolean OVERHEAD_VISION = true;
    private static XboxControllerDriver xboxControllerDriver;
    public static ArrayList<VisionCoordinate> patrolPoints;
    public static ArrayList<VisionCoordinate> innerTrackCoords;
    public static ArrayList<VisionCoordinate> advancedAI;

    public static void main(String[] args) {
        // Spark configuration
        port(8080);
        staticFiles.location("/public");
        RouteOverview.enableRouteOverview("/");

        //create new visionsystem and simulator instances
        SimulatorVisionSystem simvs;
        Simulator simulator = new Simulator();
        // Show exceptions
        exception(Exception.class, (exception,request,response) -> {
            exception.printStackTrace();
            response.status(500);
            response.body("oops");
        });

        // Global objects
        JsonParser jsonParser = new JsonParser();
        Gson gson = new Gson();
        patrolPoints = new ArrayList<>();
        Course course = new Course();
        ArrayList<VisionCoordinate> outerTrackCoords = new ArrayList<>();
        innerTrackCoords = new ArrayList<>();
        ArrayList<VisionCoordinate> startAreaCoords = new ArrayList<>();
        ArrayList<VisionCoordinate> middleAreaCoords = new ArrayList<>();
        advancedAI = new ArrayList<>();
        //HARD-CODED COORDINATES JUST FOR GUI
        /*innerTrackCoords.addAll(Arrays.asList(new VisionCoordinate(3,2), new
                VisionCoordinate(2,2), new VisionCoordinate(2,1), new
                VisionCoordinate(3,1)));
        outerTrackCoords.addAll(Arrays.asList(new VisionCoordinate(1,3), new
                VisionCoordinate(1,0), new VisionCoordinate(4,0), new
                VisionCoordinate(4,3)));
        startAreaCoords.addAll(Arrays.asList(new VisionCoordinate(3,2), new
                VisionCoordinate(4,2), new VisionCoordinate(4,1.95), new
                VisionCoordinate(3,1.95)));
        middleAreaCoords.addAll(Arrays.asList(new VisionCoordinate(2,2), new
                VisionCoordinate(2,1.95), new VisionCoordinate(1,1.95), new
                VisionCoordinate(1,2)));*/
        //HARD-CODED BASIC BOTFIELD TRACK
        /*innerTrackCoords.addAll(Arrays.asList(new VisionCoordinate
                (2.2065439453125, 1.5227012939453124), new
                VisionCoordinate(2.147612060546875, 0.38956390380859374), new VisionCoordinate(0.3778320007324219, 0.38749581909179687), new
                VisionCoordinate(0.3561910400390625, 1.5385802001953126)));
        outerTrackCoords.addAll(Aarrays.asList(new VisionCoordinate
        (0.08017002868652344, 1.8366173095703124), new
                VisionCoordinate(2.492609619140625, 1.811468017578125), new VisionCoordinate(2.402284423828125, 0.17503025817871093), new
                VisionCoordinate(0.17684439086914064, 0.09049703216552735)));
        startAreaCoords.addAll(Arrays.asList(new VisionCoordinate(1.1758720703125, 1.860831787109375), new
                VisionCoordinate(1.3459951171875, 1.863804443359375), new VisionCoordinate(1.3423072509765626, 1.549229248046875), new
                VisionCoordinate(1.1327845458984376, 1.6148818359375)));
        middleAreaCoords.addAll(Arrays.asList(new VisionCoordinate(1.3594305419921875, 0.5128773803710938), new
                VisionCoordinate(1.398449951171875, 0.044775409698486326), new VisionCoordinate(1.08611669921875, 0.04974387359619141), new
                VisionCoordinate(1.0802769775390626, 0.516857421875)));*/
        //HARD-CODED FOR CONVEX TRACK
        advancedAI.addAll(Arrays.asList(new VisionCoordinate(2.23133984375, 1.67338818359375), new VisionCoordinate(2.3830263671875, 1.4941776123046875),
                new VisionCoordinate(2.404982666015625, 0.4286590576171875), new VisionCoordinate(2.2979462890625, 0.28613916015625),
                new VisionCoordinate(1.9067784423828125, 0.27084918212890624), new VisionCoordinate(1.6620274658203125, 0.2908943176269531),
                new VisionCoordinate(1.585668701171875, 0.8443804321289062), new VisionCoordinate(1.57882666015625, 0.9999115600585937),
                new VisionCoordinate(1.42708349609375, 1.145732177734375), new VisionCoordinate(1.1258839111328125, 1.1726514892578126),
                new VisionCoordinate(0.9635026245117188, 1.1342779541015624), new VisionCoordinate(0.9021660766601562, 0.8597421264648437),
                new VisionCoordinate(0.8367010498046875, 0.40710394287109375), new VisionCoordinate(0.7722755126953125, 0.2491551513671875),
                new VisionCoordinate(0.69308349609375, 0.18451251220703124), new VisionCoordinate(0.38101016235351565, 0.2346041259765625),
                new VisionCoordinate(0.26720782470703125, 0.3347433471679688), new VisionCoordinate(0.20580989074707032, 0.5475873413085938),
                new VisionCoordinate(0.21279461669921876, 1.552355224609375), new VisionCoordinate(0.3068707275390625, 1.6739039306640624),
                new VisionCoordinate(0.894802490234375, 1.787567138671875), new VisionCoordinate(1.4014271240234375, 1.7195748291015625)));
        innerTrackCoords.addAll(Arrays.asList(new VisionCoordinate
                (2.2361962890625, 1.49191650390625), new
                VisionCoordinate(2.15298388671875, 0.4127757873535156), new VisionCoordinate(1.783540771484375, 0.4107144775390625), new
                VisionCoordinate(1.7581005859375, 1.339592529296875),new VisionCoordinate(0.7094837036132813, 1.3833878173828125), new
                VisionCoordinate(0.6379854125976563, 0.361425537109375), new VisionCoordinate(0.3548998107910156, 0.3943072814941406), new
                VisionCoordinate(0.3353608703613281, 1.57230908203125)));
        outerTrackCoords.addAll(Arrays.asList(new VisionCoordinate(1.27258740234375, 1.8670997314453126), new
                VisionCoordinate(2.49537109375, 1.8402861328125), new VisionCoordinate(2.49268701171875, 0.09253135681152344), new
                VisionCoordinate(1.4038331298828124, 0.07255047607421874),new VisionCoordinate(1.428212158203125, 0.953756103515625), new
                VisionCoordinate(1.07586669921875, 0.9575986328125), new VisionCoordinate(1.038992919921875, 0.03842947769165039), new
                VisionCoordinate(0.12286204528808593, 0.09196495819091798),
                new VisionCoordinate(0.08808555603027343, 1.843708251953125)));
        startAreaCoords.addAll(Arrays.asList(new VisionCoordinate(1.1590947265625, 1.85539990234375), new
                VisionCoordinate(1.3221160888671875, 1.8664298095703125), new VisionCoordinate(1.3385616455078124, 1.5606270751953124), new
                VisionCoordinate(1.1252403564453124, 1.624544677734375)));
        middleAreaCoords.addAll(Arrays.asList(new VisionCoordinate(1.1123798828125, 0.7938690795898438), new
                VisionCoordinate(0.5704920043945313, 0.825190185546875), new VisionCoordinate(0.6252758178710938, 0.54149462890625), new
                VisionCoordinate(1.1291646728515625, 0.4859189453125)));
        GoBot gb = new GoBot();
        Long timer = 0L;

        if (OVERHEAD_VISION) {
            OverheadVisionSystem ovs = new OverheadVisionSystem();
            BaseStation.getInstance().getVisionManager().addVisionSystem(ovs);
            simvs = simulator.getVisionSystem();
            BaseStation.getInstance().getVisionManager().addVisionSystem(simvs);
        }

        // Routes
        /* add a new bot from the gui*/
        post("/addBot", (req,res) -> {
            String body = req.body();
            JsonObject addInfo = jsonParser.parse(body).getAsJsonObject(); // gets (ip, port) from js

            /* storing json objects into actual variables */
            String ip = addInfo.get("ip").getAsString();
            int port = addInfo.get("port").getAsInt();
            String name = addInfo.get("name").getAsString();
            String type = addInfo.get("type").getAsString(); //differentiate between modbot and minibot

            System.out.println(ip);
            System.out.println(port);
            System.out.println(name);
            System.out.println(type);

            /* new modbot is created to add */
            Bot newBot;
            if(type.equals("modbot")) {
                IceConnection ice = new IceConnection(ip, port);
                newBot = new ModBot(ice, name);
            } else if(type.equals("minibot")) {
                TCPConnection c = new TCPConnection(ip, port);
                newBot = new MiniBot(c, name);
            }
               else {
                SimBotConnection sbc = new SimBotConnection();
                SimBot simbot;
                simbot = new SimBot(sbc, simulator, name, 50, simulator.getWorld
                        (), 0.0f,
                        0.0f, 1f, 3.6f, 0, true);
                newBot = simbot;

                simulator.importPhysicalObject(simbot.getMyPhysicalObject());

                // Color sensor TODO put somewhere nice
                ColorIntensitySensor colorSensorL = new ColorIntensitySensor((SimBotSensorCenter) simbot.getSensorCenter(),"right",simbot, 5);
                ColorIntensitySensor colorSensorR = new ColorIntensitySensor((SimBotSensorCenter) simbot.getSensorCenter(),"left",simbot, -5);
                ColorIntensitySensor colorSensorM = new ColorIntensitySensor((SimBotSensorCenter) simbot.getSensorCenter(),"center",simbot, 0);
            }
            return BaseStation.getInstance().getBotManager().addBot(newBot);
        });


        /**
         * POST /addScenario starts a simulation with the scenario from the
         * scenario viewer in the gui
         *
         * @apiParam scenario a string representing a list of JSON scenario
         * objects, which consist of obstacles and bot(s)
         * @return the scenario json if it was successfully added
         */
        post("/addScenario", (req,res) -> {

            String body = req.body();
            simulator.resetWorld();
            Collection<String> botsnames = BaseStation.getInstance()
                    .getBotManager()
                    .getAllTrackedBotsNames();
            for (String name :botsnames){
                BaseStation.getInstance().getBotManager().removeBotByName(name);
            }

            JsonObject scenario = jsonParser.parse(body).getAsJsonObject();
            String scenarioBody = scenario.get("scenario").getAsString();
            JsonArray addInfo = jsonParser.parse(scenarioBody).getAsJsonArray();

            for (JsonElement je : addInfo) {
                String type = je.getAsJsonObject().get("type").getAsString();
                int angle = je.getAsJsonObject().get("angle").getAsInt();
                int[] position = gson.fromJson(je.getAsJsonObject().get("position")
                        .getAsString(),int[].class);
                String name = Integer.toString(angle)
                        + Arrays.toString(position);

                //for scenario obstacles
                if (!type.equals("simulator.simbot")){
                    int size = je.getAsJsonObject().get("size").getAsInt();
                    PhysicalObject po = new PhysicalObject(name, 100,
                            simulator.getWorld(), (float)position[0],
                            (float)position[1], size, angle);
                    simulator.importPhysicalObject(po);
                }
                //for bots listed in scenario
                else {
                    Bot newBot;
                    name = "Simbot"+name;
                    SimBotConnection sbc = new SimBotConnection();
                    SimBot simbot;
                    simbot = new SimBot(sbc, simulator, name, 50, simulator
                            .getWorld(), 0.0f,
                            0.0f, (float) position[0], (float)
                            position[1], angle, true);
                    newBot = simbot;

                    simulator.importPhysicalObject(simbot.getMyPhysicalObject());

                    // Color sensor TODO put somewhere nice
//                    ColorIntensitySensor colorSensorL = new ColorIntensitySensor((SimBotSensorCenter) simbot.getSensorCenter(),"right",simbot, 5);
//                    ColorIntensitySensor colorSensorR = new ColorIntensitySensor((SimBotSensorCenter) simbot.getSensorCenter(),"left",simbot, -5);
//                    ColorIntensitySensor colorSensorM = new ColorIntensitySensor((SimBotSensorCenter) simbot.getSensorCenter(),"center",simbot, 0);
                    BaseStation.getInstance().getBotManager().addBot(newBot);
                }
            }
            return addInfo;
        });

        /**
         * POST /saveScenario saves the scenario currently loaded as a txt
         * file with the specified name
         *
         * @apiParam scenario a string representing a list of JSON scenario
         * objects, which consist of obstacles and bot(s)
         * @apiParam name the name the new scenario txt file
         * @return the name of the file if it was successfully saved
         */
        post("/saveScenario", (req,res) -> {

            String body = req.body();
            JsonObject scenario = jsonParser.parse(body).getAsJsonObject();
            String scenarioBody = scenario.get("scenario").getAsString();
            String fileName = scenario.get("name").getAsString();

            //writing new scenario file
            File file = new File
                    ("cs-minibot-platform-src/src/main/resources" +
                            "/public/scenario/"+fileName+".txt");
            OutputStream out = new FileOutputStream(file);

            FileWriter writer = new FileWriter(file, false);
            BufferedWriter bwriter = new BufferedWriter(writer);
            bwriter.write(scenarioBody);
            bwriter.close();
            out.close();
            return fileName;
        });

        /**
         * POST /loadScenario loads a scenario into the scenario viewer from a
         * txt scenario file with the specified name; does not add scenario
         * to the world or start a simulation
         *
         * @apiParam name the name the scenario txt file to load
         * @return the JSON of the scenario if it was loaded successfully
         */
        post("/loadScenario", (req,res) -> {
            String body = req.body();
            JsonObject scenario = jsonParser.parse(body).getAsJsonObject();
            String fileName = scenario.get("name").getAsString();
            String scenarioData = "";

            //loading scenario file
            File file = new File
                    ("cs-minibot-platform-src/src/main/resources" +
                            "/public/scenario/"+fileName+".txt");
            FileReader fr=	new	FileReader(file);
            BufferedReader br=	new	BufferedReader(fr);
            String line = br.readLine();
            while (line!=null){
                scenarioData+=line;
                line = br.readLine();
            }
            br.close();
            return scenarioData;
        });

        /*send commands to the selected bot*/
        post("/commandBot", (req,res) -> {
            //System.out.println("post to command bot called");
            String body = req.body();
            JsonObject commandInfo = jsonParser.parse(body).getAsJsonObject();

            // gets (botID, fl, fr, bl, br) from json
            String botName = commandInfo.get("name").getAsString();
            String fl = commandInfo.get("fl").getAsString();
            String fr = commandInfo.get("fr").getAsString();
            String bl = commandInfo.get("bl").getAsString();
            String br = commandInfo.get("br").getAsString();

            // Forward the command to the bot
            Bot myBot = BaseStation.getInstance().getBotManager()
                    .getBotByName(botName).get();
            CommandCenter cc =  myBot.getCommandCenter();
            return cc.sendKV("WHEELS", fl + "," + fr + "," + bl + "," + br);
        });

        /*remove the selected bot -  not sure if still functional*/
        post("/removeBot", (req,res) -> {
            String body = req.body();
            JsonObject removeInfo = jsonParser.parse(body).getAsJsonObject();
            String name = removeInfo.get("name").getAsString();
            return BaseStation.getInstance().getBotManager().removeBotByName(name);
        });

        post( "/logdata", (req,res) -> {
            String body = req.body();
            JsonObject commandInfo = jsonParser.parse(body).getAsJsonObject();
            String name = commandInfo.get("name").getAsString();
            Bot myBot = BaseStation.getInstance().getBotManager().getBotByName(name).get();
            CommandCenter cc = myBot.getCommandCenter();
            System.out.println("Start Logging Data...");
            cc.startLogging();
            return true;
        });

        /**
         * GET /sendScript sends script to the bot identified by botName
         *
         * @apiParam name the name of the bot
         * @apiParam script the full string containing the script
         * @return true if the script sending should be successful
         */
        post("/sendScript", (req,res) -> {
            String body = req.body();
            JsonObject commandInfo = jsonParser.parse(body).getAsJsonObject();

            /* storing json objects into actual variables */
            String name = commandInfo.get("name").getAsString();
            String script = commandInfo.get("script").getAsString();

            Bot receiver = BaseStation.getInstance()
                    .getBotManager()
                    .getBotByName(name)
                    .orElseThrow(NoSuchElementException::new);

            if (receiver instanceof SimBot)
                ((SimBot)BaseStation.getInstance()
                        .getBotManager()
                        .getBotByName(name)
                        .orElseThrow(NoSuchElementException::new)).resetServer();

            return BaseStation.getInstance()
                    .getBotManager()
                    .getBotByName(name)
                    .orElseThrow(NoSuchElementException::new)
                    .getCommandCenter().sendKV("SCRIPT",script);
        });

        get("/trackedBots", (req, res) -> {
            Collection<Bot> allBots = BaseStation.getInstance().getBotManager().getAllTrackedBots();
            return gson.toJson(allBots);
        });

        /**
            Collects updated JSON objects in the form:
            {   "x": vo.coord.x,
                "y": vo.coord.y,
                "angle": vo.coord.getThetaOrZero(),
                "id": vo.id
            }
         */
        get("/updateloc", (req, res) -> {
            // Locations of all active bots
            List<VisionObject> vol = BaseStation
                    .getInstance()
                    .getVisionManager()
                    .getAllLocationData();
            JsonArray respData = new JsonArray();
            for (VisionObject vo : vol) {
                JsonObject jo = new JsonObject();
                jo.addProperty("x", vo.coord.x);
                jo.addProperty("y", vo.coord.y);
                jo.addProperty("angle", vo.coord.getThetaOrZero());
                jo.addProperty("id", vo.vid);
                jo.addProperty("size",vo.size);
                respData.add(jo);
            }
            return respData;

        });

        /**
         * GET /sendKV sends script to the bot identified by botName
         *
         * @apiParam name the name of the bot
         * @apiParam script the full string containing the script
         * @return true if the script sending should be successful
         */
        post("/sendKV", (req,res) -> {
            String body = req.body();
            JsonObject commandInfo = jsonParser.parse(body).getAsJsonObject();

            String kv_key = commandInfo.get("key").getAsString();
            String kv_value = commandInfo.get("value").getAsString();
            String name = commandInfo.get("name").getAsString();
            System.out.println(name);

            Bot receiver = BaseStation.getInstance()
                    .getBotManager()
                    .getBotByName(name)
                    .orElseThrow(NoSuchElementException::new);

            if (receiver instanceof SimBot)
                ((SimBot)BaseStation.getInstance()
                        .getBotManager()
                        .getBotByName(name)
                        .orElseThrow(NoSuchElementException::new)).resetServer();

            return BaseStation.getInstance()
                    .getBotManager()
                    .getBotByName(name)
                    .orElseThrow(NoSuchElementException::new)
                    .getCommandCenter().sendKV(kv_key,kv_value);
        });

        post("/discoverBots", (req, res) -> {
            return gson.toJson(BaseStation.getInstance().getBotManager().getAllDiscoveredBots());
        });

        post("/runXbox", (req, res) -> {
            String body = req.body();
            JsonObject commandInfo = jsonParser.parse(body).getAsJsonObject();

            /* storing json objects into actual variables */
            String name = commandInfo.get("name").getAsString();

            // if this is called for the first time, initialize the Xbox
            // Controller
            if (xboxControllerDriver == null) {
                // xbox not initialized, initialize it first
                xboxControllerDriver = new XboxControllerDriver();
                // xboxControllerDriver != null
                if (xboxControllerDriver.xboxIsConnected()) {
                    // xbox is connected
                    // run the driver
                    xboxControllerDriver.getMbXboxEventHandler().setBotName
                            (name);
                    xboxControllerDriver.runDriver();
                    return true;
                } else {
                    // xbox is not connected, stop the driver
                    stopXboxDriver();
                    return false;
                }
            } else {
                // xboxControllerDriver != null -- xbox initialized already
                if (xboxControllerDriver.xboxIsConnected()) {
                    // xbox is connected
                    // should be already listening in this case
                    // just set the new name
                    xboxControllerDriver.getMbXboxEventHandler().setBotName
                            (name);
                    return true;
                } else {
                    // xbox is not connected, stop the driver
                    stopXboxDriver();
                    return false;
                }
            }
        });

        post("/stopXbox", (req, res) -> {
            // received stop command, stop the driver
            try{
                stopXboxDriver();
                // no error
                return true;
            } catch (Exception e) {
                // error encountered
                return false;
            }
        });

        // Examplescript routes
        get("/example/patrol/run", (req, res) -> {
            Patrol mbsd = new Patrol( (FourWheelMovement)
                    BaseStation
                            .getInstance().getBotManager().getAllTrackedBots()
                            .iterator().next().getCommandCenter());
            mbsd.start();
            return true;
        });

        get("/example/patrol/addPointToQueue", (req, res) -> {
            patrolPoints.add(BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord);
            return true;
        });

        get("/example/GoBot/addPointToInnerTrack", (req, res) -> {
            innerTrackCoords.add(BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord);
            System.out.println("InnerTrack: " + BaseStation.getInstance()
                    .getVisionManager()
                    .getAllLocationData().get(0).coord.x + ", " + BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord.y);
            return true;
        });

        get("/example/GoBot/addPointToOuterTrack", (req, res) -> {
            outerTrackCoords.add(BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord);
            System.out.println("OuterTrack: " + BaseStation.getInstance()
                    .getVisionManager()
                    .getAllLocationData().get(0).coord.x + ", " + BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord.y);
            return true;
        });

        get("/example/GoBot/addPointToStartArea", (req, res) -> {
            startAreaCoords.add(BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord);
            System.out.println("StartArea: " + BaseStation.getInstance()
                    .getVisionManager()
                    .getAllLocationData().get(0).coord.x + ", " + BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord.y);
            return true;
        });

        get("/example/GoBot/addPointToMiddleArea", (req, res) -> {
            middleAreaCoords.add(BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord);
            System.out.println("MiddleArea: " + BaseStation.getInstance()
                    .getVisionManager()
                    .getAllLocationData().get(0).coord.x + ", " + BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord.y);
            return true;
        });

        get("/example/GoBot/isBotInside", (req, res) -> course.isInsideTrack(BaseStation.getInstance().getVisionManager()
                .getAllLocationData().get(0).coord));

        get("/example/GoBot/getLoc", (req, res) -> "" + BaseStation.getInstance().getVisionManager()
                .getAllLocationData().get(0).coord.x + ", " + BaseStation
                .getInstance().getVisionManager()
                .getAllLocationData().get(0).coord.y);
        /*get("/example/GoBot/didBotFinishALap", (req, res) -> {
            if (gb.finishedLap()) {
                gb.addLapTime(System.nanoTime());
                return "Lap Time: " + gb.getLastLapTime();
            } else {
                return "Current Time: " + System.nanoTime();
            }
        });*/

        get("/example/GoBot/numLapsCompleted", (req, res) -> gb.getLapsDone());

        get("/example/GoBot/setupTrack", (req, res) -> {
            course.setOuter(outerTrackCoords);
            course.setInner(innerTrackCoords);
            course.setStartArea(startAreaCoords);
            course.setMiddleArea(middleAreaCoords);
            gb.setCourse(course);
            gb.start();
            return System.nanoTime();
        });

        get("/example/GoBot/startAI", (req, res) -> {
            gb.setBotState(gb.BOT_PLAYING);
            return "GOGOGO";
        });


        get("/example/GoBot/startHuman", (req, res) -> {
            gb.setBotState(gb.HUMAN_PLAYING);
            return "GOGOGO";
        });
    }

    /**
     * Tells the Xbox Controller Driver to stop
     * Acts as a middleman between this interface and the driver on stopping
     */
    private static void stopXboxDriver() {
        if (xboxControllerDriver != null) {
            // xbox is currently initialized
            xboxControllerDriver.stopDriver();
            xboxControllerDriver = null;
        }
        // xboxControllerDriver == null
        // might get this request from stopXbox HTTP post
    }
}
