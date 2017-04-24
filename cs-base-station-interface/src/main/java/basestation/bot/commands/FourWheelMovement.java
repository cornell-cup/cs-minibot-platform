package basestation.bot.commands;

/**
 * Interface for any wheel movement. Any wheel movement should be expressed as a percentage
 * of the maximum power from 0 to 100.
 * <p>
 * This interface assumes there are 4 wheels on the bot in a cart configuration. In other words
 * there is a front left, front right, back left, and back right wheel.
 */
public interface FourWheelMovement extends CommandCenter {
    /**
     * Sets the wheel power for all four wheels. Wheel power should be a number from -100 to 100. Note that negative
     * number imply the wheel is moving backwards
     *
     * @param fl power to set the front left wheel
     * @param fr power to set the front right wheel
     * @param bl power to set the back left wheel
     * @param br power to set the back right wheel
     * @return true if the command was likely successful
     */
    boolean setWheelPower(double fl, double fr, double bl, double br);
}
