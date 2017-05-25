package examples.gobot;

import basestation.BaseStation;
import basestation.bot.commands.FourWheelMovement;
import basestation.vision.VisionCoordinate;
import basestation.vision.VisionObject;
import minibot.BaseHTTPInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * GoBot is an extension to the base minibot. It can either be controlled by a human user
 * or by the AI algorithm to race around a course.
 */
public class GoBot extends Thread {

    public static final int WAITING = 0;
    public static final int HUMAN_PLAYING = 1;
    public static final int BOT_PLAYING = 2;
    public static final int AITYPE_BASIC = 0;
    public static final int AITYPE_ADVANCED = 1;
    private final AIUtil ai;
    private final Navigator navigator;
    public double driveAngle_prev = 0;
    public double driveAngle_prevp = 0;
    FourWheelMovement fwm;
    int index;
    private int numLaps;
    private Course course;
    private int lapsDone;
    private boolean reachedMiddle;
    private boolean crossedLapLine;
    private long startTime;
    private ArrayList<Long> lapTimes;
    private boolean inTrack;
    private int botState;
    private int lastBotState;
    private float humanTime;
    private float botTime;
    private int aiType;

    /**
     * This constructor initializes the default fields of a gobot.
     */
    public GoBot() {
        this.numLaps = 1;
        this.lapsDone = 0;
        this.crossedLapLine = false;
        this.reachedMiddle = false;
        this.startTime = 0L;
        this.inTrack = true;
        this.lapTimes = new ArrayList<>();
        this.course = new Course();
        this.botState = WAITING; //waiting state
        this.lastBotState = -1;
        this.ai = new AIUtil(5, 0, Math.PI);
        this.fwm = fwm;
        this.navigator = new Navigator();
        this.aiType = AITYPE_BASIC;
        navigator.start();
    }

    /**
     * This constructor initializes a gobot with arguments passed in for numLaps and aiType
     *
     * @param numLaps represents the number of laps that must be completed to finish the race
     * @param aiType  0 means that the gobot AI will run off the simple algorithm, 1 will be advanced algorithm
     */
    public GoBot(int numLaps, int aiType) {
        this();
        this.numLaps = numLaps;
        this.aiType = aiType;
    }

    /**
     * Sets the course field for the minibot
     *
     * @param c
     */
    public void setCourse(Course c) {
        this.course = c;
        this.ai.course = c;
    }

    /**
     * @return the start time for the race
     */
    public long getStartTime() {
        return this.startTime;
    }

    /**
     * Sets the start time for the race
     *
     * @param time
     */
    public void setStartTime(long time) {
        this.startTime = time;
    }

    /**
     * @return returns the number of laps completed for the race
     */
    public int getLapsDone() {
        return this.lapsDone;
    }

    /**
     * lapTimes stores the NanoTime that the bot crossed the finish line at, and convertNanoTimeLapsToSeconds
     * converts all the nanoTimes in the list to seconds
     *
     * @return ArrayList of how long each lap took to complete in seconds
     */
    public ArrayList<Double> convertNanoTimeLapsToSeconds() {
        ArrayList<Double> lapTimeInSec = new ArrayList<>();
        for (int i = 0; i < lapTimes.size(); i++) {
            if (i == 0) {
                lapTimeInSec.add((double) (lapTimes.get(0) - getStartTime()) / 1000000000);
            } else {
                lapTimeInSec.add((double) (lapTimes.get(i) - lapTimes.get(i - 1)) / 1000000000);
            }
        }
        return lapTimeInSec;
    }

    /**
     * @return string representation of printing out lap times in seconds
     */
    public String lapTimes() {
        ArrayList<Double> lapTimeInSec = convertNanoTimeLapsToSeconds();
        String output = "";
        for (int i = 0; i < lapTimeInSec.size(); i++) {
            output += "Lap " + (i + 1) + ": " + lapTimeInSec.get(i) + "seconds\n";
        }
        return output;
    }

