package basestation.bot.connection;

import CommModule.BaseInterfacePrx;

/**
 * A thread to manage commands to modbots. Some modbots discontinue their commands after 1 seconds while others
 * continue the last received, so this resolves the two concerns by sending twice a second.
 */
public class ControlManager extends Thread {

    private static final int COMMANDS_PER_SECOND = 2; // Since the connection is already reliable, we do not need a high frequency
    private BaseInterfacePrx iface;
    private int fl;
    private int fr;
    private int bl;
    private int br;

    public ControlManager(BaseInterfacePrx iface) {
        this.iface = iface;
        fl = fr = bl = br = 0;
    }

    public void run() {
        while (true) {
            sendMotors();
            try {
                Thread.sleep(1000 / COMMANDS_PER_SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets the new constant for motors. Forwards the command immediately so that
     * the bot will be responsive to controls.
     *
     * @param fl
     * @param fr
     * @param bl
     * @param br
     */
    void setMotors(int fl, int fr, int bl, int br) {
        this.fl = fl;
        this.fr = fr;
        this.bl = bl;
        this.br = br;
        sendMotors();
    }

    private synchronized void sendMotors() {
        iface.begin_setMotorSpeeds(fl, fr, bl, br);
    }

}
