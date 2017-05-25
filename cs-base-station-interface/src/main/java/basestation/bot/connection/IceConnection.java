package basestation.bot.connection;

import CommModule.BaseInterfacePrx;
import CommModule.BaseInterfacePrxHelper;

/**
 * Connection to support our Ice protocol.
 * Sends controls over a rate-limited thread. This is the protocol that all legacy modbots operate with.
 */
public class IceConnection extends MotorConnection {

    private final static double THROTTLE = 150; // Max 255 TODO set up a config file for this

    private Ice.Communicator ic;
    private ControlManager controlManager;
    private String identity;

    public IceConnection(String ip, int port) {
        try {
            if (port == -1) port = 10000;
            ic = Ice.Util.initialize();
            identity = "control -t -e 1.0:tcp -h " + ip + " -p " + port;
            Ice.ObjectPrx base = ic.stringToProxy(identity);
            BaseInterfacePrx iface = BaseInterfacePrxHelper.checkedCast(base);
            if (iface == null)
                throw new Error("Invalid proxy");
            else {
                controlManager = new ControlManager(iface);
                controlManager.start();
            }

        } catch (Ice.LocalException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void setMotorPower(double fl, double fr, double bl, double br) {
        // Adjustment constant for speed
        double adjustment = THROTTLE / 100.0;
        setMotorSpeed((int) (fl * adjustment), (int) (fr * adjustment), (int) (bl * adjustment), (int) (br * adjustment));
    }

    private void setMotorSpeed(int fl, int fr, int bl, int br) {
        controlManager.setMotors(fl, fr, bl, br);
    }

    @Override
    public boolean connectionActive() {
        return ic != null;
    }

    @Override
    public void destroy() {
        if (ic != null) {
            try {
                ic.destroy();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public String toString() {
        return this.identity;
    }

    public String getIP() {
        return "";
    }
}
