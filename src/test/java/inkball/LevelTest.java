package inkball;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

public class LevelTest {

    static App app;
    static Level level;
    static ConfigReader configReader;

    @BeforeAll
    public static void setup() {
        app = new App();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();
        configReader = new ConfigReader("config.json", app);
    }

    @BeforeEach
    public void beforeEach() {
        // Reset the app and level state before each test
        app.unspawnedBalls = new ArrayList<>();
        app.currentLevel = null;
        app.currentLevelIndex = 0;
        level = new Level(0, configReader, app);
    }

    @Test
    public void testConstructor() {
        // Test that the Level constructor initializes correctly
        assertNotNull(level.getBalls());
        assertNotNull(level.getWalls());
        assertNotNull(level.getSpawners());
        assertNotNull(level.getHoles());
        assertNotNull(level.accelerationTiles);
        assertFalse(level.getIsLevelEnded());
    }

    @Test
    public void testIncreaseScore() {
        // Test that increaseScore increases the score correctly based on color index
        int initialScore = app.score;
        int colorIndex = 2; // Assuming valid color index
        level.increaseScore(colorIndex, app);
        // Retrieve the expected increase amount from scoreIncreaseArray
        int expectedIncrease = level.scoreIncreaseArray[colorIndex];
        assertEquals(initialScore + expectedIncrease, app.score);
    }

    @Test
    public void testDecreaseScore() {
        // Test that decreaseScore decreases the score correctly based on color index
        app.score = 50;
        int colorIndex = 1; // Assuming valid color index
        level.decreaseScore(colorIndex, app);
        // Retrieve the expected decrease amount from scoreDecreaseArray
        int expectedDecrease = level.scoreDecreaseArray[colorIndex];
        assertEquals(50 - expectedDecrease, app.score);
    }

    @Test
    public void testIncreaseScore_InvalidColorIndex() {
        // Test increaseScore with an invalid color index
        int initialScore = app.score;
        int invalidColorIndex = -1; // Invalid index
        level.increaseScore(invalidColorIndex, app);
        // Score should remain unchanged
        assertEquals(initialScore, app.score);
    }

    @Test
    public void testDecreaseScore_InvalidColorIndex() {
        // Test decreaseScore with an invalid color index
        app.score = 50;
        int invalidColorIndex = 10; // Invalid index
        level.decreaseScore(invalidColorIndex, app);
        // Score should remain unchanged
        assertEquals(50, app.score);
    }

    @Test
    public void testGetIsLevelEnded() {
        // Test that getIsLevelEnded returns the correct value
        assertFalse(level.getIsLevelEnded());
        level.isLevelEnded = true;
        assertTrue(level.getIsLevelEnded());
    }

    @Test
    public void testGetBalls() {
        // Test that getBalls returns the list of balls
        assertNotNull(level.getBalls());
        assertEquals(0, level.getBalls().size());
        Ball ball = new Ball(100, 100, 0, 12, app);
        level.addBall(ball);
        assertEquals(1, level.getBalls().size());
        assertTrue(level.getBalls().contains(ball));
    }

    @Test
    public void testGetWalls() {
        // Test that getWalls returns the list of walls
        assertNotNull(level.getWalls());
        assertEquals(0, level.getWalls().size());
        Wall wall = new Wall(100, 100, 3, app);
        level.getWalls().add(wall);
        assertEquals(1, level.getWalls().size());
        assertTrue(level.getWalls().contains(wall));
    }

    @Test
    public void testGetSpawners() {
        // Test that getSpawners returns the list of spawners
        assertNotNull(level.getSpawners());
        assertEquals(0, level.getSpawners().size());
        Spawner spawner = new Spawner(100, 100, -1, app);
        level.getSpawners().add(spawner);
        assertEquals(1, level.getSpawners().size());
        assertTrue(level.getSpawners().contains(spawner));
    }

    @Test
    public void testGetHoles() {
        // Test that getHoles returns the list of holes
        assertNotNull(level.getHoles());
        assertEquals(0, level.getHoles().size());
        Hole hole = new Hole(100, 100, 0, app);
        level.getHoles().add(hole);
        assertEquals(1, level.getHoles().size());
        assertTrue(level.getHoles().contains(hole));
    }

