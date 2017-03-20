package minibot;

/**
 * =============================================================================
 * XboxControllerDriver.java
 * Documentation on http://www.aplu.ch/home/apluhomex.jsp?site=36 |
 * WWW.APLU.CH IS THE SHIT
 * =============================================================================
 * Created by Trevor Edwards and Anmol Kabra
 * Cornell Cup Robotics Team 2016-2017
 * =============================================================================
 * Requirements:
 *      - Hardware:
 *          - XboxController (you're kidding me?)
 *      - Libraries:
 *          - jaw.jar               --- java wrapper class for C++ code (Xbox
 *              drivers are written in C++)
 *          - XboxController.jar    --- our class derives interfaces and
 *              abstract classes from here
 *          - xboxcontroller64.dll  --- some funky Windows_x64 stuff
 *          tl,dr;  Add these libraries to the build path!
 * =============================================================================
 * Important Instructions: READ THEM TO UNDERSTAND THE PROGRAM
 * (in no specific order/priority)
 *      - Whenever exiting the program, ensure calling the release() method of
 *          the XboxController instance [NOT FIGURED OUT YET IN THIS
 *          IMPLEMENTATION]
 *      - If an action method is triggered by 2 or more callbacks, use the
 *          'synchronized' keyword. Example, dpad and Left/RightThumbs call
 *          moveBot
 *          use synchronized with moveBot
 *      - Call setLeftThumbDeadZone, setRightThumbDeadZone, because the thumbs
 *          on the controller are super-sensitive
 *      - print is to vibrate as IDE debug is to debugging the controller
 *      - [OPTIONAL] Call setLeftTriggerDeadZone, setRightTriggerDeadZone: if
 *          you need the triggers, call these
 * =============================================================================
 * How to Use the Xbox Controller:
 * ONLY DPAD AND LEFTTHUMB MOVE THE BOT
 *      - Forward: dpad N OR leftThumb N (0 in code)
 *      - Backward: dpad S OR leftThumb S (3 in code)
 *      - Right - forward or CW: dpad E or leftThumb E (1 in code)
 *      - Left - forward or CCW: dpad W or leftThumb W (2 in code)
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

import ch.aplu.xboxcontroller.*;

/**
 * An instance of the XboxControllerDriver class reads input from the
 * XboxController, and transfers the data to the MiniBot Handler class
 */
/*package*/ class XboxControllerDriver {

    // =========================================================================
    // Fields
    // =========================================================================

    // private static XboxControllerDriver instance;
    private static final int MAX_VIBRATE_VALUE = 65535;
    private XboxController xc;
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
    /*package*/ XboxControllerDriver() {

        xc = new XboxController();
        mbXboxEventHandler = new MiniBotXboxInputEventHandler();
    }

    /**
     * Get the MinibotXboxEventHandler object
     * @return MinibotXboxInputEventHandler object
     */
    /*package*/ MiniBotXboxInputEventHandler getMbXboxEventHandler() {
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
    /*package*/ boolean xboxIsConnected() {
        if (!xc.isConnected()) {
            // stopDriver();
            return false;
        }
        // xbox connected
        return true;
    }

    /**
     * Runs the Xbox Controller Driver
     */
    /*package*/ void runDriver() throws UnsupportedOperationException {
        listenToXbox();
    }

    /**
     * Listens to XboxController's inputs
     */
    private void listenToXbox() {

        // 0.0 <= thumbs' output value <= 1.0
        // values <= 0.5 are ignored (reduced to 0)
        xc.setLeftThumbDeadZone(0.2);
        xc.setRightThumbDeadZone(1.0);  // NOT USING RIGHTTHUMB

        // 0.0 <= triggers' output value <= 1.0
        // values <= 0.2 are ignored (reduced to 0)
        xc.setLeftTriggerDeadZone(0.2);
        xc.setRightTriggerDeadZone(0.2);

        // default all values so that these listeners don't take in
        // past values
        defaultAllFields();

        // Register callbacks
        // implementing methods of the XboxControllerListener Interface
        xc.addXboxControllerListener(new XboxControllerAdapter() {

            // implement all required functions
            public void leftTrigger(double value) {
                leftVibrate = (int)(MAX_VIBRATE_VALUE * value * value);
                xc.vibrate(leftVibrate, rightVibrate);
            }

            public void rightTrigger(double value) {
                rightVibrate = (int)(MAX_VIBRATE_VALUE * value * value);
                xc.vibrate(leftVibrate, rightVibrate);
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
                moveBot();
            }

            public void rightThumbMagnitude(double magnitude) {
                rightMag = magnitude;
            }

            public void leftThumbDirection(double direction) {
                leftDir = direction;
            }

            public void rightThumbDirection(double direction) {
                rightDir = direction;
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
     * Exit the driver
     */
    /*package*/ void stopDriver() {
        stopListeningToXbox();
    }

    /**
     * Stops listening to XboxController's inputs
     */
    private void stopListeningToXbox() {
        // just do nothing on inputs :P
        xc.addXboxControllerListener(new XboxControllerAdapter());
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
            mbXboxEventHandler.leftThumbMove(leftMag, leftDir);
        }
        defaultAllFields();
    }
}