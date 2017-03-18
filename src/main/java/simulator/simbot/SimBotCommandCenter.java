package simulator.simbot;

import basestation.bot.commands.FourWheelMovement;
import simulator.baseinterface.SimulatorVisionSystem;
import org.jbox2d.dynamics.Body;
import org.jbox2d.common.Vec2;

public class SimBotCommandCenter implements FourWheelMovement {

    public SimBotCommandCenter(SimBot myBot) {
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

    @Override
    public boolean sendKV(String key, String value) {
        return false;
    }
}
