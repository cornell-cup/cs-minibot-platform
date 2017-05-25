package simulator;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import simulator.simbot.ShortestPathGenerator;
import simulator.simbot.ShortestPathGenerator.Node;

import java.util.LinkedList;


/**
 * THIS FILE IS PURELY FOR TESTING PURPOSES
 */
public class SandboxOccupancyMatrix extends Thread {

    public final static float occupancyMatrixBoxSize = 0.50f; //Size of box in meters
    public static int occupancyMatrixWidth = 40; //Number of boxes
    public static int occupancyMatrixHeight = 40; // Number of boxes

    public static void main(String[] args) throws Exception {
        float xSpeed = 0.0f, ySpeed = 0.0f;
        float xPos = 1.0f, yPos = 3.0f;
        float width = 10.0f, height = 2.0f;


        BodyDef polygon = new BodyDef();
        polygon.type = BodyType.STATIC;
        polygon.position.set(xPos, yPos);

        PolygonShape poly = new PolygonShape();
        poly.setAsBox(width / 2.0f, height / 2.0f);

        FixtureDef polyfixture = new FixtureDef();
        polyfixture.shape = poly;

        //Initializing world
        World w = new World(new Vec2(0.0f, 0.0f));
        Vec2 gravity = new Vec2(0.0f, 0.0f);
        w.setGravity(gravity);

        //Creating a circle
        BodyDef circlebd = new BodyDef();
        circlebd.position.set(4.0f, 8.0f);
        circlebd.type = BodyType.STATIC;

        CircleShape cs = new CircleShape();
        cs.setRadius(6.2f);
        FixtureDef circlefd = new FixtureDef();
        circlefd.shape = cs;

        //Adding circle to world
        Body circlebody = w.createBody(circlebd);
        circlebody.createFixture(circlefd);

        //WHAT
        BodyDef triangledef = new BodyDef();
        triangledef.type = BodyType.STATIC;
        triangledef.position.set(15.0f, 20.0f); //

        PolygonShape weirdPoly = new PolygonShape();
        Vec2[] vertices = new Vec2[3];
        vertices[0] = new Vec2(1.0f, 1.0f);
        vertices[1] = new Vec2(15.0f, 0.0f);
        vertices[2] = new Vec2(0.0f, 20.0f);
        weirdPoly.set(vertices, 3);
        FixtureDef weirdFixtureDef = new FixtureDef();
        weirdFixtureDef.shape = weirdPoly;
        Body trianglebody = w.createBody(triangledef);
        trianglebody.createFixture(weirdFixtureDef);


        BodyDef triangledef2 = new BodyDef();
        triangledef2.type = BodyType.STATIC;
        triangledef2.position.set(16.0f, -2.0f); //

        PolygonShape weirdPoly2 = new PolygonShape();
        Vec2[] vertices2 = new Vec2[3];
        vertices2[0] = new Vec2(3.0f, -2.0f);
        vertices2[1] = new Vec2(18.0f, -1.0f);
        vertices2[2] = new Vec2(0.0f, 15.0f);
        weirdPoly2.set(vertices2, 3);
        FixtureDef weirdFixtureDef2 = new FixtureDef();
        weirdFixtureDef2.shape = weirdPoly2;
        Body trianglebody2 = w.createBody(triangledef2);
        trianglebody2.createFixture(weirdFixtureDef2);


        final int[][] occupancyMatrix = new int[occupancyMatrixHeight][occupancyMatrixWidth];

        for (int i = 0; i < occupancyMatrixHeight; i++) {
            for (int j = 0; j < occupancyMatrixWidth; j++) {
                Vec2 lowerVertex = new Vec2((float) (i), (float) (j));
                Vec2 upperVertex = new Vec2((float) (i + occupancyMatrixBoxSize), (float) (j + occupancyMatrixBoxSize));
                AABB currentSquare = new AABB(lowerVertex, upperVertex);
                final Vec2 middle = new Vec2(i, j);
                final int icopy = i;
                final int jcopy = j;
                QueryCallback callback = new QueryCallback() {
                    @Override
                    public boolean reportFixture(Fixture fixture) {
                        if (fixture.testPoint(middle)) {
                            occupancyMatrix[icopy][jcopy] = 1;
                        }
                        return true;
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
//        int[][] skinnedOccupancyMatrix = new int[occupancyMatrix.length-2][occupancyMatrix.length-2];
//        for(int i = 0; i < skinnedOccupancyMatrix.length; i++) {
//            for(int j = 0; j < skinnedOccupancyMatrix[0].length; j++) {
//                skinnedOccupancyMatrix[i][j] = occupancyMatrix[i+1][j+1];
//            }
//        }
//        for (int j = 0; j < skinnedOccupancyMatrix.length; j++) {
//            for (int i = 0; i < skinnedOccupancyMatrix[j].length; i++) {
//                System.out.print(skinnedOccupancyMatrix[i][j] + " ");
//            }
//            System.out.println();
//        }


        System.out.println(ShortestPathGenerator.answer(occupancyMatrix));

//        for (int j = 0; j < path.length; j++) {
//            for (int i = 0; i < path[j].length; i++) {
//                System.out.print(path[i][j] + " ");
//            }
//            System.out.println();
//        }

        Node targetNode = ShortestPathGenerator.targetNode;

        LinkedList<Node> path = ShortestPathGenerator.getPath(targetNode);
        int[][] pathMatrix = new int[occupancyMatrix.length + 1][occupancyMatrix[0].length + 1];


        for (int i = 0; i < path.size(); i++) {
            //System.out.println("X:" + path.get(i).getX() + " Y:" + path.get(i).getY());
            pathMatrix[path.get(i).getX()][path.get(i).getY()] = 1;
        }

        for (int j = 1; j < pathMatrix.length; j++) {
            for (int i = 1; i < pathMatrix[j].length; i++) {
                System.out.print(pathMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}



