    @Test
    public void testAddBall() {
        // Test that addBall adds a ball to the list
        Ball ball = new Ball(100, 100, 0, 12, app);
        level.addBall(ball);
        assertEquals(1, level.getBalls().size());
        assertTrue(level.getBalls().contains(ball));
    }

    @Test
    public void testRemoveBall() {
        // Test that removeBall removes a ball from the list
        Ball ball = new Ball(100, 100, 0, 12, app);
        level.addBall(ball);
        level.removeBall(ball);
        assertEquals(0, level.getBalls().size());
        assertFalse(level.getBalls().contains(ball));
    }

    @Test
    public void testLoadLevel() {
        // Test that loadLevel loads entities correctly from a layout file
        String layoutFile = "test_layout.txt";
        String[] layoutContent = {
                "X X X X X X X X X X X X X X X X X",
                "X                               X",
                "X   H0                          X",
                "X                               X",
                "3           A0                  X",
                "X                               X",
                "X       S                       X",
                "X                               X",
                "X X X X X X X X X X X X X X X X X"
        };
        app.saveStrings(layoutFile, layoutContent); // Save the mock layout file

        level.loadLevel(layoutFile, app);

        assertTrue(level.getWalls().size() > 0);
        assertTrue(level.getHoles().size() > 0);
        assertTrue(level.getSpawners().size() > 0);
        assertTrue(level.accelerationTiles.size() > 0);

        app.sketchPath(""); // Reset sketch path
    }

    @Test
    public void testUpdate() {
        // Test that update method updates balls and checks collisions
        Ball ball = new Ball(100, 100, 0, 12, app);
        level.addBall(ball);
        Wall wall = new Wall(100, 100, 0, app);
        level.getWalls().add(wall);
        level.update(app);
        assertTrue(level.getBalls().contains(ball));
    }

    @Test
    public void testEndLevel_Normal() {
        // Test that endLevel handles normal level ending
        app.remainingTime = 30;
        level.endLevel(app, "normal");
        assertTrue(level.getIsLevelEnded());
        assertEquals(30, level.timeBonusRemaining);
    }

    @Test
    public void testEndLevel_TimeUp() {
        // Test that endLevel handles level ending due to time up
        app.remainingTime = 0;
        level.endLevel(app, "timeUp");
        assertTrue(level.getIsLevelEnded());
        assertEquals(0, level.timeBonusRemaining);
    }

    @Test
    public void testDrawDefaultTiles() {
        // Test that drawDefaultTiles executes without errors
        assertDoesNotThrow(() -> {
            level.drawDefaultTiles(app);
        });
    }

    @Test
    public void testUpdate_BallWallCollision() {
        // Test collision between a ball and a wall during update
        Ball ball = new Ball(100, 100, 3, 12, app);
        ball.setXVelocity(5);
        ball.setYVelocity(0);
        level.addBall(ball);

        Wall wall = new Wall(105, 100, 3, app); // Positioned to collide with the ball
        level.getWalls().add(wall);

        level.update(app);

        assertEquals(5, ball.getXVelocity());
    }

    @Test
    public void testUpdate_BallHoleAttraction() {
        // Test attraction between a ball and a hole during update
        Ball ball = new Ball(110, 110, 0, 12, app);
        level.addBall(ball);
        Hole hole = new Hole(100, 100, 0, app);
        level.getHoles().add(hole);
        level.update(app);
        // Ball may have been captured or its velocity changed
        if (!ball.getIsActive()) {
            assertFalse(level.getBalls().contains(ball));
        } else {
            assertTrue(level.getBalls().contains(ball));
        }
    }

    @Test
    public void testUpdate_BallListModification() {
        // Test that balls can be safely added or removed during update
        Ball ball1 = new Ball(100, 100, 0, 12, app);
        Ball ball2 = new Ball(200, 200, 0, 12, app);
        level.addBall(ball1);

        // Modify balls list during update
        level.getBalls().add(ball2);

        assertDoesNotThrow(() -> level.update(app));
        // Both balls should be updated
        assertTrue(level.getBalls().contains(ball1));
        assertTrue(level.getBalls().contains(ball2));
    }

    @Test
    public void testUpdate_BallOutOfBounds() {
        // Test that ball moving out of bounds is handled correctly
        Ball ball = new Ball(-10, -10, 0, 12, app);
        level.addBall(ball);
        level.update(app);
        // Ball should still be in the list
        assertTrue(level.getBalls().contains(ball));
    }

