package basestation.bot.robot.simbot;

import basestation.BaseStation;
import basestation.bot.commands.FourWheelMovement;
import basestation.bot.connection.TCPConnection;
import basestation.bot.robot.simbot.SimBot;
import 

/**
 * Created by jimmychen on 3/10/17.
 */
public class SimBotCommandCenter implements FourWheelMovement {
    private final TCPConnection connection;


    public SimBotCommandCenter(TCPConnection connection, SimBot myBot) {
        this.connection = connection;
    }

    @Override
    public boolean sendKV(String type, String payload) {
        return connection.sendKV(type, payload);
    }

    @Override
    public boolean setWheelPower(double fl, double fr, double bl, double br) {
        BaseStation.getInstance().getVisionManager().
    }
}
