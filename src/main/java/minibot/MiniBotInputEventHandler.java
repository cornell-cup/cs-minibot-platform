package minibot;

/**
 * MiniBotEventInputHandler.java
 * =============================================================================
 * Created by Anmol Kabra
 * Cornell Cup Robotics Team 2016-2017
 * =============================================================================
 * Important Instructions:
 *      - motorPower order: --- (fl, fr, bl, br) ---
 *      - moveDirection: 0=forward; 1=right-forward or CW; 2=left-forward or
 *      CCW; 3=backward
 * =============================================================================
 * <<<<< UPDATE THE TABLE AS YOU CHANGE IMPLEMENTATIONS IN THIS CLASS >>>>>
 * -----------------------------------------------------------------------------
 * Movements supported:                 | Movements not supported:
 *      - dpad                          |   - NONE
 *      - leftThumb                     |
 * -----------------------------------------------------------------------------
 * Actions supported:                   | Actions not supported:
 *      - NONE                          |   - rightThumb
 *                                      |   - leftTrigger
 *                                      |   - rightTrigger
 *                                      |   - leftShoulder
 *                                      |   - rightShoulder
 *                                      |   - buttonA
 *                                      |   - buttonB
 *                                      |   - buttonX
 *                                      |   - buttonY
 *                                      |   - buttonBack
 *                                      |   - buttonStart
 * =============================================================================
 */

/**
 * An instance of the MiniBotInputEventHandler class is capable of sending
 * commands to the basestation to maneuver the MiniBot
 */
/*package*/ class MiniBotInputEventHandler implements InputEventHandler {

    private static final double MAX_MOTOR_POW = 100.0;

    /**
     * Constructor: initializes the instance
     */
    /*package*/ MiniBotInputEventHandler () {}

    /**
     * Convert degrees to radians
     * Precondition: degree >= 0
     * @param degree angle measure in degrees
     * @return degree converted to radians
     */
    private double degToRad(double degree) {
        assert degree >= 0;
        return ((int)(degree) % 360) * Math.PI / 180.0;
    }

    /**
     * Convert dpad's directions to forward, CW, CCW or
     * backward
     * @return Thumb directions converted to moveDirection values
     */
    private int moveDirDpad(int _dpadVal) {
        switch(_dpadVal) {
            case 0:
            case 1:
            case 7:
                // forward
                return 0;
            case 3:
            case 4:
            case 5:
                // backward
                return 3;
            case 2:
                // right - forward or CW
                return 1;
            case 6:
                // left - forward or CCW
                return 2;
            default:
                // no movement
                return -1;
        }
    }

    /**
     * Governs MiniBot's movement according to the input from the XboxController
     * @param dpadVal in -1..7, corresponding to the input from the dpad; -1
     *                is default state
     */
    public void dpadMove(int dpadVal) {
        switch (moveDirDpad(dpadVal)) {
            case 0:
                // move forward
                // Handler.sendMotors(MAX_MOTOR_POW, MAX_MOTOR_POW, MAX_MOTOR_POW, MAX_MOTOR_POW)
                System.out.println("forward");
                break;
            case 1:
                // move right - forward or CW
                // Handler.sendMotors(MAX_MOTOR_POW, -MAX_MOTOR_POW, MAX_MOTOR_POW, -MAX_MOTOR_POW)
                System.out.println("right - forward");
                break;
            case 2:
                // move left - forward or CCW
                // Handler.sendMotors(-MAX_MOTOR_POW, MAX_MOTOR_POW, -MAX_MOTOR_POW, MAX_MOTOR_POW)
                System.out.println("left - forward");
                break;
            case 3:
                // move backward
                // Handler.sendMotors(-MAX_MOTOR_POW, -MAX_MOTOR_POW, -MAX_MOTOR_POW, -MAX_MOTOR_POW)
                System.out.println("backward");
                break;
            case -1:
                System.out.println("no movement");
        }
    }

    /**
     * Convert leftThumb's directions (in angles) to forward, CW, CCW or
     * backward
     * @return Thumb directions converted to moveDirection values in
     */
    private int moveDirLeftThumb(double _direction) {
        if (_direction < 67.5 || _direction > 292.5) {
            // forward
            return 0;
        } else if (_direction < 112.5) {
            // right - forward or CW
            return 1;
        } else if (_direction < 247.5) {
            // backward
            return 3;
        } else if (_direction <= 292.5) {
            // left - forward or CCW
            return 2;
        }
        return -1;
    }

    /**
     * Governs MiniBot's movement according to the input from the XboxController
     * @param magnitude in 0..1; based on how strongly the leftThumb is pushed
     * @param direction in degrees 0..360; based on the direction of the
     *                  leftThumb; 0 is due North
     */
    public void leftThumbMove(double magnitude, double direction) {
        double power = magnitude * MAX_MOTOR_POW;
        switch (moveDirLeftThumb(direction)) {
            case 0:
                // move forward
                // Handler.sendMotors(power, power, power, power)
                System.out.println("forward");
                break;
            case 1:
                // move right - forward or CW
                // Handler.sendMotors(power, -power, power, -power)
                System.out.println("right - forward");
                break;
            case 2:
                // move left - forward or CCW
                // Handler.sendMotors(-power, power, -power, power)
                System.out.println("left - forward");
                break;
            case 3:
                // move backward
                // Handler.sendMotors(-power, -power, -power, -power)
                System.out.println("backward");
                break;
            case -1:
                System.out.println("no movement");
        }
    }

    public void rightThumbAction(double magnitude, double direction) {
        throw new UnsupportedOperationException("Right Thumb Action not yet " +
                "implemented!");
    }

    public void leftTriggerAction(double value) {
        throw new UnsupportedOperationException("Left Trigger Action not yet " +
                "implemented!");
    }

    public void rightTriggerAction(double value) {
        throw new UnsupportedOperationException("Right Trigger Action not yet" +
                " implemented!");
    }

    public void leftShoulderAction(boolean pressed) {
        throw new UnsupportedOperationException("Left Shoulder Action not yet" +
                " implemented!");
    }

    public void rightShoulderAction(boolean pressed) {
        throw new UnsupportedOperationException("Right Shoulder Action not " +
                "yet implemented");
    }

    public void buttonAAction(boolean pressed) {
        throw new UnsupportedOperationException("Button A Action not yet " +
                "implemented!");
    }

    public void buttonBAction(boolean pressed) {
        throw new UnsupportedOperationException("Button B Action not yet " +
                "implemented!");
    }

    public void buttonXAction(boolean pressed) {
        throw new UnsupportedOperationException("Button X Action not yet " +
                "implemented!");
    }

    public void buttonYAction(boolean pressed) {
        throw new UnsupportedOperationException("Button Y Action not yet " +
                "implemented!");
    }

    public void buttonBackAction(boolean pressed) {
        throw new UnsupportedOperationException("Button Back Action not yet " +
                "implemented!");
    }

    public void buttonStartAction(boolean pressed) {
        throw new UnsupportedOperationException("Button Start Action not yet " +
                "implemented!");
    }
}