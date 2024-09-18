package core;


import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static core.World.BOARD_LENGTH;
import static core.World.BOARD_WIDTH;

public class UI {
    private World cuteWorld;
    private TETile[][] world;
    private TERenderer ter;
    Player you;

    public UI() {
        ter = new TERenderer();
        ter.initialize(BOARD_WIDTH, BOARD_LENGTH, 0, -1);
        initialScreen();
    }

    public UI(String input) { }


    public TETile[][] getWorld() {
        return world;
    }

    private final int x = 25;
    private final int y1 = 40;
    private final int y2 = 25;
    private final int y3 = 23;
    private final int y4 = 21;
    private final int y5 = 15;
    private final int y6 = 19;
    private final int y7 = 17;
    private final int x7 = 35;

    // here is the UI before any command is given!
    public void initialScreen() {
        StdDraw.setFont(new Font("Arial", Font.BOLD, y2));
        StdDraw.setPenColor(StdDraw.WHITE);

        StdDraw.text(x, y1, "CS61B : THE GAME");
        StdDraw.setFont();
        StdDraw.text(x, y2, "New Game (N)");
        StdDraw.text(x, y3, "Load Game (L)");
        StdDraw.text(x, y4, "Quit (Q)");
        StdDraw.text(x, y6, "Avatar Menu (P)");
        StdDraw.text(x, y7, "press z to toggle the board!");
        StdDraw.show();
    }

