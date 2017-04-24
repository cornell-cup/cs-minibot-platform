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
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.FixtureDef;

import java.awt.geom.Rectangle2D;

public class PhysicalObject {
    private final String name;
    private Body body;
    private int id;
    private World world;
    private int size;

    public PhysicalObject(String name, int id, World world, float xSpeed, float ySpeed, float xPos, float yPos, boolean isDynamic) {
        this.name = name;
        this.id = id;
        this.size = 0;

        BodyDef testbody = new BodyDef();
        testbody.position.set(new Vec2(xPos, yPos));
        testbody.type = BodyType.DYNAMIC;
        Vec2 testvec = new Vec2(xSpeed, ySpeed);
        testbody.linearVelocity = testvec;
        //testbody.linearDamping = 0.5f;

        Vec2[] vertices = new Vec2[4];
        vertices[0] = new Vec2(-4/16.0f,-4/16.0f);
        vertices[1] = new Vec2(4/16.0f,-4/16.0f);
        vertices[2] = new Vec2(4/16.0f,4/16.0f);
        vertices[3] = new Vec2(-4/16.0f,4/16.0f);

        PolygonShape ps = new PolygonShape();
        ps.setAsBox(5/16.0f, 5/16.0f, new Vec2(0.0f,0.0f), 0.0f);

        FixtureDef testFixture = new FixtureDef();
        testFixture.shape = ps;
        testFixture.density = 0.5f;

        this.world = world;
        Body b = world.createBody(testbody);
        b.createFixture(testFixture);
        Vec2 gravity = new Vec2(0.0f, 0.0f);
        world.setGravity(gravity);
        this.body = b;
    }
//this one is for scenario objects
    public PhysicalObject(String name, int id, World world, float xPos, float
            yPos, int size, int angle) {
        this.name = name;
        this.id = id;
        this.size = size;

        BodyDef testbody = new BodyDef();
        testbody.position.set(new Vec2(xPos, yPos));
        testbody.angle = (float)(angle/180.0*Math.PI);
        testbody.type = BodyType.STATIC;
        testbody.linearVelocity = new Vec2(0.0f, 0.0f);;

        Vec2[] vertices = new Vec2[4];
        vertices[0] = new Vec2(-size/2.0f,-size/2.0f);
        vertices[1] = new Vec2(size/2.0f,-size/2.0f);
        vertices[2] = new Vec2(size/2.0f,size/2.0f);
        vertices[3] = new Vec2(-size/2.0f,size/2.0f);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(size/2.0f, size/2.0f, new Vec2(0.0f,0.0f), 0.0f);

        FixtureDef testFixture = new FixtureDef();
        testFixture.shape = polygonShape;
        testFixture.density = 1000.0f;

        this.world = world;
        Body b = world.createBody(testbody);
        b.createFixture(testFixture);
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

    public int getSize()
    {
        return size;
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
