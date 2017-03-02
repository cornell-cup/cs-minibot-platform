package minibot;

/**=====================================================================================================================
 * XboxControllerFile.java
 * Documentation on http://www.aplu.ch/home/apluhomex.jsp?site=36 | WWW.APLU.CH IS THE SHIT
 * =====================================================================================================================
 * Created by Trevor Edwards and Anmol Kabra
 * Cornell Cup Robotics Team 2016-2017
 * =====================================================================================================================
 * Requirements:
 *      - Hardware:
 *          - XboxController (you're kidding me?)
 *      - Libraries:
 *          - jaw.jar               --- java wrapper class for C++ code (Xbox drivers are written in C++)
 *          - XboxController.jar    --- our class derives interfaces and abstract classes from here
 *          - xboxcontroller64.dll  --- some funky Windows_x64 stuff
 *          tl,dr;  Add these libraries to the build path!
 * =====================================================================================================================
 * Important Instructions:  (in no specific order/priority)
 *      - Whenever exiting the program, ensure calling the release() method of the XboxController instance
 *      - Check if the XboxController is connected, if not don't execute anything else
 *      - Call setLeftThumbDeadZone, setRightThumbDeadZone, because the thumbs on the controller are super-sensitive
 *      - print is to vibrate as IDE debug is to debugging the controller
 *      - [OPTIONAL] Call setLeftTriggerDeadZone, setRightTriggerDeadZone: if you need the triggers, call these
 * =====================================================================================================================
 */

/**=====================================================================================================================
 * CONCISE DOCUMENTATION
 * =====================================================================================================================
 * Class XboxController
 *      - Methods
 *          - isConnected()
 *          - release()
 *          - setLeftThumbDeadZone(), setRightThumbDeadZone()
 *          - setLeftTriggerDeadZone(), setRightTriggerDeadZone()
 *          - vibrate()
 * =====================================================================================================================
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
 * =====================================================================================================================
 * Class XboxControllerAdapter implements XboxControllerListener
 * =====================================================================================================================
 */

import ch.aplu.xboxcontroller.*;
import javax.swing.JOptionPane;

/** An instance of XBXExample reads input from the XboxController and forwards [NOT IMPLEMENTED] the commands to the GUI
 */
public class XBXExample {

    private XboxController xc;
    private int leftVibrate;        // in 0..65535
    private int rightVibrate;       // in 0..65535
    private double leftMag;         // in 0..1
    private double rightMag;        // in 0..1
    private double leftDir;         // in 0..360 -- 0 is North, angles increase clockwise
    private double rightDir;        // in 0..360 -- 0 is North, angles increase clockwise
    private int dpadVal;            // in 0..7 -- 0 is North, 8 directions increase clockwise (N, NE, E, ... , NW)

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

        // thumbs are extremely sensitive, thus mark 0.5th of the thumbs as dead
        // need to push the thumbs beyond 0.5th of their coverage to move the robot
        xc.setLeftThumbDeadZone(0.5);
        xc.setRightThumbDeadZone(0.5);

        // 0.0 <= triggers' output value <= 1.0
        // values <= 0.2 are ignored (reduced to 0)
        xc.setLeftTriggerDeadZone(0.2);
        xc.setRightTriggerDeadZone(0.2);

        // Register callbacks, implementing methods of the XboxControllerListener Interface
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
            }

            public void rightThumbMagnitude(double magnitude) {
                rightMag = magnitude;
                System.out.println("RightMag: " + magnitude);
            }

            public void leftThumbDirection(double direction) {
                leftDir = direction;
                System.out.println("LeftDir: " + direction);
            }

            public void rightThumbDirection(double direction) {
                rightDir = direction;
                System.out.println("RightDir: " + direction);
            }

            public void dpad(int direction, boolean pressed) {
                dpadVal = direction;
                System.out.println("DpadDir: " + direction);
            }

        });

        JOptionPane.showMessageDialog(null,
                "Xbox controller connected.\n" +
                        "Press all buttons to test (the console prints values), Ok to quit.",
                "XboxController MiniBot",
                JOptionPane.PLAIN_MESSAGE);

        xc.release();
        System.exit(0);
    }

    public static void main(String[] args) {
        new XBXExample();
    }
}