    public void findYou(TETile[][] input) {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_LENGTH; j++) {
                if (input[i][j] == Tileset.AVATAR) {
                    this.you = new Player(i, j, 1);
                } else if (input[i][j] == Tileset.AVATAR_2) {
                    this.you = new Player(i, j, 2);
                } else if (input[i][j] == Tileset.AVATAR_3) {
                    this.you = new Player(i, j, 3);
                }
            }
        }
    }

    public void avatarScreen() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        //option 1
        StdDraw.text(y5, y2, "@");
        //option 2
        StdDraw.text(y2, y2, "%");
        //option 3
        StdDraw.text(x7, y2, "$");
        StdDraw.show();
    }

    //save the world when you don't have time to play it!
    public void saveWorld() {
        StringBuilder contents = new StringBuilder(BOARD_LENGTH + " " + BOARD_WIDTH + "\n");
        for (int i = BOARD_WIDTH - 1; i >= 0; i--) {
            for (int j = 0; j < BOARD_LENGTH; j++) {
                int result = 0;
                if (Objects.equals(world[j][i].description(), "wall")) {
                    result = 1;
                } else if (Objects.equals(world[j][i].description(), "floor")) {
                    result = 2;
                } else if (Objects.equals(world[j][i].character(), '@')) {
                    result = 3;
                } else if (Objects.equals(world[j][i].character(), '%')) {
                    result = 4;
                } else if (Objects.equals(world[j][i].character(), '$')) {
                    result = 5;
                }
                contents.append(result);
            }
            contents.append("\n");
        }

        String text = contents.toString();
        FileUtils.writeFile("save.txt", text);
    }

    //check if save.txt is already in the current folder(check if any world was saved before)
    public boolean fileExist() {
        String folderPath = ".";
        String fileName = "save.txt";
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        for (File file : files) {
            String name = file.getName();

            if (name.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    // loading the previous world
    public TETile[][] loadWorld() {
        String text = FileUtils.readFile("save.txt");

        String[] splitText = text.split("\n");

        TETile[][] loadResult = new TETile[BOARD_LENGTH][BOARD_WIDTH];
        for (int i = BOARD_WIDTH - 1; i >= 0; i--) {
            String rowText = splitText[BOARD_WIDTH - i];
            for (int j = 0; j < BOARD_LENGTH; j++) {
                if (rowText.charAt(j) == '0') {
                    loadResult[j][i] = Tileset.NOTHING;
                } else if (rowText.charAt(j) == '1') {
                    loadResult[j][i] = Tileset.WALL;
                } else if (rowText.charAt(j) == '2') {
                    loadResult[j][i] = Tileset.FLOOR;
                } else if (rowText.charAt(j) == '3') {
                    loadResult[j][i] = Tileset.AVATAR;
                } else if (rowText.charAt(j) == '4') {
                    loadResult[j][i] = Tileset.AVATAR_2;
                } else if (rowText.charAt(j) == '5') {
                    loadResult[j][i] = Tileset.AVATAR_3;
                }
            }
        }

        return loadResult;
    }

    //showing the status of whether we are inputting the seed numbers
    private boolean isN = false;
    private boolean isQ = false;
    private boolean worldMade = false;
    private int countToggle = 0;
    private int avatarOption = 1; //default avatar option

    //keep track of the seed
    private StringBuilder seed;

    public void extraFeatures(char input) {
        if ((input == 'p') || (input == 'P')) {
            avatarScreen();
        } else if (input == '@') {
            avatarOption = 1;
            StdDraw.clear(StdDraw.BLACK);
            initialScreen();
        } else if (input == '%') {
            avatarOption = 2;
            StdDraw.clear(StdDraw.BLACK);
            initialScreen();
        } else if (input == '$') {
            avatarOption = 3;
            StdDraw.clear(StdDraw.BLACK);
            initialScreen();
        }
    }

    public void basicStuff(char input) {
        if (!(input == 'S' || input == 's') && isN) {
            seed.append(input);
            StdDraw.clear(StdDraw.BLACK);
            initialScreen();
            StdDraw.text(x, y5, String.valueOf(seed));
            StdDraw.show();

        } else if (input == 'N' || input == 'n') {
            seed = new StringBuilder();
            isN = true;

        } else if ((input == 'S' || input == 's') && isN) {

            long actualSeed = Long.parseLong(seed.toString());
            this.cuteWorld = new World(actualSeed);
            this.cuteWorld.drawRooms(avatarOption);
            this.cuteWorld.drawHallways();
            worldMade = true;

            this.world = this.cuteWorld.getWorld();
            findYou(this.world);
            StdDraw.clear();
            ter.renderFrame(world);

            isN = false;
            seed = null;

        } else if (input == 'L' || input == 'l') {
            if (fileExist()) {
                this.world = loadWorld();
                findYou(this.world);
                worldMade = true;
            }
            if (this.world == null) {
                System.exit(0);
            }

            StdDraw.clear();
            ter.renderFrame(world);

        } else if (input == ':') {
            isQ = true;

        } else if ((input == 'Q' || input == 'q') && isQ) {
            saveWorld();
            System.exit(0);
            isQ = false;
        }
    }

    public void moving(char input) {
        if ((input == 'A' || input == 'a') && worldMade) {
            if (you != null) {
                you.moveLeft(world);
                if (countToggle % 2 == 0) {
                    ter.renderFrame(world);
                    hudDisplay(world);
                } else {
                    World.ToggledWorld myTogWorld = new World.ToggledWorld(world);
                    TETile[][] toggledWorld = myTogWorld.getToggledWorldTiles();
                    ter.renderFrame(toggledWorld);
                    hudDisplay(toggledWorld);
                }
            }
        } else if ((input == 'S' || input == 's') && worldMade) {
            if (you != null) {
                you.moveDown(world);
                if (countToggle % 2 == 0) {
                    ter.renderFrame(world);
                    hudDisplay(world);
                } else {
                    World.ToggledWorld myTogWorld = new World.ToggledWorld(world);
                    TETile[][] toggledWorld = myTogWorld.getToggledWorldTiles();
                    ter.renderFrame(toggledWorld);
                    hudDisplay(toggledWorld);
                }
            }

        } else if ((input == 'D' || input == 'd') && worldMade) {
            if (you != null) {
                you.moveRight(world);
                if (countToggle % 2 == 0) {
                    ter.renderFrame(world);
                    hudDisplay(world);
                } else {
                    World.ToggledWorld myTogWorld = new World.ToggledWorld(world);
                    TETile[][] toggledWorld = myTogWorld.getToggledWorldTiles();
                    ter.renderFrame(toggledWorld);
                    hudDisplay(toggledWorld);
                }
            }

        } else if ((input == 'W' || input == 'w') && worldMade) {
            if (you != null) {
                you.moveUp(world);
                if (countToggle % 2 == 0) {
                    ter.renderFrame(world);
                    hudDisplay(world);
                } else {
                    World.ToggledWorld myTogWorld = new World.ToggledWorld(world);
                    TETile[][] toggledWorld = myTogWorld.getToggledWorldTiles();
                    ter.renderFrame(toggledWorld);
                    hudDisplay(toggledWorld);
                }
            }
        }
    }

    //like updateBoard in lab10!
    //sorry if i made your method look so weird!! it's because the display would not stop
    //flickering no matter what i did (T.T) so i had to do something weird
    public void updateWorld() {
        if (worldMade && (mousePointerX != StdDraw.mouseX() || mousePointerY != StdDraw.mouseY())) {
            StdDraw.clear(StdDraw.BLACK);
            worldMadeUpdate();
            if (countToggle % 2 == 0) {
                ter.renderFrame(world);
                hudDisplay(world);
            } else {
                World.ToggledWorld myTogWorld = new World.ToggledWorld(world);
                TETile[][] toggledWorld = myTogWorld.getToggledWorldTiles();
                ter.renderFrame(toggledWorld);
                hudDisplay(toggledWorld);
            }
        }
        if (StdDraw.hasNextKeyTyped()) {
            char input = StdDraw.nextKeyTyped();
            extraFeatures(input);
            basicStuff(input);

            if ((input == 't' || input == 'T') && worldMade) {

            }if ((input == 'z') && worldMade) {
                countToggle += 1;
                if (countToggle % 2 == 1) {
                    World.ToggledWorld myTogWorld = new World.ToggledWorld(world);
                    TETile[][] toggledWorld = myTogWorld.getToggledWorldTiles();
                    ter.renderFrame(toggledWorld);
                } else {
                    ter.renderFrame(world);
                }
            }

            moving(input);
        }
    }

    private int index = 0;

    //create for autograder
    public TETile[][] updateWorld(String testinput) {
        if (this.index < testinput.length()) {

            char input = testinput.charAt(this.index);
            this.index += 1;

            if (!(input == 'S' || input == 's') && isN) {
                seed.append(input);

            } else if (input == 'N' || input == 'n') {
                seed = new StringBuilder();
                isN = true;

            } else if ((input == 'S' || input == 's') && isN) {

                long actualSeed = Long.parseLong(seed.toString());
                this.cuteWorld = new World(actualSeed);
                this.cuteWorld.drawRooms(avatarOption);
                this.cuteWorld.drawHallways();


                worldMade = true;

                this.world = this.cuteWorld.getWorld();
                findYou(this.world);

                isN = false;
                seed = null;

            } else if (input == 'L' || input == 'l') {
                this.world = loadWorld();
                findYou(this.world);
                worldMade = true;

            } else if (input == ':') {
                isQ = true;

            } else if ((input == 'Q' || input == 'q') && isQ) {
                saveWorld();

                isQ = false;
            } else if ((input == 'A' || input == 'a') && worldMade) {
                findYou(this.world);
                if (you != null) {
                    you.moveLeft(world);
                }

            } else if ((input == 'S' || input == 's') && worldMade) {
                findYou(this.world);
                if (you != null) {
                    you.moveDown(world);
                }

            } else if ((input == 'D' || input == 'd') && worldMade) {
                findYou(this.world);
                if (you != null) {
                    you.moveRight(world);
                }

            } else if ((input == 'W' || input == 'w') && worldMade) {
                findYou(this.world);
                if (you != null) {
                    you.moveUp(world);
                }

            }
            return updateWorld(testinput);
        }
        return getWorld();
    }

    private double mousePointerX = 0.0;
    private double mousePointerY = 0.0;

    public void worldMadeUpdate() {
        if (StdDraw.hasNextKeyTyped()) {
            char input = StdDraw.nextKeyTyped();

            if ((input == 'A' || input == 'a') && worldMade) {
                you.moveLeft(world);
                ter.renderFrame(world);

            } else if ((input == 'S' || input == 's') && worldMade) {
                you.moveDown(world);
                ter.renderFrame(world);

            } else if ((input == 'D' || input == 'd') && worldMade) {
                you.moveRight(world);
                ter.renderFrame(world);

            } else if ((input == 'W' || input == 'w') && worldMade) {
                you.moveUp(world);
            }
        }
    }

    TETile mouseTile;

    public void hudDisplay(TETile[][] myWorld) {
        mousePointerX = StdDraw.mouseX();
        mousePointerY = StdDraw.mouseY();
        int mouseX = (int) Math.round(StdDraw.mouseX());
        int mouseY = (int) Math.round(StdDraw.mouseY());
        StdDraw.setPenColor(Color.WHITE);
        final int x0 = 50;
        final int y = 49;
        if (mouseX < x0 && mouseY < x0 && (world != null)) {
            mouseTile = myWorld[mouseX][mouseY];
            if (mouseTile.equals(Tileset.FLOOR)) {
                StdDraw.text(4, y, "floor");
            } else if (mouseTile.equals(Tileset.WALL)) {
                StdDraw.text(4, y, "wall");
            } else if (mouseTile.equals(Tileset.NOTHING)) {
                StdDraw.text(4, y, "nothing");
            } else if (mouseTile.equals(Tileset.AVATAR)) {
                StdDraw.text(4, y, "you");
            }
            StdDraw.show();
        }
    }

    public void changeLight() {
        World.Room mouseRoom = mouseTile.getParent();
        mouseRoom.getLight().changeStatus();
    }

    private TETile previousTile;

    public class Player {

        private int x;
        private int y;
        private TETile avatarTile;


        public Player(int x, int y, int optionNum) {

            this.x = x;
            this.y = y;
            if (optionNum == 1) {
                world[x][y] = Tileset.AVATAR;
                avatarTile = Tileset.AVATAR;
            } else if (optionNum == 2) {
                world[x][y] = Tileset.AVATAR_2;
                avatarTile = Tileset.AVATAR_2;
            } else if (optionNum == 3) {
                world[x][y] = Tileset.AVATAR_3;
                avatarTile = Tileset.AVATAR_3;
            }

            previousTile = Tileset.FLOOR;
        }

        public void moveLeft(TETile[][] myWorld) {
            //move passed in world
            if (Objects.equals(myWorld[this.x - 1][this.y].description(), "floor")) {
                myWorld[this.x][this.y] = previousTile;
                previousTile = myWorld[this.x - 1][this.y];
                myWorld[this.x - 1][this.y] = avatarTile;
                this.x = this.x - 1;
            }
        }

        public void moveRight(TETile[][] myWorld) {
            //move passed in world
            if (Objects.equals(myWorld[this.x + 1][this.y].description(), "floor")) {
                myWorld[this.x][this.y] = previousTile;
                previousTile = myWorld[this.x + 1][this.y];
                myWorld[this.x + 1][this.y] = avatarTile;
                this.x = this.x + 1;
            }
        }

        public void moveDown(TETile[][] myWorld) {
            //move passed in world
            if (Objects.equals(myWorld[this.x][this.y - 1].description(), "floor")) {
                myWorld[this.x][this.y] = previousTile;
                previousTile = myWorld[this.x][this.y - 1];
                myWorld[this.x][this.y - 1] = avatarTile;
                this.y = this.y - 1;
            }

        }

        public void moveUp(TETile[][] myWorld) {
            //move passed in world
            if (Objects.equals(myWorld[this.x][this.y + 1].description(), "floor")) {
                myWorld[this.x][this.y] = previousTile;
                previousTile = myWorld[this.x][this.y + 1];
                myWorld[this.x][this.y + 1] = avatarTile;
                this.y = this.y + 1;
            }
        }
    }
}
