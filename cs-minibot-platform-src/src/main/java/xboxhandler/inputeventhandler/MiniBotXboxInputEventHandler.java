package xboxhandler.inputeventhandler;

/**
 * MiniBotEventInputHandler.java
 * =============================================================================
 * Cornell Cup Robotics Team 2016-2017
 * =============================================================================
 * Important Instructions:
 * - motorPower order: --- (fl, fr, bl, br) ---
 * - Move Direction: Using Enum Direction
 * =============================================================================
 * <<<<< UPDATE THE TABLE AS YOU CHANGE IMPLEMENTATIONS IN THIS CLASS >>>>>
 * -----------------------------------------------------------------------------
 * Movements supported:                 | Movements not supported:
 * - dpad                          |   - NONE
 * - leftThumb                     |
 * -----------------------------------------------------------------------------
 * Actions supported:                   | Actions not supported:
 * - NONE                          |   - rightThumb
 * |   - leftTrigger
 * |   - rightTrigger
 * |   - leftShoulder
 * |   - rightShoulder
 * |   - buttonA
 * |   - buttonB
 * |   - buttonX
 * |   - buttonY
 * |   - buttonBack
 * |   - buttonStart
 * =============================================================================
 */

import basestation.BaseStation;
import basestation.bot.commands.FourWheelMovement;
import basestation.bot.robot.Bot;
import xboxhandler.Direction;

import java.util.Optional;

/**
 * An instance of the MiniBotXboxInputEventHandler class is capable of sending
 * commands to the basestation to maneuver the MiniBot
 */
public class MiniBotXboxInputEventHandler extends
        XboxInputEventHandler {

    // =========================================================================
    // Fields
    // =========================================================================

    private static final double MAX_MOTOR_POW = 100.0;
    double curLeft;
    double curRight;
    private String botName;

    // =========================================================================
    // Constructors
    // =========================================================================

    /**
     * Constructor: initializes the instance and the bot
     * @param _botName Name of the Bot
     */
    MiniBotXboxInputEventHandler(String _botName) {
        botName = _botName;
    }

    public MiniBotXboxInputEventHandler() {
        botName = "";
    }

    // =========================================================================
    // MiniBotXboxInputEventHandler Utility Functions
    // =========================================================================

    /**
     * Set the MiniBot's Name
     * @param botName Name of the Bot
     */
    public void setBotName(String botName) {
        this.botName = botName;
    }

    /**
     * sets wheel power for the bot
     * @param _fl front left wheel power
     * @param _fr front right wheel power
     * @param _bl back left wheel power
     * @param _br back right wheel power
     */
    private void localSetWheelPower(double _fl, double _fr, double _bl,
                                    double _br) {
        Optional<Bot> possibleBot = BaseStation.getInstance().getBotManager()
                .getBotByName(botName);
        if (possibleBot.isPresent()) {
            // if bot exists, make it mine
            Bot myBot = possibleBot.get();

            if (myBot.getCommandCenter() instanceof FourWheelMovement) {
                // command center is correctly referenced
                FourWheelMovement fwmCommandCenter = (FourWheelMovement) myBot
                        .getCommandCenter();
                fwmCommandCenter.setWheelPower(_fl, _fr, _bl, _br);
            }
        }
    }

    // =========================================================================
    // Movement Functions
    // =========================================================================

    /**
     * Convert dpad's directions to forward, CW, CCW or
     * backward
     * @return Thumb directions converted to moveDirection values
     */
    private Direction moveDirDpad(int _dpadVal) {
        switch (_dpadVal) {
            case 0:
            case 1:
            case 7:
                // forward
                return Direction.FORWARD;
            case 3:
            case 4:
            case 5:
                // backward
                return Direction.BACKWARD;
            case 2:
                // right - forward or CW
                return Direction.RIGHTFORWARD;
            case 6:
                // left - forward or CCW
                return Direction.LEFTFORWARD;
            default:
                // no movement
                return null;
        }
    }

    /**
     * Governs MiniBot's movement according to the input from the XboxController
     * @param dpadVal in -1..7, corresponding to the input from the dpad; -1
     *                is default state
     */
    public void dpadMove(int dpadVal) {
        switch (moveDirDpad(dpadVal)) {
            case FORWARD:
                // move forward
                localSetWheelPower(MAX_MOTOR_POW, MAX_MOTOR_POW, MAX_MOTOR_POW,
                        MAX_MOTOR_POW);
                break;
            case RIGHTFORWARD:
                // move right - forward or CW
                localSetWheelPower(MAX_MOTOR_POW, -MAX_MOTOR_POW, MAX_MOTOR_POW,
                        -MAX_MOTOR_POW);
                break;
            case LEFTFORWARD:
                // move left - forward or CCW
                localSetWheelPower(-MAX_MOTOR_POW, MAX_MOTOR_POW, -MAX_MOTOR_POW,
                        MAX_MOTOR_POW);
                break;
            case BACKWARD:
                // move backward
                localSetWheelPower(-MAX_MOTOR_POW, -MAX_MOTOR_POW,
                        -MAX_MOTOR_POW, -MAX_MOTOR_POW);
                break;
            default:
                localSetWheelPower(0.0, 0.0, 0.0, 0.0);
        }
    }

    /**
     * Convert leftThumb's directions (in angles) to forward, CW, CCW or
     * backward
     * @return Thumb directions converted to moveDirection values in
     */
    private Direction moveDirLeftThumb(double _direction) {
        if (_direction < 67.5 || _direction > 292.5) {
            // forward
            return Direction.FORWARD;
        } else if (_direction < 112.5) {
            // right - forward or CW
            return Direction.RIGHTFORWARD;
        } else if (_direction < 247.5) {
            // backward
            return Direction.BACKWARD;
        } else if (_direction <= 292.5) {
            // left - forward or CCW
            return Direction.LEFTFORWARD;
        }
        // default
        return null;
    }

    /**
     * Governs MiniBot's movement according to the input from the XboxController
     * @param magnitude in 0..1; based on how strongly the leftThumb is pushed
     * @param direction in degrees 0..360; based on the direction of the
     *                  leftThumb; 0 is due North
     */
    public void leftThumbMove(double magnitude, double direction) {
//        double power = magnitude * MAX_MOTOR_POW;
        curLeft = MAX_MOTOR_POW * magnitude * Math.sin(Math.PI / 2 + Math
                .toRadians
                        (direction));
        localSetWheelPower(curLeft, curRight, 0, 0);

//        switch (moveDirLeftThumb(direction)) {
//            case FORWARD:
//                // move forward
//                localSetWheelPower(power, power, power, power);
//                break;
//            case RIGHTFORWARD:
//                // move right - forward or CW
//                localSetWheelPower(power, -power, power, -power);
//                break;
//            case LEFTFORWARD:
//                // move left - forward or CCW
//                localSetWheelPower(-power, power, -power, power);
//                break;
//            case BACKWARD:
//                // move backward
//                localSetWheelPower(-power, -power, -power, -power);
//                break;
//            default:
//                localSetWheelPower(0.0, 0.0, 0.0, 0.0);
//        }
    }

    public void rightThumbMove(double magnitude, double direction) {
        curRight = MAX_MOTOR_POW * magnitude * Math.sin(Math.PI / 2 + Math
                .toRadians
                        (direction));
        localSetWheelPower(curLeft, curRight, 0, 0);
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