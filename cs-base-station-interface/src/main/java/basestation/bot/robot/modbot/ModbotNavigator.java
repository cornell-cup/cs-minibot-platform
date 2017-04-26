package basestation.bot.robot.modbot;

import basestation.BaseStation;
import basestation.vision.VisionCoordinate;

/**
 * Controller for moving the bot to specified coordinates. Requires that the bot
 * is linked in an active VisionSystem. The navigator works by receiving a destination, rotating toward that destination,
 * and driving toward it in a loop. The navigator is meant as a proof of concept and can be used as a starting point
 * for more advanced navigators.
 */
public class ModbotNavigator {

    private BaseStation myStation;
    private VisionCoordinate destination;
    private final ModBot myBot;
    private boolean destinationReached;

    private static final double DISTANCE_THRESHOLD = 0.04;
    private static final double ANGLE_THRESHOLD = 10;
    private static final int MAX_SPEED = 20;
    private static final int MIN_SPEED = 10;


    /**
     * Initialize the controller without any active commands
     *
     * @param myBot       the modbot this navigator is meant to control
     */
    public ModbotNavigator(ModBot myBot) {
        this.myStation = BaseStation.getInstance();
        this.myBot = myBot;
        this.destinationReached = true;
        NavigationManager myNavigationManager = new NavigationManager(this);
        myNavigationManager.start();
    }

    /**
     * @return true if the bot has reached its last set destination and has been sent a stop command
     */
    public boolean destinationReached() {
        return destinationReached;
    }

    /**
     * Sets the destination for the bot. The navigator will then make an effort to reach that destination using bot commands.
     *
     * @param dest A visioncoordinate representing the (x,y) desired. Angle currently ignored.
     */
    public void setDestination(VisionCoordinate dest) {
        this.destination = dest;
        destinationReached = false;
        calcRoute();
    }

    /**
     * A sign-independent mod to be compatible with java
     *
     * @param a The left argument of the mod
     * @param n The number to mod by
     * @return the positive mod result
     */
    private double mod(double a, double n) {
        return a - Math.floor(a / n) * n;
    }

    /**
     * Calculates the next necessary action to reach the desired destination and sends
     * the action to the bot. Sends the stop command once destination is reached.
     * Route calculation is done by rotating until the bot's angle is within ANGLE_THRESHOLD of the angle to the
     * destination. Then the bot moves forward until it is within DISTANCE_THRESHOLD of the destination.
     * Speed is slowed when close to the destination, but MAX_SPEED when far.
     */
    private void calcRoute() {

        if (destinationReached) return;
        System.out.println("calc");
        VisionCoordinate vc = myStation.getVisionManager().getBotCoordinate(myBot);
        if (vc == null) {
            (myBot.getCommandCenter()).stop();
            return;
        }

        // Calculate angle and distance to destination
        double spectheta = vc.getThetaOrZero();
        double toAngle = vc.getAngleTo(destination); // 360 scale TODO: Convert to radians
        double angle = mod((toAngle - spectheta + 180), 360) - 180;
        double dist = vc.getDistanceTo(destination);


        // Navigation decision
        if (dist > DISTANCE_THRESHOLD) {
            if (Math.abs(angle) > ANGLE_THRESHOLD) {
                // Need to rotate to face destination

                // Calculate angular speed
                double angSpeed = MIN_SPEED;
                if (Math.abs(angle) > 20) {
                    angSpeed = MIN_SPEED + (MAX_SPEED - MIN_SPEED) * (Math.abs(angle) - 20) / 160.;
                }

                if (angSpeed > MAX_SPEED) angSpeed = MAX_SPEED;

                // Rotate in proper direction
                if (angle > 0) {
                    myBot.getCommandCenter().counterClockwise(angSpeed);
                } else {
                    myBot.getCommandCenter().clockwise(angSpeed);
                }
            } else {
                if (dist > DISTANCE_THRESHOLD) {
                    // Facing destination, need to move forward

                    // Calculate Forward speed
                    double speed = MIN_SPEED;
                    if (dist > 0.2) {
                        speed = MIN_SPEED + (MAX_SPEED - MIN_SPEED) * (dist - 0.2) * 3;
                    }

                    if (speed > MAX_SPEED) speed = MAX_SPEED;

                    // Move forward
                    myBot.getCommandCenter().forward(speed);
                } else {
                    myBot.getCommandCenter().stop();
                    destinationReached = true;
                }
            }
        } else {
            myBot.getCommandCenter().stop();
            destinationReached = true;
        }
    }

    /**
     * Used to call calcRoute at a roughly regular rate. Java has better mechanisms
     * for rate-reliable scheduling, so this should be replaced in the future.
     * SleepDuration may be set with a custom contructor for more precise navigation.
     */
    private class NavigationManager extends Thread {

        ModbotNavigator parent;
        private int sleepDurationMillis;

        NavigationManager(ModbotNavigator parent) {
            this.parent = parent;
            this.sleepDurationMillis = 200;
        }

        // Custom sleep duration constructor
        NavigationManager(ModbotNavigator parent, int sleepDurationMillis) {
            this.parent = parent;
            this.sleepDurationMillis = sleepDurationMillis;
        }

        public void run() {
            while (true) {
                parent.calcRoute();
                try {
                    Thread.sleep(sleepDurationMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
