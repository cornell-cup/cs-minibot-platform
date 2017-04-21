package basestation.bot.commands;

import basestation.bot.connection.Connection;

/**
 * Extends four wheel movement with some useful commands.
 * <p>
 * Power is the power that will be supplied to all the wheels to achieve this command. It should be a number
 * that is between 0 and 100
 */
public abstract class ExtendedFourWheelMovement implements FourWheelMovement {

    public void forward(double power) {
        assert power >= 0 && power <= 100;
        setWheelPower(power, power, power, power);
    }

    public void backward(double power) {
        assert power >= 0 && power <= 100;
        setWheelPower(-power, -power, -power, -power);
    }

    public void clockwise(double power) {
        assert power >= 0 && power <= 100;
        setWheelPower(power, -power, power, -power);
    }

    public void counterClockwise(double power) {
        assert power >= 0 && power <= 100;
        setWheelPower(-power, power, -power, power);
    }

    public void stop() {
        setWheelPower(0, 0, 0, 0);
    }
}