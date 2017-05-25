package simulator.simbot;

import basestation.bot.robot.Bot;
import basestation.bot.sensors.SensorCenter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jbox2d.dynamics.World;
import simulator.Simulator;
import simulator.physics.PhysicalObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class SimBot extends Bot {

    public static transient TCPServer runningThread;
    public static int IPADDRESS = 11111;
    private final transient SimBotCommandCenter commandCenter;
    private final transient SimBotSensorCenter sensorCenter;
    public transient DataLog loggingThread;
    public transient PhysicalObject myPhysicalObject;

    /**
     * Currently minibots are implemented using a TCP connection
     */
    public SimBot(SimBotConnection sbc, Simulator simulator, String name, int
            id,
                  World world,
                  float xSpeed, float ySpeed, float xPos, float yPos,
                  float angle, boolean isDynamic) {
        super(sbc, name);

        sensorCenter = new SimBotSensorCenter();

        this.myPhysicalObject = new PhysicalObject(name, id, world, xSpeed, ySpeed,
                xPos, yPos, angle, isDynamic);
        this.commandCenter = new SimBotCommandCenter(this, this
                .myPhysicalObject.getBody(), simulator);

        try {
            if (this.runningThread != null && this.runningThread.isAlive()) {
                try {
                    if (this.runningThread.isAlive()) {
                        this.runningThread.stopStream();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //starts thread for tcp server
            Thread t = new TCPServer(IPADDRESS, this, commandCenter, this.sensorCenter);
            runningThread = (TCPServer) t;
            runningThread.start();

            //starts thread for data logging
            DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            Date dateObj = new Date();
            Thread log = new DataLog(df.format(dateObj).substring(9) + "-log.csv", commandCenter, this.sensorCenter);
            loggingThread = (DataLog) log;
            loggingThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the JBox2D physical object associated with this bot
     */
    public PhysicalObject getMyPhysicalObject() {
        return myPhysicalObject;
    }

    @Override
    public SimBotCommandCenter getCommandCenter() {
        return commandCenter;
    }

    @Override
    public SensorCenter getSensorCenter() {
        return sensorCenter;
    }

    public void resetServer() {
        try {
            if (this.runningThread.isAlive()) {
                this.runningThread.stopRead();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Datalog thread logs data for (Sim)bot <-- once command center is updated, change to bot in general
     */
    public class DataLog extends Thread {
        private static final char DEFAULT_SEPARATOR = ',';
        private static final int INTERVAL = 100;    //for now it is set to 100 ms
        private final transient SimBotCommandCenter commandCenter;
        private final transient SimBotSensorCenter sensorCenter;
        private final String path;
        private FileWriter writer;
        private boolean fileCreated = false;
        private volatile boolean exit = false;

        public DataLog(String path, SimBotCommandCenter commandCenter, SimBotSensorCenter sensorCenter) throws IOException {
            this.commandCenter = commandCenter;
            this.sensorCenter = sensorCenter;
            this.path = path;

            //Create csv file only if command center is currently logging
            if (commandCenter.isLogging()) {
                createFile(path);
            }
        }

        public void createFile(String path) {
            try {
                File file = new File(path);
                file.createNewFile();
                this.writer = new FileWriter(file);
                this.fileCreated = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void stopLogging() {
            this.exit = true;
        }

        public void run() {
            try {
                boolean firstHeader = true;
                while (!exit) {
                    if (commandCenter.isLogging()) {
                        //Creates csv file if it hasn't been created yet
                        if (!fileCreated) {
                            createFile(this.path);
                        }

                        //Interval of sampling data
                        this.sleep(INTERVAL);

                        //Grabs command data
                        boolean first = true;
                        StringBuilder sbCommand = new StringBuilder();
                        StringBuilder sbHeaderCommand = new StringBuilder();
                        JsonObject commandData = commandCenter.getAllData();
                        for (Map.Entry<String, JsonElement> entry : commandData.entrySet()) {
                            String name = entry.getKey();
                            String value = entry.getValue().getAsString();
                            if (!first) {
                                sbCommand.append(DEFAULT_SEPARATOR);
                                sbHeaderCommand.append(DEFAULT_SEPARATOR);
                            }
                            sbCommand.append(value);
                            sbHeaderCommand.append(name);
                            first = false;
                        }

                        //Grabs sensor data
                        StringBuilder sbSensor = new StringBuilder();
                        StringBuilder sbHeaderSensor = new StringBuilder();
                        JsonObject sensorData = this.sensorCenter.getAllDataGson();

                        //Adds seperator between movement and sensor if movement has recorded some value
                        if (!first) {
                            sbSensor.append(DEFAULT_SEPARATOR);
                            sbHeaderSensor.append(DEFAULT_SEPARATOR);
                        }
                        first = true;

                        for (Map.Entry<String, JsonElement> entry : sensorData.entrySet()) {
                            String name = entry.getKey();
                            JsonObject data = entry.getValue().getAsJsonObject();
                            String value = Integer.toString(data.get("data").getAsInt());
                            if (value.equals("")) {
                                value = " ";
                            }
                            if (!first) {
                                sbSensor.append(DEFAULT_SEPARATOR);
                                sbHeaderSensor.append(DEFAULT_SEPARATOR);
                            }
                            sbSensor.append(value);
                            sbHeaderSensor.append(name);
                            first = false;
                        }

                        //TODO Add log additional data here

                        //Record Header if there is a header
                        if (firstHeader &&
                                !sbHeaderSensor.toString().equals("") &&
                                !sbHeaderCommand.toString().equals("")) {
                            sbHeaderCommand.append(sbHeaderSensor);
                            sbHeaderCommand.append("\n");
                            writer.append(sbHeaderCommand.toString());
                            firstHeader = false;
                        }

                        //Write to csv
                        if (!sbCommand.toString().equals("") && !sbSensor.toString().equals("")) {
                            sbCommand.append(sbSensor);
                            sbCommand.append("\n");
                            writer.append(sbCommand.toString());
                            writer.flush();
                        }
                    }
                }
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TCP server thread, server side
     */
    public class TCPServer extends Thread {
        private final transient SimBot sim;
        private final transient SimBotCommandCenter commandCenter;
        private final transient SimBotSensorCenter sensorCenter;
        private ServerSocket serverSocket;
        private volatile boolean exit = false;
        private volatile boolean runRead = true;
//        private final static int TIMEOUT = 100000;

        public TCPServer(int port, SimBot simbot, SimBotCommandCenter commandCenter, SimBotSensorCenter sensorCenter) throws IOException {
            sim = simbot;
            this.commandCenter = commandCenter;
            this.sensorCenter = sensorCenter;
            serverSocket = new ServerSocket(port);
//            serverSocket.setSoTimeout(TIMEOUT);
        }

        public void stopStream() {
            exit = true;
            try {
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void stopRead() {
            runRead = false;
        }

        public void run() {
            while (!exit) {
                try {
                    System.out.println("Waiting for client on port " +
                            serverSocket.getLocalPort() + "...");
                    Socket server = serverSocket.accept();

                    System.out.println("Just connected to " + server.getRemoteSocketAddress());

                    String content;
                    BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    PrintWriter out = new PrintWriter(server.getOutputStream(), true);

                    runRead = true;
                    while (runRead && !exit) {
                        content = in.readLine();

                        if (content != null) {
                            String value = "";
                            if (!content.contains("WHEELS") && !content.contains("REGISTER") && !content.contains("GET")) {
                                value = content.substring(content.indexOf(':') + 1);
                            }
                            String name = content.substring(content.indexOf(':') + 1);
                            switch (content.substring(0, content.indexOf(':'))) {
                                case "FORWARD":
                                    commandCenter.sendKV("WHEELS", value + "," + value + "," + value + "," + value);
                                    break;
                                case "BACKWARD":
                                    commandCenter.sendKV("WHEELS", "-" + value + ",-" + value + ",-" + value + ",-" + value);
                                    break;
                                case "RIGHT":
                                    commandCenter.sendKV("WHEELS", value + ",-" + value + "," + value + ",-" + value);
                                    break;
                                case "LEFT":
                                    commandCenter.sendKV("WHEELS", "-" + value + "," + value + ",-" + value + "," + value);
                                    break;
                                case "WAIT":
                                    System.out.println("WAITING FOR " + value + " SECONDS");
                                    break;
                                case "STOP":
                                    commandCenter.sendKV("WHEELS", "0,0,0,0");
                                    break;
                                case "KILL":
                                    runRead = false;
                                    System.out.println("Exiting\n");
                                    break;
                                case "GET":
                                    if (name.equals("ALL")) {
                                        out.println(this.sensorCenter.getAllDataGson());
                                    } else {
                                        out.println(this.sensorCenter.getSensorData(name));
                                    }
                                    break;
                                case "REGISTER":
//                                    String name = content.substring(content.indexOf(':') + 1);
//                                    ColorIntensitySensor colorSensor = new ColorIntensitySensor((SimBotSensorCenter) this.sensorCenter,name, this.sim);
                                    System.out.println("Added New sensor");
                                    break;
                                default:
                                    String cmd = content.substring(content.indexOf(':') + 1);
                                    String[] wheelCmds = cmd.split(",");

                                    commandCenter.sendKV("WHEELS", wheelCmds[0] + "," + wheelCmds[1]
                                            + "," + wheelCmds[2] + "," + wheelCmds[3]);
                                    break;
                            }
                        }
                    }
                } catch (SocketTimeoutException s) {
                    System.out.println("Socket timed out!");
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

            }
        }
    }
}

