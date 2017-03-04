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
 *          the XboxController instance
 *      - Check if the XboxController is connected, if not don't execute
 *          anything else
 *      - If an action method is triggered by 2 or more callbacks, use the
 *          'synchronized' keyword. Example, dpad and Left/RightThumbs call move
 *          use synchronized with move
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
import javax.swing.JOptionPane;

/**
 * An instance of the XboxControllerDriver class reads input from the
 * XboxController, and transfers the data to the MiniBot Handler class
 */
public class XboxControllerDriver extends Thread {

    private static final int MAX_VIBRATE_VALUE = 65535;
    private XboxController xc;
    private MiniBotXboxInputEventHandler mbXboxEventHandler;
    private String botName;         // name of the bot
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

    /**
     * Constructor: runs all xbox functions
     */
    /*package*/ XboxControllerDriver(String _botName) {

        xc = new XboxController();
        botName = _botName;
        mbXboxEventHandler = new MiniBotXboxInputEventHandler(botName);

        // testIsConnected();
        /* --- already being checked where an instance of this class is
            initialized --- if this instance's main method is called, then it
             must be using the testIsConnected method ---
        if (!xc.isConnected()) {
            // if Xbox is not connected, pop up a dialog box
            JOptionPane.showMessageDialog(null,
                    "Xbox controller not connected.",
                    "Fatal error",
                    JOptionPane.ERROR_MESSAGE);
            xc.release();
            return;
        }*/
    }

    /**
     * tests if the bot is connected, if not then terminates with an error
     * @return false if not connected, true is connected
     */
    private boolean testIsConnected() {
        if (!xc.isConnected()) {
            // if Xbox is not connected, pop up a dialog box
            JOptionPane.showMessageDialog(null,
                    "Xbox controller not connected.",
                    "Fatal error",
                    JOptionPane.ERROR_MESSAGE);
            xc.release();
            return false;
        }
        return true;
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

        defaultAllFields();

        // Register callbacks
        // implementing methods of the XboxControllerListener Interface
        xc.addXboxControllerListener(new XboxControllerAdapter() {

            // implement all required functions
            public void leftTrigger(double value) {
                leftVibrate = (int)(MAX_VIBRATE_VALUE * value * value);
                xc.vibrate(leftVibrate, rightVibrate);
                // System.out.println("LeftTrigger: " + value);
            }

            public void rightTrigger(double value) {
                rightVibrate = (int)(MAX_VIBRATE_VALUE * value * value);
                xc.vibrate(leftVibrate, rightVibrate);
                // System.out.println("RightTrigger: " + value);
            }

            public void buttonA(boolean pressed) {
                leftVibrate = MAX_VIBRATE_VALUE;
                rightVibrate = MAX_VIBRATE_VALUE;
                xc.vibrate(leftVibrate, rightVibrate);
            }

            public void buttonB(boolean pressed) {
                leftVibrate = MAX_VIBRATE_VALUE;
                rightVibrate = MAX_VIBRATE_VALUE;
                xc.vibrate(leftVibrate, rightVibrate);
            }

            public void buttonX(boolean pressed) {
                leftVibrate = MAX_VIBRATE_VALUE;
                rightVibrate = MAX_VIBRATE_VALUE;
                xc.vibrate(leftVibrate, rightVibrate);
            }

            public void buttonY(boolean pressed) {
                leftVibrate = MAX_VIBRATE_VALUE;
                rightVibrate = MAX_VIBRATE_VALUE;
                xc.vibrate(leftVibrate, rightVibrate);
            }

            public void leftThumbMagnitude(double magnitude) {
                leftMag = magnitude;
                // System.out.println("LeftMag " + magnitude);

                move();
            }

            public void rightThumbMagnitude(double magnitude) {
                rightMag = magnitude;
                // System.out.println("RightMag: " + magnitude);

                // move(); -- removed moving functionality from rightThumb
            }

            public void leftThumbDirection(double direction) {
                leftDir = direction;
                // System.out.println("LeftDir: " + direction);
            }

            public void rightThumbDirection(double direction) {
                rightDir = direction;
                // System.out.println("RightDir: " + direction);

                // move(); -- removed moving functionality from rightThumb
            }

            public void dpad(int direction, boolean pressed) {
                dpadVal = direction;
                // System.out.println("DpadDir: " + direction);

                move();
            }

        });

        JOptionPane.showMessageDialog(null,
                "Xbox controller connected.\n" +
                        "Press all buttons to test (the console prints values), " +
                        "Ok to quit.",
                "XboxController MiniBot",
                JOptionPane.PLAIN_MESSAGE);

        // stop listening
        xc.release();
        System.exit(0);
    }
    /**
     * Assigns default values to all fields
     */
    private void defaultAllFields() {
        dpadVal = -1;
        leftDir = 0.0;
        rightDir = 0.0;
        leftMag = 0.0;
        rightMag = 0.0;
    }

    /**
     * Moves the bot forward, CW, CCW or backward depending on the input
     * from dpad (priority) or the leftThumb
     */
    public synchronized void move() {
        // move based on input from either dpadVal or leftThumb
        if (dpadVal != -1) {
            // dpad is pressed
            mbXboxEventHandler.dpadMove(dpadVal);
        } else if (leftMag > 0.0) {
            // leftThumb is moved
            mbXboxEventHandler.leftThumbMove(leftMag, leftDir);
        }
        // System.out.println("DEFAULTEDDDDDD");
        defaultAllFields();
    }

    @Override
    public void run() {
        listenToXbox();
    }

    public static void main(String[] args) {
        // --- USE ONLY FOR TESTING PURPOSES ---
        XboxController testxc = new XboxController();

        if (!(testxc.isConnected())) {
            // if Xbox is not connected, pop up a dialog box
            JOptionPane.showMessageDialog(null,
                    "Xbox controller not connected.",
                    "Fatal error",
                    JOptionPane.ERROR_MESSAGE);
            testxc.release();
            return;
        }

        // xbox controller is connected
        XboxControllerDriver xcd = new XboxControllerDriver("anmol");
        xcd.listenToXbox();
    }
}