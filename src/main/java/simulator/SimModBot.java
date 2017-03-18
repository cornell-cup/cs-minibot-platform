package simulator;

import org.jbox2d.dynamics.World;
import simulator.physics.PhysicalObject;
import org.jbox2d.common.Vec2;
/**
 * Created by Administrator on 11/15/2016.
 */
public class SimModBot extends PhysicalObject {
    public static final double MAX_SPEED = 10; // 10 m/s
    //The amount of radians a robot with radius 0.5 m changes by when rotating for an arc length of 1 m
    public static final double MAX_ANGULAR_SPEED = 0.0418879*2;//4.18879;// 1 revolution every 1.5 seconds

    public SimModBot(String name, int id, World world, float xSpeed, float ySpeed, float xPos, float yPos, boolean isDynamic) {
        super(name, id, world, xSpeed, ySpeed, 1f, 1f, true);
    }

    /**
     *
     * @param percent Percentage from 0 to 100
     */
    public void forward(double percent) {
        assert(percent <= 100 && percent >= 0);
        float new_x = (float)(MAX_SPEED * 0.1 * percent);
        float currYSpeed = this.getYVelocity();
        this.getBody().setLinearVelocity(new Vec2(new_x, currYSpeed));
    }

    public void backward(double percent) {
        assert(percent <= 100 && percent >= 0);
        float new_x = (float)(-MAX_SPEED * 0.1 * percent);
        float currYSpeed = this.getYVelocity();
        this.getBody().setLinearVelocity(new Vec2(new_x, currYSpeed));
    }

    public void left(double percent) {
        assert(percent <= 100 && percent >= 0);
        float new_y = (float)(MAX_SPEED * 0.1 * percent);
        float currXSpeed = this.getXVelocity();
        this.getBody().setLinearVelocity(new Vec2(currXSpeed, new_y));
    }

    public void right(double percent) {
        assert(percent <= 100 && percent >= 0);
        float new_y = (float)(MAX_SPEED * 0.1 * percent);
        float currXSpeed = this.getXVelocity();
        this.getBody().setLinearVelocity(new Vec2(currXSpeed, new_y));
    }

    public void clockwise(double percent) {
        //this.getPhysics().setAngularVelocity(-MAX_ANGULAR_SPEED*0.01*percent);

    }

    public void counterClockwise(double percent) {
        //this.getPhysics().setAngularVelocity(MAX_ANGULAR_SPEED*0.01*percent);
    }


}
