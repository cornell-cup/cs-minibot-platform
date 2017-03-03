package minibot;

/**=============================================================================
 * XboxControllerFile.java
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
 *      - motorPower order: --- (fl, fr, bl, br) ---
 *      = moveDirection: 0=forward; 1=right-forward or CW; 2=left-forward or
 *      CCW; 3=backward
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

/**=============================================================================
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

/** An instance of XBXExample reads input from the XboxController and forwards
 * [NOT IMPLEMENTED] the commands to the GUI
 */
public class XBXExample {

    // private Handler x;
    private XboxController xc;
    private int leftVibrate;        // in 0..65535
    private int rightVibrate;       // in 0..65535
    private double leftMag;         // in 0..1
    private double rightMag;        // in 0..1
    private double leftDir;         // in 0..360 -- 0 is North, angles increase
        // clockwise
    private double rightDir;        // in 0..360 -- 0 is North, angles increase
        // clockwise
    private int dpadVal = -1;       // in -1..7 -- 0 is North, 8 directions
        // increase clockwise (N, NE, E, ... , NW). -1 is default
    private static final double MAX_MOTOR_POW = 100.0;

    /** Constructor: runs all xbox functions
     */
    private XBXExample() {

        xc = new XboxController();

        if (!xc.isConnected()) {
            // if Xbox is not connected, pop up a dialog box
            JOptionPane.showMessageDialog(null,
                    "Xbox controller not connected.",
                    "Fatal error",
                    JOptionPane.ERROR_MESSAGE);
            xc.release();
            return;
        }

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
                leftVibrate = (int)(65535 * value * value);
                xc.vibrate(leftVibrate, rightVibrate);
                // System.out.println("LeftTrigger: " + value);
            }

            public void rightTrigger(double value) {
                rightVibrate = (int)(65535 * value * value);
                xc.vibrate(leftVibrate, rightVibrate);
                // System.out.println("RightTrigger: " + value);
            }

            public void buttonA(boolean pressed) {
                leftVibrate = 65535;
                rightVibrate = 65535;
                xc.vibrate(leftVibrate, rightVibrate);
            }

            public void buttonB(boolean pressed) {
                leftVibrate = 65535;
                rightVibrate = 65535;
                xc.vibrate(leftVibrate, rightVibrate);
            }

            public void buttonX(boolean pressed) {
                leftVibrate = 65535;
                rightVibrate = 65535;
                xc.vibrate(leftVibrate, rightVibrate);
            }

            public void buttonY(boolean pressed) {
                leftVibrate = 65535;
                rightVibrate = 65535;
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

        xc.release();
        System.exit(0);
    }

    /** assigns default values to all fields
     */
    private void defaultAllFields() {
        dpadVal = -1;
        leftDir = 0.0;
        rightDir = 0.0;
        leftMag = 0.0;
        rightMag = 0.0;
    }

    /** convert degrees to radians
     * Precondition: degree >= 0
     * @param degree angle measure in degrees
     * @return degree converted to radians
     */
    private double degToRad(double degree) {
        assert degree >= 0;
        return ((int)(degree) % 360) * Math.PI / 180.0;
    }

    /** convert leftThumb's directions (in angles) to forward, CW, CCW or
     * backward
     * @return Thumb directions converted to moveDirection values in
     */
    private int moveDirLeftThumb() {
        if (leftDir < 67.5 || leftDir > 292.5) {
            // forward
            return 0;
        } else if (leftDir < 112.5) {
            // right - forward or CW
            return 1;
        } else if (leftDir < 247.5) {
            // backward
            return 3;
        } else if (leftDir <= 292.5) {
            // left - forward or CCW
            return 2;
        }
        return -1;
    }

    /**
     * convert dpad's directions to forward, CW, CCW or
     * backward
     * @return Thumb directions converted to moveDirection values
     */
    private int moveDirDpad() {
        switch(dpadVal) {
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

    /** moves the bot forward, CW, CCW or backward depending on the input
     * from dpad (priority) or the leftThumb
     */
    public synchronized void move() {
        // move based on input from either dpadVal or leftThumb
        if (dpadVal != -1) {
            // dpad is pressed
            switch (moveDirDpad()) {
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
        } else if (leftMag > 0.0) {
            // leftThumb is moved
            double pow = leftMag * MAX_MOTOR_POW;
            switch (moveDirLeftThumb()) {
                case 0:
                    // move forward
                    // Handler.sendMotors(pow, pow, pow, pow)
                    System.out.println("forward");
                    break;
                case 1:
                    // move right - forward or CW
                    // Handler.sendMotors(pow, -pow, pow, -pow)
                    System.out.println("right - forward");
                    break;
                case 2:
                    // move left - forward or CCW
                    // Handler.sendMotors(-pow, pow, -pow, pow)
                    System.out.println("left - forward");
                    break;
                case 3:
                    // move backward
                    // Handler.sendMotors(-pow, -pow, -pow, -pow)
                    System.out.println("backward");
                    break;
                case -1:
                    System.out.println("no movement");
            }
        }
        // System.out.println("DEFAULTEDDDDDD");
        defaultAllFields();
    }


    public static void main(String[] args) {
        new XBXExample();
    }
}