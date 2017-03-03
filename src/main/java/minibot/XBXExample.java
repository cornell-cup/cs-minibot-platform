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
 *      - moveFunctions: --- (fl, fr, bl, br) ---
 *      - print is to vibrate as IDE debug is to debugging the controller
 *      - [OPTIONAL] Call setLeftTriggerDeadZone, setRightTriggerDeadZone: if
 *          you need the triggers, call these
 * =============================================================================
 * How to Use the Xbox Controller:
 * ONLY DPAD AND LEFTTHUMB MOVE THE BOT
 *      - Forward: dpad N OR leftThumb N
 *      - Backward: dpad S OR leftThumb S
 *      - Right - forward: dpad NE/E or leftThumb NE
 *      - Right - backward: dpad SE or leftThumb SE
 *      - Left - forward: dpad NW/W or leftThumb NW
 *      - Left - backward: dpad SW or leftThumb SW
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
        xc.setLeftThumbDeadZone(0.5);
        xc.setRightThumbDeadZone(0.5);

        // 0.0 <= triggers' output value <= 1.0
        // values <= 0.2 are ignored (reduced to 0)
        xc.setLeftTriggerDeadZone(0.2);
        xc.setRightTriggerDeadZone(0.2);

        defaultAllFields();

        // Register callbacks
        // implementing methods of the XboxControllerListener Interface
        xc.addXboxControllerListener(new XboxControllerAdapter() {

            // implement all functions
            public void leftTrigger(double value) {
                leftVibrate = (int)(65535 * value * value);
                xc.vibrate(leftVibrate, rightVibrate);
                System.out.println("LeftTrigger: " + value);
            }

            public void rightTrigger(double value) {
                rightVibrate = (int)(65535 * value * value);
                xc.vibrate(leftVibrate, rightVibrate);
                System.out.println("RightTrigger: " + value);
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
                System.out.println("LeftMag " + magnitude);

                move();
            }

            public void rightThumbMagnitude(double magnitude) {
                rightMag = magnitude;
                System.out.println("RightMag: " + magnitude);

                // move(); -- removed moving functionality from rightThumb
            }

            public void leftThumbDirection(double direction) {
                leftDir = direction;
                System.out.println("LeftDir: " + direction);

                move();
            }

            public void rightThumbDirection(double direction) {
                rightDir = direction;
                System.out.println("RightDir: " + direction);

                // move(); -- removed moving functionality from rightThumb
            }

            public void dpad(int direction, boolean pressed) {
                dpadVal = direction;
                System.out.println("DpadDir: " + direction);

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
     *
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

    /** convert dpadVal, leftThumb and rightThumb directions to moveDirection
     * values --- priority to directions from dpad, then leftThumb and
     * rightThumb --- if different directions are given to both thumbs, no
     * movement
     * @return Thumb directions converted to moveDirection values in -1..7
     * (-1 for no movement)
     */
    private int moveDir() {
        if ((dpadVal == 0 || dpadVal == 1 || dpadVal == 7) ||
                (leftDir < 67.5 || leftDir > 292.5)) {
            // forward
            return 0;
        } else if ((dpadVal == 2) ||
                (leftDir < 112.5)) {
            // right - forward or CW
            return 1;
        } else if ((dpadVal == 3 || dpadVal == 4 || dpadVal == 5) ||
                (leftDir < 247.5)) {
            // backward
            return 3;
        } else if ((dpadVal == 6) ||
                (leftDir <= 292.5)) {
            // left - forward or CCW
            return 2;
        }

        /*if (rightDir > 0.0 || dpadVal != -1) {
            if ((dpadVal <= 1 || dpadVal >= 7) ||
                    (rightDir <= 90.0 || rightDir >= 270.0)) {
                // all forward related motions
                if (dpadVal == 1 || (leftDir >= 30.0 && leftDir <= 150.0)) {
                    // right - forward
                    return 1;
                }
                if (dpadVal == 7 || (leftDir >= 210.0 && leftDir <= 330.0)) {
                    // left - forward
                    return 7;
                }
                return 0;   // just forward

            } else if ((dpadVal >= 3 && dpadVal <= 5) ||
                    (rightDir > 90.0 || rightDir < 270.0)) {
                // all backward related motions
                if (dpadVal == 3 || (leftDir >= 30.0 && leftDir <= 150.0)) {
                    // right - backward
                    return 3;
                }
                if (dpadVal == 5 || (leftDir >= 210.0 && leftDir <= 330.0)) {
                    // left - backward
                    return 5;
                }
                return 4;   // just backward
            }
            if (dpadVal == 2 || (leftDir >= 30.0 && leftDir <= 150.0)) {
                // turn wheels right
                return 2;
            }
            if (dpadVal == 6 || (leftDir >= 210.0 && leftDir <= 330.0)) {
                // turn wheels left
                return 6;
            }
        }
        defaultAllFields();
        return -1;*/
    }

    public synchronized void move() {
        switch (moveDir()) {
            case 0:
                // move forward
                System.out.println("forward");
                break;
            case 1:
                // move right - forward
                System.out.println("right - forward");
                break;
            case 2:
                // turn wheels right
                System.out.println("right");
                break;
            case 3:
                // move right - backward
                System.out.println("right - backward");
                break;
            case 4:
                // move backward
                System.out.println("backward");
                break;
            case 5:
                // move left - backward
                System.out.println("left - backward");
                break;
            case 6:
                // turn wheels left
                System.out.println("left");
                break;
            case 7:
                // move left - forward
                System.out.println("left - forward");
                break;
            case -1:
                System.out.println("no movement");
        }
        // defaultAllFields();
    }


    public static void main(String[] args) {
        new XBXExample();
    }
}