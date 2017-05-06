package examples.gobot;

import basestation.BaseStation;
import basestation.bot.commands.FourWheelMovement;
import basestation.vision.VisionCoordinate;
import basestation.vision.VisionObject;
import minibot.BaseHTTPInterface;

import java.util.ArrayList;
import java.util.List;

public class GoBot extends Thread {

    private final int NUM_LAPS = 1;
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
    public static final int WAITING = 0;
    public static final int HUMAN_PLAYING = 1;
    public static final int BOT_PLAYING = 2;

    private double min;
    private double max;
    private int numLines;
//    private static final int MAX_SPEED = 80;
//    private static final int MIN_SPEED = 15;

    private final Navigator navigator;
    FourWheelMovement fwm;
    int index;

    public GoBot() {
        this.lapsDone = 0;
        this.crossedLapLine = false;
        this.reachedMiddle = false;
        this.startTime = 0L;
        this.inTrack = true;
        this.lapTimes = new ArrayList<>();
        this.course = new Course();
        this.botState = WAITING; //waiting state
        this.lastBotState = -1;
        this.numLines = 5;
        this.min = 0+.01;
        this.max = Math.PI-0.01;

        this.fwm = fwm;
        this.navigator = new Navigator();
        navigator.start();
    }

    public void setCourse(Course c) {
        this.course = c;
    }

