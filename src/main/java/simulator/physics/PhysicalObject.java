package simulator.physics; /**
 * Physical Object can be any object in the simulator including modbots, walls, and obstacles.
 */
//import graphics.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class PhysicalObject {
    private final String name;
    private Body body;
    private int id;

    public PhysicalObject(String name, int id, World world, float xSpeed, float ySpeed, float xPos, float yPos, boolean isDynamic) {
        this.name = name;
        this.id = id;
        BodyDef bd = new BodyDef();
        bd.type = isDynamic ? BodyType.DYNAMIC : BodyType.STATIC;
        bd.position = (new Vec2(xPos, yPos));
        bd.linearVelocity = (new Vec2(xSpeed, ySpeed));
        this.body = new Body(bd, world);
    }

    /**
     *
     * @return the object's name
     */
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
    }



    /**
     *
     * @return the object's id
     */
    public int getID() {
        return id;
    }


    public float getXVelocity() {
        Vec2 speed = this.body.getLinearVelocity();
        return speed.x;
    }

    public float getYVelocity() {
        Vec2 speed = this.body.getLinearVelocity();
        return speed.y;
    }

    public void setSpeed(float x, float y) {
        this.body.setLinearVelocity(new Vec2(x,y));
    }


    /**
     *
     * @return the object's x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * @return the object's JBox2D body
     */
    public Body getBody() { return body;}

    //change all instances of rectangle to Rectangle2D/whichever allows for doubles

    /**
     * Updates the object's velocity and position based off it's acceleration and velocity
     */
    public void move () {
        //If the angular velocity is 0, then the robot is moving linearly
        if (this.getPhysics().getAngularVel()==0) {

            //Find x and y components of acceleration and add to velocity
            double vel_x = (((this.getPhysics().getAcceleration() / simulator.Simulator.STEPS_PER_SECOND) *
                    Math.cos(this.getPhysics().getDirection()))  +
                    (this.getPhysics().getXVelocity() / simulator.Simulator.STEPS_PER_SECOND));
            double vel_y = (((this.getPhysics().getAcceleration() / simulator.Simulator.STEPS_PER_SECOND) *
                    Math.sin(this.getPhysics().getDirection())) +
                    (this.getPhysics().getYVelocity() / simulator.Simulator.STEPS_PER_SECOND));

            double new_vel = this.getPhysics().getSpeed();

            //Change the rectangle coordinates to move it
            ((Rectangle2D.Double) this.getShape()).setRect(((Rectangle2D.Double) this.getShape()).getX() + vel_x,
                    ((Rectangle2D.Double) this.getShape()).getY() + vel_y,
                    ((Rectangle2D.Double) this.getShape()).getHeight(),
                    ((Rectangle2D.Double) this.getShape()).getWidth());

            this.x = this.getX() + vel_x;
            this.y = this.getY() + vel_y;

            //Change velocity and momentum in simulator.physics.Physics of this object
            this.setPhysics(new Physics(this.getPhysics().getMass(), new_vel, this.getPhysics().getDirection(),
                    this.getPhysics().getAcceleration(), this.getPhysics().getStaticFriction(),
                    this.getPhysics().getDynamicFriction(), this.getPhysics().getForce(),
                    this.getPhysics().getMass() * new_vel, this.getPhysics().getAngularVel()));
        }
        //If the angular velocity is not 0, then the robot is spinning
        else{
            double new_ang = (((this.getPhysics().getAngularVel() / simulator.Simulator.STEPS_PER_SECOND) +
                    this.getPhysics().getDirection()) % (2*Math.PI));

            this.setPhysics(new Physics(this.getPhysics().getMass(), this.getPhysics().getSpeed(),new_ang,
                    this.getPhysics().getAcceleration(), this.getPhysics().getStaticFriction(),
                    this.getPhysics().getDynamicFriction(), this.getPhysics().getForce(),
                    this.getPhysics().getMomentum(), this.getPhysics().getAngularVel()));

        }
    }




    /**
     *
     * @return a string of the PhysicalObject
     */
    public String toString() {
        return "Name: " + this.getName().toString();
    }
}
