package simulator.physics; /**
 * Physical Object can be any object in the simulator including modbots, walls, and obstacles.
 */
//import graphics.*;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.FixtureDef;

import java.awt.geom.Rectangle2D;

public class PhysicalObject {
    private final String name;
    private Body body;
    private int id;
    private World world;

    public PhysicalObject(String name, int id, World world, float xSpeed, float ySpeed, float xPos, float yPos, boolean isDynamic) {
        this.name = name;
        this.id = id;

        BodyDef testbody = new BodyDef();
        testbody.position.set(new Vec2(xPos, yPos));
        testbody.type = BodyType.DYNAMIC;
        Vec2 testvec = new Vec2(xSpeed, ySpeed);
        testbody.linearVelocity = testvec;
        //testbody.linearDamping = 0.1f;

        CircleShape cs = new CircleShape();
        cs.m_radius = 2.0f;

        FixtureDef testFixture = new FixtureDef();
        testFixture.shape = cs;
        testFixture.density = 0.5f;

        this.world = world;
        Body b = world.createBody(testbody);
        b.createFixture(testFixture);
        Vec2 gravity = new Vec2(0.0f, 0.0f);
        world.setGravity(gravity);
        this.body = b;

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

    public World getWorld() {
        return world;
    }
    public void setSpeed(float x, float y) {
        this.body.setLinearVelocity(new Vec2(x,y));
    }


    /**
     *
     * @return the object's x-coordinate
     */
    public double getX() {
        return this.body.getPosition().x;
    }

    public double getY() {
        return this.body.getPosition().y;
    }

    public double getAngle() {
        return this.body.getAngle();
    }

    /**
     * @return the object's JBox2D body
     */
    public Body getBody() { return body;}

    //change all instances of rectangle to Rectangle2D/whichever allows for doubles



    /**
     *
     * @return a string of the PhysicalObject
     */
    public String toString() {
        return "Name: " + this.getName().toString();
    }
}
