package simulator;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import simulator.physics.PhysicalObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by CornellCup on 4/25/2017.
 */
public class SandboxOccupancyMatrix extends Thread{

    //public static int[][] path;
    public static Node targetNode;
    private static HashSet<Node> settledNodes = new HashSet<Node>();
    private static HashSet<Node> unsettledNodes = new HashSet<Node>();
    private static HashMap<Node, Integer> distances;
    public static Map<Node, Node> predecessors = new HashMap<Node, Node>();

    //Fills maze with 1's
    public static int[][] initializeMaze(int n, int m) {
        int[][] maze = new int[n][m];
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < m; j++) {
                maze[i][j] = 1;
            }
        }
        //path = new int[n][m];
        return maze;
    }

    //Takes the given maze and returns that maze wrapped in 1's
    public static int[][] generateWrappedMaze(int[][] maze) {
        int height = maze.length;
        int width = maze[0].length;
        int[][] wrappedMaze = initializeMaze(height+2,width+2);
        for(int i = 1; i < height +1; i++) {
            for(int j = 1; j < width + 1; j++) {
                wrappedMaze[i][j] = maze[i-1][j-1];
            }
        }
        return wrappedMaze;
    }
    //Makes the Nodes array but doesn't fill in neighbors
    //Also sets cost to max int
    public static Node[][] initializeNodeMatrix(int[][] maze) {
        int height = maze.length;
        int width = maze[0].length;
        Node[][] res = new Node[height][width];
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                Node n = new Node(maze[i][j], false,i,j);
                if(i == height-2 && j == width-2) {
                    targetNode = n;
                }
                res[i][j] = n;
                if(maze[i][j] == 0) {
                    res[i][j].cost = Integer.MAX_VALUE;
                }
            }
        }

        res[height-2][width-2].isEnd = true;
        res[1][1].cost = 1;
        return res;
    }
    //Returns a HashSet of neighbors given a position and maze
    public static HashSet<Node> generateNeighbors(int i, int j, Node[][] maze) {
        HashSet<Node> res = new HashSet<Node>();
        if(maze[i+1][j].value == 0) {
            res.add(maze[i+1][j]);
        }
        if(maze[i][j+1].value == 0) {
            res.add(maze[i][j+1]);
        }
        if(maze[i-1][j].value == 0) {
            res.add(maze[i-1][j]);
        }
        if(maze[i][j-1].value == 0) {
            res.add(maze[i][j-1]);
        }
        return res;
    }

    //Returns an array of Nodes with the neighbors field filled out
    public static Node[][] populateNeighbors(Node[][] maze) {
        int height = maze.length;
        int width = maze[0].length;
        for(int i = 1; i < height-1; i++) {
            for(int j = 1; j < width -1; j++) {
                if(maze[i][j].value == 0) {
                    maze[i][j].neighbors = generateNeighbors(i, j, maze);
                }
            }
        }
        return maze;
    }

    public static Node getNodeWithLowestCost(HashSet<Node> unsettledNodes) {
        int lowestCost = Integer.MAX_VALUE;
        Node res = null;
        for(Node n: unsettledNodes) {
            if(n.cost < lowestCost) {
                lowestCost = n.cost;
                res = n;

            }
        }
        return res;
    }

    public static void processNeighbors(Node currentNode) {
        for(Node n: currentNode.neighbors) {
            if(!settledNodes.contains(n)) {
                int newCost = currentNode.cost + 1;
                if(newCost < n.cost) {

                    n.cost = newCost;
                    predecessors.put(n, currentNode);
                }
                unsettledNodes.add(n);
            }
        }
    }
    public static LinkedList<Node> getPath(Node target) throws InterruptedException {
        System.out.println("FUCK");
        LinkedList<Node> path = new LinkedList<Node>();
        Node step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            System.out.println("ughh null");
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            //System.out.println("while get path");
            step = predecessors.get(step);
            //System.out.println("GOT NODE:  " + step.getX() + " " + step.getY());
            path.add(step);
            Thread.sleep(50);


        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }



    public static int execute(Node[][] maze) {
        Node sourceNode = maze[1][1];
        unsettledNodes.add(sourceNode);
        Node curr = sourceNode;
        predecessors.put(curr, null);
        Node currentNode = null;
        while(!unsettledNodes.isEmpty()) {
             currentNode = getNodeWithLowestCost(unsettledNodes);

            if(currentNode.isEnd){
                System.out.println("ENDING");
                System.out.println(currentNode.getX() + ", " + currentNode.getY());
                predecessors.put(currentNode, curr);

                break;
            }
            //path[currentNode.getX()][currentNode.getY()] = 1;
            unsettledNodes.remove(currentNode);
            settledNodes.add(currentNode);
            //System.out.println("PUT");
            curr = currentNode;
            processNeighbors(currentNode);
        }
        System.out.println("LAST CURRENTNODE " + currentNode.getX() + " " + currentNode.getY());

        int width = maze.length;
        int height = maze[0].length;
        return maze[width-2][height-2].cost;
    }

    public static int answer(int[][] maze) {
        int min = Integer.MAX_VALUE;

        int[][] x = generateWrappedMaze(maze);
        Node[][] y = initializeNodeMatrix(x);
        Node[][] z = populateNeighbors(y);
        int res = execute(z);
        min = res < min ? res : min;


        return min;
    }
    public static class Node {
        private int value;
        private boolean isEnd;
        private int cost;
        private HashSet<Node> neighbors;
        private int x;
        private int y;

        public Node( int value, boolean isEnd, int x, int y) {
            this.value = value;
            this.isEnd = isEnd;
            this.x = x;
            this.y = y;
        }
        public int getX(){ return x;}
        public int getY(){ return y;}
    }

    public static void main(String[] args) throws Exception{
        float xSpeed = 0.0f, ySpeed = 0.0f;
        float xPos = 1.0f, yPos = 3.0f;
        float width = 10.0f, height = 2.0f;
        World w = new World(new Vec2(0.0f, 0.0f));
        Vec2 gravity = new Vec2(0.0f, 0.0f);
        w.setGravity(gravity);


        //dfdfdf
        BodyDef polygon=new BodyDef();
        polygon.type=BodyType.STATIC;
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
        circlebd.position.set(xPos - 5.0f, yPos - 3.0f);
        circlebd.type = BodyType.STATIC;

        CircleShape cs = new CircleShape();
        cs.setRadius(8.5f);

        FixtureDef circlefd = new FixtureDef();
        circlefd.shape = cs;
        Body circlebody = w.createBody(circlebd);
        circlebody.createFixture(circlefd);

        //WHAT
        BodyDef triangledef=new BodyDef();
        triangledef.type=BodyType.KINEMATIC;
        triangledef.position.set(-4.0f,-4.0f); //

        PolygonShape weirdPoly = new PolygonShape();
        Vec2 [] vertices = new Vec2[3];
        vertices[0] = new Vec2(-1.0f, -1.0f);
        vertices[1] = new Vec2(5.0f, 0.0f);
        vertices[2] = new Vec2(0.0f, 5.0f);
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
        System.out.println(answer(occupancyMatrix));

//        for (int j = 0; j < path.length; j++) {
//            for (int i = 0; i < path[j].length; i++) {
//                System.out.print(path[i][j] + " ");
//            }
//            System.out.println();
//        }
        if(targetNode == null) {
            System.out.println("target node is null");
        }
        else {
            System.out.println("target node is");
        }
        System.out.println(targetNode.getX() + " " + targetNode.getY());
        LinkedList<Node> path = getPath(targetNode);
        int[][] pathMatrix = new int[21][21];
        for(int i = 0; i < path.size(); i++) {
            System.out.println("X:" + path.get(i).getX() + " Y:" + path.get(i).getY());
            pathMatrix[path.get(i).getX()][path.get(i).getY()] = 1;
        }

        for (int j = 0; j < pathMatrix.length; j++) {
            for (int i = 0; i < pathMatrix[j].length; i++) {
                System.out.print(pathMatrix[i][j] + " ");
            }
            System.out.println();
        }


    }


}