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
import com.google.gson.JsonParser;
import examples.gobot.Course;
import examples.gobot.GoBot;
import examples.patrol.Patrol;
import simulator.Simulator;
import simulator.baseinterface.SimulatorVisionSystem;
import simulator.simbot.SimBot;
import spark.route.RouteOverview;
import xboxhandler.XboxControllerDriver;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static spark.Spark.*;

//import minibot.example.avoidance.Avoidance;

/**
 * HTTP connections for modbot GUI
 * <p>
 * Used for modbot to control and use the GUI while connecting to basestation.
 */
public class BaseHTTPInterface {

    // Temp config settings
    public static final boolean OVERHEAD_VISION = true;
    public static ArrayList<VisionCoordinate> patrolPoints;
    public static ArrayList<VisionCoordinate> innerTrackCoords;
    public static ArrayList<VisionCoordinate> advancedAI;
    private static XboxControllerDriver xboxControllerDriver;

    public static void main(String[] args) {
        // Spark configuration
        port(8080);
        staticFiles.location("/public");
        RouteOverview.enableRouteOverview("/");

        //create new visionsystem and simulator instances
        SimulatorVisionSystem simvs;
        Simulator simulator = new Simulator();
        // Show exceptions
        exception(Exception.class, (exception, request, response) -> {
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
        post("/addBot", (req, res) -> {
            String body = req.body();
            JsonObject addInfo = jsonParser.parse(body).getAsJsonObject(); // gets (ip, port) from js

            /* storing json objects into actual variables */
            String ip = addInfo.get("ip").getAsString();
            int port = addInfo.get("port").getAsInt();
            String name = addInfo.get("name").getAsString();
            String type = addInfo.get("type").getAsString(); //differentiate between modbot and minibot

            /* new modbot is created to add */
            Bot newBot;
            String mangledName;
            switch (type) {
                case "modbot":
                    IceConnection ice = new IceConnection(ip, port);
                    newBot = new ModBot(ice, name);
                    mangledName = BaseStation.getInstance().getBotManager().addBot(newBot);
                    break;
                case "minibot":
                    TCPConnection c = new TCPConnection(ip, port);
                    newBot = new MiniBot(c, name);
                    mangledName = BaseStation.getInstance().getBotManager()
                            .addBot(newBot);
                    break;
                default:
                    newBot = simulator.addSimBot(name, 0f, 0f, 0);
                    mangledName = newBot.getName();
                    break;
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
        post("/addScenario", (req, res) -> {

            String body = req.body();
            for (String name : BaseStation.getInstance().getBotManager().getAllTrackedBots().stream().map(Bot::getName).collect(Collectors.toList())) {
                BaseStation.getInstance().getBotManager().removeBotByName(name);
            }
            return simulator.importScenario(gson, jsonParser, jsonParser.parse(body).getAsJsonObject());

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
        post("/saveScenario", (req, res) -> {

            String body = req.body();
            JsonObject scenario = jsonParser.parse(body).getAsJsonObject();
            String scenarioBody = scenario.get("scenario").getAsString();
            String fileName = scenario.get("name").getAsString();

            //writing new scenario file
            File file = new File
                    ("cs-minibot-platform-src/src/main/resources" +
                            "/public/scenario/" + fileName + ".txt");
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
        post("/loadScenario", (req, res) -> {
            String body = req.body();
            JsonObject scenario = jsonParser.parse(body).getAsJsonObject();
            String fileName = scenario.get("name").getAsString();
            String scenarioData = "";

            //loading scenario file
            File file = new File
                    ("cs-minibot-platform-src/src/main/resources" +
                            "/public/scenario/" + fileName + ".txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                scenarioData += line;
                line = br.readLine();
            }
            br.close();
            return scenarioData;
        });

        /*send commands to the selected bot*/
        post("/commandBot", (req, res) -> {
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
            CommandCenter cc = myBot.getCommandCenter();
            return cc.sendKV("WHEELS", fl + "," + fr + "," + bl + "," + br);
        });

        /*remove the selected bot -  not sure if still functional*/
        post("/removeBot", (req, res) -> {
            String body = req.body();
            JsonObject removeInfo = jsonParser.parse(body).getAsJsonObject();
            String name = removeInfo.get("name").getAsString();
            return BaseStation.getInstance().getBotManager().removeBotByName(name);
        });

        post("/logdata", (req, res) -> {
            String body = req.body();
            JsonObject commandInfo = jsonParser.parse(body).getAsJsonObject();
            String name = commandInfo.get("name").getAsString();
            Bot myBot = BaseStation.getInstance().getBotManager().getBotByName(name).get();
            CommandCenter cc = myBot.getCommandCenter();
            if (!cc.isLogging()) {
                System.out.println("Start Logging");
            } else {
                System.out.println("Stop Logging");
            }
            cc.toggleLogging();
            return true;
        });

        /**
         * POST /sendScript sends script to the bot identified by botName
         *
         * @apiParam name the name of the bot
         * @apiParam script the full string containing the script
         * @return true if the script sending should be successful
         */
        post("/sendScript", (req, res) -> {
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
                ((SimBot) BaseStation.getInstance()
                        .getBotManager()
                        .getBotByName(name)
                        .orElseThrow(NoSuchElementException::new)).resetServer();

            return BaseStation.getInstance()
                    .getBotManager()
                    .getBotByName(name)
                    .orElseThrow(NoSuchElementException::new)
                    .getCommandCenter().sendKV("SCRIPT", script);
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
                jo.addProperty("size", vo.size);
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
        post("/sendKV", (req, res) -> {
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
                ((SimBot) BaseStation.getInstance()
                        .getBotManager()
                        .getBotByName(name)
                        .orElseThrow(NoSuchElementException::new)).resetServer();

            return BaseStation.getInstance()
                    .getBotManager()
                    .getBotByName(name)
                    .orElseThrow(NoSuchElementException::new)
                    .getCommandCenter().sendKV(kv_key, kv_value);
        });

        post("/discoverBots", (req, res) -> {
            return gson.toJson(BaseStation.getInstance().getBotManager().getAllDiscoveredBots());
        });

        post("/postOccupancyMatrix", (req, res) -> {
            //Thread.sleep(5000);
            String body = req.body();
            JsonObject settings = jsonParser.parse(body).getAsJsonObject();
            String height = settings.get("height").getAsString();
            String width = settings.get("width").getAsString();
            String size = settings.get("size").getAsString();
            simulator.generateOccupancyMatrix(Integer.parseInt(height), Integer.parseInt(width), Float.parseFloat(size));
            int[][] path = simulator.getDijkstras();
            int[][] foo = simulator.getOccupancyMatrix();
            for (int i = 0; i < foo.length; i++) {
                for (int j = 0; j > foo[0].length; j++) {
                    System.out.print(foo[i][j]);

                }
                System.out.println("hi");
            }
            return gson.toJson(simulator.getOccupancyMatrix());
        });

        post("/postDijkstras", (req, res) -> {
            String body = req.body();
            JsonObject matrix = jsonParser.parse(body).getAsJsonObject();
            JsonArray parentJsonArray = matrix.get("matrix").getAsJsonArray();
            ArrayList<ArrayList<Integer>> total = new ArrayList<ArrayList<Integer>>();
            for (int i = 0; i < parentJsonArray.size(); i++) {
                ArrayList<Integer> child = new ArrayList<Integer>();
                for (int j = 0; j < parentJsonArray.get(0).getAsJsonArray().size(); j++) {
                    child.add(parentJsonArray.get(i).getAsJsonArray().get(j).getAsInt());
                }
                total.add(child);
            }
            int[][] om = new int[total.size()][total.get(0).size()];
            simulator.setOccupancyMatrix(om);
            return gson.toJson(simulator.getDijkstras());
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
            try {
                stopXboxDriver();
                // no error
                return true;
            } catch (Exception e) {
                // error encountered
                return false;
            }
        });


        get("/algo", (req, res) -> {
            //Avoidance a = new Avoidance();
            //a.start();
            return true;
        });

        // Examplescript routes
        get("/example/patrol/run", (req, res) -> {
            Patrol p = new Patrol((FourWheelMovement)
                    BaseStation
                            .getInstance().getBotManager().getAllTrackedBots()
                            .iterator().next().getCommandCenter());
            p.start();
            return true;
        });

        //Adds VisionCoordinate of current bot's location to the queue of points that Patrolbot will patrol
        get("/example/patrol/addPointToQueue", (req, res) -> {
            /**
             * Patrolbot works by moving through a list of points.
             * These points can be picked by the user. To add a point, go
             * to the desired point and this path. This will cause the bot
             * to move between points added to the queue in the order they were added
             */
            patrolPoints.add(BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord);
            return true;
        });

        get("/example/GoBot/addPointToInnerTrack", (req, res) -> {
            /**
             * Add points that make up the inner track in order
             */
            innerTrackCoords.add(BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord);
            System.out.println("InnerTrack: " + BaseStation.getInstance()
                    .getVisionManager()
                    .getAllLocationData().get(0).coord.x + ", " + BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord.y);
            return true;
        });

        get("/example/GoBot/addPointToOuterTrack", (req, res) -> {
            /**
             * Add points that make up the outer track in order
             */
            outerTrackCoords.add(BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord);
            System.out.println("OuterTrack: " + BaseStation.getInstance()
                    .getVisionManager()
                    .getAllLocationData().get(0).coord.x + ", " + BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord.y);
            return true;
        });

        get("/example/GoBot/addPointToStartArea", (req, res) -> {
            /*
             * Add points that create a bounding rectange around where the bot is supposed to start
             */
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
            return "GO AI";
        });


        get("/example/GoBot/startHuman", (req, res) -> {
            gb.setBotState(gb.HUMAN_PLAYING);
            return "GO HUMAN";
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
