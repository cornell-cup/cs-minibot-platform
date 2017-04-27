package simulator;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import simulator.physics.PhysicalObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by CornellCup on 4/25/2017.
 */
public class SandboxOccupancyMatrix {

    public static void main(String[] args) {
        float xSpeed = 0.0f, ySpeed = 0.0f;
        float xPos = 1.0f, yPos = 3.0f;
        float width = 10.0f, height = 2.0f;
        World w = new World(new Vec2(0.0f, 0.0f));
        Vec2 gravity = new Vec2(0.0f, 0.0f);
        w.setGravity(gravity);


        //dfdfdf
        BodyDef polygon=new BodyDef();
        polygon.type=BodyType.DYNAMIC;
        polygon.position.set(xPos,yPos); //

        PolygonShape poly =new PolygonShape();
        poly.setAsBox(width/2.0f,height/2.0f); //
        //fixture def
        FixtureDef polyfixture = new FixtureDef();
        polyfixture.shape=poly;

        //creating actual body
//        Body polybody;
//        polybody=w.createBody(polygon);
//        polybody.createFixture(polyfixture);


        //CREATING CIRCLE
        BodyDef circlebd = new BodyDef();
        circlebd.position.set(xPos - 6.0f, yPos - 6.0f);
        circlebd.type = BodyType.DYNAMIC;

        CircleShape cs = new CircleShape();
        cs.setRadius(4.5f);

        FixtureDef circlefd = new FixtureDef();
        circlefd.shape = cs;
        Body circlebody = w.createBody(circlebd);
        circlebody.createFixture(circlefd);

        //WHAT
        BodyDef triangledef=new BodyDef();
        triangledef.type=BodyType.DYNAMIC;
        triangledef.position.set(0.0f,0.0f); //

        PolygonShape weirdPoly = new PolygonShape();
        Vec2 [] vertices = new Vec2[3];
        vertices[0] = new Vec2(0.0f, 0.0f);
        vertices[1] = new Vec2(6.0f, 0.0f);
        vertices[2] = new Vec2(0.0f, 6.0f);
        weirdPoly.set(vertices, 3);
        FixtureDef weirdFixtureDef = new FixtureDef();
        weirdFixtureDef.shape = weirdPoly;
        Body trianglebody = w.createBody(triangledef);
        trianglebody.createFixture(weirdFixtureDef);



        final int[][] occupancyMatrix = new int[20][20];

        for(int i = -10; i < 9; i++){
            for(int j = -10; j < 9; j++) {
                Vec2 lowerVertex = new Vec2((float)(i), (float)(j));
                Vec2 upperVertex = new Vec2((float)(i+1), (float)(j+1));
                AABB currentSquare = new AABB(lowerVertex, upperVertex);
                final Vec2 middle = new Vec2(i + 0.5f, j + 0.5f);
                final int icopy = i;
                final int jcopy = j;
                QueryCallback callback = new QueryCallback() {
                    @Override
                    public boolean reportFixture(Fixture fixture) {
                        if(fixture.testPoint(middle)) {
                            occupancyMatrix[icopy+10][jcopy+10] = 1;
                        }
                        return false;
                    }
                };
                w.queryAABB(callback, currentSquare);
            }
        }

        for (int j = 0; j < occupancyMatrix.length; j++) {
            for (int i = 0; i < occupancyMatrix[j].length; i++) {
                System.out.print(occupancyMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }


}