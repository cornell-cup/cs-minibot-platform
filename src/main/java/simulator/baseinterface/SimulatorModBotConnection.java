package simulator.baseinterface;

import basestation.bot.connection.MotorConnection;
import simulator.SimModBot;

/**
 * Created by Administrator on 11/8/2016.
 */
public class SimulatorModBotConnection extends MotorConnection {

    private SimModBot myBot;

    public SimulatorModBotConnection(SimModBot myBot) {
        this.myBot = myBot;
    }

    @Override
    public boolean connectionActive() {
        return false;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void setMotorPower(double fl, double fr, double bl, double br) {
        if (fl == fr && bl == fl && bl == br && fl >=0) {
            myBot.forward(fl);

        } else if (fl == fr && bl == fl && bl == br && fl < 0 ) {
            myBot.backward(-fl);
        } else if (fl == bl && br == fr && fl == (-fr) && fl >0) {
            myBot.counterClockwise(fl);
        } else if (fl == bl && br == fr && fl == (-fr) && fl <=0) {
            myBot.clockwise(fr);
        } else {
            System.err.println("INVALID SIM COMMAND");
            myBot.forward(0);
        }
    }
}
