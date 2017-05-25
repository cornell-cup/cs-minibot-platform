package xboxhandler.inputeventhandler;

/**
 * =============================================================================
 * XboxInputEventHandler.java
 * =============================================================================
 * Cornell Cup Robotics Team 2016-2017
 * =============================================================================
 */

/**
 * The interface XboxInputEventHandler specifies basic functions for
 * implementing
 * MiniBot movements
 */
abstract class XboxInputEventHandler {

    public abstract void dpadMove(int dpadVal);

    public abstract void leftThumbMove(double magnitude, double direction);

    public abstract void rightThumbMove(double magnitude, double direction);

    public abstract void leftTriggerAction(double value);

    public abstract void rightTriggerAction(double value);

    public abstract void leftShoulderAction(boolean pressed);

    public abstract void rightShoulderAction(boolean pressed);

    public abstract void buttonAAction(boolean pressed);

    public abstract void buttonBAction(boolean pressed);

    public abstract void buttonXAction(boolean pressed);

    public abstract void buttonYAction(boolean pressed);

    public abstract void buttonBackAction(boolean pressed);

    public abstract void buttonStartAction(boolean pressed);

}
