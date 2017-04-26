package basestation.bot.sensors;

/**
 * Hosts methods to retrieve data from all applicable sensors
 * NOTE: This was not used when designed so it may need to be refactored in the future.
 */
public abstract class SensorCenter {
    /**
     * @return a JSON string with the data for all the sensors.
     */
    public abstract String getAllDataJson();
}
