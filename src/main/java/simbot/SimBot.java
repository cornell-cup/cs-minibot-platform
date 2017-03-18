package simbot;

import basestation.bot.connection.TCPConnection;
import basestation.bot.robot.Bot;
import basestation.bot.sensors.SensorCenter;

/**
 * Created by jimmychen on 3/10/17.
 */
public class SimBot extends Bot {
    private final transient SimBotCommandCenter commandCenter;
    private final transient SimBotSensorCenter sensorCenter;


    /**
     * Currently minibots are implemented using a TCP connection
     *
     * @param c a TCP connection that has already been created
     */
    public SimBot(TCPConnection c) {
        super(c);
        this.commandCenter = new SimBotCommandCenter(c, this);
        this.sensorCenter = new SimBotSensorCenter();
    }

    public SimBot(TCPConnection c, String name) {
        super(c, name);
        this.commandCenter = new SimBotCommandCenter(c, this);
        this.sensorCenter = new SimBotSensorCenter();
    }

    @Override
    public SimBotCommandCenter getCommandCenter() {
        return commandCenter;
    }

    @Override
    public SensorCenter getSensorCenter() {
        return sensorCenter;
    }
}

