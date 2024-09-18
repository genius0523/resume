package core;


import tileengine.TETile;
import tileengine.Tileset;

import utils.RandomUtils;


import java.util.ArrayList;

import java.util.Objects;
import java.util.Random;

import static java.lang.Math.*;

public class World {

    // build your own world!
    private long SEED;
    static final int BOARD_WIDTH = 50;
    static final int BOARD_LENGTH = 50;
    private static final int NUM_ITERATIONS = 5000;
    private static final double ROOM_SCALE_FAC = 2.25;
    private Random RANDOM;
    private TETile[][] world;
    private ArrayList<Room> drawnRooms; // --> keeps track of what rooms are actually drawn to screen!

    public static class ToggledWorld {
        private TETile[][] toggledWorldTiles;

        public ToggledWorld(TETile[][] myWorld) {
            toggledWorldTiles = modifyWorld(myWorld);
        }

        public TETile[][] getToggledWorldTiles() {
            return toggledWorldTiles;
        }

        public TETile[][] modifyWorld(TETile[][] world) {
            TETile[][] modifiedWorld = new TETile[BOARD_WIDTH][BOARD_LENGTH];
            int[] avatarPos = new int[2]; // <-- (x,y) of avatar

            //make a copy of array while also finding avatar tile
            for (int i = 0; i < BOARD_LENGTH; i++) {
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    modifiedWorld[j][i] = world[j][i];
                    if (world[j][i].equals(Tileset.AVATAR)) {
                        avatarPos[0] = j;
                        avatarPos[1] = i;
                    }
                }
            }

            //change modified tile array so everything around avatar is nothing
            for (int i = 0; i < BOARD_LENGTH; i++) {
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    if (i < avatarPos[1] - 2 || i > avatarPos[1] + 2 || j < avatarPos[0] - 2 || j > avatarPos[0] + 2) {
                        modifiedWorld[j][i] = Tileset.NOTHING;
                    }
                }
            }

