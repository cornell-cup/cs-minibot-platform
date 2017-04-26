package examples.gobot;

import basestation.BaseStation;
import basestation.vision.VisionCoordinate;

import java.util.ArrayList;

public class GoBot extends Thread {

    private final int NUM_LAPS = 3;
    private Course course;
    private int lapsDone;
    private boolean reachedMiddle;
    private boolean crossedLapLine;
    private long startTime;
    private ArrayList<Long> lapTimes;
    private boolean inTrack;

    public GoBot() {
        this.lapsDone = 0;
        this.crossedLapLine = false;
        this.reachedMiddle = false;
        this.startTime = 0L;
        this.inTrack = true;
        this.lapTimes = new ArrayList<>();
        this.course = new Course();
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

    public String printLapTimes() {
        ArrayList<Double> lapTimeInSec = convertNanoTimeLapsToSeconds();
        String output = "";
        for(int i = 0; i < lapTimeInSec.size(); i++) {
            output+= "Lap " + (i+1) + ": " + lapTimeInSec.get(i) + "seconds\n";
        }
        return output;
    }

    /*public boolean finishedLap() {
        VisionCoordinate vc = BaseStation.getInstance().getVisionManager()
                .getAllLocationData().get(0).coord;
        if(course.getStartArea().contains(vc.x,vc.y)) {
            if(!crossedLapLine && !reachedMiddle) {
                this.crossedLapLine = true;
            } else if(crossedLapLine && reachedMiddle) {
                this.lapsDone++;
                this.reachedMiddle = false;
                return true;
            }
            return false;
        } else if(course.getMiddleArea().contains(vc.x,vc.y)) {
            if(!reachedMiddle) {
                this.reachedMiddle = true;
            }
            return false;
        } else {
            return false;
        }
    }*/

    @Override
    public void run() {
        //this.setTimer(System.nanoTime());
        while (true) {
            VisionCoordinate vc = BaseStation.getInstance().getVisionManager()
                    .getAllLocationData().get(0).coord;
            if (this.course.isInsideTrack(vc)) {
                if(course.getStartArea().contains(vc.x,vc.y)) {
                    if(!crossedLapLine && !reachedMiddle) {
                        this.crossedLapLine = true;
                    } else if(crossedLapLine && reachedMiddle) {
                        this.lapsDone++;
                        this.reachedMiddle = false;
                        this.lapTimes.add(System.nanoTime());
                        System.out.println(printLapTimes());
                    }
                } else if(course.getMiddleArea().contains(vc.x,vc.y)) {
                    if(!reachedMiddle) {
                        this.reachedMiddle = true;
                    }
                }
            } else {
                //do stuff with timer later to tell to get back
                System.out.println("go back inside pls");
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //grab current time
        //check if bot is in bounds/finished lap/crossed start or middle area

    }
}
