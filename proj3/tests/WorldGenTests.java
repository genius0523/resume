import core.AutograderBuddy;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;

public class WorldGenTests {
    @Test
    public void basicTest() {
        // put different seeds here to test different worlds
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn5643591630821615871swwaawdn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn7313251667695476404sasdwn731325166769");

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(50000);

//        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n1swasddddddw");
//
//        TERenderer ter = new TERenderer();
//        ter.initialize(tiles.length, tiles[0].length);
//        ter.renderFrame(tiles);
//        StdDraw.pause(5000);
        TETile[][] tiles1 = AutograderBuddy.getWorldFromInput("N999SDDD:QLWWWDDD");
        StdDraw.pause(5000);

//        TETile[][] tiles1 = AutograderBuddy.getWorldFromInput("n1s:qlwasdddddw");
//        StdDraw.pause(5000);
//
//        TERenderer ter1 = new TERenderer();
//        ter1.initialize(tiles1.length, tiles[0].length);
//        ter1.renderFrame(tiles1);
//        StdDraw.pause(5000); // pause for 5 seconds so you can see the output
    }

    @Test
    public void basicInteractivityTest() {
        // TODO: write a test that uses an input like "n123swasdwasd"
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n123s");
        tiles[25][25].getParent();
    }

    @Test
    public void basicSaveTest() {
        // TODO: write a test that calls getWorldFromInput twice, with "n123swasd:q" and with "lwasd"
    }
}
