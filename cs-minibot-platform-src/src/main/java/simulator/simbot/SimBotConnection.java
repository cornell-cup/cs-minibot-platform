package simulator.simbot;

import basestation.bot.connection.MotorConnection;
import simulator.SimModBot;

public class SimBotConnection extends MotorConnection {


    public SimBotConnection( ) {
    }

    @Override
    public boolean connectionActive() {
        return true;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void setMotorPower(double fl, double fr, double bl, double br) {
        throw new Error("unsupported");
    }
}
