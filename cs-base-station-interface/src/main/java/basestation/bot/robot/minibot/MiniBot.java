package basestation.bot.robot.minibot;

import basestation.bot.connection.TCPConnection;
import basestation.bot.robot.Bot;
import basestation.bot.sensors.SensorCenter;

/**
 * Maintains our interface with a MiniBot
 */
public class MiniBot extends Bot{
    private final transient MiniBotCommandCenter commandCenter;
    private final transient MiniBotSensorCenter sensorCenter;


    /**
     * Currently minibots are implemented using a TCP connection
     *
     * @param c a TCP connection that has already been created
     */
    public MiniBot(TCPConnection c) {
        super(c);
        this.commandCenter = new MiniBotCommandCenter(c, this);
        this.sensorCenter = new MiniBotSensorCenter();
    }

    public MiniBot(TCPConnection c, String name) {
        super(c, name);
        this.commandCenter = new MiniBotCommandCenter(c, this);
        this.sensorCenter = new MiniBotSensorCenter();
    }

    @Override
    public MiniBotCommandCenter getCommandCenter() {
        return commandCenter;
    }

    @Override
    public SensorCenter getSensorCenter() {
        return sensorCenter;
    }
}

