package basestation.bot.robot.minibot;

import basestation.bot.commands.CommandCenter;
import basestation.bot.commands.FourWheelMovement;
import basestation.bot.connection.*;

/**
 * Class who's methods are all the commands that can be issued to a bot
 * <p>
 * Each bot must implement this class with their own commands.
 */
public class MiniBotCommandCenter implements FourWheelMovement {
    private final TCPConnection connection;
    public transient boolean record = false;


    public MiniBotCommandCenter(TCPConnection connection, MiniBot myBot) {
        this.connection = connection;
    }

    public void startLogging() {
        this.record = true;
    }

    public boolean isLogging() {
        return this.record;
    }

    @Override
    public boolean sendKV(String type, String payload) {
        return connection.sendKV(type, payload);
    }

    @Override
    public boolean setWheelPower(double fl, double fr, double bl, double br) {
        return sendKV("WHEELS",fl+","+fr+","+bl+","+br);
    }

    public Connection getConnection() {
        return this.connection;
    }
}
