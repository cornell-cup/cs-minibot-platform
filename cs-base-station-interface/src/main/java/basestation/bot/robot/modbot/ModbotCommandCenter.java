package basestation.bot.robot.modbot;

import basestation.bot.commands.ExtendedFourWheelMovement;
import basestation.bot.connection.Connection;
import basestation.bot.connection.MotorConnection;
import basestation.vision.VisionCoordinate;
import com.google.gson.JsonObject;

/**
 * A CommandCenter for a ModBot
 */
public class ModbotCommandCenter extends ExtendedFourWheelMovement {
    private final MotorConnection connection;
    private final ModbotNavigator navigator;
    public transient boolean record = false;

    public ModbotCommandCenter(MotorConnection connection, ModBot myBot) {
        super();
        this.navigator = new ModbotNavigator(myBot);
        this.connection = connection;
    }

    public void startLogging() {
        this.record = true;
    }

    public boolean isLogging() {
        return this.record;
    }

    /**
     * @return true if the bot has reached its destination. TODO: convert to a destination queue
     */
    public boolean destinationReached() {
        return this.navigator.destinationReached();
    }

    /**
     * Navigates the bot to (x,y) using its built in navigator.
     * Requires an active vision system and association between the bot and the system.
     *
     * @param vc The coordinate to go to, under the canonical vision system.
     */
    public void gotoCoord(VisionCoordinate vc) {
        this.navigator.setDestination(vc);
    }


    /**
     * bot translates left
     *
     * @param power the amount of power that should be supplied to the wheels. It should be between 0 and 100
     */
    public void left(double power) {
        assert power >= 0 && power <= 100;
        setWheelPower(-power, power, power, -power);
    }

    /**
     * bot translates right
     *
     * @param power the amount of power that should be supplied to the wheels. It should be between 0 and 100
     */
    public void right(double power) {
        assert power >= 0 && power <= 100;
        setWheelPower(power, -power, -power, power);
    }

    @Override
    public boolean setWheelPower(double fl, double fr, double bl, double br) {
            ((MotorConnection) connection).setMotorPower(fl, fr, bl, br);
            return true;
    }

    public boolean sendKV(String key, String value) {
        return false; // TODO
    }

    public Connection getConnection() {
        return this.connection;
    }
    @Override
    public JsonObject getAllData(){
        //TODO: implement
        return null;
    }

}