            //return modified tile array
            return modifiedWorld;
        }
    }


    public World(long s) {
        //initialize world to array of desired width and height
        SEED = s;
        RANDOM = new Random(SEED);
        world = new TETile[BOARD_WIDTH][BOARD_LENGTH];
        drawnRooms = new ArrayList<>();

        //fill array w/ nothing tiles
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_LENGTH; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    //returns length/width of board (bc style won't let me have public vars T.T)
    public int getWidthOrLength(char x) {
        if (x == 'w') {
            return BOARD_WIDTH;
        } else {
            return BOARD_LENGTH;
        }
    }

    //returns the tiled 2-d board
    public TETile[][] getWorld() {
        return world;
    }

    public void drawRooms(int avatarOption) {
        // draw as many rooms as possible, not exceeding numIterations;
        int count = 0;
        while (count < NUM_ITERATIONS) {
            Room myRoom = new Room();
            myRoom.tryDrawRoom(avatarOption);
            count += 1;
        }
    }

    public void drawHallways() {
        //draw the hallways
        Hallways myHallways = new Hallways(); //this is ugly --> maybe make static class?
        myHallways.drawMST(myHallways.roomSpanningTree(drawnRooms));
    }
    public class Room {
        private int x;
        private int y;
        private int xLength;
        private int yLength;

        //room constructor w/ instance variables (x,y) and (length, width)
        public Room() {
            //(x, y) is center of the room
            x = RandomUtils.uniform(RANDOM, 2, BOARD_WIDTH - 2);
            y = RandomUtils.uniform(RANDOM, 2, BOARD_LENGTH - 2);
            //creates a room of valid x-y length
            xLength = RandomUtils.uniform(RANDOM,
                    (int) ((Math.round(
                            Math.min(BOARD_WIDTH - x, x)) / ROOM_SCALE_FAC) + 4));
            // arbitary -> i'm just messing around w/ sizing
            yLength = RandomUtils.uniform(RANDOM,
                    (int) (Math.round((Math.min(BOARD_WIDTH - y, y))) / ROOM_SCALE_FAC) + 4);

        }

        private Light light;

        public Light getLight() {
            return this.light;
        }



        public class Light {

            int x;
            int y;

            private boolean isTurnedon;
            public Light(int x, int y) {
                this.x = x;
                this.y = y;
                for (int i = max(bottomX, x - 3); i < min(x + 4, BOARD_LENGTH); i++) {
                    for (int j = max(bottomY, y - 3); j < min(y + 4, BOARD_WIDTH); j++) {
                        if (Objects.equals(world[i][j].description(), "floor") && world[i][j].getParent() == Room.this) {
                            if (max(abs(i - x), abs(j - y)) == 0) {
                                world[i][j] = Tileset.LIGHT_0;
                            } else if (max(abs(i - x), abs(j - y)) == 1) {
                                world[i][j] = Tileset.LIGHT_1;
                            } else if (max(abs(i - x), abs(j - y)) == 2) {
                                world[i][j] = Tileset.LIGHT_2;
                            }
                        }
                    }
                }

                isTurnedon = true;

            }


            public void changeStatus() {
                if (isTurnedon) {
                    for (int i = max(bottomX, x - 3); i < min(x + 4, BOARD_LENGTH); i++) {
                        for (int j = max(bottomY, y - 3); j < min(y + 4, BOARD_WIDTH); j++) {
                            if (Objects.equals(world[i][j].description(), "floor") && world[i][j].getParent() == Room.this) {
                                if (max(abs(i - x), abs(j - y)) == 0) {
                                    world[i][j] = Tileset.FLOOR;
                                } else if (max(abs(i - x), abs(j - y)) == 1) {
                                    world[i][j] = Tileset.FLOOR;
                                } else if (max(abs(i - x), abs(j - y)) == 2) {
                                    world[i][j] = Tileset.FLOOR;
                                }
                            }
                        }
                    }

                    isTurnedon = false;
                } else {
                    for (int i = max(bottomX, x - 3); i < min(x + 4, BOARD_LENGTH); i++) {
                        for (int j = max(bottomY, y - 3); j < min(y + 4, BOARD_WIDTH); j++) {
                            if (Objects.equals(world[i][j].description(), "floor") && world[i][j].getParent() == Room.this) {
                                if (max(abs(i - x), abs(j - y)) == 0) {
                                    world[i][j] = Tileset.LIGHT_0;
                                } else if (max(abs(i - x), abs(j - y)) == 1) {
                                    world[i][j] = Tileset.LIGHT_1;
                                } else if (max(abs(i - x), abs(j - y)) == 2) {
                                    world[i][j] = Tileset.LIGHT_2;
                                }
                            }
                        }
                    }
                    isTurnedon = true;
                }
            }

        }

        int bottomX;
        int bottomY;

        //returns whether overlapping w/ another room
        //returns true if placed in invalid position on board
        public boolean isOverlapping() {
            //find bottom left corner of room

            int bottomX = x - xLength - 1; // -1 for padding
            int bottomY = y - yLength - 1;

            //iterate through all tiles in room
            //returns true if there already exists non-empty tile in its place
            for (int i = bottomY; i < bottomY + (2 * yLength) + 2; i++) {
                for (int j = bottomX; j < bottomX + (2 * xLength) + 2; j++) { //+2 for padding
                    if (i < 0 || j < 0 || i >= BOARD_LENGTH || j >= BOARD_WIDTH) {
                        return true;
                    }
                    if (!world[j][i].equals(Tileset.NOTHING)) {
                        return true;
                    }
                }
            }
            return false;
        }

        //draws room on board if no overlap and is a valid room
        public void tryDrawRoom(int avatarOption) {
            if (!isOverlapping() && xLength > 1 && yLength > 1) {
                //draw floor tiles + 2 edge wall tiles, starting from bottom left of room
                int bottomX = x - xLength + 1;
                int bottomY = y - yLength + 1;

                for (int i = bottomY; i < bottomY + (2 * yLength) - 2; i++) {
                    world[bottomX - 1][i] = Tileset.WALL;
                    world[bottomX - 1][i].setParent(this);
                    for (int j = bottomX; j < bottomX + (2 * xLength) - 1; j++) {
                        world[j][i] = Tileset.FLOOR;
                        world[j][i].setParent(this);
                    }
                    world[bottomX + (2 * xLength) - 1][i] = Tileset.WALL;
                }

                //create top and bottom edge wall tiles
                for (int i = bottomX - 1; i < bottomX + (2 * xLength); i++) {
                    world[i][bottomY - 1] = Tileset.WALL;
                    world[i][bottomY - 1].setParent(this);
                    world[i][bottomY - 2 + (2 * yLength)] = Tileset.WALL;
                    world[i][bottomY - 2 + (2 * yLength)].setParent(this);
                }

                this.light = new Light(bottomX + 2, bottomY + 2);

                generateYou(avatarOption);

                //add room to list of drawn rooms
                drawnRooms.add(this);
            }
        }


        public void generateYou(int avatarOption) {
            for (int i = 0; i < BOARD_LENGTH; i++) {
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    if (!playerCreated && world[j][i] == Tileset.FLOOR) {
                        Room tempParent = world[j][i].getParent();
                        if (avatarOption == 1) {
                            world[j][i] = Tileset.AVATAR;
                            playerCreated = true;
                        } else if (avatarOption == 2) {
                            world[j][i] = Tileset.AVATAR_2;
                            playerCreated = true;
                        } else if (avatarOption == 3) {
                            world[j][i] = Tileset.AVATAR_3;
                            playerCreated = true;
                        }
                        world[j][i].setParent(tempParent);
                        return;
                    }
                }
            }
        }

    }

    public ArrayList<Room> getRoomlist() {
        return this.drawnRooms;
    }

    public class Hallways {
        //distance using l1/taxicab norm
        public int distBetweenRooms(Room rm1, Room rm2) {
            return abs(rm1.x - rm2.x) + abs(rm1.y - rm2.y);
        }

        //edge class --> stores info about which rooms are connected
        //and what distance between each room is
        public class Edge {
            private Room room1;
            private Room room2;
            private int length;
            public Edge(Room rm1, Room rm2) {
                room1 = rm1;
                room2 = rm2;
                length = distBetweenRooms(rm1, rm2);
            }
        }

        // uses kruskal's to return a list of edges of minimum spanning tree
        public ArrayList<Edge> roomSpanningTree(ArrayList<Room> myRooms) {
            // creates all possible edges between rooms
            // (using distance endpoints as centers of room)
            ArrayList<Edge> myEdges = new ArrayList<>();
            for (int i = 0; i < myRooms.size() - 1; i++) {
                for (int j = i + 1; j < myRooms.size(); j++) {
                    Edge newEdge = new Edge(myRooms.get(i), myRooms.get(j));
                    myEdges.add(newEdge);
                }
            }

            //insertion sort
            for (int i = 1; i < myEdges.size(); i++) {
                Edge elemAti = myEdges.get(i);
                while (myEdges.indexOf(elemAti) > 0
                        && elemAti.length < myEdges.get(myEdges.indexOf(elemAti) - 1).length) {
                    //swap elements
                    Edge oldPrev = myEdges.get(myEdges.indexOf(elemAti) - 1);
                    Integer currIndex = myEdges.indexOf(elemAti);
                    myEdges.set(currIndex, oldPrev);
                    myEdges.set(currIndex - 1, elemAti);
                }
            }

            //create disjoint set of room vertices
            DisjointSet<Room> myDisjointSet = new DisjointSet(myRooms);

            //myEdges is now sorted --> use kruskal's
            ArrayList<Edge> myMST = new ArrayList<>();

            for (int i = 0; i < myEdges.size(); i++) {
                Edge currEdge = myEdges.get(i);
                if (!myDisjointSet.isConnected(currEdge.room1, currEdge.room2)) {
                    myMST.add(currEdge);
                    myDisjointSet.connect(currEdge.room1, currEdge.room2);
                }
            }
            return myMST;
        }

        //draws minimum spanning tree based on mst array passed in
        public void drawMST(ArrayList<Edge> roomMST) {
            for (Edge e : roomMST) {
                //find corner vtx
                int[] corner = findCornerVtx(e);
                //draw straight hallway between first room and corner
                drawStraight(e.room1.x, e.room1.y, corner[0], corner[1]);
                //draw straight hallway between corner and second room
                drawStraight(e.room2.x, e.room2.y, corner[0], corner[1]);
            }
        }

        //returns corner of an edge bwtn two rooms, returns [x,y] array
        //since there are two choices for a corner, arbitrarily chooses one
        public int[] findCornerVtx(Edge e) {
            int caseNum = RANDOM.nextInt(2);
            int[] coordinates = new int[2];
            if (caseNum == 1) {
                coordinates[0] = e.room1.x;
                coordinates[1] = e.room2.y;
            } else {
                coordinates[0] = e.room2.x;
                coordinates[1] = e.room1.y;
            }
            return coordinates;
        }

        //draw straight hallway between two (x,y) coordinates
        //starts at room center and goes to corner point
        //assumes at least one coordinate is matching!
        public void drawStraight(int rx, int ry, int cx, int cy) {
            // draw horizontal hallway
            if (ry == cy) {
                //checks location of room relative to corner
                int loc = 0;
                if (rx <= cx) {
                    loc = 1; // location is 1 if room to left of corner
                } else {
                    loc = -1; // location is -1 if room to right of corner
                }
                int startIndex = rx;
                int endIndex = cx;
                while (startIndex != endIndex + loc) {
                    if (world[startIndex][ry].equals(Tileset.WALL)) {
                        world[startIndex][ry] = Tileset.FLOOR; // create opening for hallway

                        if (!world[startIndex][ry + 1].equals(Tileset.FLOOR)) {
                            world[startIndex][ry + 1] = Tileset.WALL;
                        }
                        if (!world[startIndex][ry - 1].equals(Tileset.FLOOR)) {
                            world[startIndex][ry - 1] = Tileset.WALL;
                        }
                    } else if (world[startIndex][ry].equals(Tileset.NOTHING)) {
                        world[startIndex][ry] = Tileset.FLOOR; //create floor of hallway
                        world[startIndex][ry - 1] = Tileset.WALL; //create lower wall
                        world[startIndex][ry + 1] = Tileset.WALL; //create upper wall
                    }
                    startIndex = startIndex + loc;
                }
                //create end corner of hallway --> basically always stop if you hit a floor
                if (!world[endIndex + loc][ry].equals(Tileset.FLOOR)) {
                    world[endIndex + (loc)][ry] = Tileset.WALL;
                }
                if (!world[endIndex + loc][ry - 1].equals(Tileset.FLOOR)) {
                    world[endIndex + (loc)][ry - 1] = Tileset.WALL;
                }
                if (!world[endIndex + loc][ry + 1].equals(Tileset.FLOOR)) {
                    world[endIndex + (loc)][ry + 1] = Tileset.WALL;
                }
            } else if (rx == cx) { // draw vertical hallway
                //checks location of room relative to corner
                int loc = 0;
                if (ry <= cy) {
                    loc = 1; // location is 1 if room to left of corner
                } else {
                    loc = -1; // location is -1 if room to right of corner
                }
                int startIndex = ry;
                int endIndex = cy;
                while (startIndex != endIndex + loc) {
                    if (world[rx][startIndex].equals(Tileset.WALL)) {
                        world[rx][startIndex] = Tileset.FLOOR; // create opening for hallway
                        if (!world[rx - 1][startIndex].equals(Tileset.FLOOR)) {
                            world[rx - 1][startIndex] = Tileset.WALL;
                        }
                        if (!world[rx + 1][startIndex].equals(Tileset.FLOOR)) {
                            world[rx + 1][startIndex] = Tileset.WALL;
                        }
                    } else if (world[rx][startIndex].equals(Tileset.NOTHING)) {
                        world[rx][startIndex] = Tileset.FLOOR; //create floor of hallway
                        world[rx - 1][startIndex] = Tileset.WALL; //create lower wall
                        world[rx + 1][startIndex] = Tileset.WALL; //create upper wall
                    }
                    startIndex = startIndex + loc;
                }
                //create end corner of hallway --> basically always stop if you hit a floor
                if (!world[rx][endIndex + loc].equals(Tileset.FLOOR)) {
                    world[rx][endIndex + loc] = Tileset.WALL;
                }
                if (!world[rx - 1][endIndex + loc].equals(Tileset.FLOOR)) {
                    world[rx - 1][endIndex + loc] = Tileset.WALL;
                }
                if (!world[rx + 1][endIndex + loc].equals(Tileset.FLOOR)) {
                    world[rx + 1][endIndex + loc] = Tileset.WALL;
                }
            }
        }
    }

    private boolean playerCreated = false;

}
