package simulator.simbot;

import basestation.bot.sensors.SensorCenter;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimBotSensorCenter extends SensorCenter {

    Map<String, Sensor> sensors;

    public SimBotSensorCenter() {
        this.sensors = new ConcurrentHashMap<>();
    }

    @Override
    public String getAllDataJson() {
        return "";
    }

    /**
     * Returns all the data for each sensor in the sensor center.
     *
     * @return
     */
    public JsonObject getAllDataGson() {
        JsonObject allData = new JsonObject();
        for (Sensor s : sensors.values()) {
            allData.add(s.getName(), s.read());
        }

        return allData;
    }

    public JsonObject getSensorData(String name) {
        JsonObject data = new JsonObject();
        for (Sensor s : sensors.values()) {
            if (s.getName().equals(name)) {
                data.add(s.getName(), s.read());
                return data;
            }
        }
        data.addProperty(name, -1);
        return data;
    }

    /**
     * Adds s to the sensor set.
     *
     * @param s The sensor to add.
     */
    public void registerSensor(Sensor s) {
        if (sensors.keySet().contains(s.getName())) {
            throw new RuntimeException("Duplicate sensor name!");
        }

        sensors.put(s.getName(), s);
    }
}
