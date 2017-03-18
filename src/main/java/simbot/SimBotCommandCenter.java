package simbot;

import basestation.BaseStation;
import basestation.bot.commands.FourWheelMovement;
import basestation.bot.connection.TCPConnection;
import simulator.baseinterface.SimulatorVisionSystem;
import org.jbox2d.dynamics.Body;
import org.jbox2d.common.Vec2;

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
        Body b = SimulatorVisionSystem.getInstance().getWorld().getBodyList();

        if(fl > 0 && fr > 0) {
            System.out.println("forward");
            b.setLinearVelocity(new Vec2(2.0f, 0.0f));
        }
        if(fl <= 0 && fr <= 0) {
            System.out.println("backward");
            b.setLinearVelocity(new Vec2(-2.0f, 0.0f));
        }

        return true;
    }
}
