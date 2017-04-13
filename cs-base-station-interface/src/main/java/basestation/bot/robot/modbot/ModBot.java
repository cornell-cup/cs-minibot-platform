package basestation.bot.robot.modbot;

import basestation.bot.robot.Bot;
import basestation.bot.connection.MotorConnection;
import basestation.bot.sensors.SensorCenter;

/**
 * An implementation of a modbot.
 */
public class ModBot extends Bot {
    private final ModbotCommandCenter commandCenter;
    private final ModbotSensorCenter sensorCenter;

    /**
     * Currently modbots are implemented using an ice connection
     *
     * @param c a motorconnection connected to the ModBot
     */
    public ModBot(MotorConnection c) {
        super(c);
        this.commandCenter = new ModbotCommandCenter(c, this);
        this.sensorCenter = new ModbotSensorCenter();
    }

    public ModBot(MotorConnection c, String name) {
        super(c, name);
        this.commandCenter = new ModbotCommandCenter(c, this);
        this.sensorCenter = new ModbotSensorCenter();
    }

    @Override
    public ModbotCommandCenter getCommandCenter() {
        return commandCenter;
    }

    @Override
    public SensorCenter getSensorCenter() {
        return sensorCenter;
    }
}
