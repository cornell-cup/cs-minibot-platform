package simulator;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import simulator.physics.PhysicalObject;

/**
 * Created by Administrator on 11/15/2016.
 */
public class SimModBot extends PhysicalObject {
    public static final double MAX_SPEED = 0.655;
    // 0.655 m/s, the measured top speed (03/17)

    //The amount of radians a robot with radius 0.5 m changes by when rotating for an arc length of 1 m
    public static final double MAX_ANGULAR_SPEED = Math.PI;//4.18879;// 1 revolution every 2 seconds

    public SimModBot(String name, int id, World world, float xSpeed, float
            ySpeed, float xPos, float yPos, int angle, boolean isDynamic) {
        //constructor just creates a physicalObject with the specified params
        super(name, id, world, xSpeed, ySpeed, xPos, yPos, angle, true);
    }

    /**
     * @param percent Percentage from 0 to 100
     */
    public void forward(double percent) {
        assert (percent <= 100 && percent >= 0);
        float new_speed = (float) (MAX_SPEED * 0.01 * percent);
        float angle = this.getBody().getAngle();
        float newX = (float) (new_speed * Math.cos(angle));
        float newY = (float) (new_speed * Math.sin(angle));
        this.getBody().setLinearVelocity(new Vec2(newX, newY));
        this.getBody().setAngularVelocity(0.0f);
    }

    public void backward(double percent) {
        assert (percent <= 100 && percent >= 0);
        float new_speed = (float) (-MAX_SPEED * 0.01 * percent);
        float angle = this.getBody().getAngle();
        float newX = (float) (new_speed * Math.cos(angle));
        float newY = (float) (new_speed * Math.sin(angle));
        this.getBody().setLinearVelocity(new Vec2(newX, newY));
        this.getBody().setAngularVelocity(0.0f);
    }

    public void left(double percent) { //currently this does the same thing as a CCW turn
        counterClockwise(percent);
    }

    public void right(double percent) { //currently this does the same thing as a CW turn
        clockwise(percent);
    }

    public void clockwise(double percent) {
        this.getBody().setLinearVelocity(new Vec2(0.0f, 0.0f));
        this.getBody().setAngularVelocity((float) (-MAX_ANGULAR_SPEED));

    }

    public void counterClockwise(double percent) {
        this.getBody().setLinearVelocity(new Vec2(0.0f, 0.0f));
        this.getBody().setAngularVelocity((float) (MAX_ANGULAR_SPEED));
    }


}
