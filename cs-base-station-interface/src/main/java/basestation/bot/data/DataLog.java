package basestation.bot.data;

import basestation.bot.commands.CommandCenter;
import basestation.bot.sensors.SensorCenter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import sun.management.Sensor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Datalog thread logs data for (Sim)bot <-- once command center is updated, change to bot in general
 */
public class DataLog extends Thread {
    private final transient CommandCenter commandCenter;
    private final transient SensorCenter sensorCenter;
    private static final char DEFAULT_SEPARATOR = ',';
    private final String path;
    private FileWriter writer;
    private boolean fileCreated = false;
    private volatile boolean exit = false;
    private static final int INTERVAL = 100;    //for now it is set to 100 ms

    public DataLog(CommandCenter commandCenter, SensorCenter sensorCenter) throws IOException {
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateObj = new Date();
        this.path = df.format(dateObj).substring(9) + "-log.csv";
        this.commandCenter = commandCenter;
        this.sensorCenter = sensorCenter;

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

    public void run(){
        try {
            boolean firstHeader = true;
            while (!exit) {
                if (commandCenter.isLogging()) {
                    //Creates csv file if it hasn't been created yet
                    if (!fileCreated) {
                        createFile(this.path);
                    }

                    //Interval of sampling data
                    Thread.sleep(INTERVAL);

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
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}