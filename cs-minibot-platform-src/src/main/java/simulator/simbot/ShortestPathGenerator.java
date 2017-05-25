package simulator.simbot;

import java.util.*;

public class ShortestPathGenerator {

    //public static int[][] path;
    public static Node targetNode;
    public static Map<Node, Node> predecessors = new HashMap<Node, Node>();
    private static HashSet<Node> settledNodes = new HashSet<Node>();
    private static HashSet<Node> unsettledNodes = new HashSet<Node>();
    private static HashMap<Node, Integer> distances;

    //Fills maze with 1's
    public static int[][] initializeMaze(int n, int m) {
        int[][] maze = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
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
        int[][] wrappedMaze = initializeMaze(height + 2, width + 2);
        for (int i = 1; i < height + 1; i++) {
            for (int j = 1; j < width + 1; j++) {
                wrappedMaze[i][j] = maze[i - 1][j - 1];
            }
        }
        return wrappedMaze;
    }

    //Makes the Nodes array but doesn't fill in neighbors
    //Also sets cost to max int
    //The end node is the bottom-right corner of the maze. Offset by 1 because
    //The entire maze is padded with 1's on the border.
    public static Node[][] initializeNodeMatrix(int[][] maze) {
        int height = maze.length;
        int width = maze[0].length;
        Node[][] res = new Node[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Node n = new Node(maze[i][j], false, i, j);
                if (i == height - 2 && j == width - 2) {
                    targetNode = n;
                }
                res[i][j] = n;
                if (maze[i][j] == 0) {
                    res[i][j].cost = Integer.MAX_VALUE;
                }
            }
        }

        res[height - 2][width - 2].isEnd = true;
        res[1][1].cost = 1;
        return res;
    }

    //Returns a HashSet of neighbors given a position and maze
    //Neighbors are considered to be cells in the up/left/down/right direction
    public static HashSet<Node> generateNeighbors(int i, int j, Node[][] maze) {
        HashSet<Node> res = new HashSet<Node>();
        if (maze[i + 1][j].value == 0) {
            res.add(maze[i + 1][j]);
        }
        if (maze[i][j + 1].value == 0) {
            res.add(maze[i][j + 1]);
        }
        if (maze[i - 1][j].value == 0) {
            res.add(maze[i - 1][j]);
        }
        if (maze[i][j - 1].value == 0) {
            res.add(maze[i][j - 1]);
        }
        return res;
    }

    //Returns an array of Nodes with the neighbors field filled out
    public static Node[][] populateNeighbors(Node[][] maze) {
        int height = maze.length;
        int width = maze[0].length;
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                if (maze[i][j].value == 0) {
                    maze[i][j].neighbors = generateNeighbors(i, j, maze);
                }
            }
        }
        return maze;
    }

    //Given a set of nodes, return the node with the lowest cost
    public static Node getNodeWithLowestCost(HashSet<Node> unsettledNodes) {
        int lowestCost = Integer.MAX_VALUE;
        Node res = null;
        for (Node n : unsettledNodes) {
            if (n.cost < lowestCost) {
                lowestCost = n.cost;
                res = n;

            }
        }
        return res;
    }

    //Updates the cost of the neighbors that are not settled and updates the path.
    //Assumes that all the edges in the graph have cost 1.
    public static void processNeighbors(Node currentNode) {
        for (Node n : currentNode.neighbors) {
            if (!settledNodes.contains(n)) {
                int newCost = currentNode.cost + 1;
                if (newCost < n.cost) {

                    n.cost = newCost;
                    predecessors.put(n, currentNode);
                }
                unsettledNodes.add(n);
            }
        }
    }

    public static LinkedList<Node> getPath(Node target) {
        LinkedList<Node> path = new LinkedList<Node>();
        Node step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);


        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }

    public static int execute(Node[][] maze) {
        Node sourceNode = maze[1][1];
        unsettledNodes.add(sourceNode);
        Node curr = sourceNode;
        //predecessors.put(curr, null);
        Node currentNode = null;
        while (!unsettledNodes.isEmpty()) {
            currentNode = getNodeWithLowestCost(unsettledNodes);

            if (currentNode.isEnd) {
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
        //System.out.println("LAST CURRENTNODE " + currentNode.getX() + " " + currentNode.getY());

        int width = maze.length;
        int height = maze[0].length;
        return maze[width - 2][height - 2].cost;
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

        public Node(int value, boolean isEnd, int x, int y) {
            this.value = value;
            this.isEnd = isEnd;
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

}
