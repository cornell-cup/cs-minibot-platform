package examples.gobot;

import basestation.BaseStation;
import basestation.vision.VisionCoordinate;

import java.util.ArrayList;

public class GoBot {
    private Course course = new Course();
    private int lapsDone;
    private boolean reachedMiddle;
    private boolean crossedLapLine;
    private long timer;
    private ArrayList<Long> lapTimes;

    public GoBot() {
        this.lapsDone = 0;
        this.crossedLapLine = false;
        this.reachedMiddle = false;
        this.timer = 0L;
    }

    public void setCourse(Course c) {
        this.course = c;
    }

    public void setTimer(long time) {
        this.timer = time;
    }

    public long getTime() {
        return this.timer;
    }

    public int getLapsDone() {
        return this.lapsDone;
    }

    public void addLapTime(Long lt) {
        lapTimes.add(lt);
    }

    public long getLastLapTime() {
        if(lapTimes.size()>0) {
            return lapTimes.get(lapTimes.size()-1);
        } else {
            return 0L;
        }
    }

    public boolean finishedLap() {
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
    }
}
