package simulator;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import simulator.physics.PhysicalObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by CornellCup on 4/25/2017.
 */
public class SandboxOccupancyMatrix {

    public static void main(String[] args) {
        float xSpeed = 0.0f, ySpeed = 0.0f;
        float xPos = 0.0f, yPos = 0.0f;
        float width = 10.0f, height = 2.0f;
        World w = new World(new Vec2(0f, 0f));
        Vec2 gravity = new Vec2(0.0f, 0.0f);
        w.setGravity(gravity);


        //dfdfdf
        BodyDef polygon=new BodyDef();
        polygon.type=BodyType.DYNAMIC;
        polygon.position.set(xPos,yPos); //


        PolygonShape poly =new PolygonShape();
        poly.setAsBox(width/2,height/2); //
        //fixture def
        FixtureDef polyfixture = new FixtureDef();
        polyfixture.shape=poly;

        //creating actual body
        Body polybody;
        polybody=w.createBody(polygon);
        polybody.createFixture(polyfixture);


        System.out.print(w.getBodyCount());
        Body b = w.getBodyList();
        float centerX = b.getPosition().x;
        System.out.println(centerX);
        float centerY = b.getPosition().y;
        System.out.println(centerY);
        float wid = b.getPosition().x;
        float startX = centerX - width/2;

        System.out.println("startX = " + startX);
        Vec2 bottom = b.getFixtureList().getAABB(0).lowerBound;
        Vec2 top = b.getFixtureList().getAABB(0).upperBound;

        System.out.println(bottom.x + ", " + bottom.y);
        System.out.println(top.x + ", " + top.y);


        double detailCoefficient = 10;

    }


}