    public void setStartTime(long time) {
        this.startTime = time;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public int getLapsDone() {
        return this.lapsDone;
    }

    public ArrayList<Double> convertNanoTimeLapsToSeconds() {
        ArrayList<Double>lapTimeInSec = new ArrayList<>();
        for(int i = 0; i < lapTimes.size();i++) {
            if(i == 0) {
                lapTimeInSec.add((double)(lapTimes.get(0)-getStartTime())/1000000000);
            } else {
                lapTimeInSec.add((double)(lapTimes.get(i)-lapTimes.get(i-1))/1000000000);
            }
        }
        return lapTimeInSec;
    }

    public String lapTimes() {
        ArrayList<Double> lapTimeInSec = convertNanoTimeLapsToSeconds();
        String output = "";
        for(int i = 0; i < lapTimeInSec.size(); i++) {
            output+= "Lap " + (i+1) + ": " + lapTimeInSec.get(i) + "seconds\n";
        }
        return output;
    }

    public float totalTime() {
        float sum = 0;
        for(int i = 0; i < lapTimes.size(); i++) {
            if(i == 0) {
                sum+= lapTimes.get(i) - this.startTime;
            } else
            sum += (lapTimes.get(i) - lapTimes.get(i-1));
        }
        return sum/1000000000;
    }

    public void setBotState(int state) {
        this.botState = state;
    }

    private void printState() {
        System.out.printf("HUMAN: %s, AI: %s", humanTime, botTime);
    }

    class Point{
        private double xcor;
        private double ycor;

        Point(double x, double y){
            xcor = x;
            ycor = y;
        }
    }

    //Find equation of line between two points
    class Equation{

        //in terms of y
        private double slope;
        private double yIntercept;

        //special case: vertical lines
        private boolean vertical;
        private double xval;

        Equation(VisionCoordinate start, VisionCoordinate end){
            if (start.x == end.x){
                vertical = true;
                xval = start.x;
            }
            else{
                slope = (end.y - start.y)/(end.x - start.x);
                yIntercept = start.y - slope*start.x;
            }
        }

        Equation(VisionCoordinate start, double angle){
            if (angle == Math.PI/2){
                vertical = true;
                xval = start.x;
            }
            else{
                slope = Math.tan(angle);
                yIntercept = start.y - slope*start.x;
            }
        }

    }



    //Find intersection between two equations
    public Point intersection(Equation e1, Equation e2){
        try{
            //two vertical lines cannot intersect each other (unless they are
            // the same, which should only occur if the car is driving on the
            // boundary
            if (e1.vertical && e2.vertical){
                return null;
            }
            //If either are vertical, just plug in
            else if (e1.vertical || e2.vertical) {
                if (e1.vertical){
                    return new Point(e1.xval, e1.xval * e2.slope +
                            e2.yIntercept);
                }
                else{
                    return new Point(e2.xval, e2.xval * e1.slope +
                            e1.yIntercept);
                }

            }
            else {
                double x = (e2.yIntercept - e1.yIntercept)/(e1.slope -
                        e2.slope);
                double y = e1.slope * x + e1.yIntercept;
                return new Point(x, y);
            }
        }

        //no intersection
        catch (Exception alan) {
            alan.printStackTrace();
        }
        return null;
    }

    //Find all equations between points in order
    public ArrayList<Equation> findEquations(ArrayList<VisionCoordinate> cors){
        int size = cors.size();
        ArrayList<Equation> eqs = new ArrayList<Equation>();
        for (int i = 0; i < size; i++){
            eqs.add(new Equation(cors.get(i), cors.get((i+1)%size)));
        }
        return eqs;
    }

    //find intersections to target equation
    public ArrayList<Point> findIntersection(ArrayList<Equation> e, Equation target){
        int size = e.size();
        ArrayList<Point> pts = new ArrayList<Point>();
        for (int i = 0; i < size; i++){
          Point interPoint = intersection(e.get(i), target);
          if (interPoint != null){
              pts.add(interPoint);
          }
        }
        return pts;
    }

    //Find distance between current location of bot and an intersection point
    public double distanceBetween(VisionCoordinate vc, Point p){
        double dx = vc.x - p.xcor;
        double dy = vc.y - p.ycor;
        return Math.sqrt(dx*dx + dy*dy);
    }

    //return a list of all equations to sweep across
    public ArrayList<Equation> sweep(VisionCoordinate vc, int numLines){
        ArrayList<Equation> sweepLines = new ArrayList<>();
        for (int i =0; i < numLines; i ++){
            sweepLines.add(new Equation(vc, vc.getThetaOrZero() + i *
                    intervalAngle() - (max - min)/2));
        }
        return sweepLines;
    }

    //return the angle between sweep lines
    public double intervalAngle(){
        return (max - min)/(numLines - 1);
    }

    public ArrayList<Double> allAngles(){
        ArrayList<Double> angles = new ArrayList<>();
        for(int i = 0; i < numLines; i++){
            angles.add(min + i*intervalAngle());
        }
        return angles;
    }

    //determine angle between positive x axis to point
    public double pointAngle(VisionCoordinate origin, Point target){
//        double botAngle = origin.getThetaOrZero();
        boolean isVertical = origin.x - target.xcor == 0;
        if (!isVertical){
            double deltaY = target.ycor - origin.y;
            double deltaX = target.xcor - origin.x;
            return Math.atan2(deltaY,deltaX);
//            if (deltaY >= 0 && deltaX >= 0){
//                return Math.atan(slope);
//            }
//            else if (deltaY >= 0 && deltaX <= 0){
//                return Math.atan(slope) + Math.PI;
//            }
//            else if (deltaY <= 0 && deltaX <= 0){
//                return Math.atan(slope) + Math.PI;
//            }
//            else{
//                return Math.atan(slope) + 2*Math.PI;
//            }
        }
        else{
            if (target.ycor > origin.y){
                return Math.PI;
            }
            else{
                return 3*Math.PI;
            }
        }
    }

    //return if point is valid
    public boolean isValid(double pAngle, VisionCoordinate vc, double
            sweepTotalAngle, double
            sweepAngle){
        return Math.abs(pAngle - (vc.getThetaOrZero() - (sweepTotalAngle/2) +
                sweepAngle)) < 0.1; // TODO MAGIC ### -t revor
    }


    public ArrayList<Double> calculateRayDistances(ArrayList<Equation> allLines,
                                                   ArrayList<Equation> sweepLines,
                                                   VisionCoordinate botCoordinate){
        ArrayList<Double> distances = new ArrayList<>();
        for (int i = 0; i < sweepLines.size(); i++){
            double minDist = Double.MAX_VALUE / 100f;
            for (Point intersectionPoint: findIntersection(allLines, sweepLines.get(i))){
                if (isValid(pointAngle(botCoordinate, intersectionPoint), botCoordinate, max - min, i*intervalAngle()
                )) {
//                    System.err.println("WOAOW");
                    double dist = distanceBetween(botCoordinate, intersectionPoint);
                    if (dist < minDist) {
                        minDist = dist;
                    }
                }
            }
            distances.add(minDist);
        }
        return distances;
    }

    public double AiMaybe(){
        List<VisionObject> v1 = BaseStation.getInstance().getVisionManager()
                .getAllLocationData();
        if (v1.size() != 0){
            VisionCoordinate pos = v1.get(0).coord;
            System.out.println("Current pos: " + pos + "\n");
            ArrayList<Equation> sweepEquations = sweep(pos, numLines);
            for(Equation f : sweepEquations){
                System.out.println("Y=" + f.slope + "x + " + f.yIntercept );
            }
            ArrayList<VisionCoordinate> inner = course.getInner().returnCoords();
            ArrayList<VisionCoordinate> outer = course.getOuter()
                    .returnCoords();
            ArrayList<Equation> innerEqs = findEquations(inner);
            ArrayList<Equation> outerEqs = findEquations(outer);
            innerEqs.addAll(outerEqs);
            ArrayList<Double> important = calculateRayDistances(innerEqs, sweepEquations,
                    pos);
            important.forEach(System.out::println);
            double sumDistances = 0;
            for(double d: important){
                sumDistances += d;
            }
            double theta = max - min;
            double out = 0;
            for(int i = 0; i < numLines; i++){
                out += theta/(numLines-1)*i*(important.get(i))/(sumDistances);
            }
            System.out.println("Angle: " + (out+(Math.PI-theta)/2));
            return out + (Math.PI - theta)/2;

        }
        return 0;
    }

    @Override
    public void run() {
        //this.setTimer(System.nanoTime());
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
                if (lapsDone >= NUM_LAPS) {
                    // Finished!
                    botState = WAITING;
                    humanTime = totalTime();
                    lapTimes.clear();
                    lapsDone = 0;
                    printState();
                } else {
                    List<VisionObject> vl =  BaseStation.getInstance().getVisionManager()
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
                                    System.out.println(lapTimes());
                                }
                            } else if (course.getMiddleArea().contains(vc.x, vc.y)) {
                                if (!reachedMiddle) {
                                    this.reachedMiddle = true;
                                }
                            }
                        } else {
                            //do stuff with timer later to tell to get back
                            System.out.println("go back inside pls");
                            inTrack = false;
                        }
                    }
                }
            }
            else if (botState == BOT_PLAYING) { //ASSUMING CCL TRACK
                if (lapsDone >= NUM_LAPS) {
                    // Finished!
                    botState = WAITING;
                    botTime = totalTime();
                    lapTimes.clear();
                    lapsDone = 0;
                    printState();
                } else {
                    List<VisionObject> vl =  BaseStation.getInstance().getVisionManager()
                            .getAllLocationData();
                    if (vl.size() != 0) {
                        VisionCoordinate vc = vl.get(0).coord;
                        inTrack = this.course.isInsideTrack(vc);
                        if (navigator.destinationReached()) {
                            int max = BaseHTTPInterface.innerTrackCoords.size();
                            if (max != 0) {
                                navigator.goToDestination(BaseHTTPInterface.innerTrackCoords.get
                                        (index));
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
                }

            } else {
                //System.err.println("UNKNOWN STATE");
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class Navigator extends Thread {
        private boolean destinationReached;
        private VisionCoordinate destination;

        private static final double DISTANCE_THRESHOLD = 0.08;
        private static final double ANGLE_THRESHOLD = Math.PI/(10);
        private static final int MAX_SPEED = 100;
        private static final int MIN_SPEED = 50;

        @Override
        public void run() {
            while (true) {
                calcRoute();
                try {
                    Thread.sleep(3000);
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

        private double mod(double a, double n){
            return a - Math.floor(a/n)*n;
        }

        public void calcRoute(){
            if (destinationReached()) return;
            if(destination == null){
                return;
            }

            VisionCoordinate vc;
            List<VisionObject> locs = BaseStation.getInstance()
                    .getVisionManager
                            ().getAllLocationData();

            if (locs.size() == 0 || locs.get(0) == null){
                vc = null;
                fwm.setWheelPower(0,0,0,0);
                return;

            }
            else {
                vc = locs.get(0).coord;
                //System.out.println(vc);
            }

            double spectheta = vc.getThetaOrZero();
            double toAngle = vc.getAngleTo(destination);
            double angle = mod((toAngle - spectheta + Math.PI), 2*Math.PI) -
                    Math.PI;
            double dist = vc.getDistanceTo(destination);

            // driver

            if (!inTrack) return;
            if (true) {
                double driveAngle = AiMaybe();
                double MIDDLE = Math.PI / 2;
                double QUARTER = MIDDLE - MIDDLE * .5;
                double THREEQUARTER = MIDDLE + MIDDLE * .5;
                if (driveAngle < QUARTER) {
                    fwm.setWheelPower(100,-100,100,-100);
                } else if (driveAngle < THREEQUARTER) {
                    fwm.setWheelPower(100,100,100,100);
                } else {
                    fwm.setWheelPower(-100,100,-100,100);
                }
            } else {
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
                            fwm.setWheelPower(-angSpeed,
                                    angSpeed,-angSpeed,angSpeed);
                            //System.out.println("turn CCW");

                        } else {
                            fwm.setWheelPower(angSpeed,
                                    -angSpeed,angSpeed,-angSpeed);
                            //System.out.println("turn CW");
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
}
