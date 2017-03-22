package simulator.simbot;

import basestation.bot.sensors.SensorCenter;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class SimBotSensorCenter extends SensorCenter {

    Map<String,Sensor> sensors;

    public SimBotSensorCenter() {
        this.sensors = new HashMap<>();
    }

    @Override
    public String getAllDataJson() {
        return "";
    }

    /**
     * Returns all the data for each sensor in the sensor center.
     * @return
     */
    public JsonObject getAllDataGson() {
        JsonObject allData = new JsonObject();
        for (Sensor s : sensors.values()) {
            allData.add(s.getName(),s.read());
        }

        return allData;
    }

    /**
     * Adds s to the sensor set.
     * @param s The sensor to add.
     */
    public void registerSensor(Sensor s) {
        if (sensors.keySet().contains(s.getName())) {
            throw new RuntimeException("Duplicate sensor name!");
        }

        sensors.put(s.getName(),s);
    }
}
