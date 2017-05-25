package simulator.physics; /**
 * Physical Object can be any object in the simulator including modbots, walls, and obstacles.
 */
//import graphics.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;

// TODO: Derives location, angle from its parent
public class ParentedPhysicalObject {
    private String name;
    private Shape shape;
    private Physics physics;
    private boolean isDynamic;
    private double x;
    private double y;
    private int id;

    public ParentedPhysicalObject() {
        this.name = "";
        this.id = 0;
        this.shape = new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);
        this.physics = new Physics(0, 0, 0, 0, 0, 0, 0, 0, 0);
        this.isDynamic = true;
        this.x = 0;
        this.y = 0;
    }

    public ParentedPhysicalObject(String name, Shape shape, Physics physics, boolean isDynamic, double x, double y) {
        this.name = name;
        this.id = name.hashCode();
        this.shape = shape;
        this.physics = physics;
        this.isDynamic = isDynamic;
        this.x = x;
        this.y = y;
    }

    /**
     * @return the object's name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name becomes the object's new name, assigns a new id corresponding to new name
     */
    public void setName(String name) {
        this.name = name;
        this.id = name.hashCode();
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
     * @return the object's id
     */
    public int getID() {
        return id;
    }

    /**
     * @return the Shape of the object
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * @param shape becomes the new shape
     */
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    /**
     * @return the Physics of the object
     */
    public Physics getPhysics() {
        return physics;
    }

    /**
     * @param physics becomes the new simulator.physics of the object
     */
    public void setPhysics(Physics physics) {
        this.physics = physics;
    }

    /**
     * @return if the object can move
     */
    public boolean isDynamic() {
        return isDynamic;
    }

    /**
     * @param dynamic sets whether the object can move
     */
    public void setDynamic(boolean dynamic) {
        isDynamic = dynamic;
    }

    /**
     * @return the object's x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * @param x becomes the object's new x-coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the object's x-coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * @param y becomes the object's new x-coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @param object1 is the first object
     * @param object2 is the second object
     * @return a boolean if the two objects are touching (have collided)
     */
    public boolean areTouching(ParentedPhysicalObject object1, ParentedPhysicalObject object2) {
        if (object1.shape instanceof Rectangle2D.Double) {
            if (object2.shape instanceof Rectangle2D.Double) {
                if ((object1.shape).intersects((Rectangle2D.Double) object2.shape)) {
                    return true;
                }
            }
        }
        return false;
    }
    //change all instances of rectangle to Rectangle2D/whichever allows for doubles

    /**
     * Updates the object's velocity and position based off it's acceleration and velocity
     */
    public void move() {

        //Find x and y components of acceleration and add to velocity
        double vel_x = (this.getPhysics().getAcceleration() * Math.cos(this.getPhysics().getDirection())) + this.getPhysics().getXVelocity();
        double vel_y = (this.getPhysics().getAcceleration() * Math.sin(this.getPhysics().getDirection())) + this.getPhysics().getYVelocity();
        double new_vel = Math.sqrt(Math.pow(vel_x, 2) + Math.pow(vel_y, 2));

        //Change the rectangle coordinates to move it
        //Rectangle2D.Double r = (Rectangle2D.Double)this.getShape();
        ((Rectangle2D.Double) this.getShape()).setRect(((Rectangle2D.Double) this.getShape()).getX() + vel_x, ((Rectangle2D.Double) this.getShape()).getY() + vel_y,
                ((Rectangle2D.Double) this.getShape()).getHeight(), ((Rectangle2D.Double) this.getShape()).getWidth());

        //Change velocity and momentum in simulator.physics.Physics of this object
        this.setPhysics(new Physics(this.getPhysics().getMass(), new_vel, this.getPhysics().getDirection(),
                this.getPhysics().getAcceleration(), this.getPhysics().getStaticFriction(),
                this.getPhysics().getDynamicFriction(), this.getPhysics().getForce(),
                this.getPhysics().getMass() * new_vel, this.getPhysics().getAngularVel()));
    }

    /**
     * @param object1 is the first PhysicalObject
     * @param object2 is the second PhysicalObject
     * @return a boolean if two objects are both moving in the same direction at the same velocity
     * (have already collided/will never collide)
     */
    public boolean sameVelocity(ParentedPhysicalObject object1, ParentedPhysicalObject object2) {
        if (object1.getPhysics().getDirection() == object2.getPhysics().getDirection() &&
                object1.getPhysics().getSpeed() == object2.getPhysics().getSpeed()) return true;
        else return false;
    }

    /**
     * Checks to see if there is a collision between two objects,
     * and if so, updates the velocities and directions accordingly
     *
     * @param object2 is the PhysicalObject the current PhysicalObject is testing for collisions with
     */
    public void collisions(ParentedPhysicalObject object2) {
        return;
        /*
        if (areTouching(this, object2) && !sameVelocity(this,object2)) {
            System.out.println("Collision between objects");

            //collision with a wall/immovable object2, object1 speed and momentum become 0
            if (this.isDynamic() && !object2.isDynamic()) {
                this.setPhysics(new Physics(this.getPhysics().getMass(), 0, this.getPhysics().getDirection(),
                        this.getPhysics().getAcceleration(), this.getPhysics().getStaticFriction(),
                        this.getPhysics().getDynamicFriction(), this.getPhysics().getForce(),
                        0));
            }
            //inelastic collision with immovable object1, object2's speed and momentum become 0
            else if (!this.isDynamic() && object2.isDynamic()){
                object2.setPhysics(new Physics(object2.getPhysics().getMass(), 0, object2.getPhysics().getDirection(),
                        object2.getPhysics().getAcceleration(), object2.getPhysics().getStaticFriction(),
                        object2.getPhysics().getDynamicFriction(), object2.getPhysics().getForce(),
                        0));
            }
            else if (this.isDynamic() && object2.isDynamic()){
                //m1v1 + m2v2 = (m1+m2)vf

                //Find new x velocity
                double x_vel = ( (this.getPhysics().getMass()*this.getPhysics().getXVelocity()) +
                        (object2.getPhysics().getMass()*object2.getPhysics().getXVelocity()) ) /
                        (this.getPhysics().getMass()+object2.getPhysics().getMass());

                //Find new y velocity
                double y_vel = ( (this.getPhysics().getMass()*this.getPhysics().getYVelocity()) +
                        (object2.getPhysics().getMass()*object2.getPhysics().getYVelocity()) ) /
                        (this.getPhysics().getMass()+object2.getPhysics().getMass());

                //Find new speed
                double speed = Math.sqrt((Math.pow(x_vel,2))+Math.pow(y_vel,2));

                //Find new direction
                double angle = Math.atan(y_vel/x_vel);

                System.out.println("x_vel = " + x_vel+", y_vel = " + y_vel+", speed = " + speed+ "angle = " + angle);

                //Assign new angle and speed to both objects
                this.setPhysics(new Physics(this.getPhysics().getMass(), speed, angle,
                        this.getPhysics().getAcceleration(), this.getPhysics().getStaticFriction(),
                        this.getPhysics().getDynamicFriction(), this.getPhysics().getForce(),
                        this.getPhysics().getMomentum()));

                object2.setPhysics(new Physics(object2.getPhysics().getMass(), speed, angle,
                        object2.getPhysics().getAcceleration(), object2.getPhysics().getStaticFriction(),
                        object2.getPhysics().getDynamicFriction(), object2.getPhysics().getForce(),
                        object2.getPhysics().getMomentum()));
            }
        }
        */
    }

    /**
     * @return a string of the PhysicalObject
     */
    public String toString() {
        return "Shape: " + this.getShape().toString() + this.getPhysics().toString() +
                "\nIsDynamic: " + this.isDynamic;
    }
}
