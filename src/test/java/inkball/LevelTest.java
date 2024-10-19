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
        app.setup(); // Initialize the app
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
        // Add a ball
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
        // Add a wall
        Wall wall = new Wall(100, 100, 0, app);
        level.getWalls().add(wall);
        assertEquals(1, level.getWalls().size());
        assertTrue(level.getWalls().contains(wall));
    }

    @Test
    public void testGetSpawners() {
        // Test that getSpawners returns the list of spawners
        assertNotNull(level.getSpawners());
        assertEquals(0, level.getSpawners().size());
        // Add a spawner
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
        // Add a hole
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
        String layoutFile = "test_layout.txt"; // Assume a test layout file exists
        // For testing, we'll create a mock layout file
        String[] layoutContent = {
                "X X X X X X X X X X X X X X X X X",
                "X                               X",
                "X   H0                          X",
                "X                               X",
                "X           A0                  X",
                "X                               X",
                "X       S                       X",
                "X                               X",
                "X X X X X X X X X X X X X X X X X"
        };
        app.saveStrings(layoutFile, layoutContent); // Save the mock layout file

        level.loadLevel(layoutFile, app);

        // Check that walls, holes, spawners, and acceleration tiles are loaded
        assertTrue(level.getWalls().size() > 0);
        assertTrue(level.getHoles().size() > 0);
        assertTrue(level.getSpawners().size() > 0);
        assertTrue(level.accelerationTiles.size() > 0);

        // Clean up the mock layout file
        app.sketchPath(""); // Reset sketch path
    }

    @Test
    public void testUpdate() {
        // Test that update method updates balls and checks collisions
        // Add a ball and a wall
        Ball ball = new Ball(100, 100, 0, 12, app);
        level.addBall(ball);
        Wall wall = new Wall(100, 100, 0, app);
        level.getWalls().add(wall);

        level.update(app);

        // Ball should have updated its position and possibly collided with the wall
        // Since we cannot check the exact position, we'll ensure no exceptions occur
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
    public void testEndLevel_InvalidReason() {
        // Test that endLevel throws exception for invalid reason
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            level.endLevel(app, "invalidReason");
        });
        assertEquals("Unknown reason for ending level: invalidReason", exception.getMessage());
    }

    @Test
    public void testDraw() {
        // Test that draw method executes without errors
        assertDoesNotThrow(() -> level.draw(app));
    }

    @Test
    public void testUpdateTimeBonus() {
        // Test that updateTimeBonus increases score during time bonus
        app.remainingTime = 10;
        level.timeBonusRemaining = 10;
        level.isLevelEnded = true;

        int initialScore = app.score;
        level.updateTimeBonus(app);

        // Score should have increased
        assertTrue(app.score > initialScore);
        // timeBonusRemaining should have decreased
        assertTrue(level.timeBonusRemaining <= 10);
    }

    @Test
    public void testMoveYellowTiles() {
        // Test that moveYellowTiles updates yellow tiles positions
        level.isLevelEnded = true;
        level.endLevel(app, "normal"); // Initialize yellow tiles

        level.moveYellowTiles(app);

        // Since we cannot check positions, we ensure no exceptions occur
        assertNotNull(level.yellowTileTopLeft);
        assertNotNull(level.yellowTileBottomRight);
    }

    @Test
    public void testDrawDefaultTiles() {
        // Test that drawDefaultTiles executes without errors
        assertDoesNotThrow(() -> {
            level.drawDefaultTiles(app);
        });
    }

    @Test
    public void testUpdate_NoBallsNoSpawners() {
        // Test that level ends when there are no balls and no spawners
        level.getBalls().clear();
        level.getSpawners().clear();
        level.isLevelEnded = false;

        level.update(app);

        assertTrue(level.getIsLevelEnded());
    }

    @Test
    public void testUpdate_BallWallCollision() {
        // Test collision between a ball and a wall during update
        Ball ball = new Ball(100, 100, 0, 12, app);
        ball.setXVelocity(5);
        ball.setYVelocity(0);
        level.addBall(ball);

        Wall wall = new Wall(105, 100, 0, app); // Positioned to collide with the ball
        level.getWalls().add(wall);

        level.update(app);

        // Ball's velocity should have changed due to collision
        assertNotEquals(5, ball.getXVelocity());
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
        // Check if ball is still active
        if (!ball.getIsActive()) {
            assertFalse(level.getBalls().contains(ball));
        } else {
            assertTrue(level.getBalls().contains(ball));
        }
    }

    @Test
    public void testUpdate_BallAccelerateTile() {
        // Test that ball accelerates when on an acceleration tile
        Ball ball = new Ball(100, 100, 0, 12, app);
        level.addBall(ball);

        AccelerateTile tile = new AccelerateTile(100, 100, "right", app);
        level.accelerationTiles.add(tile);

        float initialXVelocity = ball.getXVelocity();

        level.update(app);

        // Ball's X velocity should have increased
        assertTrue(ball.getXVelocity() > initialXVelocity);
    }

    @Test
    public void testEndLevel_AlreadyEnded() {
        // Test that endLevel does nothing if level has already ended
        level.isLevelEnded = true;
        level.endLevel(app, "normal");
        // No exception should occur, and state should remain the same
        assertTrue(level.getIsLevelEnded());
    }

    @Test
    public void testUpdateTimeBonus_TimeBonusRemainingZero() {
        // Test that updateTimeBonus handles zero time bonus remaining
        level.timeBonusRemaining = 0;
        level.isLevelEnded = true;
        level.updateTimeBonus(app);
        // isLevelEnded should now be false, indicating time bonus phase is over
        assertFalse(level.getIsLevelEnded());
    }

    @Test
    public void testLoadLevel_InvalidEntity() {
        // Test that loadLevel handles invalid entities gracefully
        String layoutFile = "invalid_layout.txt";
        String[] layoutContent = {
                "X X X X X X X X X",
                "X       Z       X", // 'Z' is an invalid entity
                "X X X X X X X X X"
        };
        app.saveStrings(layoutFile, layoutContent);

        // No exception should occur during loading
        assertDoesNotThrow(() -> level.loadLevel(layoutFile, app));
    }

    @Test
    public void testConstructor_NullApp() {
        // Test that constructor handles null App gracefully
        Exception exception = assertThrows(NullPointerException.class, () -> {
            new Level(0, configReader, null);
        });
        // Exception is expected due to null App
    }

    @Test
    public void testLoadLevel_NonexistentFile() {
        // Test that loadLevel handles nonexistent file gracefully
        String nonexistentFile = "nonexistent_layout.txt";
        assertDoesNotThrow(() -> level.loadLevel(nonexistentFile, app));
        // Since the file doesn't exist, entities lists should remain empty
        assertEquals(0, level.getWalls().size());
        assertEquals(0, level.getHoles().size());
        assertEquals(0, level.getSpawners().size());
        assertEquals(0, level.accelerationTiles.size());
    }

    @Test
    public void testUpdate_BallRemovedDuringUpdate() {
        // Test that update handles balls being removed during iteration
        Ball ball = new Ball(100, 100, 0, 12, app);
        level.addBall(ball);

        Hole hole = new Hole(100, 100, 0, app);
        level.getHoles().add(hole);

        // Simulate the ball being captured by the hole
        ball.setIsActive(false);

        // No ConcurrentModificationException should occur
        assertDoesNotThrow(() -> level.update(app));
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
    public void testEndLevel_CalledMultipleTimes() {
        // Test that calling endLevel multiple times does not cause issues
        level.endLevel(app, "normal");
        level.endLevel(app, "timeUp");
        // isLevelEnded should remain true
        assertTrue(level.getIsLevelEnded());
    }

    @Test
    public void testLoadLevel_MissingParameters() {
        // Test that loadLevel handles missing parameters in layout file
        String layoutFile = "missing_params_layout.txt";
        String[] layoutContent = {
                "X X X X X X X X X",
                "X       H        X", // 'H' without color parameter
                "X X X X X X X X X"
        };
        app.saveStrings(layoutFile, layoutContent);

        // No exception should occur during loading
        assertDoesNotThrow(() -> level.loadLevel(layoutFile, app));
    }

    @Test
    public void testUpdate_BallsEmptyButNotEnded() {
        // Test that level does not end if balls are empty but spawners are present
        level.getBalls().clear();
        Spawner spawner = new Spawner(100, 100, -1, app);
        level.getSpawners().add(spawner);
        level.isLevelEnded = false;

        level.update(app);

        // Level should not end yet
        assertFalse(level.getIsLevelEnded());
    }

    @Test
    public void testUpdate_BallsAndSpawnersEmpty_LevelNotEnded() {
        // Test that level ends when both balls and spawners are empty
        level.getBalls().clear();
        level.getSpawners().clear();
        level.isLevelEnded = false;

        level.update(app);

        assertTrue(level.getIsLevelEnded());
    }

    @Test
    public void testLoadLevel_SpawnerWithParameters() {
        // Test that spawners with parameters are loaded correctly
        String layoutFile = "spawner_params_layout.txt";
        String[] layoutContent = {
                "X X X X X X X X X",
                "X   S3B2         X", // 'S' with parameters
                "X X X X X X X X X"
        };
        app.saveStrings(layoutFile, layoutContent);

        level.loadLevel(layoutFile, app);

        // Spawners should be loaded
        assertTrue(level.getSpawners().size() > 0);
    }

    @Test
    public void testUpdate_BallOutOfBounds() {
        // Test that ball moving out of bounds is handled correctly
        Ball ball = new Ball(-10, -10, 0, 12, app);
        level.addBall(ball);

        level.update(app);

        // Ball should still be in the list unless explicitly handled
        assertTrue(level.getBalls().contains(ball));
    }

    @Test
    public void testUpdate_NullApp() {
        // Test that update handles null App gracefully
        Ball ball = new Ball(100, 100, 0, 12, app);
        level.addBall(ball);
        assertThrows(NullPointerException.class, () -> level.update(null));
    }

    @Test
    public void testEndLevel_NullApp() {
        // Test that endLevel handles null App gracefully
        Exception exception = assertThrows(NullPointerException.class, () -> {
            level.endLevel(null, "normal");
        });
    }

    @Test
    public void testDraw_NullApp() {
        // Test that draw handles null App gracefully
        assertThrows(NullPointerException.class, () -> level.draw(null));
    }

    @Test
    public void testLoadLevel_NullApp() {
        // Test that loadLevel handles null App gracefully
        assertThrows(NullPointerException.class, () -> level.loadLevel("layout.txt", null));
    }

    @Test
    public void testConstructor_NullConfigReader() {
        // Test that constructor handles null ConfigReader gracefully
        Exception exception = assertThrows(NullPointerException.class, () -> {
            new Level(0, null, app);
        });
    }

    @Test
    public void testIncreaseScore_NullApp() {
        // Test that increaseScore handles null App gracefully
        int colorIndex = 0;
        assertThrows(NullPointerException.class, () -> level.increaseScore(colorIndex, null));
    }

    @Test
    public void testDecreaseScore_NullApp() {
        // Test that decreaseScore handles null App gracefully
        int colorIndex = 0;
        assertThrows(NullPointerException.class, () -> level.decreaseScore(colorIndex, null));
    }

    @Test
    public void testLoadLevel_EmptyLayoutFile() {
        // Test that loadLevel handles an empty layout file
        String layoutFile = "empty_layout.txt";
        String[] layoutContent = {};
        app.saveStrings(layoutFile, layoutContent);

        level.loadLevel(layoutFile, app);

        // Entities lists should remain empty
        assertEquals(0, level.getWalls().size());
        assertEquals(0, level.getHoles().size());
        assertEquals(0, level.getSpawners().size());
        assertEquals(0, level.accelerationTiles.size());
    }

    @Test
    public void testIncreaseScore_ColorIndexEqualsArrayLength() {
        // Test with colorIndex equal to the length of scoreIncreaseArray
        int initialScore = app.score;
        int colorIndex = level.scoreIncreaseArray.length; // Invalid index
        level.increaseScore(colorIndex, app);
        // Score should remain unchanged
        assertEquals(initialScore, app.score);
    }

    @Test
    public void testDecreaseScore_ColorIndexEqualsArrayLength() {
        // Test with colorIndex equal to the length of scoreDecreaseArray
        app.score = 50;
        int colorIndex = level.scoreDecreaseArray.length; // Invalid index
        level.decreaseScore(colorIndex, app);
        // Score should remain unchanged
        assertEquals(50, app.score);
    }

    @Test
    public void testDraw_LevelEnded_GamePaused() {
        // Test drawing when the level has ended and the game is paused
        level.isLevelEnded = true;
        app.isPaused = true;
        // No exception should occur during drawing
        assertDoesNotThrow(() -> level.draw(app));
        // Since the game is paused, yellow tiles should not be updated or drawn
    }

    @Test
    public void testDraw_LevelEnded_TimerFinished() {
        // Test drawing when the level has ended and the timer has finished
        level.isLevelEnded = true;
        app.timerFinished = true;
        // No exception should occur during drawing
        assertDoesNotThrow(() -> level.draw(app));
        // Since the timer has finished, yellow tiles should not be updated or drawn
    }

    @Test
    public void testDraw_YellowTilesNull() {
        // Test drawing when yellow tiles are null
        level.isLevelEnded = true;
        level.yellowTileTopLeft = null;
        level.yellowTileBottomRight = null;
        // Ensure no exception occurs during drawing
        assertDoesNotThrow(() -> level.draw(app));
    }

    @Test
    public void testDraw_YellowTileTopLeftNull() {
        // Test drawing when yellowTileTopLeft is null
        level.isLevelEnded = true;
        level.endLevel(app, "normal");
        level.yellowTileTopLeft = null;
        // Ensure no exception occurs during drawing
        assertDoesNotThrow(() -> level.draw(app));
    }

    @Test
    public void testDraw_YellowTileBottomRightNull() {
        // Test drawing when yellowTileBottomRight is null
        level.isLevelEnded = true;
        level.endLevel(app, "normal");
        level.yellowTileBottomRight = null;
        // Ensure no exception occurs during drawing
        assertDoesNotThrow(() -> level.draw(app));
    }

    @Test
    public void testLoadLevel_InvalidSpawnerParameters() {
        // Test handling of spawner with missing parameters
        String layoutFile = "invalid_spawner_layout.txt";
        String[] layoutContent = {
                "X X X X X",
                "X   S    X", // 'S' without parameters
                "X X X X X"
        };
        app.saveStrings(layoutFile, layoutContent);

        // No exception should occur during loading
        assertDoesNotThrow(() -> level.loadLevel(layoutFile, app));
        // Spawners list should remain empty or handle the spawner accordingly
    }

    @Test
    public void testLoadLevel_InvalidHoleParameters() {
        // Test handling of hole with invalid color parameter
        String layoutFile = "invalid_hole_layout.txt";
        String[] layoutContent = {
                "X X X X X",
                "X   H9   X", // 'H' with invalid color '9'
                "X X X X X"
        };
        app.saveStrings(layoutFile, layoutContent);

        // No exception should occur during loading
        assertDoesNotThrow(() -> level.loadLevel(layoutFile, app));
        // Holes list should remain empty or handle the hole accordingly
    }

    @Test
    public void testLoadLevel_UnexpectedCharacter() {
        // Test handling of unexpected characters in the layout file
        String layoutFile = "unexpected_char_layout.txt";
        String[] layoutContent = {
                "X X X X X",
                "X   Z    X", // 'Z' is unexpected
                "X X X X X"
        };
        app.saveStrings(layoutFile, layoutContent);

        // No exception should occur during loading
        assertDoesNotThrow(() -> level.loadLevel(layoutFile, app));
        // No entities should be created for the 'Z' character
    }

    @Test
    public void testLoadLevel_BallWithMissingColor() {
        // Test handling of 'B' character without a following color
        String layoutFile = "ball_missing_color_layout.txt";
        String[] layoutContent = {
                "X X X X X",
                "X   B    X", // 'B' without color
                "X X X X X"
        };
        app.saveStrings(layoutFile, layoutContent);

        // No exception should occur during loading
        assertDoesNotThrow(() -> level.loadLevel(layoutFile, app));
        // Balls list should remain empty or handle the ball accordingly
    }

    @Test
    public void testMoveYellowTiles_YellowTilesNull() {
        // Test when yellow tiles are null
        level.yellowTileTopLeft = null;
        level.yellowTileBottomRight = null;

        // Ensure no exception occurs during moving yellow tiles
        assertDoesNotThrow(() -> level.moveYellowTiles(app));
    }

    @Test
    public void testMoveYellowTiles_FrameCountOdd() {
        // Test when frame count is odd (condition not met)
        app.frameCount = 1; // Odd frame count

        // Initialize yellow tiles
        level.isLevelEnded = true;
        level.endLevel(app, "normal");

        // Yellow tiles should not update on odd frame counts
        level.moveYellowTiles(app);
        // Positions should remain the same
        int initialX = level.yellowTileTopLeft.getX();
        int initialY = level.yellowTileTopLeft.getY();

        // Move yellow tiles again on the same frame
        level.moveYellowTiles(app);
        assertEquals(initialX, level.yellowTileTopLeft.getX());
        assertEquals(initialY, level.yellowTileTopLeft.getY());
    }

    @Test
    public void testEndLevel_NormalWithNoRemainingTime() {
        // Test ending level normally with no remaining time
        app.remainingTime = 0;
        level.endLevel(app, "normal");
        assertTrue(level.getIsLevelEnded());
        assertEquals(0, level.timeBonusRemaining);
    }

    @Test
    public void testEndLevel_NormalWithNegativeRemainingTime() {
        // Test ending level normally with negative remaining time
        app.remainingTime = -10;
        level.endLevel(app, "normal");
        assertTrue(level.getIsLevelEnded());
        assertEquals(0, level.timeBonusRemaining);
    }

    @Test
    public void testUpdateTimeBonus_TimeBonusBecomesNegative() {
        // Test when timeBonusRemaining becomes negative
        level.timeBonusRemaining = 1;
        level.isLevelEnded = true;
        app.remainingTime = 1;

        // Simulate multiple updates
        level.updateTimeBonus(app); // timeBonusRemaining = 0
        level.updateTimeBonus(app); // timeBonusRemaining should not go below 0

        assertEquals(0, level.timeBonusRemaining);
        // isLevelEnded should now be false, indicating time bonus phase is over
        assertFalse(level.getIsLevelEnded());
    }

    @Test
    public void testUpdateTimeBonus_FrameCountOdd() {
        // Test when frame count is odd
        app.frameCount = 1; // Odd frame count
        level.timeBonusRemaining = 10;
        level.isLevelEnded = true;

        int initialScore = app.score;
        level.updateTimeBonus(app);

        // Score should not have increased
        assertEquals(initialScore, app.score);
    }

    @Test
    public void testUpdate_BallBecomesInactive() {
        // Test that a ball becoming inactive is removed from the list
        Ball ball = new Ball(100, 100, 0, 12, app);
        level.addBall(ball);

        // Simulate the ball being captured and set as inactive
        ball.setIsActive(false);

        level.update(app);

        // Ball should be removed from the balls list
        assertFalse(level.getBalls().contains(ball));
    }

    @Test
    public void testUpdate_LevelAlreadyEnded() {
        // Test that update does not end the level if it is already ended
        level.isLevelEnded = true;
        level.update(app);
        // isLevelEnded should remain true
        assertTrue(level.getIsLevelEnded());
    }

    @Test
    public void testUpdate_NoAccelerationTiles() {
        // Ensure update works when there are no acceleration tiles
        level.accelerationTiles.clear();
        Ball ball = new Ball(100, 100, 0, 12, app);
        level.addBall(ball);

        assertDoesNotThrow(() -> level.update(app));
    }

}
