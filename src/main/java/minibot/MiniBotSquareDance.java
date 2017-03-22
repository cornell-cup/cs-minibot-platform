package minibot;

import basestation.BaseStation;
import basestation.bot.commands.FourWheelMovement;
import basestation.bot.robot.minibot.MiniBot;
import basestation.vision.VisionCoordinate;

/**
 * Created by CornellCup on 3/18/2017.
 *
 * TO RUN:
 * Run this code
 * Start vision system
 * RUn TCP.py
 * add bot to GUI
 * runSquareDance
 *
 */
public class MiniBotSquareDance extends Thread {

    private final Navigator navigator;
    FourWheelMovement fwm;
    boolean d1;

    public MiniBotSquareDance(FourWheelMovement fwm) {
        this.fwm = fwm;
        this.navigator = new Navigator();
        navigator.start();
    }

    @Override
    public void run() {
        // Planning
        while (true) {
            if (navigator.destinationReached()) {
                if (d1) {
                    navigator.goToDestination(new VisionCoordinate(-1.6,
                            -0.64));
                } else {
                    navigator.goToDestination(new VisionCoordinate(-0.3,
                            -0.64));
                }
                d1 = !d1;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private class Navigator extends Thread {
        private boolean destinationReached;
        private VisionCoordinate destination;

        private static final double DISTANCE_THRESHOLD = 0.04;
        private static final double ANGLE_THRESHOLD = 10;
        private static final int MAX_SPEED = 20;
        private static final int MIN_SPEED = 10;

        @Override
        public void run() {
            System.out.println("RUNNING");
            while (true) {
                calcRoute();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean destinationReached() {
            return destination == null || destinationReached;
        }

        public void goToDestination(VisionCoordinate vc) {
            System.out.println("GO TO DESTINATION");
            this.destination = vc;
            destinationReached = false;
        }

        private double mod(double a, double n){
            return a - Math.floor(a/n)*n;
        }

        public void calcRoute(){
            if (destinationReached()) return;
            if(destination == null){
                System.out.println("destination is null" );

                return;
            }

            VisionCoordinate vc;
            if (BaseStation.getInstance().getVisionManager
                    ().getAllLocationData().get(0) == null){
                System.out.println("location data is null" );

                vc = null;
                    fwm.setWheelPower(0,0,0,0);
                    return;

            }
            else {
                vc = BaseStation.getInstance().getVisionManager
                        ().getAllLocationData().get(0).coord;
                System.out.println(vc);
            }

            double spectheta = vc.getThetaOrZero();
            double toAngle = vc.getAngleTo(destination);
            double angle = mod((toAngle - spectheta + 180), 360) - 180;
            double dist = vc.getDistanceTo(destination);
            System.out.println("angle"+ angle);

            if (dist > DISTANCE_THRESHOLD) {
                System.out.println("yes");
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
                        System.out.println("Turn CCW");
                        fwm.setWheelPower(-angSpeed,
                                angSpeed,-angSpeed,angSpeed);

                    } else {
                        System.out.println("Turn CW");
                        fwm.setWheelPower(angSpeed,
                                -angSpeed,angSpeed,-angSpeed);
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
                        fwm.setWheelPower(0,0,0,0);
                        destinationReached = true;
                    }
                }
            } else {
                fwm.setWheelPower(0,0,0,0);
                destinationReached = true;
            }
        }


        }
    }

