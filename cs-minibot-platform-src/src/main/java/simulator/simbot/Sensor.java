package simulator.simbot;


import com.google.gson.JsonObject;

public class Sensor {

    private String name;

    /**
     * Creates the sensor and registers it with myCenter under name.
     *
     * @param myCenter
     * @param name
     */
    public Sensor(SimBotSensorCenter myCenter, String name) {
        this.name = name;
        myCenter.registerSensor(this);
    }

    public String getName() {
        return this.name;
    }

    public JsonObject read() {
        JsonObject dataBlob = new JsonObject();
        dataBlob.addProperty("data", -1);
        return dataBlob;
    }
}
