package simulator.simbot;

import basestation.bot.robot.Bot;
import basestation.bot.sensors.SensorCenter;

public class SimBot extends Bot {
    private final transient SimBotCommandCenter commandCenter;
    private final transient SimBotSensorCenter sensorCenter;


    /**
     * Currently minibots are implemented using a TCP connection
     */

    public SimBot(SimBotConnection sbc, String name) {
        super(sbc);
        this.commandCenter = new SimBotCommandCenter(this);
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

