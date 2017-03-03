package minibot;

/**
 * =============================================================================
 * InputEventHandler.java
 * =============================================================================
 * Created by Anmol Kabra
 * Cornell Cup Robotics Team 2016-2017
 * =============================================================================
 */

/**
 * The interface InputEventHandler specifies basic functions for implementing
 * MiniBot movements
 */
/*package*/ interface InputEventHandler {

    void dpadMove(int dpadVal);

    void leftThumbMove(double magnitude, double direction);
    void rightThumbAction(double magnitude, double direction);

    void leftTriggerAction(double value);
    void rightTriggerAction(double value);

    void leftShoulderAction(boolean pressed);
    void rightShoulderAction(boolean pressed);

    void buttonAAction(boolean pressed);
    void buttonBAction(boolean pressed);
    void buttonXAction(boolean pressed);
    void buttonYAction(boolean pressed);

    void buttonBackAction(boolean pressed);
    void buttonStartAction(boolean pressed);

}
