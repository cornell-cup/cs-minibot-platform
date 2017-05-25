package xboxhandler;

/**
 * =============================================================================
 * XboxControllerDriver.java
 * Documentation on http://www.aplu.ch/home/apluhomex.jsp?site=36 |
 * =============================================================================
 * Cornell Cup Robotics Team 2016-2017
 * =============================================================================
 * Requirements:
 * - Hardware:
 * - XboxController
 * - Libraries:
 * - jaw.jar               --- java wrapper class for C++ code (Xbox
 * drivers are written in C++)
 * - XboxController.jar    --- our class derives interfaces and
 * abstract classes from here
 * - xboxcontroller64.dll  --- some funky Windows_x64 stuff
 * tl,dr;  Add these libraries to the build path!
 * =============================================================================
 * Important Instructions: READ THEM TO UNDERSTAND THE PROGRAM
 * (in no specific order/priority)
 * - Whenever exiting the program, ensure calling the release() method of
 * the XboxController instance [NOT YET FIGURED OUT IN THIS
 * IMPLEMENTATION]
 * - If an action method is triggered by 2 or more callbacks, use the
 * 'synchronized' keyword. Example, dpad and Left/RightThumbs call
 * moveBot
 * use synchronized with moveBot
 * - Call setLeftThumbDeadZone, setRightThumbDeadZone, because the thumbs
 * on the controller are super-sensitive
 * - print is to vibrate as IDE debug is to debugging the controller
 * - [OPTIONAL] Call setLeftTriggerDeadZone, setRightTriggerDeadZone: if
 * you need the triggers, call these
 * =============================================================================
 * How to Use the Xbox Controller:
 * ONLY DPAD AND LEFTTHUMB MOVE THE BOT
 * - Forward: dpad N OR leftThumb N
 * - Backward: dpad S OR leftThumb S
 * - Right - forward or CW: dpad E or leftThumb E
 * - Left - forward or CCW: dpad W or leftThumb W
 * =============================================================================
 * <p>
 * =============================================================================
 * CONCISE DOCUMENTATION
 * =============================================================================
 * Class XboxController
 * - Methods
 * - isConnected()
 * - release()
 * - setLeftThumbDeadZone(), setRightThumbDeadZone()
 * - setLeftTriggerDeadZone(), setRightTriggerDeadZone()
 * - vibrate()
 * =============================================================================
 * Interface XboxControllerListener
 * - Methods
 * - buttonA(), buttonB(), buttonX(), buttonY()
 * - dpad()
 * - leftShoulder(), rightShoulder()
 * - leftThumb(), rightThumb()
 * - leftThumbDirection(), rightThumbDirection()
 * - leftThumbMagnitude(), rightThumbMagnitude()
 * - leftTrigger(), rightTrigger()
 * - start(), back()
 * =============================================================================
 * Class XboxControllerAdapter implements XboxControllerListener
 * =============================================================================
 */

/**
 * =============================================================================
 * CONCISE DOCUMENTATION
 * =============================================================================
 * Class XboxController
 *      - Methods
 *          - isConnected()
 *          - release()
 *          - setLeftThumbDeadZone(), setRightThumbDeadZone()
 *          - setLeftTriggerDeadZone(), setRightTriggerDeadZone()
 *          - vibrate()
 * =============================================================================
 * Interface XboxControllerListener
 *      - Methods
 *          - buttonA(), buttonB(), buttonX(), buttonY()
 *          - dpad()
 *          - leftShoulder(), rightShoulder()
 *          - leftThumb(), rightThumb()
 *          - leftThumbDirection(), rightThumbDirection()
 *          - leftThumbMagnitude(), rightThumbMagnitude()
 *          - leftTrigger(), rightTrigger()
 *          - start(), back()
 * =============================================================================
 * Class XboxControllerAdapter implements XboxControllerListener
 * =============================================================================
 */

import ch.aplu.xboxcontroller.XboxController;
import ch.aplu.xboxcontroller.XboxControllerAdapter;
import xboxhandler.inputeventhandler.MiniBotXboxInputEventHandler;

/**
 * An instance of the XboxControllerDriver class reads input from the
 * XboxController, and transfers the data to the MiniBot Handler class
 */
public class XboxControllerDriver {

    // =========================================================================
    // Fields
    // =========================================================================

    // private static XboxControllerDriver instance;
    private static final int MAX_VIBRATE_VALUE = 65535;
    private XboxController xboxController;
    private MiniBotXboxInputEventHandler mbXboxEventHandler;
    private int leftVibrate;        // in 0..MAX_VIBRATE_VALUE
    private int rightVibrate;       // in 0..MAX_VIBRATE_VALUE
    private double leftMag;         // in 0..1
    private double rightMag;        // in 0..1
    private double leftDir;         // in 0..360 -- 0 is North, angles increase
    // clockwise
    private double rightDir;        // in 0..360 -- 0 is North, angles increase
    // clockwise
    private int dpadVal = -1;       // in -1..7 -- 0 is North, 8 directions
    // increase clockwise (N, NE, E, ... , NW). -1 is default