    /**
     * @return the total time the race took in seconds
     */
    public float totalTime() {
        float sum = 0;
        for (int i = 0; i < lapTimes.size(); i++) {
            if (i == 0) {
                sum += lapTimes.get(i) - this.startTime;
            } else
                sum += (lapTimes.get(i) - lapTimes.get(i - 1));
        }
        return sum / 1000000000;
    }

    /**
     * Sets the bot state (human or AI)
     *
     * @param state
     */
    public void setBotState(int state) {
        this.botState = state;
    }

    /**
     * Prints the bot state
     */
    private void printState() {
        System.out.printf("HUMAN: %s, AI: %s", humanTime, botTime);
    }

    /**
     * Runs the goBot (human or AI) on the course
     */
    @Override
    public void run() {
        this.fwm = (FourWheelMovement)
                BaseStation
                        .getInstance().getBotManager().getAllTrackedBots()
                        .iterator().next().getCommandCenter();

        while (true) {
            if (lastBotState != botState) {
                this.startTime = System.nanoTime();
            }
            lastBotState = botState;
            if (botState == HUMAN_PLAYING) {
                if (lapsDone >= numLaps) {
                    // Finished!
                    botState = WAITING;
                    humanTime = totalTime();
                    lapTimes.clear();
                    lapsDone = 0;
                    printState();
                } else {
                    List<VisionObject> vl = BaseStation.getInstance().getVisionManager()
                            .getAllLocationData();
                    if (vl.size() != 0) {
                        VisionCoordinate vc = vl.get(0).coord;
                        if (this.course.isInsideTrack(vc)) {
                            inTrack = true;
                            if (course.getStartArea().contains(vc.x, vc.y)) {
                                if (!crossedLapLine && !reachedMiddle) {
                                    this.crossedLapLine = true;
                                } else if (crossedLapLine && reachedMiddle) {
                                    this.lapsDone++;
                                    this.reachedMiddle = false;
                                    this.lapTimes.add(System.nanoTime());
                                }
                            } else if (course.getMiddleArea().contains(vc.x, vc.y)) {
                                if (!reachedMiddle) {
                                    this.reachedMiddle = true;
                                }
                            }
                        } else {
                            System.out.println("Out of bounds");
                            inTrack = false;
                        }
                    }
                }
            } else if (botState == BOT_PLAYING) { //ASSUMING CCL TRACK
                if (lapsDone >= numLaps) {
                    // Finished!
                    botState = WAITING;
                    botTime = totalTime();
                    lapTimes.clear();
                    lapsDone = 0;
                    printState();
                } else if (aiType == AITYPE_BASIC) {
                    List<VisionObject> vl = BaseStation.getInstance().getVisionManager()
                            .getAllLocationData();
                    if (vl.size() != 0) {
                        VisionCoordinate vc = vl.get(0).coord;
                        inTrack = this.course.isInsideTrack(vc);
                        if (navigator.destinationReached()) {
                            //int max = BaseHTTPInterface.innerTrackCoords
                            // .size(); for tracing inner track
                            int max = BaseHTTPInterface.advancedAI.size();
                            if (max != 0) {
                                //navigator.goToDestination(BaseHTTPInterface
                                // .innerTrackCoords.get(index)); for tracing
                                // inner track
                                navigator.goToDestination(BaseHTTPInterface
                                        .advancedAI.get(index));
                                System.out.println(navigator.destination);
                                index = (index + 1) % max;
                            }
                        }
                        if (course.getStartArea().contains(vc.x, vc.y)) {
                            if (!crossedLapLine && !reachedMiddle) {
                                this.crossedLapLine = true;
                            } else if (crossedLapLine && reachedMiddle) {
                                this.lapsDone++;
                                this.reachedMiddle = false;
                                this.lapTimes.add(System.nanoTime());
                                System.out.println(lapTimes());
                            }
                        } else if (course.getMiddleArea().contains(vc.x, vc.y)) {
                            if (!reachedMiddle) {
                                this.reachedMiddle = true;
                            }
                        }
                    }
                } else {
                    navigator.run();
                }

            } else {
                return;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class Navigator extends Thread {
        private static final double DISTANCE_THRESHOLD = 0.08;
        private static final double ANGLE_THRESHOLD = Math.PI / (10);
        private static final int MAX_SPEED = 100;
        private static final int MIN_SPEED = 50;
        private boolean destinationReached;
        private VisionCoordinate destination;

        @Override
        public void run() {
            while (true) {
                calcRoute();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean destinationReached() {
            return destination == null || destinationReached;
        }

        public void goToDestination(VisionCoordinate vc) {
            this.destination = vc;
            destinationReached = false;
        }

        private double mod(double a, double n) {
            return a - Math.floor(a / n) * n;
        }

        public void calcRoute() {
            if (fwm == null) return;
            VisionCoordinate vc;
            List<VisionObject> locs = BaseStation.getInstance()
                    .getVisionManager
                            ().getAllLocationData();

            if (locs.size() == 0 || locs.get(0) == null) {
                vc = null;
                fwm.setWheelPower(0, 0, 0, 0);
                return;
            } else {
                vc = locs.get(0).coord;
            }

            if (aiType == AITYPE_BASIC) {
                if (destinationReached()) return;
                if (destination == null) {
                    return;
                }
                double spectheta = vc.getThetaOrZero();
                double toAngle = vc.getAngleTo(destination);
                double angle = mod((toAngle - spectheta + Math.PI), 2 * Math.PI);
                double dist = vc.getDistanceTo(destination);
                if (!inTrack) return;
                else {
                    if (dist > DISTANCE_THRESHOLD) {
                        if (Math.abs(angle) > ANGLE_THRESHOLD) {
                            // Need to rotate to face destination

                            // Calculate angular speed
                            double angSpeed = MIN_SPEED;
                            if (Math.abs(angle) > Math.toRadians(20)) {
                                angSpeed = MIN_SPEED + (MAX_SPEED - MIN_SPEED) *
                                        (Math.abs(angle) - Math.toRadians(20)) /
                                        Math.toRadians(160.);
                            }

                            if (angSpeed > MAX_SPEED) angSpeed = MAX_SPEED;

                            // Rotate in proper direction
                            if (angle < 0) {
                                //turn CCW
                                fwm.setWheelPower(angSpeed,
                                        -angSpeed, angSpeed, -angSpeed);
                            } else {
                                //turn CW
                                fwm.setWheelPower(-angSpeed,
                                        angSpeed, -angSpeed, angSpeed);
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
                                fwm.setWheelPower(speed, speed,
                                        speed, speed);
                            } else {
                                fwm.setWheelPower(0, 0, 0, 0);
                                destinationReached = true;
                            }
                        }
                    } else {
                        fwm.setWheelPower(0, 0, 0, 0);
                        destinationReached = true;
                    }
                }
            } else {
                double driveAngle = ai.calculateDriveAngle();
                double MIDDLE = Math.PI / 2;
                double QUARTER = MIDDLE - MIDDLE * .1;
                double THREEQUARTER = MIDDLE + MIDDLE * .1;
                int POWER = 70;
                if (driveAngle_prev - driveAngle_prevp < 0.001 && driveAngle_prev - driveAngle < 0.001) {
                    fwm.setWheelPower(POWER, POWER, 70, 70);
                } else if (driveAngle < QUARTER) {
                    fwm.setWheelPower(POWER, -POWER, POWER, -POWER);
                } else if (driveAngle < THREEQUARTER) {
                    fwm.setWheelPower(POWER, POWER, POWER, POWER);
                } else {
                    fwm.setWheelPower(-POWER, POWER, -POWER, POWER);
                }
                driveAngle_prevp = driveAngle_prev;
                driveAngle_prev = driveAngle;
            }
        }
    }
}