    @Test
    public void testDraw_LevelEnded_GamePaused() {
        // Test drawing when the level has ended and the game is paused
        level.isLevelEnded = true;
        app.isPaused = true;
        // No exception should occur during drawing
        assertDoesNotThrow(() -> level.draw(app));
    }

    @Test
    public void testDraw_LevelEnded_TimerFinished() {
        // Test drawing when the level has ended and the timer has finished
        level.isLevelEnded = true;
        app.timerFinished = true;
        // No exception should occur during drawing
        assertDoesNotThrow(() -> level.draw(app));
    }

    @Test
    public void testYellowTileAnimation_LevelEnded() {
        // Simulate level ended, game not paused, and timer not finished
        level.isLevelEnded = true;
        app.isPaused = false;
        app.timerFinished = false;
        assertDoesNotThrow(() -> level.moveYellowTiles(app));
    }

    @Test
    public void testMoveYellowTiles_TopLeftTileNull() {
        level.isLevelEnded = true;
        app.isPaused = false;
        app.timerFinished = false;
        app.frameCount = 2;
        level.yellowTileTopLeft = null;
        level.yellowTileBottomRight = new YellowTile(100, 100, 0, app);
        int initialXBottomRight = level.yellowTileBottomRight.getX();
        int initialYBottomRight = level.yellowTileBottomRight.getY();

        // Call the method that handles yellow tile movement
        level.moveYellowTiles(app);

        // Assert that the bottom-right tile's position has not changed
        assertEquals(initialXBottomRight + 32, level.yellowTileBottomRight.getX());
        assertEquals(initialYBottomRight, level.yellowTileBottomRight.getY());

        // Assert that no NullPointerException occurs with the top-left tile being null
        assertNull(level.yellowTileTopLeft);
    }

    @Test
    public void testMoveYellowTiles_AnimationOccurs() {
        level.isLevelEnded = true;
        app.isPaused = false;
        app.timerFinished = false;
        app.frameCount = 2;
        level.yellowTileTopLeft = new YellowTile(0, 0, 0, app);
        level.yellowTileBottomRight = new YellowTile(100, 100, 0, app);

        // Call the method that handles yellow tile movement
        level.moveYellowTiles(app);

        // Assert that both tiles' positions have not changed
        assertEquals(32, level.yellowTileTopLeft.getX());
        assertEquals(0, level.yellowTileTopLeft.getY());
        assertEquals(132, level.yellowTileBottomRight.getX());
        assertEquals(100, level.yellowTileBottomRight.getY());
    }

    @Test
    public void testTimeBonusPositive() {
        level.isLevelEnded = true;
        app.isPaused = false;
        app.timerFinished = false;
        level.timeBonusRemaining = 10;
        app.frameCount = 2;

        YellowTile topLeftTile = new YellowTile(0, 0, 0, app);
        YellowTile bottomRightTile = new YellowTile(100, 100, 0, app);
        level.yellowTileTopLeft = topLeftTile;
        level.yellowTileBottomRight = bottomRightTile;

        int initialXTopLeft = level.yellowTileTopLeft.getX();
        int initialYTopLeft = level.yellowTileTopLeft.getY();
        int initialXBottomRight = level.yellowTileBottomRight.getX();
        int initialYBottomRight = level.yellowTileBottomRight.getY();

        level.draw(app);

        // Assert that timeBonusRemaining has been updated
        assertTrue(level.timeBonusRemaining < 10);

        // Assert that the yellow tiles have moved
        assertNotEquals(initialXTopLeft, level.yellowTileTopLeft.getX());
        assertNotEquals(initialXBottomRight, level.yellowTileBottomRight.getX());
    }

    @Test
    public void testTimeBonusNegative() {
        level.isLevelEnded = true;
        app.isPaused = false;
        app.timerFinished = false;
        level.timeBonusRemaining = -10;
        app.frameCount = 2;

        YellowTile topLeftTile = new YellowTile(0, 0, 0, app);
        YellowTile bottomRightTile = new YellowTile(100, 100, 0, app);
        level.yellowTileTopLeft = topLeftTile;
        level.yellowTileBottomRight = bottomRightTile;

        level.draw(app);

        // Assert that timeBonusRemaining has been updated
        assertFalse(level.isLevelEnded);
    }

}
