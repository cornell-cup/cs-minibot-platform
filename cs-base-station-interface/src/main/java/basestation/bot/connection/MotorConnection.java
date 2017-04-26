package basestation.bot.connection;

/**
 * An extension to a normal connection which allows custom power setting to each of the four wheels.
 * Power values range from 0-255.
 */
public abstract class MotorConnection extends Connection {

    /**
     * Sets the motor power for each wheel of the motor connection.
     * Each wheel can range from -255 to 255 in power.
     * @param fl Front-left power
     * @param fr Front-right power
     * @param bl Back-left power
     * @param br Back-right power
     */
    public abstract void setMotorPower(double fl, double fr, double bl, double br);

}