    // =========================================================================
    // Constructors
    // =========================================================================

    /**
     * Constructor: Initializes the Controller and the Bot
     */
    public XboxControllerDriver() {

        xboxController = new XboxController
                ("cs-minibot-platform-src/lib/xboxcontroller64", 1, 50, 50);
        mbXboxEventHandler = new MiniBotXboxInputEventHandler();
    }

    /**
     * Get the MinibotXboxEventHandler object
     * @return MinibotXboxInputEventHandler object
     */
    public MiniBotXboxInputEventHandler getMbXboxEventHandler() {
        return this.mbXboxEventHandler;
    }

    // =========================================================================
    // XboxControllerDriver Utility Functions
    // =========================================================================

    /**
     * Assigns default values to all fields
     */
    private void defaultAllFields() {
        dpadVal = -1;
        leftDir = -1.0;
        rightDir = -1.0;
        leftMag = -1.0;
        rightMag = -1.0;
    }

    /**
     * Tests if the bot is connected, if not then displays a dialog box with
     * error
     * @return False if not connected, true if connected
     */
    public boolean xboxIsConnected() {
        return xboxController.isConnected();
    }

    /**
     * Listens to XboxController's inputs
     */
    public void runDriver() {

        // 0.0 <= thumbs' output value <= 1.0
        // values <= 0.5 are ignored (reduced to 0)
        xboxController.setLeftThumbDeadZone(0.2);
        xboxController.setRightThumbDeadZone(0.2);

        // 0.0 <= triggers' output value <= 1.0
        // values <= 0.2 are ignored (reduced to 0)
        xboxController.setLeftTriggerDeadZone(0.2);
        xboxController.setRightTriggerDeadZone(0.2);

        // default all values so that these listeners don't take in
        // past values
        defaultAllFields();

        // Register callbacks
        // implementing methods of the XboxControllerListener Interface
        xboxController.addXboxControllerListener(new XboxControllerAdapter() {

            // implement all required functions
            public void leftTrigger(double value) {
                leftVibrate = (int) (MAX_VIBRATE_VALUE * value * value);
                xboxController.vibrate(leftVibrate, rightVibrate);
            }

            public void rightTrigger(double value) {
                rightVibrate = (int) (MAX_VIBRATE_VALUE * value * value);
                xboxController.vibrate(leftVibrate, rightVibrate);
            }

            public void buttonA(boolean pressed) {
                // do something
            }

            public void buttonB(boolean pressed) {
                // do something
            }

            public void buttonX(boolean pressed) {
                // do something
            }

            public void buttonY(boolean pressed) {
                // do something
            }

            public void leftThumbMagnitude(double magnitude) {
                leftMag = magnitude;
                mbXboxEventHandler.leftThumbMove(leftMag, leftDir);
            }

            public void rightThumbMagnitude(double magnitude) {
                rightMag = magnitude;
                mbXboxEventHandler.rightThumbMove(rightMag, rightDir);
            }

            public void leftThumbDirection(double direction) {
                leftDir = direction;
                mbXboxEventHandler.leftThumbMove(leftMag, leftDir);
            }

            public void rightThumbDirection(double direction) {
                rightDir = direction;
                mbXboxEventHandler.rightThumbMove(rightMag, rightDir);
            }

            public void dpad(int direction, boolean pressed) {
                dpadVal = direction;
                moveBot();
            }

            public void back(boolean pressed) {
                // do something
            }

            public void start(boolean pressed) {
                // do something
            }
        });
    }


    /**
     * Stops listening to XboxController's inputs
     */
    public void stopDriver() {
        // just do nothing on inputs :P
        xboxController.addXboxControllerListener(new XboxControllerAdapter());
    }

    // =========================================================================
    // Functions to Forward Commands to Bot
    // =========================================================================

    /**
     * Moves the bot forward, CW, CCW or backward depending on the input
     * from dpad (priority) or the leftThumb
     */
    private synchronized void moveBot() {
        // move based on input from either dpadVal or leftThumb
        if (dpadVal != -1) {
            // dpad is pressed
            mbXboxEventHandler.dpadMove(dpadVal);
        } else if (leftMag > 0.0) {
            // leftThumb is moved
//            mbXboxEventHandler.leftThumbMove(leftMag, leftDir);
        } else if (rightMag > 0.0) {
            // rightThumb is moved
//            mbXboxEventHandler.rightThumbMove(rightMag, rightDir);
        }
        defaultAllFields();
    }
}